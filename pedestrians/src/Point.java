import java.util.ArrayList;
import java.util.Random;

public class Point {

	public ArrayList<Point> neighbors;
	public static Integer []types ={0,1,2,3};
	public int type;
	public int staticField;
	public boolean isPedestrian;
	public boolean blocked;

	public Point() {
		type=0;
		staticField = 100000;
		neighbors= new ArrayList<Point>();
		blocked = false;
	}
	
	public void clear() {
		staticField = 100000;

	}

	public boolean calcStaticField() {
		int smallest = 100000;
		for (Point p : neighbors){
			if (p.staticField < smallest) smallest = p.staticField;
		}
		if (this.staticField > smallest + 1) {
			this.staticField = smallest + 1;
			return true;
		}

		return false;
	}
	
	public void move(){
		if (isPedestrian && !blocked){
			Point nextPos = this;
			for (Point p : neighbors){
				if (p.staticField < nextPos.staticField && !p.isPedestrian && p.type != 1) nextPos = p;
			}
			if (!nextPos.isPedestrian){
				if (nextPos.type != 2){
					nextPos.isPedestrian = true;
					nextPos.blocked = true;
				}
				this.isPedestrian = false;
			}
			else if (nextPos == this){
				Random r = new Random();
				for (Point p : neighbors){
					int tmp = r.nextInt(100);
					if (!p.isPedestrian && p.type != 1 && tmp < 5) nextPos = p;
				}
				if (!nextPos.isPedestrian){
					if (nextPos.type != 2){
						nextPos.isPedestrian = true;
						nextPos.blocked = true;
					}
					this.isPedestrian = false;
				}
			}
		}
	}

	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}
}