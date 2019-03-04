/**
 ***************************************************************************
 * Copyright (C) 2007, Roberto Perdisci                                    *
 * roberto.perdisci@gmail.com                                              *
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

package edu.gatech.mcpad.core;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import java.io.*;

import edu.gatech.mcpad.network.ClassificationResults;
import edu.gatech.mcpad.network.PacketClassifier;
import edu.gatech.mcpad.network.ThresholdFactory;
import edu.gatech.mcpad.network.PacketClassifier.CombinationRule;
import edu.gatech.mcpad.utils.DirectoryHandler;
import gnu.getopt.Getopt;

public class TestMcPAD {

	public static void main(String[] args) {

		String rootDirectory = null;
		String pcapSource = null;
		String pcapFilesDir = null;
		String pcapFilter = null;
		String combinationRule = null;
		int numOfPackets = -1;
		boolean noThreshold = false;

		Getopt g = new Getopt(TestMcPAD.class.getSimpleName(), args,
				"r:t:d:f:c:n:h");

		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {

			case 'r':
				rootDirectory = g.getOptarg();
				System.out.println("-r " + rootDirectory);
				break;

			case 't':
				pcapSource = g.getOptarg();
				System.out.println("-t " + pcapSource);
				break;

			case 'd':
				pcapFilesDir = g.getOptarg();
				System.out.println("-d " + pcapFilesDir);
				break;
				
			case 'f':
				pcapFilter = g.getOptarg();
				System.out.println("-f " + pcapFilter);
				break;
				
			case 'c':
				combinationRule = g.getOptarg();
				System.out.println("-c " + combinationRule);
				break;

			case 'n':
				numOfPackets = Integer.parseInt(g.getOptarg());
				System.out.println("-n " + numOfPackets);
				break;
				
			case 'h':
				noThreshold = true;
				System.out.println("-h " + noThreshold);
				break;

			default:
				printUsage();
				System.exit(1);
				break;
			}
		}
		
		if(rootDirectory==null || (pcapSource==null && pcapFilesDir==null) || pcapFilter==null || combinationRule==null) {
			printUsage();
			System.exit(1);
		}
		
		if(pcapSource!=null && pcapFilesDir!=null) {
			printUsage();
			System.exit(1);
		}

		DirectoryHandler directoryHandler = new DirectoryHandler(rootDirectory);
		Map<CombinationRule, Double> thresholds = ThresholdFactory
				.loadThreshodls(directoryHandler);
		
		Map<CombinationRule, Double> th = new HashMap<CombinationRule, Double>();
		if(noThreshold) {
			th.put(CombinationRule.MAJ_VOTING,Double.MAX_VALUE);
			th.put(CombinationRule.AVG_PROB,Double.MAX_VALUE);
			th.put(CombinationRule.PROD_PROB,Double.MAX_VALUE);
			th.put(CombinationRule.MIN_PROB,Double.MAX_VALUE);
			th.put(CombinationRule.MAX_PROB,Double.MAX_VALUE);
		}
		else {
			if(combinationRule.equals("MAJ")) {
				th.put(CombinationRule.MAJ_VOTING,thresholds.get(CombinationRule.MAJ_VOTING));
			}
			else if(combinationRule.equals("AVG")) {
				th.put(CombinationRule.AVG_PROB,thresholds.get(CombinationRule.AVG_PROB));
			}
			else if(combinationRule.equals("PROD")) {
				th.put(CombinationRule.PROD_PROB,thresholds.get(CombinationRule.PROD_PROB));
			}
			else if(combinationRule.equals("MIN")) {
				th.put(CombinationRule.MIN_PROB,thresholds.get(CombinationRule.MIN_PROB));
			}
			else if(combinationRule.equals("MAX")) {
				th.put(CombinationRule.MAX_PROB,thresholds.get(CombinationRule.MAX_PROB));
			}
			else if(combinationRule.equals("ALL")) {
				th.putAll(thresholds);
			}
			else {
				System.out.println("ERROR: Unknown Combination Rule!");
				printUsage();
				System.exit(1);
			}	
		}
		
		
		ThresholdFactory.printThresholds(th);
		PacketClassifier pc = new PacketClassifier(directoryHandler);
		
		ClassificationResults res = null;
		
		if(pcapSource!=null) {
			res = pc.test(pcapSource, pcapFilter, th, numOfPackets);
			System.out.println(res);
			System.out.println(pc.getTimePerPacket());
		}
		else if(pcapFilesDir!=null) {
			int detectedMAJ = 0;
			int detectedAVG = 0;
			int detectedPROD = 0;
			int detectedMIN = 0;
			int detectedMAX = 0;
			
			File dir = new File(pcapFilesDir);
			if(!dir.isDirectory()) {
				printUsage();
				System.exit(1);
			}
			
			File[] pcaps = dir.listFiles();
			Arrays.sort(pcaps);
			
			for(int i=0; i<pcaps.length; i++) {
				System.out.println("=> pcap file : " + pcaps[i]);
				res = pc.test(pcaps[i].getAbsolutePath(), pcapFilter, th, numOfPackets);
				System.out.println(res);
				
				if(res.numDetectedAttacksMajVoting()>0)
					detectedMAJ++;
				if(res.numDetectedAttacksAvgProb()>0)
					detectedAVG++;
				if(res.numDetectedAttacksProdProb()>0)
					detectedPROD++;
				if(res.numDetectedAttacksMinProb()>0)
					detectedMIN++;
				if(res.numDetectedAttacksMaxProb()>0)
					detectedMAX++;
			}
			
			System.out.println("MAJ  Detected Attacks = " + detectedMAJ + "/" + pcaps.length);
			System.out.println("AVG  Detected Attacks = " + detectedAVG + "/" + pcaps.length);
			System.out.println("PROD Detected Attacks = " + detectedPROD + "/" + pcaps.length);
			System.out.println("MIN  Detected Attacks = " + detectedMIN + "/" + pcaps.length);
			System.out.println("MAX  Detected Attacks = " + detectedMAX + "/" + pcaps.length);
		}
	}

	private static void printUsage() {
		System.out
				.println(TestMcPAD.class.getSimpleName()
						+ " -r root_dir -t test_source -f pcap_filter -n num_of_packets");
		System.out
				.println("test_source may be either a pcap file or a NIC interface.");
		System.out
				.println("e.g., use -t NIC_eth0 -f \"dst port 80\" to monitor traffic towards port 80 on eth0");
		System.out.println();
	}

}
