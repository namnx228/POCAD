package edu.gatech.mcpad.scripts;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import edu.gatech.mcpad.network.ClassificationResults;
import edu.gatech.mcpad.network.PacketClassifier;
import edu.gatech.mcpad.network.ThresholdFactory;
import edu.gatech.mcpad.network.PacketClassifier.CombinationRule;
import edu.gatech.mcpad.utils.DirectoryHandler;

public class TestScript {
	private static final String dirSep = File.separator; 
	private static final String linSep = System.getProperty("line.separator");

	private static int[] clusters = {10,20,40,80,160};

	public static void main(String[] args) {
		String sourcesDirectory = args[0];
		String mountPoint = args[1]; 
		int firstDay = Integer.parseInt(args[2]);
		int lastDay = Integer.parseInt(args[3]);
		String falsePositive = args[4];
		
		Date date;

		String sourcePcapDirectory = (sourcesDirectory + dirSep + "pcap");
		String sourcePcapCrossValidationDirectory = (sourcePcapDirectory + dirSep + "crossvalidation");

		File rootDirectory;
		File crossValidationDir = new File(sourcePcapCrossValidationDirectory);

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVAttack = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVNormal = new HashMap<CombinationRule, Double>();
		
		ClassificationResults results;

		for(int day = firstDay; day<=lastDay; day++){
			for(int k = 0; k < clusters.length; k++){
				try{

					File crossValidationFile = new File(crossValidationDir + dirSep + "day" + day + ".pcap");
					rootDirectory = makeRootDirectory(mountPoint, day, clusters[k], falsePositive);
					PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(mountPoint + dirSep + "crossvalidation" + dirSep + "Test_day_" + day + "_k_" + clusters[k]+ "_FP_" + falsePositive + ".out"))); 
					System.setOut(out);
					PrintStream err = new PrintStream(new BufferedOutputStream(new FileOutputStream(mountPoint + dirSep + "crossvalidation" + dirSep +"Test_day_" + day + "_k_" + clusters[k]+ "_FP_" + falsePositive + ".err"))); 
					System.setErr(err);
					DirectoryHandler directoryHandler = new DirectoryHandler(rootDirectory.toString());
					ThresholdFactory thFactory = new ThresholdFactory(directoryHandler);
					
					date = new Date();
					System.out.println("Start of test: " + date);
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

					System.out.println("===============================================");
					System.out.println("----- TEST SVM MODELS -----");
					System.out.println("Testing " + crossValidationFile.getName().toString());
					PacketClassifier pc = new PacketClassifier(directoryHandler);
					results = pc.test(crossValidationFile.toString(),"",thresholds,-1);
					results.printStatistics();
					date = new Date();
					System.out.println("End of test : " + date);

					/*System.out.println("===============================================");
					System.out.println("----- TEST SVM MODELS (WITH MV-ATTACKS THRESHOLD) -----");
					pc = new PacketClassifier(directoryHandler);
					results = pc.test(crossValidationFile.toString(),"",thresholdsMVAttack,-1);
					results.printStatistics();


					System.out.println("===============================================");
					System.out.println("----- TEST SVM MODELS (WITH MV-NORMAL THRESHOLD) -----");
					pc = new PacketClassifier(directoryHandler);
					results = pc.test(crossValidationFile.toString(),"",thresholdsMVNormal,-1);*/


					System.out.println("===============================================");
					System.out.println("Done!");
					
					err.close();
					out.close();
					
				}catch(IOException e){
					System.err.println("IOException " + e);
					System.exit(-1);
				}
			}
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
}
