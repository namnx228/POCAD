/**
 ***************************************************************************
 * Copyright (C) 2007, Roberto Perdisci, Davide Arium                      *
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

package edu.gatech.mcpad.utils;

import java.io.*;
import java.util.*;

import jpcap.*;
import jpcap.packet.*;

/**
 * Splits a pcap file into 2 parts
 *
 */
public class SplitPcapTrainValidation implements PacketReceiver {

	private static final String TRAIN_FILE = "training.pcap";
	private static final String VALIDATION_FILE = "validation.pcap";

	private float trainRatio = 1;
	private JpcapWriter trainPcap = null;
	private JpcapWriter validationPcap = null;

	private Vector<Integer> trainIndex = new Vector<Integer>();
	private int totalPackets = 0;


	public SplitPcapTrainValidation(float trainRatio) {
		this.trainRatio = trainRatio;
	}

	public void receivePacket(Packet p) {

		totalPackets++;

		if(trainPcap!=null) { // writes the training packets
			if(Math.random()<trainRatio){ 
				trainPcap.writePacket(p);
				trainIndex.add(totalPackets);
			}
		}
		else if(validationPcap!=null) { // writes the validation packets
			if(!trainIndex.contains(totalPackets))
				validationPcap.writePacket(p);
		}
	}

	public void setTrainPcap(JpcapWriter out) {
		this.trainPcap = out;
		this.validationPcap = null;
	}

	public void setValidationPcap(JpcapWriter out) {
		this.trainPcap = null;
		this.validationPcap = out;
	}

	public void resetTotalPackets() {
		this.totalPackets = 0;
	}

	public static void splitDataset(String pcapFile, String pcapFilter, float trainRatio) {

		String outPath = (new File(pcapFile)).getParent();

		System.out.println("pcap input file = " + pcapFile);
		System.out.println("pcap filter = " + pcapFilter);
		System.out.println("training ratio = " + trainRatio);
		System.out.println("traininig file = " + outPath+File.separator+TRAIN_FILE);
		System.out.println("validation file = " + outPath+File.separator+VALIDATION_FILE);

		try {
			//	Initialize training packets index
			//	Write training packets
			
			JpcapCaptor captor = JpcapCaptor.openFile(pcapFile);
			captor.setFilter(pcapFilter, true);
			SplitPcapTrainValidation split = new SplitPcapTrainValidation(trainRatio);
			JpcapWriter tw = JpcapWriter.openDumpFile(captor,outPath+File.separator+TRAIN_FILE);
			split.setTrainPcap(tw);
			split.resetTotalPackets();
			captor.loopPacket(-1, split);
			captor.close();
			
			// Write validation packets
			captor = JpcapCaptor.openFile(pcapFile);
			captor.setFilter(pcapFilter, true);
			JpcapWriter vw = JpcapWriter.openDumpFile(captor,outPath+File.separator+VALIDATION_FILE);
			split.setValidationPcap(vw);
			split.resetTotalPackets();
			captor.loopPacket(-1, split);
			captor.close();

		} catch(IOException e){
			System.err.println("Pcap IOException " + e);
			System.exit(1);

		}	
	}
}
