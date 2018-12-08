package radial;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 * Class constructs a unique representation of data, passed
 * to the constructor as a 2D integer array, whereby a series of
 * points are rotated around the mean at intervals until the
 * minimum distance along one data axis (in this representation
 * the x-axis in a 2d Cartesian plane) is achieved. The class
 * then draws a series of points around the data mean and the
 * data is assigned to the nearest of these artificial points.
 * The associated data is then divided by the total number of
 * points.
 */
public class Feature {
	
	private List<Point> points;
	private double meanX, meanY, minI, maxI, minJ, maxJ, distance;
	private int count;
	private Point meanPoint;
	private double[] representation;

	
	public Feature(int[][] matrix) {
		this.setPoints(matrix);
		this.normalize();
		this.rotate();
		this.polarDescription();
	}
	
	private void setPoints(int[][] matrix) {
		points = new ArrayList<Point>();
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					meanX += (i+1);
					meanY += (j+1);
					count++;
					points.add(new Point(i+1, j+1, matrix[i][j]/255.0));
				}
			}
		}
		meanX /= count;
		meanY /= count;
		meanPoint = new Point(meanX, meanY, 0);
	}
	
	private void normalize() {
		double xDev = 0.0, yDev = 0.0;
		
		for (Point p : points) {
			xDev += Math.pow(p.getX() - meanPoint.getX(), 2);
			yDev += Math.pow(p.getY() - meanPoint.getY(), 2);
		}
		
		xDev = Math.sqrt(xDev/count);
		yDev = Math.sqrt(yDev/count);
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.normalize(meanPoint, xDev, yDev);
			
			if (p.getX() < minI) {
				minI = p.getX();
			}
			if (p.getX() < maxI) {
				maxI = p.getX();
			}
		}
		
		meanPoint = new Point(0,0,0);
	}
	
	private void rotate() {
		distance = maxI - minI;
		double bestRotation = 0;
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (double i = 1.0; i <= 15.0; i += 3.0) {
			for (Point p : points) {
				p.rotate(meanPoint, i);
				
				if (p.getRotateX() < minI) {
					minI = p.getRotateX();
				}
				if (p.getRotateX() > maxI) {
					maxI = p.getRotateX();
				}
			}
			
			if (maxI-minI < distance) {
				distance = maxI-minI;
				bestRotation = i;
			}

			i *= -1;
			maxI = 0; minI = Double.MAX_VALUE;
			
			for (Point p : points) {
				p.rotate(meanPoint, i);
				
				if (p.getRotateX() < minI) {
					minI = p.getRotateX();
				}
				if (p.getRotateX() > maxI) {
					maxI = p.getRotateX();
				}
			}
			
			if (maxI-minI < distance) {
				distance = maxI-minI;
				bestRotation = i;
			}
			
			maxI = 0; minI = Double.MAX_VALUE;
			i *= -1;
		}
		
		maxI = 0; minI = Double.MAX_VALUE;
		maxJ = 0; minJ = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.setRotate(meanPoint, bestRotation);
			
			if (p.getX() < minI) {
				minI = p.getX();
			}
			if (p.getX() > maxI) {
				maxI = p.getRotateX();
			}
			if (p.getY() < minJ) {
				minJ = p.getY();
			}
			if (p.getY() > maxJ) {
				maxJ = p.getY();
			}
			
		}
	}
	
	private void polarDescription() {
		int numPoints = 16; int subDivs = 5;
		double distance = Math.max(minJ, maxJ);
		
		double angle = 0;
		double iters = 360 / numPoints;
		
		Point[] circle = new Point[numPoints * subDivs];
		representation = new double[numPoints * subDivs];
		
		int k = 0;
		for (int i = 0; i < numPoints; ++i) {
			for (int j = 1; j <= subDivs; ++j) {
				circle[k] = new Point(meanPoint.getX() + (distance/j) * Math.cos(Math.toRadians(angle)),
						  meanPoint.getY() + (distance/j) * Math.sin(Math.toRadians(angle)), 0);
				k++;
			}
			angle += iters;
		}
		
		for (Point p : points) {
			double min = Double.MAX_VALUE;
			int binNum = 0;
			
			for (int i = 0; i < representation.length; ++i) {
				double pos = p.getDistance(circle[i]);
				
				if (pos < min) {
					min = pos;
					binNum = i;
				}
			}
			representation[binNum] += p.getValue();
		}
		
		for (int i = 0; i < representation.length; ++i) {
			representation[i] /= count;
		}
	}
	
	public double compare(Feature c) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			error += Math.pow(representation[i] - c.representation[i], 2);
		}
		
		return 1.0 - error;
	}
	
	public double compare(double[] c) {
		double error = 0.0;
		
		for (int i = 0; i < representation.length; ++i) {
			error += Math.pow(representation[i] - c[i], 2);
		}
		
		return 1.0 - error;
	}
	
	public double[] getRepresentation() {
		return representation;
	}
	
	public String toString() {
		DecimalFormat df = new DecimalFormat( "#.####" );
		String data = "|\t";
		
		for (int i = 0; i < representation.length; ++i) {
			data += df.format(representation[i]) + "\t";
		}
		
		return data + "|\n";
	}

}

