/**
 ***************************************************************************
 * Copyright (C) 2007, 													   * 
 * Davide Ariu                                        					   *
 * davide.ariu@diee.unica.it                                               *
 * 																		   *
 * Roberto Perdisci														   *		
 * roberto.perdisci@diee.unica.it										   *
 *                                                                         *
 * Distributed under the GNU Public License                                *
 * http://www.gnu.org/licenses/gpl.txt                                     *   
 *                                                                         *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 *                                                                         *
 ***************************************************************************
 */

package edu.gatech.mcpad.scripts;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

import edu.gatech.mcpad.io.Copy;
import edu.gatech.mcpad.network.*;
import edu.gatech.mcpad.network.PacketClassifier.CombinationRule;
import edu.gatech.mcpad.utils.*;
import gnu.getopt.Getopt;

public class PAD2vGram  {
	
	/*
	 * Options from the command line.
	 * */

	private static int numOfPackets = -1;
	private static double falsePositiveRate = 0;
	private static float trainRatio = 0;
	private static final String dirSep = File.separator;
	private static String filter = null;
	private static String rootDirectory = null;
	private static String pcapFile = null;
	private static String testFile = null;
	private static String splitFile = null;
	private static Boolean analyzeNetwork = false;
	private static Boolean test = false;
	private static Boolean train = false;
	private static Boolean calculateThreshold = false;
	private static Boolean splitDataset = false;
	private static Boolean xvalidation = false;
	private static Boolean noClustering = false;
	private static Boolean noDataset = false;

	private static DirectoryHandler directoryHandler;
	
	public static void main(String[] args) {

		Getopt g = new Getopt("PAD2vGram", args, "eE:CDf:F:hi:l:np:r:s:tTx");

		int c;
		while((c = g.getopt()) != -1) {
			switch(c) {

			case 'C':
				noClustering = true;
				System.out.println("-C " + noClustering);
				break;

			case 'D':
				noDataset = true;
				System.out.println("-D " + noDataset);
				break;
			
			case 'e':
				splitDataset = true;
				System.out.println("-e " + splitDataset);
				break;

			case 'E':
				splitFile = g.getOptarg();
				System.out.println("-E " + splitFile);
				break;

			case 'f':
				pcapFile = g.getOptarg();
				System.out.println("-f " + pcapFile);
				break;
			
			case 'h':
				calculateThreshold = true;
				System.out.println("-h " + calculateThreshold);
				break;
		
			case 'i':
				trainRatio = Float.parseFloat(g.getOptarg());
				System.out.println("-i" + trainRatio);
				break;
				
			case 'l':
				filter = g.getOptarg();
				System.out.println("-l " + filter);
				break;

			case 'n':
				analyzeNetwork = true;
				System.out.println("-n " + analyzeNetwork);
				break;

			case 'p':
				numOfPackets = Integer.parseInt(g.getOptarg());
				System.out.println("-p " + numOfPackets);
				break;

			case 'r':
				rootDirectory = g.getOptarg();
				System.out.println("-r " + rootDirectory);
				break;

			case 's':
				falsePositiveRate = Double.parseDouble(g.getOptarg());
				System.out.println("-s " + falsePositiveRate);
				break;	
				
			case 'T':
				test = true;
				System.out.println("-T " + test);
				break;

			case 't':
				train = true;
				System.out.println("-t " + train);
				break;

			case 'x':
				xvalidation = true;
				System.out.println("-x");
				break;

			default:
				printUsage(0);
			System.exit(1);	

			}
		}

		checkCommandLineParameters();
		
		if(splitDataset){
			splitDataset();
			System.exit(1);
		}

		directoryHandler = new DirectoryHandler(rootDirectory);
		SVMClassifierHandler svm = new SVMClassifierHandler(directoryHandler);

		if(train){

			if(pcapFile != null){
				System.out.println("TRAINING FROM\t" + pcapFile);
				trainFileCopy(pcapFile);
				System.out.println("----- TRAIN FILE COPY DONE -----");
			}

			if(noDataset == false){
				if(noClustering == false){

					System.out.println("\n----- RUN FEATURE CLUSTERING -----");
					svm.generateFeatureClusters();
					System.out.println("----- FEATURE CLUSTERING DONE-----");
				}

				System.out.println("----- GENERATE TRAIN DATASET -----");
				svm.generateTrainDatasets();
				System.out.println("----- TRAIN DATASET GENERATED -----");
			}

			System.out.println("----- TRAIN SVM MODELS -----");
			svm.generateModels();
			System.out.println("----- SVM MODELS TRAINED -----");
		}
		
		
		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVAttack = new HashMap<CombinationRule, Double>();
		Map<CombinationRule, Double> thresholdsMVNormal = new HashMap<CombinationRule, Double>();
		thresholds.put(CombinationRule.MAJ_VOTING, 0.0);
		thresholdsMVAttack.put(CombinationRule.MAJ_VOTING, 0.0);
		thresholdsMVNormal.put(CombinationRule.MAJ_VOTING, 0.0);
		
		if(calculateThreshold){
			ThresholdFactory thFactory = new ThresholdFactory(directoryHandler);
			thresholds = thFactory.getThresholds(filter, numOfPackets, falsePositiveRate);
			ThresholdFactory.printThresholds(thresholds);
			thFactory.saveThreshodls(thresholds,"thresholds.obj");
			
			thresholdsMVAttack = thFactory.getThresholdsMVAttack(filter, numOfPackets);
			ThresholdFactory.printThresholds(thresholdsMVAttack);
			thFactory.saveThreshodls(thresholdsMVAttack,"thresholdsMVAttacks.obj");
			
			thresholdsMVNormal = thFactory.getThresholdsMVNormal(filter, numOfPackets);
			ThresholdFactory.printThresholds(thresholdsMVNormal);
			thFactory.saveThreshodls(thresholdsMVNormal,"thresholdsMVNormal.obj");
		}

		if(test){
			if(testFile == null){
				System.err.println("A file for the test must be specified");
			}

			System.out.println("TESTING FROM " + pcapFile);
			ThresholdFactory thFactory = new ThresholdFactory(directoryHandler);

			System.out.println("===============================================");
			System.out.println("----- TEST SVM MODELS -----");
			PacketClassifier pc = new PacketClassifier(directoryHandler);
			System.out.println("----- LOADING THRESHOLDS thresholds.obj -----");
			thresholds = thFactory.loadThreshodls("thresholds.obj");
			ThresholdFactory.printThresholds(thresholds);
			System.out.println("----- TESTING -----");
			System.out.println(pc.test(pcapFile,filter,thresholds,numOfPackets));
			
			System.out.println("===============================================");
			System.out.println("----- TEST SVM MODELS (WITH MV-ATTACKS THRESHOLD) -----");
			pc = new PacketClassifier(directoryHandler);
			System.out.println("----- LOADING THRESHOLDS FROM thresholdsMVAttack.obj -----");
			thresholdsMVAttack = thFactory.loadThreshodls("thresholdsMVAttacks.obj");
			ThresholdFactory.printThresholds(thresholdsMVAttack);
			System.out.println("----- TESTING -----");
			System.out.println(pc.test(pcapFile,filter,thresholdsMVAttack,numOfPackets));
			
			System.out.println("===============================================");
			System.out.println("----- TEST SVM MODELS (WITH MV-NORMAL THRESHOLD) -----");
			pc = new PacketClassifier(directoryHandler);
			System.out.println("----- LOADING THRESHOLDS FROM thresholdsMVNormal.obj -----");
			thresholdsMVNormal = thFactory.loadThreshodls("thresholdsMVNormal.obj");
			ThresholdFactory.printThresholds(thresholdsMVNormal);
			System.out.println("----- TESTING -----");
			System.out.println(pc.test(pcapFile,filter,thresholdsMVNormal,numOfPackets));

			System.out.println("===============================================");
			System.out.println("Done!");
		}
	}
	
