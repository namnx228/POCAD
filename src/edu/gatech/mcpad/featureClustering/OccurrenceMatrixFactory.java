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

import java.io.IOException;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class OccurrenceMatrixFactory implements PacketReceiver {

	private static final int MIN_NUM_OF_GRAMS = 1; // min number of grams in a

	// payload

	public static final int NUM_OF_CHARACTERS = 256; // Number of different

	// values for a byte

	private int totalPacketCounter = 0;

	private int packetCounter = 0;

	private int payloadLength;

	private int nu;

	private double randomSamplingProbability; // The probability of a packet

	// to be analyzed

	private double[][] occurrenceSum;

	private JpcapCaptor captor;

	/**
	 * @param payloadLength
	 *            Only the packets having the given length will be considered.
	 *            <br>
	 *            If payloadLength == 0 all the packets will be analyzed,
	 *            regardless their payloadLength.
	 * 
	 * @param nu
	 *            the gap in the 2-gram window (see the computeSumModel()
	 *            function below)
	 * 
	 */

	public void receivePacket(Packet packet) {

		try {
			totalPacketCounter++;

			IPPacket ipp = (IPPacket) packet;
			byte[] payload = null;

			if (ipp.protocol == IPPacket.IPPROTO_TCP) {
				TCPPacket tcpPacket = (TCPPacket) packet;
				payload = tcpPacket.data;
			} else if (ipp.protocol == IPPacket.IPPROTO_UDP) {
				UDPPacket udpPacket = (UDPPacket) packet;
				payload = udpPacket.data;
			} else {
				System.err
						.println("CANNOT PROCESS PACKET (PROTOCOL !in {TCP,UDP})");
				// System.exit(1);
			}

			// If payloadLength == -1 all the packets will be analyzed,
			// regardless their payloadLength
			if (payload != null
					&& (payload.length == this.payloadLength || this.payloadLength == -1)
					&& (payload.length - nu - 1) >= MIN_NUM_OF_GRAMS) {
				if (Math.random() <= randomSamplingProbability) {
					packetCounter++;
					computeSumModel(payload, nu);
				}
			}

			/* DEBUG! *
			if (totalPacketCounter % 10000 == 0)
				System.err.print(totalPacketCounter + "(" + packetCounter
						+ ") - ");
			/**/

		} catch (ClassCastException e) {
			System.err.println("CANNOT PROCESS PACKET (NOT AN *IP* PACKET)");
		} catch (Exception e) {
			System.err
					.println("Exception in OccurrenceMatrixFactory.receivePacket : "
							+ e);
		}
	}

	// Computes the occurrence of the byte-values in a dataset
	private void computeSumModel(byte[] data, int nu) {

		int i = 0;
		if (data.length > 0) {
			while (i < data.length - nu - 1) {
				occurrenceSum[(int) (data[i] & 0x00FF)][(int) (data[i + nu + 1] & 0x00FF)]++;
				i++;
			}
		}
	}

	public int getNumOfAnalyzedPackets() {
		return packetCounter;
	}

	public OccurrenceMatrix getOccurrenceMatrix() {
		return new OccurrenceMatrix(occurrenceSum);
	}

	/**
	 * 
	 * @param pcapFile
	 *            the training dataset
	 * @param pcapFilter
	 * @param maxNumOfPackets
	 *            max number of packets to be considered in computing the
	 *            occurrence matrix
	 * @param randomSamplingProbability
	 *            (between 0 and 1 )
	 * @return
	 */
	public OccurrenceMatrix generateOccurrenceMatrix(String pcapFile,
			String pcapFilter, int payloadLength, int nu,
			double randomSamplingProbability) {

		this.totalPacketCounter = 0;
		this.packetCounter = 0;
		this.payloadLength = payloadLength;
		this.nu = nu;
		this.occurrenceSum = new double[NUM_OF_CHARACTERS][NUM_OF_CHARACTERS];
		this.randomSamplingProbability = randomSamplingProbability;

		try {
			// Initialize jpcap
			captor = JpcapCaptor.openFile(pcapFile);
			if (pcapFilter != null)
				captor.setFilter(pcapFilter, true);
			captor.loopPacket(-1, this);
			captor.close();

			return this.getOccurrenceMatrix();

		} catch (IOException e) {
			System.err.println("IOException " + e);
			System.exit(1);

		}

		return null;
	}
}
