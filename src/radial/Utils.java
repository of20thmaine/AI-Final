package radial;

/**
 * @author Bobby Palmer
 * Simple Utility Function Class
 */
public class Utils {
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
	}
	
	public static int sideOfLine(Point p0, Point p1, Point p2) {
		double d = ((p0.getX()-p1.getX()) * (p2.getY()-p1.getY())) - 
					((p0.getY()-p1.getY()) * (p2.getX()-p1.getX()));
		
		if (d > 0) {
			return 1;
		} else if (d < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
}



