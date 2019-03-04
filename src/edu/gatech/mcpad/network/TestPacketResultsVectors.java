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

import java.util.Vector;

public class TestPacketResultsVectors {
	public Vector<Double> decisions = new Vector<Double>();
	public Vector<Double>  probabilityAvg = new Vector<Double>();
	public Vector<Double>  probabilityProd = new Vector<Double>();
	public Vector<Double>  probabilityMin = new Vector<Double>();
	public Vector<Double>  probabilityMax = new Vector<Double>();
}
