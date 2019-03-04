/**
 ***************************************************************************
 * Copyright (C) 2005, Roberto Perdisci                                    *
 * roberto.perdisci@diee.unica.it                                          *
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

package edu.gatech.mcpad.featureClustering;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import uet.nam.General;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class DataSetFactory implements PacketReceiver {

	private static final int MIN_NUM_OF_GRAMS = 1; // min number of grams in a

	// payload

	public static final int NUM_OF_CHARACTERS = 256;

	private int totalPacketCounter = 0;

	private int packetCounter = 0;

	private int payloadLength = -1;

	private int nu;

	private int maxNu; // Makes sure that all the datasets contain the same

	// number of patterns

	private double randomSamplingProbability;

	private BufferedWriter outputFile;

	private FeatureClustersMap fcMap;

	JpcapCaptor captor;
	
	private void pickRandomPacket(byte[] data)
	{
		General general = General.getInstance();
		if (general.getIsFirstNu())
		{
			
			if (Math.random() <= randomSamplingProbability) {
				packetCounter++;
				writeToDataSet(outputFile, computePairFrequency(data, nu,
						fcMap));
				general.getPick().add(PICK);
			}
			else general.getPick().add(NOT_PICK);
			//general.increaseIndexPacket();
		}
		else
		{
			if (general.getPick().get(general.getIndexPacket()))
			{
				packetCounter++;
				writeToDataSet(outputFile, computePairFrequency(data, nu,
						fcMap));
			}
			general.increaseIndexPacket();
		}
	
	}

	public void receivePacket(Packet packet) {

		try {
			totalPacketCounter++;

			IPPacket ipp = (IPPacket) packet;
			byte[] data = null;

			if (ipp.protocol == IPPacket.IPPROTO_TCP) {
				TCPPacket tcpPacket = (TCPPacket) packet;
				data = tcpPacket.data;
			} else if (ipp.protocol == IPPacket.IPPROTO_UDP) {
				UDPPacket tcpPacket = (UDPPacket) packet;
				data = tcpPacket.data;
			} else {
				System.err.println("UNKNOWN PROTOCOL!!!");
				System.exit(1);
			}

			// If payloadLength == -1 all the packets will be analyzed,
			// regardless their payloadLength
			if ((data.length == this.payloadLength || this.payloadLength == -1)
					&& (data.length - nu - 1) >= MIN_NUM_OF_GRAMS
					&& (data.length - maxNu - 1) >= MIN_NUM_OF_GRAMS) {
				//sua day
				pickRandomPacket(data);
				/*if (Math.random() <= randomSamplingProbability) {
					packetCounter++;
					writeToDataSet(outputFile, computePairFrequency(data, nu,
							fcMap));
				}*/
			}

			/* DEBUG! *
			if (totalPacketCounter % 10000 == 0)
				System.err.print(totalPacketCounter + "(" + packetCounter
						+ ") - ");
			/**/

		} catch (ClassCastException e) {
			System.err.println("CANNOT PROCESS PACKET (NOT AN *IP* PACKET)");
		} catch (Exception e) {
			System.err.println("Exception in DataSetFactory.receivePacket : "
					+ e);
		}
	}

	public static double[] computePairFrequency(byte[] data, int nu,
			FeatureClustersMap fcMap) {
		//nu = const = 10. For nu
		double[] perClusterFreq = new double[fcMap.getNumberOfClusters()];
		int[] sum = new int[fcMap.getNumberOfClusters()];

		int payloadLength = data.length;
		
		
		
		if (payloadLength > 0) {
			//for(nu = 0 ; nu < 10 ; nu++)// sua day
			{
				for (int l = 0; l < payloadLength - nu - 1; l++) {
					int i = (int) (data[l] & 0x00FF);
					int j = (int) (data[l + nu + 1] & 0x00FF);
					sum[fcMap.get(new PairIndex(i, j))]++;
				}
				for (int m = 0; m < sum.length; m++) {
					perClusterFreq[m] += (double) sum[m]
							/ (double) (payloadLength - nu - 1);
				}
			}
		}

		return perClusterFreq;
	}
	
	private void preparation()
	{
		General.getInstance().resetIndexPacket();
	}

	public void generateDataSetFromPcap(String pcapFile, String filter,
			String outputFile, int payloadLength, int nu,
			FeatureClustersMap fcMap, int maxNu, double randomSamplingProbability) {

		this.totalPacketCounter = 0;
		this.packetCounter = 0;

		this.payloadLength = payloadLength;
		this.nu = nu;
		this.fcMap = fcMap;
		this.maxNu = maxNu;
		this.randomSamplingProbability = randomSamplingProbability;
		preparation();

		try {

			this.outputFile = new BufferedWriter(new FileWriter(outputFile));
			captor = JpcapCaptor.openFile(pcapFile);
			if (filter != null)
				captor.setFilter(filter, true);
			captor.loopPacket(-1, this);
			captor.close();
			this.outputFile.close();

		} catch (IOException e) {
			System.err.println("IOException " + e);
			System.exit(1);
		}
	}

	// Write to file using the libSVM format
	private void writeToDataSet(BufferedWriter out, double[] v) {

		try {
			out.write(new String("+1\t"));
			for (int i = 0; i < v.length; i++) {
				//sua day
				
				int g_index = General.getInstance().getIndex();
				out.write(new String((g_index + i + 1) + ":" + v[i] + "\t"));
				//General.getInstance().incIndex();
			}
			out.write("\r\n");
		} catch (Exception e) {
			System.err.println("Exception in DataSetFactory.writeToDataSet: "
					+ e);
		}
	}
	private final boolean PICK = true;
	private final boolean NOT_PICK = false;
}
