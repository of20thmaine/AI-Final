package radial;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bobby Palmer
 * Learning model based on Grossberg's ART-1 modified for supervised
 * learning. Model is instantiated with a global vigilance parameter,
 * and contains a sub-class called cluster. During training an input
 * (feature vector) is given to the model, and compared to the set of
 * clusters contained within. Each cluster has a label assigned to it.
 * If the feature vector label and cluster vector label match, and the
 * similarity of the two is greater than the global vigilance parameter,
 * the cluster accepts the feature. For clusters that do not contain the
 * label but are more similar than global vigilance, a local vigilance
 * parameter persistent in the cluster instance is updated so it will no
 * longer compare to that feature. If no cluster is accepted, the model
 * adds the feature as a cluster, with local vigilance equal to global.
 */
public class AdaptiveResonance {
	
	private List<Cluster> clusters;
	private int validClassification;
	double globalVigilance, learningRate;
	
	public AdaptiveResonance(double globalVigilance, double learningRate) {
		validClassification = 0;
		clusters = new ArrayList<Cluster>();
		this.globalVigilance = globalVigilance;
		this.learningRate = learningRate;
	}
	
	public void trainSupervised(Feature f, int label) {
		boolean accepted = false;
		Cluster mostSimilar = null;
		double similarity = 0;
		
		for (int i = 0; i < clusters.size(); ++i) {			
			boolean found = clusters.get(i).train(f, label);
			if (found) {
				accepted = true;
				
				if (similarity < clusters.get(i).getSimilarity()) {
					mostSimilar = clusters.get(i);
					similarity = clusters.get(i).getSimilarity();
				}
			}
		}
		if (accepted) {
			mostSimilar.resonate(f, 0.0);
		} else {
			clusters.add(new Cluster(f.getRepresentation(), label));
		}
	}
	
	public void testSupervised(Feature f, int label) {
		Cluster mostSimilar = null;
		double similarity = 0;
		
		for (int i = 0; i < clusters.size(); ++i) {			
			double c = clusters.get(i).test(f, label);
			
			if (c > similarity) {
				mostSimilar = clusters.get(i);
				similarity = c;
			}
		}
		
		if (mostSimilar != null) {
			if (mostSimilar.label == label) {
				validClassification += 1;
			}
		}
		
	}
	
	public int getNumberClusters() {
		return clusters.size();
	}
	
	public int getSuccesfullyClassified() {
		return validClassification;
	}
	
	
	/**
	 * Class is used to generate cluster objects
	 * which store the cluster data, a local vigilance
	 * parameter, and a label. Class contains methods
	 * for testing the cluster against a feature vector.
	 */
	private class Cluster {
		
		double[] cluster;
		double vigilance;
		int label;
		double similarity;
		
		public Cluster(double[] cluster, int label) {
			this.cluster = cluster;
			this.label = label;
			vigilance = globalVigilance;
		}
		
		public boolean train(Feature f, int fLabel) {
			similarity = f.compare(cluster);
			
			if (label == fLabel) {
				if (similarity > vigilance) {
					this.resonate(f, learningRate);
					return true;
				}
				return false;
			} else {
				if (similarity > vigilance) {
					vigilance = similarity;
				}
				return false;
			}
		}
		
		public double test(Feature f, int fLabel) {
			double c = f.compare(cluster);
			
			if (c > vigilance) {
				return c;
			} else {
				return 0;
			}
		}
		
		public void resonate(Feature f, double learningRate) {
			double[] bin2 = f.getRepresentation();
			for (int i = 0; i < cluster.length; ++i) {
				cluster[i] += ((cluster[i] - bin2[i]) * learningRate);
			}
		}
		
		
		public double getSimilarity() {
			return similarity;
		}
	}
	
}
