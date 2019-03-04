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

import java.io.Serializable;

public class PairIndex implements Serializable {

	static final long serialVersionUID = 4246415083109L;
	
	private int row;
	private int column;
	
	public PairIndex(int r, int c) {
		this.row = r;
		this.column = c;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}	
	
	public boolean equals(Object o) {
		PairIndex p = (PairIndex)o;
		
		if((this.getRow()==p.getRow()) && (this.getColumn()==p.getColumn())) {
			return true;
		}
		return false;
	}
	
	public int hashCode() {
		return row+column;
	}
}
