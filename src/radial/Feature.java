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
	private double meanX, meanY, minI, maxI, minJ, maxJ, distanceX, avgColor, colorVariance;
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
		avgColor = 0;
		
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix[i].length; ++j) {
				if (matrix[i][j] > 0) {
					meanX += (i+1);
					meanY += (j+1);
					count++;
					points.add(new Point(i+1, j+1, matrix[i][j]/255.0));
					avgColor += matrix[i][j];
				}
			}
		}
		avgColor /= count;
		meanX /= count;
		meanY /= count;
		meanPoint = new Point(meanX, meanY, 0);
	}
	
	private void normalize() {
		double xDev = 0.0, yDev = 0.0;
		colorVariance = 0;
		
		for (Point p : points) {
			xDev += Math.pow(p.getX() - meanPoint.getX(), 2);
			yDev += Math.pow(p.getY() - meanPoint.getY(), 2);
			colorVariance += Math.pow(avgColor-p.getValue(), 2);
		}
		
		xDev = Math.sqrt(xDev/count);
		yDev = Math.sqrt(yDev/count);
		colorVariance = Math.sqrt(colorVariance/count);
		maxI = 0; minI = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.normalize(meanPoint, xDev, yDev);
			
			if (p.getX() < minI) {
				minI = p.getX();
			}
			if (p.getX() < maxI) {
				maxI = p.getX();
			}
			if (p.getY() < minJ) {
				minJ = p.getY();
			}
			if (p.getY() > maxJ) {
				maxJ = p.getY();
			}
		}
		
		meanPoint = new Point(0,0,0);
	}
	
	private void rotate() {
		distanceX = maxI - minI;
		
		double bestRotationX = 0;
		
		for (double i = -30.0; i <= 30.0; i += 1.0) {
			maxI = Double.MIN_VALUE; minI = Double.MAX_VALUE;
			maxJ = Double.MIN_VALUE; minJ = Double.MAX_VALUE;
			
			for (Point p : points) {
				p.rotate(meanPoint, i);
				
				if (p.getRotateX() < minI) {
					minI = p.getRotateX();
				}
				if (p.getRotateX() > maxI) {
					maxI = p.getRotateX();
				}
			}
			
			if (maxI-minI < distanceX) {
				distanceX = maxI-minI;
				bestRotationX = i;
			}

		}
		
		maxI = Double.MIN_VALUE; minI = Double.MAX_VALUE;
		maxJ = Double.MIN_VALUE; minJ = Double.MAX_VALUE;
		
		for (Point p : points) {
			p.setRotate(meanPoint, bestRotationX);
			
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
		int numPoints = 16; int subDivs = 6;
		double distance = Math.max(Math.max(
				Math.abs(maxI),
				Math.abs(minI)), Math.max(
				Math.abs(maxJ),
				Math.abs(minJ)));
		
		double angle = 0;
		double deltaA = 360 / numPoints;
		double deltaD = distance / subDivs;
		
		Point[] circle = new Point[numPoints * subDivs];
		representation = new double[numPoints * subDivs];
		
		int k = 0;
		for (int i = 0; i < numPoints; ++i) {
			for (int j = 1; j <= subDivs; ++j) {
				circle[k] = new Point(meanPoint.getX() + (j*deltaD) * Math.cos(Math.toRadians(angle)),
						  meanPoint.getY() + (j*deltaD) * Math.sin(Math.toRadians(angle)), 0);
				k++;
			}
			angle += deltaA;
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
		double avgDiff = 0;
		
		for (int i = 0; i < representation.length; ++i) {
			avgDiff += Math.pow(representation[i] - c.representation[i], 2);
		}
		return 1.0 / (1.0 + Math.sqrt(avgDiff));
	}
	
	public double compare(double[] c) {
		double avgDiff = 0;
		
		for (int i = 0; i < representation.length; ++i) {
			avgDiff += Math.pow(representation[i] - c[i], 2);
		}
		return 1.0 / (1.0 + Math.sqrt(avgDiff));
	}
	
	public double[] getRepresentation() {
		return representation;
	}
	
	public List<Point> getPoints() {
		return points;
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

