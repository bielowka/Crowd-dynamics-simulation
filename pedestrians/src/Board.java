import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import static java.lang.Math.max;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 10;
	public int editType=0;
	private boolean isMoore = true;
	private int length;
	private int height;
	private ArrayList<Point> smoke = new ArrayList<Point>();

	public void swichMoore() {
		isMoore = !isMoore;
	}

	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
		this.initialize(length, height);
		this.length = length;
		this.height = height;
	}

	public void iteration() {
		calculateSmoke();
		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].blocked = false;

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y)
				points[x][y].move();
		this.repaint();
	}

	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].clear();
			}
		calculateField();
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();

		addNeighbors();
	}

	public void addNeighbors(){
		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				points[x][y].neighbors.clear();
				if (isMoore){
					points[x][y].addNeighbor(points[x - 1][y - 1]);
					points[x][y].addNeighbor(points[x - 1][y]);
					points[x][y].addNeighbor(points[x - 1][y + 1]);
					points[x][y].addNeighbor(points[x][y - 1]);
					points[x][y].addNeighbor(points[x][y + 1]);
					points[x][y].addNeighbor(points[x + 1][y - 1]);
					points[x][y].addNeighbor(points[x + 1][y]);
					points[x][y].addNeighbor(points[x + 1][y + 1]);
				}
				else{
					points[x][y].addNeighbor(points[x][y - 1]);
					points[x][y].addNeighbor(points[x][y + 1]);
					points[x][y].addNeighbor(points[x + 1][y]);
					points[x][y].addNeighbor (points[x - 1][y]);
				}
			}
		}
	}
	
	private void calculateField(){
		ArrayList<Point> toCheck = new ArrayList<Point>();
		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				if (points[x][y].type == 2){
					points[x][y].staticField = 0;
					for (Point p : points[x][y].neighbors){
						if (p.type != 2){
							toCheck.add(p);
						}
					}
				}
			}
		}

		while (!toCheck.isEmpty()){
			Point n = toCheck.get(0);
			if (n.calcStaticField()){
				for (Point p : n.neighbors){
					if (p.type == 0){
						toCheck.add(p);
					}
				}
			}
			toCheck.remove(0);
		}

	}

	private void calculateSmoke(){
		if (!smokeExpanding()){
			for (int x = 1; x < points.length-1; ++x) {
				for (int y = 1; y < points[x].length-1; ++y) {
					if (points[x][y].smokeDensity < 10){
						points[x][y].smokeDensity = points[x][y].smokeDensity + 1;
					}
				}
			}
		}
	}

	private boolean smokeExpanding() {
		boolean result = false;
		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				if (points[x][y].type == 4){
					points[x][y].smokeDensity = 0;
					points[x][y].type = 0;
				}
			}
		}

		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				points[x][y].smokeCalculated = false;
			}
		}

		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
				if (points[x][y].smokeDensity < 10 && !points[x][y].smokeCalculated){
					for (Point p : points[x][y].neighbors) {
						if (!p.smokeCalculated){
							if (p.calcSmokeDensity()) result = true;
							p.smokeCalculated = true;
						}
					}
				}
			}
		}
		return result;
	}


	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		for (x = 1; x < points.length-1; ++x) {
			for (y = 1; y < points[x].length-1; ++y) {
				if(points[x][y].type==0){
					float staticField = points[x][y].staticField;
					float intensity = staticField/100;
					if (intensity > 1.0) {
						intensity = 1.0f;
					}
					g.setColor(new Color(intensity, intensity,intensity ));

					float smoke = points[x][y].smokeDensity;
					if (smoke < 10) {
						float density = smoke / 10;
						if (density > 1.0) {
							density = 1.0f;
						}
						g.setColor(new Color(1, density, density/2));
					}
				}
				else if (points[x][y].type==1){
					g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.7f));
				}
				else if (points[x][y].type==2){
					g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.7f));
				}
				else if (points[x][y].type==4){
					g.setColor(Color.ORANGE);
				}
				if (points[x][y].isPedestrian){
					g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.7f));
				}
				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			if(editType==3){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
			}
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			if(editType==3){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
			}
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
