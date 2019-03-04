/**
 ***************************************************************************
 * Copyright (C) 2007, Roberto Perdisci, Davide Ariu                       *
 * roberto.perdisci@gmail.com, davide.ariu@diee.unica.it                   *
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

package edu.gatech.mcpad.network;

import java.io.*;
import java.util.*;

import jpcap.packet.Packet;
import edu.gatech.mcpad.utils.ClusteringConfiguration;
import edu.gatech.mcpad.utils.DirectoryHandler;

public class ThresholdFactory extends PacketClassifier {

	private static final String DIR_SEPARATOR = File.separator;

	public static final double EPSILON = Double.MIN_VALUE;

	private TestPacketResultsVectors vectors = new TestPacketResultsVectors();

	public ThresholdFactory(DirectoryHandler directoryHandler) {
		super(directoryHandler);
	}

	public void receivePacket(Packet packet) {

		try {
			totalPacketCounter++;

			byte[] payload = extractPayload(packet);

			if (payload != null && payload.length > 0 && Math.random()<packetSamplingRatio) {
				testedPackets++;
				addTestPacketResults(testPacket(packet, totalPacketCounter));
			}
			
			/* DEBUG *
			if (totalPacketCounter % 1000 == 0)
				System.err.print("--->" + totalPacketCounter + "("
						+ testedPackets + ") - ");
			/**/

		} catch (Exception e) {
			System.out.println("Exception in ThresholdFactory.receivePacket : "
					+ e);
		}
	}

	private void addTestPacketResults(TestPacketResults res) {
		if (res != null) {
			vectors.decisions.add(res.decisions);
			vectors.probabilityAvg.add(res.probabilityAvg);
			vectors.probabilityProd.add(res.probabilityProd);
			vectors.probabilityMin.add(res.probabilityMin);
			vectors.probabilityMax.add(res.probabilityMax);
		}
	}

	public Map<CombinationRule, Double> getThresholds(int numOfPackets, 
			double desiredFractionOfOutliers) {

		ClusteringConfiguration clustConfig = new ClusteringConfiguration(
				directoryHandler.getClusterConfigFile().getAbsolutePath());
		return getThresholds(clustConfig.filter,numOfPackets,desiredFractionOfOutliers);

	}
	
	public Map<CombinationRule, Double> getThresholds(String filter,
			int numOfPackets, double desiredFractionOfOutliers) {

		ClassificationResults res = super.test(directoryHandler
				.getValidationNormalFile().getAbsolutePath(), filter, null,
				numOfPackets);
		return computeThresholds(res, vectors, desiredFractionOfOutliers);

	}

	/**
	 * Computes the thresholds with respect to the retults of the pure Majority
	 * on the Attacks Voting
	 * 
	 * @param filter
	 * @param numOfPackets
	 * @return
	 */
	public Map<CombinationRule, Double> getThresholdsMVAttack(String filter,
			int numOfPackets) {

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		thresholds.put(CombinationRule.MAJ_VOTING, 0.0);
		ClassificationResults res = super.test(directoryHandler
				.getValidationAttackFile().getAbsolutePath(), filter,
				thresholds, numOfPackets);

		return computeThresholdsMV(res, vectors);

	}

	/**
	 * Computes the thresholds with respect to the retults of the pure Majority
	 * on the normal traffic Voting
	 * 
	 * @param filter
	 * @param numOfPackets
	 * @return
	 */
	public Map<CombinationRule, Double> getThresholdsMVNormal(String filter,
			int numOfPackets) {

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		thresholds.put(CombinationRule.MAJ_VOTING, 0.0);
		ClassificationResults res = super.test(directoryHandler
				.getValidationNormalFile().getAbsolutePath(), filter,
				thresholds, numOfPackets);

		return computeThresholdsMV(res, vectors);

	}

	private Map<CombinationRule, Double> computeThresholds(
			ClassificationResults res, TestPacketResultsVectors v,
			double desiredFractionOfOutliers) {

		int numOfOutliers = (int) Math.round(res.numOfAnalyzedPackets()
				* desiredFractionOfOutliers);
		System.out.println("Num outler = " + String.valueOf(numOfOutliers));
		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		thresholds.put(CombinationRule.MAJ_VOTING, threshold(v.decisions,
				numOfOutliers));
		thresholds.put(CombinationRule.AVG_PROB, threshold(v.probabilityAvg,
				numOfOutliers));
		thresholds.put(CombinationRule.PROD_PROB, threshold(v.probabilityProd,
				numOfOutliers));
		thresholds.put(CombinationRule.MIN_PROB, threshold(v.probabilityMin,
				numOfOutliers));
		thresholds.put(CombinationRule.MAX_PROB, threshold(v.probabilityMax,
				numOfOutliers));

		return thresholds;
	}

	/**
	 * Computes the thresholds w.r.t. the results of the Majority Voting
	 * 
	 * @param res
	 * @param v
	 * @return
	 */
	private Map<CombinationRule, Double> computeThresholdsMV(
			ClassificationResults res, TestPacketResultsVectors v) {

		Map<CombinationRule, Double> thresholds = new HashMap<CombinationRule, Double>();
		thresholds.put(CombinationRule.MAJ_VOTING, 0.0);
		thresholds.put(CombinationRule.AVG_PROB, threshold(v.probabilityAvg,
				res.numDetectedAttacksMajVoting()));
		thresholds.put(CombinationRule.PROD_PROB, threshold(v.probabilityProd,
				res.numDetectedAttacksMajVoting()));
		thresholds.put(CombinationRule.MIN_PROB, threshold(v.probabilityMin,
				res.numDetectedAttacksMajVoting()));
		thresholds.put(CombinationRule.MAX_PROB, threshold(v.probabilityMax,
				res.numDetectedAttacksMajVoting()));

		return thresholds;
	}

	private double threshold(Vector<Double> v, int numOfOutliers) {
		Collections.sort(v);
		if (numOfOutliers < v.size())
			return v.elementAt(numOfOutliers);
		else
			return v.elementAt(v.size() - 1) + EPSILON;
	}

	public static void printThresholds(Map<CombinationRule, Double> map) {

		System.out.println();
		System.out.println("THRESHOLDS ---");
		Iterator<CombinationRule> i = map.keySet().iterator();
		while (i.hasNext()) {
			CombinationRule cr = i.next();
			System.out.println(cr + " = " + map.get(cr));
		}
		System.out.println();

	}

	public Map<CombinationRule, Double> loadThreshodls(String fileName) {
		return loadThreshodls(directoryHandler,fileName);
	}
	
	public static Map<CombinationRule, Double> loadThreshodls(DirectoryHandler dirHandler) {
		return loadThreshodls(dirHandler,dirHandler.getThresholdsFile().getName());
	}
	
	public static Map<CombinationRule, Double> loadThreshodls(DirectoryHandler dirHandler, String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					dirHandler.getThresholdsDir() + DIR_SEPARATOR
							+ fileName));
			Map<CombinationRule, Double> map = (Map<CombinationRule, Double>) in
					.readObject();
			in.close();

			return map;

		} catch (Exception e) {
			System.err.println(e);
			// System.exit(1);
		}

		return null;
	}

	public void saveThreshodls(Map<CombinationRule, Double> map, String fileName) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(directoryHandler.getThresholdsDir()
							+ DIR_SEPARATOR + fileName));
			out.writeObject(map);
			out.close();
		} catch (Exception e) {
			System.err.println(e);
			// System.exit(1);
		}
	}
}
