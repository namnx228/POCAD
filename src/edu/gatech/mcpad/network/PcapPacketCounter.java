/**
 ***************************************************************************
 * Copyright (C) 2007, Roberto Perdisci                                    *
 * roberto.perdisci@google.com                                             *
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

import java.io.IOException;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

public class PcapPacketCounter implements PacketReceiver {

	private int totalPacketCounter = 0;

	private JpcapCaptor captor;

	public void receivePacket(Packet packet) {

		try {
			totalPacketCounter++;
		} catch (Exception e) {
			System.out
					.println("Exception in PcapPacketCounter.receivePacket : "
							+ e);
		}

	}

	public long countPackets(String pcapFile, String pcapFilter) {

		try {
			// Initialize jpcap
			captor = JpcapCaptor.openFile(pcapFile);
			captor.setFilter(pcapFilter, true);
			captor.loopPacket(-1, this);
			captor.close();

		} catch (IOException e) {
			System.err.println("Pcap IOException " + e);
			System.exit(1);

		}

		return totalPacketCounter;
	}
}
