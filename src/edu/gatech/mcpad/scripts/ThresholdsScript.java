package edu.gatech.mcpad.scripts;

import java.io.File;


public class ThresholdsScript {
	
	private static final String dirSep = File.separator; 

	private static int[] clusters = {10,20,40,80,160};

	public static void main(String[] args){
		String mountPoint = args[0];
		int firstDay = Integer.parseInt(args[1]);
		int lastDay = Integer.parseInt(args[2]);
		String falsePositive = args[3];
		
		File destRootDirectory;
		
		for(int day = firstDay; day <= lastDay; day++){
			for(int k = 0; k < clusters.length; k++){
					
					destRootDirectory = makeRootDirectory(mountPoint, day, clusters[k], falsePositive);
					String[] training = {"-l","","-p","-1","-r",destRootDirectory.toString(),"-h","-s", falsePositive};
					PAD2vGram.main(training);
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
