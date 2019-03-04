/**
 ***************************************************************************
 * Copyright (C) 2007, Davide Ariu                                         *
 * davide.ariu@diee.unica.it                                               *
 *                                                                         *
 * Distributed under the GNU Public License                                *
 * http://www.gnu.org/licenses/gpl.txt                                     *   
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 *                                                                         *             
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 ***************************************************************************
 */

package edu.gatech.mcpad.utils;

import edu.gatech.mcpad.io.ConfigFileReader;

import java.io.*;

/**
 * This class contains the parameters read from the configuration file used
 * for the clustering procedure. This file is located into the directory
 * "RootDirectory/clustering/clustering.conf". For the double variables you
 * have to use the dotted notation (0.9 is expressed as .9). The 
 * OutlierPriorProbabiliy is calculated starting from the 
 * TargetPriorProbability, as 1 - TargetPriorProbability. The 
 * RandomClusterInitialization variable establish wheter the clustering 
 * procedure is randomly initialized or not. The values of nu may be 
 * specified like an interval of values (e.g. 1-9) or explicitly expressed as
 * comma separated values (e.g. 1,4,7). Mixed notation, like 1-5,7,10 is 
 * also accepted. 
 */
public final class ClusteringConfiguration {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public double targetPriorProbability;	
	public double outlierPriorProbability;
	public double acceptedInformationLoss;
	public boolean randomClusterInitialization;
	public int numOfClusters;
	public int[] nu;
	
	public int maxNumOfTrainingPackets;
	//public double randomSamplingRatio;
	public int payloadLength;
	public String filter;
	
	public int maxNu() {
		int maxNu = nu[0];
		for(int i=1; i<nu.length; i++) {
			if(nu[i]>maxNu)
				maxNu=nu[i];
		}
		
		return maxNu;
	}
	
	public ClusteringConfiguration(String cfgFileName) {
		
		try {
			ConfigFileReader cfr = new ConfigFileReader(cfgFileName);
			acceptedInformationLoss = cfr.readDouble("ACCEPTED_INFORMATION_LOSS");
			maxNumOfTrainingPackets = cfr.readInt("MAX_NUM_OF_TRAINING_PACKETS");
			payloadLength = cfr.readInt("PAYLOAD_LENGTH");
			randomClusterInitialization = cfr.readBoolean("RANDOM_CLUSTER_INITIALIZATION");
			//randomSamplingRatio = cfr.readDouble("RANDOM_SAMPLING_RATIO");
			targetPriorProbability = cfr.readDouble("TARGET_PRIOR_PROBABILITY");
			outlierPriorProbability = 1-cfr.readDouble("TARGET_PRIOR_PROBABILITY");
			numOfClusters = cfr.readInt("CLUSTERS");
			nu = cfr.readIntVector("NU");
			filter = cfr.readString("FILTER");
		}
		catch(Exception e) {
			System.err.println("EXCEPTION in ClusteringConfiguration::loadClustConfig -> " + e);
			System.exit(1);
		}
		
	}
	
	public void save(String cfgFileName) {
		try {
 			BufferedWriter w = new BufferedWriter(new FileWriter(cfgFileName));

			w.write("NU = " + nu[0] + "-" + nu[nu.length-1]);
			w.write(LINE_SEPARATOR);
			w.write("ACCEPTED_INFORMATION_LOSS = " + acceptedInformationLoss);
			w.write(LINE_SEPARATOR);
			w.write("MAX_NUM_OF_TRAINING_PACKETS = " + maxNumOfTrainingPackets);
			w.write(LINE_SEPARATOR);
			w.write("PAYLOAD_LENGTH = " + payloadLength);
			w.write(LINE_SEPARATOR);
			w.write("RANDOM_CLUSTER_INITIALIZATION = " + randomClusterInitialization);
			w.write(LINE_SEPARATOR);
			//w.write("RANDOM_SAMPLING_RATIO = " + randomSamplingRatio);
			//w.write(LINE_SEPARATOR);
			w.write("TARGET_PRIOR_PROBABILITY = " + targetPriorProbability);
			w.write(LINE_SEPARATOR);
			w.write("CLUSTERS = " + numOfClusters);
			w.write(LINE_SEPARATOR);
			w.write("FILTER = " + filter);
			w.write(LINE_SEPARATOR);
			
			w.close();
			
		}
		catch(Exception e) {
			System.err.println("EXCEPTION in ClusteringConfiguration::save -> " + e);
			System.exit(1);
		}
	}
	
	public void print() {
		
		System.out.println("ACCEPTED_INFORMATION_LOSS = " + acceptedInformationLoss);
		System.out.println("MAX_NUM_OF_TRAINING_PACKETS = " + maxNumOfTrainingPackets);
		System.out.println("PAYLOAD LENGTH = " + payloadLength);
		System.out.println("RANDOM CLUSTER INITIALIZATION = " + randomClusterInitialization);
		//System.out.println("RANDOM SAMPLING RATIO = " + randomSamplingRatio);
		System.out.println("OUTLIER PRIOR PROBABILITY = " + outlierPriorProbability);
		System.out.println("TARGET PRIOR PROBABILITY = " + targetPriorProbability);
		System.out.println("CLUSTERS = " + numOfClusters);
		System.out.println("FILTER = " + filter);

	}
	
}
