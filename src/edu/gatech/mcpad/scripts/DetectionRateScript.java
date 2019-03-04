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

public class DetectionRateScript {

	private static final String dirSep = File.separator; 
	private static final String linSep = System.getProperty("line.separator");
	private static int[] clusters = {10,20,40,80,160};

	public static void main(String[] args) {
		String mountPoint = args[0]; 
		int firstDay = Integer.parseInt(args[1]);
		int lastDay = Integer.parseInt(args[2]);
		String falsePositive = args[3];
		String testFile = args[4];
		String prefix = args[5];

		File rootDirectory;

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVAttack = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVNormal = new HashMap<CombinationRule, Double>();

		ClassificationResults results1;
		ClassificationResults results2;
		ClassificationResults results3;
		PacketClassifier pc;
		File testResults = new File(prefix + "_day" + firstDay + "-" + lastDay + "_FP-" + falsePositive + ".txt");
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(testResults));
			for(int day = firstDay; day<=lastDay; day++){
				for(int k = 0; k < clusters.length; k++){
					rootDirectory = makeRootDirectory(mountPoint, day, clusters[k], falsePositive);
					DirectoryHandler directoryHandler = new DirectoryHandler(rootDirectory.toString());
					ThresholdFactory thFactory = new ThresholdFactory(directoryHandler);
					pc = new PacketClassifier(directoryHandler);

					System.out.println("===============================================");
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
					System.out.println("----- TEST SVM MODELS -----");
					System.out.println("Testing " + testFile);
					results1 = pc.test(testFile,"",thresholds,-1);
					w.write(rootDirectory.getName().toString());	

					System.out.println("===============================================");
					System.out.println("----- TEST SVM MODELS (WITH MV-ATTACKS THRESHOLD) -----");
					results2 = pc.test(testFile,"",thresholdsMVAttack,-1);


					System.out.println("===============================================");
					System.out.println("----- TEST SVM MODELS (WITH MV-NORMAL THRESHOLD) -----");
					results3 = pc.test(testFile,"",thresholdsMVNormal,-1);
					w.write(toString(results1, results2, results3));
					w.write("==============================================================================================");
					w.write(linSep);
					
					System.out.println("===============================================");
					System.out.println("Done!");
				}
			}
			w.close();
		}catch(IOException e){
			System.err.println("IOException " + e);
		}
	}
	
	public static String toString(ClassificationResults results1, ClassificationResults results2, ClassificationResults results3) {
		StringBuffer sb = new StringBuffer();
		
		int n = results1.numOfAnalyzedPackets();
		sb.append(linSep);
		sb.append("Number of analyzed packets: " + n);
		sb.append(linSep);
		sb.append("DETECTION RATES :\t\t\tNoThresholds"+ "\t\t\t" + "thresholdsMVAttack" + "\t\t" + "thresholdsMVNormal");
		sb.append(linSep);
		sb.append("Majority Voting : \t\t" + results1.numDetectedAttacksMajVoting() + " ( " + results1.numDetectedAttacksMajVoting()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results2.numDetectedAttacksMajVoting() + " ( " + results2.numDetectedAttacksMajVoting()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results3.numDetectedAttacksMajVoting() + " ( " + results3.numDetectedAttacksMajVoting()*100/(double)n + "% ) \t");
		sb.append(linSep);
		sb.append("Average of Probabilities : \t" + results1.numDetectedAttacksAvgProb() + " ( " + results1.numDetectedAttacksAvgProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results2.numDetectedAttacksAvgProb() + " ( " + results2.numDetectedAttacksAvgProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results3.numDetectedAttacksAvgProb() + " ( " + results3.numDetectedAttacksAvgProb()*100/(double)n + "% ) \t");
		sb.append(linSep);
		sb.append("Product of Probability : \t" + results1.numDetectedAttacksProdProb() + " ( " + results1.numDetectedAttacksProdProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results2.numDetectedAttacksProdProb() + " ( " + results2.numDetectedAttacksProdProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results3.numDetectedAttacksProdProb() + " ( " + results3.numDetectedAttacksProdProb()*100/(double)n + "% ) \t");
		sb.append(linSep);
		sb.append("Minimum Probability : \t\t" + results1.numDetectedAttacksMinProb() + " ( " + results1.numDetectedAttacksMinProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results2.numDetectedAttacksMinProb() + " ( " + results2.numDetectedAttacksMinProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results3.numDetectedAttacksMinProb() + " ( " + results3.numDetectedAttacksMinProb()*100/(double)n + "% ) \t");
		sb.append(linSep);
		sb.append("Maximum Probability : \t\t" + results1.numDetectedAttacksMaxProb() + " ( " + results1.numDetectedAttacksMaxProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results2.numDetectedAttacksMaxProb() + " ( " + results2.numDetectedAttacksMaxProb()*100/(double)n + "% ) \t");
		sb.append("\t\t  " + results3.numDetectedAttacksMaxProb() + " ( " + results3.numDetectedAttacksMaxProb()*100/(double)n + "% ) \t");
		sb.append(linSep);
		
		return sb.toString();
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