	private static void splitDataset(){
		SplitPcapTrainValidation.splitDataset(splitFile,filter,trainRatio);
	}

	private static void trainFileCopy(String trainFile){
		System.out.println("----- COPYING TRAIN FILE INTO PCAP DIRECTORY -----");
		try{
			Copy.fileCopy(trainFile, (directoryHandler.getPcapDir()+ dirSep + "training.pcap"));
		}catch(IOException e){
			System.err.println("IOException " + e);
			System.exit(1);
		}
	}

	private static void checkCommandLineParameters(){ 

		boolean exit = false;
		if(train){
			if(test || analyzeNetwork || xvalidation || calculateThreshold) {
				printUsage(1);
				exit = true;
			}
		}
		if(test) {
			if(train || analyzeNetwork || xvalidation || calculateThreshold) {
				printUsage(2);
				exit = true;
			}
		}
		if(analyzeNetwork) {
			if(train || test || xvalidation) {
				printUsage(3);
				exit = true;
			}
		}
		if(xvalidation) {
			if(train || test || analyzeNetwork || calculateThreshold) {
				printUsage(4);
				exit = true;
			}
		}
		
		if(calculateThreshold){
			if(train || test || analyzeNetwork || xvalidation){
				printUsage(4);
				exit = true;
			}
		}

		if(exit){
			System.exit(1);
		}
	}

	private static void printUsage(int operation) {

		if(operation == 0){
			printUsage(1);
			printUsage(2);
			printUsage(3);
			printUsage(4);
			printUsage(5);
			System.exit(1);
		}

		if(operation == 1){
			System.out.println("----- RUN TRAINING -----");
			System.out.println("-f 'trainFile' -l 'filter' -p 'numOfPackets' -r 'rootDirectory' -t (train)");
		}

		if(operation == 2){
			System.out.println("----- RUN TEST FROM FILE -----");
			System.out.println("-f 'testFile' -l 'filter' -p 'numOfPackets' -r 'rootDirectory' -T (test)");
		}

		if(operation == 3){
			System.out.println("----- RUN TEST FROM NETWORK INTERFACE -----");
			System.out.println("-n (analyzeNetwork) -l 'filter' -p 'numOfPackets' -r 'rootDirectory'");
		}

		if(operation == 4){
			System.out.println("----- RUN XVALIDATION -----");
			System.out.println("-x (crossValidation) -l 'filter' -p 'numOfPackets' -r 'rootDirectory'");
		}
		
		if(operation == 5){
			System.out.println("----- CALCULATE THRESHOLDS OVER VALIDATION SET -----");
			System.out.println("-h (calculateThreshold) -l 'filter' -p 'numOfPackets' -r 'rootDirectory' -s desiredFalsePositiveRate");
		}
	}

}
