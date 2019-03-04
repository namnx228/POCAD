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

/**
 * 
 * @author Roberto Perdisci
 * 
 * Computes the probability distributions for the Target and Outlier classes<br><br>
 * For the get methods below i and j are the coordinates of the feature
 *
 */
public class FeatureProbabilityDistributions implements Serializable {
	
	static final long serialVersionUID = 54455042L;
	
	private double[][] classConditionalTargetProbMatrix;
	private double[][] posteriorTargetProbMatrix;
	private double[][] posteriorOutlierProbMatrix;
	
	private double classConditionalOutlierProb; // = 1/(total number of features)
	
	private double priorTargetProb;  //prior target class probability
	private double priorOutlierProb; //prior outlier class probability
	
	/**
	 * 
	 * @param om
	 * @param priorTP  the prior probability of the Target Class
	 * @param priorOP  the prior probability of the Otlier Class
	 */
	public FeatureProbabilityDistributions(OccurrenceMatrix om, double priorTP, double priorOP) {
	
		priorTargetProb = priorTP;
		priorOutlierProb = priorOP;
		
		classConditionalOutlierProb = 1.0 / om.getNumOfElements(); //Assumes uniform distribution for p(w_t|O)
		classConditionalTargetProbMatrix = computeClassConditionalTargetProb(om);
		
		posteriorTargetProbMatrix = computePosteriorTargetProbMatrix(classConditionalTargetProbMatrix, classConditionalOutlierProb, priorTP, priorOP);
		posteriorOutlierProbMatrix = computePosteriorOutlierProbMatrix(classConditionalTargetProbMatrix, classConditionalOutlierProb, priorTP, priorOP);
	}
	
	public int getNumberOfRows() {
		return classConditionalTargetProbMatrix.length;
	}
	
	public int getNumberOfColumns() {
		return classConditionalTargetProbMatrix[0].length;
	}
	
	public double getClassConditionalTargetProb(int i, int j) {
		return classConditionalTargetProbMatrix[i][j];
	}
	
	public double getClassConditionalOutlierProb(int i, int j) {
		return classConditionalOutlierProb;
	}	
	
	//Only for debug purposes
	public double getConditionalTargetProbMatrixSum() {
		return computeSum(classConditionalTargetProbMatrix);
	}
	
	//Only for debug purposes
	public double getPosteriorProbSum(int i, int j) {
		return (posteriorTargetProbMatrix[i][j] + posteriorOutlierProbMatrix[i][j]);
	}

	
	public double getPosteriorTargetProb(int i, int j) {
		return posteriorTargetProbMatrix[i][j];
	}
	
	public double getPosteriorOutlierProb(int i, int j) {
		return posteriorOutlierProbMatrix[i][j];
	}	
	
	public double getFeatureProb(int i, int j) {
		//p(w_t) = p(w_t|T)*p(T) + p(w_t|O)*p(O)
		
		return (classConditionalTargetProbMatrix[i][j]*priorTargetProb + classConditionalOutlierProb*priorOutlierProb);
	}		
	
	private double[][] computeClassConditionalTargetProb(OccurrenceMatrix om) {
		//Compute the conditional (w.r.t. the Target class) probability Matrix p(w_t|T) 
		//using the Laplace's rule of sucession
		
		double[][] ccTargetP = new double[om.getNumOfRows()][om.getNumOfColumns()];
		int m = om.getNumOfElements();
		for(int i=0; i<om.getNumOfRows(); i++) {
			for(int j=0; j<om.getNumOfColumns(); j++) {
				ccTargetP[i][j] = (1 + om.get(i,j))/(m + om.getSum()); //Laplace's rule of sucession
			}
		}		

		//System.out.println("SUM = " + this.computeSum(ccTargetP));
		
		return ccTargetP;
	}
	
	private double[][] computePosteriorTargetProbMatrix(double[][] ccTargetP, double ccOutlierP, double priorTP, double priorOP) { 
		//Compute the posterior probability p(T|w_t)
		
		
		double[][] ptp = new double[ccTargetP.length][ccTargetP[0].length]; // ptp = posterior target probability
		
		for(int i=0; i<ptp.length; i++) {
			for(int j=0; j<ptp[0].length; j++) {
				ptp[i][j] = (ccTargetP[i][j]*priorTP) / getFeatureProb(i,j); // p(T|w_t) = p(w_t|T)*p(T)/p(w_t)
			}
		}
		
		return ptp;
	}
	
	private double[][] computePosteriorOutlierProbMatrix(double[][] ccTargetP, double ccOutlierP, double priorTP, double priorOP) {
		// ccp = class conditional probability (w.r.t. the Target class) 
		
		double[][] pop = new double[ccTargetP.length][ccTargetP[0].length]; // pop = posterior outlier probability
		
		for(int i=0; i<pop.length; i++) {
			for(int j=0; j<pop[0].length; j++) {
				pop[i][j] = (ccOutlierP*priorOP) / getFeatureProb(i,j); // p(O|w_t) = p(w_t|O)*p(O)/p(w_t)
			}
		}
		
		return pop;
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
