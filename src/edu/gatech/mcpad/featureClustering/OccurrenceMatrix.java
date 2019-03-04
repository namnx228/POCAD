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

public class OccurrenceMatrix {
	
	private double[][] matrix = null;
	private int rows;
	private int columns;
	private double sum;
	
	OccurrenceMatrix(double[][] m) {
		matrix = m;
		rows = m.length;
		columns = m[0].length;
		sum = computeSum(matrix);
	}
	
	public double get(int i, int j) {
		return matrix[i][j];
	}
	
	public double getSum() {
		return sum;
	}
	
	public int getNumOfRows() {
		return rows;
	}
	
	public int getNumOfColumns() {
		return columns;
	}	
	
	public int getNumOfElements() {
		return rows*columns;
	}	
	
	private double computeSum(double[][] m) {
		
		double sum = 0;
		int r = m.length;
		int c = m[0].length;
		for(int i=0; i<r; i++) {
			for(int j=0; j<c; j++) {
				sum += m[i][j];
			}
		}
		
		return sum; 
	}
}
