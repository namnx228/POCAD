package edu.gatech.mcpad.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.gatech.mcpad.network.ClassificationResults;
import edu.gatech.mcpad.network.PacketClassifier;
import edu.gatech.mcpad.network.ThresholdFactory;
import edu.gatech.mcpad.network.PacketClassifier.CombinationRule;
import edu.gatech.mcpad.utils.DirectoryHandler;

public class TestScriptSingleAttack {

	private static final String dirSep = File.separator; 
	private static final String linSep = System.getProperty("line.separator");

	private static int[] clusters = {10,20,40,80,160};

	public static void main(String[] args) {
		String sourcesDirectory = args[0];
		String mountPoint = args[1]; 
		int firstDay = Integer.parseInt(args[2]);
		int lastDay = Integer.parseInt(args[3]);
		String falsePositive = args[4];


		String sourcePcapDirectory = (sourcesDirectory + dirSep + "pcap");
		String sourcePcapAttacksDirectory = (sourcePcapDirectory + dirSep + "test" + dirSep + "attacks");

		File rootDirectory;
		File attacksDir = new File(sourcePcapAttacksDirectory);
		File[] attackFiles = attacksDir.listFiles();

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVAttack = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVNormal = new HashMap<CombinationRule, Double>();

		File testResults = new File(mountPoint + dirSep + "test_day" + firstDay + "-" + lastDay + "_FP-" + falsePositive + ".txt");
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(testResults));
			ClassificationResults results;

			for(int day = firstDay; day<=lastDay; day++){
				for(int k = 0; k < clusters.length; k++){
					rootDirectory = makeRootDirectory(mountPoint, day, clusters[k], falsePositive);
					DirectoryHandler directoryHandler = new DirectoryHandler(rootDirectory.toString());
					ThresholdFactory thFactory = new ThresholdFactory(directoryHandler);

					System.out.println("----- LOADING THRESHOLDS -----");
					System.out.println("----- LOADING THRESHOLDS thresholds.obj -----");
					thresholds = thFactory.loadThreshodls("thresholds.obj");
					thFactory.printThresholds(thresholds);
					System.out.println("----- LOADING THRESHOLDS FROM thresholdsMVAttack.obj -----");
					thresholdsMVAttack = thFactory.loadThreshodls("thresholdsMVAttacks.obj");
					thFactory.printThresholds(thresholdsMVAttack);
					System.out.println("----- LOADING THRESHOLDS FROM thresholdsMVNormal.obj -----");
					thresholdsMVNormal = thFactory.loadThreshodls("thresholdsMVNormal.obj");
					thFactory.printThresholds(thresholdsMVNormal);
					System.out.println("===============================================");
					System.out.println("Done!");

					for(int i = 0; i < attackFiles.length; i++){

						System.out.println("===============================================");
						System.out.println("----- TEST SVM MODELS -----");
						System.out.println("Testing " + attackFiles[i].getName().toString());
						PacketClassifier pc = new PacketClassifier(directoryHandler);
						results = pc.test(attackFiles[i].toString(),"",thresholds,-1);
						printResultsToFile(w,results,day,clusters[k],falsePositive,attackFiles[i].getName().toString(),"NO-MV");

						System.out.println("===============================================");
						System.out.println("----- TEST SVM MODELS (WITH MV-ATTACKS THRESHOLD) -----");
						pc = new PacketClassifier(directoryHandler);
						results = pc.test(attackFiles[i].toString(),"",thresholdsMVAttack,-1);
						printResultsToFile(w,results,day,clusters[k],falsePositive,attackFiles[i].getName().toString(),"MV-ATT");

						System.out.println("===============================================");
						System.out.println("----- TEST SVM MODELS (WITH MV-NORMAL THRESHOLD) -----");
						pc = new PacketClassifier(directoryHandler);
						results = pc.test(attackFiles[i].toString(),"",thresholdsMVNormal,-1);
						printResultsToFile(w,results,day,clusters[k],falsePositive,attackFiles[i].getName().toString(),"MV-NORM");

						System.out.println("===============================================");
						System.out.println("Done!");
					}

				}
			}
			w.close();
		}catch(IOException e){
			System.err.println("IOException " + e);
		}
	}


	private static File makeRootDirectory(String mountPoint, int day, int cluster, String falsePositive){

		String dayString =  ("_day-" + day);
		String clustersString =  ("_k-" + cluster);
		String falsePositiveString =  ("_FP-" + falsePositive);

		File dir = new File(mountPoint + dirSep + "SVM_" + dayString + clustersString + falsePositiveString);

		if(!dir.exists() || !dir.isDirectory()) {
			System.out.println(dir.getAbsolutePath() + " must be a directory");
			System.exit(1);
		}
		return dir;
	}

	private static void printResultsToFile(BufferedWriter w, ClassificationResults results, int day, int clusters, String falsePositive,
			String fileName, String typeOfThreshold){
		String prefix = ("day" + day + "_k" + clusters + "_FP-" + falsePositive + "_" + fileName + "_");
		try{

			if(results.numDetectedAttacksAvgProb() >0){
				w.write(prefix + typeOfThreshold + "_AvgProb\tDETECTED" + linSep);
			} else
				w.write(prefix + typeOfThreshold + "_AvgProb\tNOT-DETECTED" + linSep);

			if(results.numDetectedAttacksMajVoting() >0){
				w.write(prefix + typeOfThreshold + "_MajVoting\tDETECTED" + linSep);
			} else
				w.write(prefix + typeOfThreshold + "_MajVoting\tNOT-DETECTED" + linSep);

			if(results.numDetectedAttacksMaxProb() >0){
				w.write(prefix + typeOfThreshold + "_MaxProb\tDETECTED" + linSep);
			} else
				w.write(prefix + typeOfThreshold + "_MaxProb\tNOT-DETECTED" + linSep);

			if(results.numDetectedAttacksMinProb() >0){
				w.write(prefix + typeOfThreshold + "_MinProb\tDETECTED" + linSep);
			} else
				w.write(prefix + typeOfThreshold + "_MinProb\tNOT-DETECTED" + linSep);

			if(results.numDetectedAttacksProdProb() >0){
				w.write(prefix + typeOfThreshold + "_ProdProb\tDETECTED" + linSep);
			} else
				w.write(prefix + typeOfThreshold + "_ProdProb\tNOT-DETECTED" + linSep);

		}catch(IOException e){
			System.out.println("Exception in TestScriptSingleAttack" + e);
		}
	}

}
