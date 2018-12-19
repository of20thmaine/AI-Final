package radial;

/**
 * @author Bobby Palmer
 * Class serves as representation of Cartesian point data.
 */
public class Point {
	
	private double x, y, value;
	private double rotateX, rotateY, distanceToMean;
	
	public Point(double x, double y, double value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getValue() {
		return value;
	}
	
	public void normalizeValue(double averageColor, double colorVariance) {
		if (Math.abs(value - averageColor) <= colorVariance) {
			value = 1.0;
		} else if (Math.abs(value - averageColor) <= colorVariance*2) {
			value = 0.8;
		} else {
			value = 0.4;
		}
	}
	
	public void rotate(Point mean, double angleDeg) {
		double angleRad = (angleDeg / 180) * Math.PI;
	    double cosAngle = Math.cos(angleRad);
	    double sinAngle = Math.sin(angleRad);
	    double dx = (x - mean.x);
	    double dy = (y - mean.y);

	    rotateX = mean.x + (dx*cosAngle-dy*sinAngle);
	    rotateY = mean.y + (dx*sinAngle+dy*cosAngle);
	}
	
	public void setRotate(Point mean, double angleDegX) {
		this.rotate(mean, angleDegX);

		x = rotateX;
		y = rotateY;
	}
	
	public double getRotateX() {
		return rotateX;
	}
	
	public double getRotateY() {
		return rotateY;
	}
	
	public void normalize(Point mean, double xDev, double yDev) {
		x = (x - mean.getX()) / xDev;
		y = (y - mean.getY()) / yDev;
	}
	
	public void setDistanceToMean(Point mean) {
		distanceToMean = this.getDistance(mean);
	}
	
	public double getDistanceToMean() {
		return distanceToMean;
	}
	
	public String toString() {
		return  "x = " + x + ", y = " + y;
	}
	
	public double getDistance(Point p) {
		return Utils.distance(x, y, p.x, p.y);
	}
}
