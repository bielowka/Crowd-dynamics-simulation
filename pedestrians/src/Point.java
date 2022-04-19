import java.util.ArrayList;
import java.util.Random;

public class Point {

	public ArrayList<Point> neighbors;
	public static Integer []types ={0,1,2,3,4};
	public int type;
	public int staticField;
	public boolean isPedestrian;
	public boolean blocked;
	public int smokeDensity;
	public boolean smokeCalculated;

	public Point() {
		type=0;
		staticField = 100000;
		neighbors= new ArrayList<Point>();
		blocked = false;
		smokeDensity = 10;
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

	public boolean calcSmokeDensity() {
		int smallest = 10;
		for (Point p : neighbors){
			if (p.smokeDensity < smallest) smallest = p.smokeDensity;
		}
		if (this.smokeDensity > smallest + 1) {
			this.smokeDensity = smallest + 1;
			return true;
		}

		return false;
	}

	
	public void move(){
		Random r = new Random();
		Point nextPosition = this;
		if (isPedestrian && !blocked){
			ArrayList<Point> nextPos = new ArrayList<Point>();
			int smallest = this.staticField;
			for (Point p : neighbors){
				if (p.staticField < smallest && !p.isPedestrian && p.type != 1) smallest = p.staticField;
			}
			for (Point p : neighbors){
				if (p.staticField == smallest && !p.isPedestrian && p.type == 0) nextPos.add(p);
			}
			if (!nextPos.isEmpty()) {
				int id = r.nextInt(nextPos.size());
				nextPosition = nextPos.get(id);
				int panic = r.nextInt(10);
				if (panic > nextPosition.smokeDensity){
					nextPosition = this;
				}
				if (!nextPosition.isPedestrian) {
					if (nextPosition.type != 2) {
						nextPosition.isPedestrian = true;
						nextPosition.blocked = true;
					}
					this.isPedestrian = false;
				}
			}
			else if (nextPosition == this){ //trying random order when there wasn't any better
				for (Point p : neighbors){
					int tmp = r.nextInt(100);
					if (!p.isPedestrian && p.type != 1 && tmp < 5) nextPosition = p;
				}
				if (!nextPosition.isPedestrian){
					if (nextPosition.type != 2){
						nextPosition.isPedestrian = true;
						nextPosition.blocked = true;
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