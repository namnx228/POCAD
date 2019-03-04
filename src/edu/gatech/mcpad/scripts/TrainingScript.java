package edu.gatech.mcpad.scripts;

import java.io.File;
import java.io.IOException;

import edu.gatech.mcpad.io.Copy;
import edu.gatech.mcpad.svm.svm_parameter;
import edu.gatech.mcpad.utils.ClusteringConfiguration;


public class TrainingScript {

	private static final String dirSep = File.separator; 

	private static int[] clusters = {10,20,40,80,160};

	public static void main(String[] args){
		String sourcesDirectory = args[0];
		String mountPoint = args[1]; 
		int firstDay = Integer.parseInt(args[2]);
		int lastDay = Integer.parseInt(args[3]);
		String falsePositive = args[4];

		String sourceConfigurationDirectory = (sourcesDirectory + dirSep + "configuration" + dirSep);
		String sourceClusteringConfigurationFile = (sourceConfigurationDirectory + "clustering.conf");
		String sourceSvmConfigurationFile = (sourceConfigurationDirectory + "svm.conf");
		String sourcePcapDirectory = (sourcesDirectory + dirSep + "pcap");

		File destRootDirectory;
		File destClusteringDirectory;
		File destSvmDirectory;
		File destPcapDirectory;

		ClusteringConfiguration clusteringConfiguration = new ClusteringConfiguration(sourceClusteringConfigurationFile);
		svm_parameter svmConfiguration = new svm_parameter(sourceSvmConfigurationFile);


		for(int day = firstDay; day<=lastDay; day++){
			for(int k = 0; k < clusters.length; k++){
				System.out.println("");
				System.out.println("");
				System.out.println("........................................");
				System.out.println("---------- TRAINING PROCEDURE ----------");
				System.out.println("........................................");
				System.out.println("");
				System.out.println("---------- Traffic Day " + day + " ----------");
				System.out.println("---------- Number of Clusters " + clusters[k] + " -------");
				System.out.println("---------- False Positive Rate " + falsePositive + " ----");
				destRootDirectory = makeRootDirectory(mountPoint, day, clusters[k], falsePositive);
				destClusteringDirectory = makeSubDirectory(destRootDirectory, "clustering");
				destSvmDirectory = makeSubDirectory(destRootDirectory, "svm");
				destPcapDirectory = makeSubDirectory(destRootDirectory, "pcap");
				clusteringConfiguration.numOfClusters = clusters[k];
				clusteringConfiguration.save(destClusteringDirectory + dirSep + "clustering.conf");
				svmConfiguration.nu = Double.parseDouble(falsePositive);
				svmConfiguration.save(destSvmDirectory + dirSep + "svm.conf");
				String sourcePcapTrainFile = (sourcePcapDirectory + dirSep + "day" + day + dirSep + "training" + day + ".pcap");
				String destPcapTrainFile = (destPcapDirectory + dirSep + "training.pcap");
				fileCopy(sourcePcapTrainFile,destPcapTrainFile);

				String sourcePcapValNormalFile = (sourcePcapDirectory + dirSep + "day" + day + dirSep + "validation" + day + ".pcap");
				String destPcapValNormalFile = (destPcapDirectory + dirSep + "validationNormal.pcap");
				fileCopy(sourcePcapValNormalFile,destPcapValNormalFile);

				String sourcePcapValAttackFile = (sourcePcapDirectory + dirSep + "attacks" + dirSep + "validationAttack.pcap");
				String destPcapValAttackFile = (destPcapDirectory + dirSep + "validationAttack.pcap");
				fileCopy(sourcePcapValAttackFile,destPcapValAttackFile);

				System.out.println("The length of the vector clusters is " + clusters.length);

				String[] training = {"-l","","-p","-1","-r",destRootDirectory.toString(),"-t","-s",falsePositive};
				System.out.println(training.length);
				PAD2vGram.main(training);

				File trainingSetDir = new File(destRootDirectory + dirSep + "svm" + dirSep + "trainingsets");

				File[] dataFiles = trainingSetDir.listFiles();

				for(int i = 0; i<dataFiles.length; i++){
					System.out.println("");
					System.out.println("Files " + i + ".data erased...");
					dataFiles[i].delete();
				}

				trainingSetDir.delete();

				System.out.println("TrainingSet directory erased...");
				System.out.println("");

				System.out.println("........................................");
				System.out.println("------ TRAINING PROCEDURE FINISHED!!! ------");
				System.out.println("........................................");
				System.out.println("");
				System.out.println("");
				System.out.println("");
			}
		}
	}

	private static File makeRootDirectory(String mountPoint, int day, int cluster, String falsePositive){

		String dayString =  ("_day-" + day);
		String clustersString =  ("_k-" + cluster);
		String falsePositiveString =  ("_FP-" + falsePositive);

		File dir = new File(mountPoint + "SVM_" + dayString + clustersString + falsePositiveString);

		if(!dir.exists()) {
			System.out.println();
			System.out.println("Creating " + dir.getAbsolutePath());
			System.out.println();
			dir.mkdir();
		}

		if(!dir.isDirectory()) {
			System.out.println(dir.getAbsolutePath() + " must be a directory");
			System.exit(1);
		}
		return dir;
	}

	private static File makeSubDirectory(File rootDirectory, String subDirectory){
		File dir = new File(rootDirectory, subDirectory);
		if(!dir.exists()) {
			System.out.println();
			System.out.println("Creating " + dir.getAbsolutePath());
			System.out.println();
			dir.mkdir();
		}

		if(!dir.isDirectory()) {
			System.out.println(dir.getAbsolutePath() + " must be a directory");
			System.exit(1);
		}
		return dir;
	}

	private static void fileCopy(String sourceFile, String destinationFile){
		System.out.println("----- Copying " + " -----");
		System.out.println("From : -> " + sourceFile);
		System.out.println("To   : -> " + destinationFile);
		try{
			Copy.fileCopy(sourceFile, destinationFile);
			System.out.println("DONE");
		}catch(IOException e){
			System.err.println("IOException " + e);
			System.exit(1);
		}
	}
}
