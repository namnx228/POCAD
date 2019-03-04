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

package edu.gatech.mcpad.utils;

import edu.gatech.mcpad.network.*;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;
import jpcap.PacketReceiver;

public class ComputeByteDistribution implements PacketReceiver {
	
	private long[] distTable = new long[256];
	private double samplingRatio = 1;
	
	public ComputeByteDistribution(double samplingRatio) {
		this.samplingRatio = samplingRatio;
	}
	
	public void receivePacket(Packet p) {
		if(Math.random()<samplingRatio)
			updateDistTable(PacketClassifier.extractPayload(p));
	}
	
	private void updateDistTable(byte[] payload) {
		for(int i=0; i<payload.length; i++)
			distTable[(int)(payload[i] & 0x00FF)]++;
	}
	
	private void printDistTable() {
		for(int i=0; i<distTable.length; i++)
			System.out.println(Integer.toHexString(i).toLowerCase()
					+ " " + distTable[i]);
	}
	
	public static void main(String[] args) throws Exception {
		
		if(args.length<3) {
			System.err.println("Usage: " + ComputeByteDistribution.class.getSimpleName() +
					"  pcapFile  pcapFilter  numOfPackets");
			System.exit(1);
		}
		
		String pcapFile = args[0];
		String pcapFilter = args[1];
		int numOfPackets = Integer.parseInt(args[2]);
		
		PcapPacketCounter pcount = new PcapPacketCounter();
		long totalPcapFilePackets = pcount.countPackets(pcapFile,pcapFilter);
		
		double samplingRatio = 1;
		if(numOfPackets > 0) {
			samplingRatio = numOfPackets/(double)totalPcapFilePackets;
			numOfPackets = -1;
		}
		
		JpcapCaptor captor = JpcapCaptor.openFile(pcapFile);
		captor.setFilter(pcapFilter, true);
		ComputeByteDistribution cbd = new ComputeByteDistribution(samplingRatio);
		captor.loopPacket(-1, cbd);
		captor.close();
		
		cbd.printDistTable();
	}

}
