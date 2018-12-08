package test;

import mnist.MnistDataReader;
import mnist.MnistMatrix;
import radial.*;

/**
 * @author Bobby Palmer
 * Class tests shape representation on the modified
 * adaptive resonance model against the MNIST data-set.
 */
public class Test {

	public static void main(String[] args) {
//		quickMnistTest();
		mnistTest();
//		mnistEpochalTest();
	}
	
	/**
	 * Quick/Simple MNIST test which accepts the first 10 occurrences
	 * of a label in the training set as cluster vectors and
	 * compares the entire testing set against them for accuracy.
	 * Used to quickly test modifications to the data representation.
	 */
	public static void quickMnistTest() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			Feature[] features = new Feature[mnistMatrix.length];
			Feature[] clusters = new Feature[10];
			int count = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
				
				if (clusters[mnistMatrix[i].getLabel()] == null) {
					clusters[mnistMatrix[i].getLabel()] = features[i];
					count++;
					if (count == 10) {
						break;
					}
				}
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			double accuracy = 0;
			double avgAccuracy = 0;
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());

				int label = -1;
				double max = 0;
				
				for (int j = 0; j < clusters.length; ++j) {
					double v = clusters[j].compare(features[i]);
					avgAccuracy += v;
					if (v > max) {
						label = j;
						max = v;
					}
				}
				
				if (mnistMatrix[i].getLabel() == label) {
					accuracy++;
				}
			}
			
			System.out.println("MNIST Training Accuracy: " + (double)accuracy / (double)mnistMatrix.length);
			System.out.println("Average Accuracy: " + avgAccuracy / (mnistMatrix.length * 10));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mnistTest() {
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			double globalVigilance = 0.98;
			
			AdaptiveResonance model = new AdaptiveResonance(globalVigilance, 0.0);
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.trainSupervised(new Feature(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.testSupervised(new Feature(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}
			
			System.out.println("MNIST Training Accuracy: " + (double)(model.getSuccesfullyClassified())/(double)mnistMatrix.length);
			System.out.println("Cluster Count: " + model.getNumberClusters());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void mnistEpochalTest() {
		// Adjustable parameters:
		int epochs = 8; int epochCount = 0;
		double globalVigilance = 0.98; double learningRate = 0.0;
		
		try {
			MnistMatrix[] mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/train-images.idx3-ubyte",
																   "./resources/mnistdata/train-labels.idx1-ubyte");
			
			AdaptiveResonance model = new AdaptiveResonance(globalVigilance, learningRate);
			
			Feature[] features = new Feature[mnistMatrix.length];
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				features[i] = new Feature(mnistMatrix[i].getData());
			}
			
			while (epochCount < epochs) {
				for (int i = 0; i < mnistMatrix.length; ++i) {
					model.trainSupervised(features[i], mnistMatrix[i].getLabel());
				}
				epochCount++;
			}

			mnistMatrix = new MnistDataReader().readData("./resources/mnistdata/t10k-images.idx3-ubyte",
														"./resources/mnistdata/t10k-labels.idx1-ubyte");
			
			for (int i = 0; i < mnistMatrix.length; ++i) {
				model.testSupervised(new Feature(mnistMatrix[i].getData()), mnistMatrix[i].getLabel());
			}
			
			System.out.println("MNIST Training Accuracy: " + (double)(model.getSuccesfullyClassified())/(double)mnistMatrix.length);
			System.out.println("Cluster Count: " + model.getNumberClusters());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
