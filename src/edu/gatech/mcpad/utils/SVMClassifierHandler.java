/**
 ***************************************************************************
 * Copyright (C) 2007, Davide Ariu, Roberto Perdisci                       *
 * davide.ariu@diee.unica.it, roberto.perdisci@gmail.com                   *
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

import java.util.Arrays;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import edu.gatech.mcpad.featureClustering.*;
import edu.gatech.mcpad.network.PcapPacketCounter;
import edu.gatech.mcpad.svm.*;
import uet.nam.General;

public class SVMClassifierHandler {

	private static final String DIR_SEPARATOR = File.separator;

	private svm_parameter param;

	private svm_problem prob;

	private svm_model model;

	private String error_msg;

	private int nrFold;

	private DirectoryHandler directoryHandler;

	public SVMClassifierHandler(DirectoryHandler directoryHandler) {
		this.directoryHandler = directoryHandler;
		param = new svm_parameter(directoryHandler.getSVMConfigFile()
				.getAbsolutePath());
	}
	
	private void notFirstAnyMore()
	{
		General.getInstance().notFirstAnyMore();
	}

	public void generateTrainDatasets() {

		ClusteringConfiguration clustConfig = new ClusteringConfiguration(
				directoryHandler.getClusterConfigFile().getAbsolutePath());
		
		try {

			File trainingSetDir = directoryHandler.getTrainingsetDirectory();
			String inputPcapFile = directoryHandler.getTrainingFile().getAbsolutePath();
			String filter = clustConfig.filter;
			int maxNumOfPackets = clustConfig.maxNumOfTrainingPackets;
			
			double packetSamplingRatio = computeSamplingRatio(inputPcapFile,filter,maxNumOfPackets);
			
			System.out.println("> Generating training dataset...");
			System.out.println("> Sampling ratio for Normal traffic = "
					+ packetSamplingRatio);
			
			DataSetFactory dsf = new DataSetFactory();

			File[] mapsFileList = directoryHandler.getFcMapsDir().listFiles();
			Arrays.sort(mapsFileList);

			for (int h = 0; h < mapsFileList.length; h++) {

				String fcMapFile = mapsFileList[h].getName();
				System.out.println();
				System.out.println("> Reading feature map from " + fcMapFile);
				System.out.println();

				ObjectInputStream os = new ObjectInputStream(
						new FileInputStream(mapsFileList[h]));
				
				@SuppressWarnings("unused")
				String trainingTrafficFile = ((String) os.readObject());
				
				int nu = ((Integer) os.readObject()).intValue();
				FeatureClustersMap fcm = (FeatureClustersMap) os.readObject();
				os.close();

				String outputFile = trainingSetDir.getAbsolutePath()
						+ DIR_SEPARATOR + nu + ".data";
				System.out.println();
				System.out
						.println(" > Creating libSVM dataset from training traffic : "
								+ outputFile);
				System.out.println();
				dsf.generateDataSetFromPcap(inputPcapFile, clustConfig.filter,
						outputFile, -1, nu, fcm, clustConfig.maxNu(),packetSamplingRatio);
				notFirstAnyMore();
				General.getInstance().incIndex();

			}

			System.out.println("> Finished! ");

		} catch (Exception e) {
			System.out.println("ERROR: " + e);
			System.exit(1);
		}

	}

	public void generateFeatureClusters() {

		String trainingTrafficFile = (directoryHandler.getTrainingFile()
				.getAbsolutePath());
		ClusteringConfiguration clustConfig = new ClusteringConfiguration(
				directoryHandler.getClusterConfigFile().getAbsolutePath());
		
		String filter = clustConfig.filter;
		int maxNumOfPackets = clustConfig.maxNumOfTrainingPackets;
		
		double packetSamplingRatio = computeSamplingRatio(trainingTrafficFile,filter,maxNumOfPackets);

		System.out.println("> Computing feature clusters...");
		System.out.println("> Num of feature clusters = "
				+ clustConfig.numOfClusters);

		OccurrenceMatrixFactory omf = new OccurrenceMatrixFactory();

		System.out.println();
		System.out.println();
		try {
			File mapsDir = directoryHandler.getFcMapsDir();

			int[] nu = clustConfig.nu;
			for (int i = 0; i < nu.length; i++) {
				System.out.println("> nu[" + i + "] = " + nu[i]);

				OccurrenceMatrix om = omf.generateOccurrenceMatrix(
						trainingTrafficFile, clustConfig.filter,
						clustConfig.payloadLength, nu[i],
						packetSamplingRatio);

				FeatureProbabilityDistributions fdp = new FeatureProbabilityDistributions(
						om, clustConfig.targetPriorProbability,
						clustConfig.outlierPriorProbability);

				FeatureClustersMapFactory fcmf = new FeatureClustersMapFactory(
						fdp, clustConfig.numOfClusters,
						clustConfig.acceptedInformationLoss,
						clustConfig.randomClusterInitialization);

				FeatureClustersMap fcm = fcmf.getFeatureClusterMap();
				//print to file.map
				FileOutputStream out = new FileOutputStream(mapsDir
						.getAbsolutePath()
						+ DIR_SEPARATOR
						+ "2-"
						+ nu[i]
						+ "-gram-featureClusters.map");
				ObjectOutputStream os = new ObjectOutputStream(out);
				os.writeObject(trainingTrafficFile);
				os.writeObject(nu[i]);
				os.writeObject(fcm);
				os.writeObject(fdp);
				os.flush();
				os.close();

			}
		} catch (Exception e) {
			System.out.println("Exception :: " + e);
			System.exit(1);
		}

		System.out.println("> Finished!");

	}

	/*
	 * Step3:: generateSVM calls the method train
	 */

	public void generateModels() {

		System.out.println("> Computing one-class SVM models...");

		try {

			File trainingsetDir = directoryHandler.getTrainingsetDir();
			File[] trainingsetFilesName = trainingsetDir.listFiles();
			Arrays.sort(trainingsetFilesName);
			//sua day
			for (int i = 0; i < trainingsetFilesName.length; i++) {
			//for (int i = 0; i < 1; i++) {
				String trainFileName = trainingsetFilesName[i].getName()
						.toString();
				StringTokenizer strk = new StringTokenizer(trainFileName, ".");
				int nu = Integer.parseInt(strk.nextToken());
				String svmFileName = nu + ".svm";
				train(svmFileName, trainFileName);
				System.gc();
			}

		} catch (Exception e) {
			System.out.println("Exception :: " + e);
			System.exit(1);
		}

		System.out.println("> Finished!");
	}
	
	private double computeSamplingRatio(String inputPcapFile, String filter, int maxNumOfPackets) {
		// computes the sampling ration on the training dataset
		int maxPackets = maxNumOfPackets;
		PcapPacketCounter pcount = new PcapPacketCounter();
		long totalPcapFilePackets = pcount.countPackets(inputPcapFile,filter);
		double packetSamplingRatio = maxPackets/(double)totalPcapFilePackets;
		// sua day
		return packetSamplingRatio;
		//return ONE_HUNDRED_PERCENT;
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	public void printSVMConfig() {
		(new svm_parameter(directoryHandler.getSVMConfigFile()
				.getAbsolutePath())).print();
	}

	public void printFeatureClusteringConfig() {
		(new ClusteringConfiguration(directoryHandler.getClusterConfigFile()
				.getAbsolutePath())).print();
	}

	/*
	 * ============================================================ 
	 * The code below has been imported from the libSVM code by 
	 * Chih-Chung Chang and Chih-Jen Lin
	 * (http://www.csie.ntu.edu.tw/~cjlin/libsvm/)
	 * and is therefore covered by the licence of libSVM.
	 * A few changes may have been applied to integrated the 
	 * imported code with the rest of the software.
	 * ============================================================
	 */

	/**
	 * first calls the readProblem method. After this, it call the method
	 * svm_check_parameter, svm_train and svm_save_model provided by the class
	 * svm of libSVM
	 */
	private void train(String svmFileName, String trainFileName)
			throws IOException {
		readProblem(trainFileName);
		error_msg = svm.svm_check_parameter(prob, param);

		if (error_msg != null) {
			System.err.print("Error: " + error_msg + "\n");
			System.exit(1);
		}

		else {
			svmFileName = directoryHandler.getModelsDir() + DIR_SEPARATOR
					+ svmFileName;
			model = svm.svm_train(prob, param);
			svm.svm_save_model(svmFileName, model);
		}
	}

	/**
	 * readS the trainingSet files and set the read problem variable that will
	 * be used from the train method.
	 */

	private void readProblem(String trainFileName) throws IOException {
		BufferedReader fp = new BufferedReader(new FileReader(directoryHandler
				.getTrainingsetDir()
				+ DIR_SEPARATOR + trainFileName));
		Vector vy = new Vector();
		Vector vx = new Vector();
		int max_index = 0;
		while (true) {
			String line = fp.readLine();
			if (line == null)
				break;

			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			vy.addElement(st.nextToken());
			int m = st.countTokens() / 2;
			svm_node[] x = new svm_node[m];
			for (int j = 0; j < m; j++) {
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			if (m > 0)
				max_index = Math.max(max_index, x[m - 1].index);
			vx.addElement(x);

		}

		prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for (int i = 0; i < prob.l; i++)
			prob.x[i] = (svm_node[]) vx.elementAt(i);
		prob.y = new double[prob.l];
		for (int i = 0; i < prob.l; i++)
			prob.y[i] = atof((String) vy.elementAt(i));

		if (param.gamma == 0)
			param.gamma = 1.0 / max_index;

		fp.close();
	}

	public void doCrossValidation() throws IOException {

		File[] trainingFilesList = directoryHandler.getTrainingsetDir()
				.listFiles();
		Arrays.sort(trainingFilesList);

		for (int j = 0; j < trainingFilesList.length; j++) {

			readProblem(trainingFilesList[j].toString());
			error_msg = svm.svm_check_parameter(prob, param);

			if (error_msg != null) {
				System.err.print("Error: " + error_msg + "\n");
				System.exit(1);
			}

			int i;
			int total_correct = 0;
			double total_error = 0;
			double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
			double[] target = new double[prob.l];

			svm.svm_cross_validation(prob, param, nrFold, target);
			if (param.svm_type == svm_parameter.EPSILON_SVR
					|| param.svm_type == svm_parameter.NU_SVR) {
				for (i = 0; i < prob.l; i++) {
					double y = prob.y[i];
					double v = target[i];
					total_error += (v - y) * (v - y);
					sumv += v;
					sumy += y;
					sumvv += v * v;
					sumyy += y * y;
					sumvy += v * y;
				}
				System.out.print("Cross Validation Mean squared error = "
						+ total_error / prob.l + "\n");
				System.out
						.print("Cross Validation Squared correlation coefficient = "
								+ ((prob.l * sumvy - sumv * sumy) * (prob.l
										* sumvy - sumv * sumy))
								/ ((prob.l * sumvv - sumv * sumv) * (prob.l
										* sumyy - sumy * sumy)) + "\n");
			} else
				for (i = 0; i < prob.l; i++)
					if (target[i] == prob.y[i])
						++total_correct;
			System.out.print("Cross Validation Accuracy = " + 100.0
					* total_correct / prob.l + "%\n");
		}
	}
	private final int ONE_HUNDRED_PERCENT = 1;
}
