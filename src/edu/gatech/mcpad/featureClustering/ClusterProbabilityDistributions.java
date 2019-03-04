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

import java.util.Iterator;
import java.util.Set;


public class ClusterProbabilityDistributions {

	private FeatureProbabilityDistributions fpDist;
	private FeatureClustersMap cMap;
	
	private double[] priorProb; // p(Wj)
	
	private double[] posteriorTargetProb;  // p(T|Wj)
	private double[] posteriorOutlierProb; // p(O|Wj)
	
	private int k;
	
	/**
	 * 
	 * @param fpDist feature probability distribution
	 * @param cMap features to clusters mapping
	 */
	ClusterProbabilityDistributions(FeatureProbabilityDistributions fpDist, FeatureClustersMap cMap) {
		this.fpDist = fpDist;
		this.cMap = cMap;
		this.k = cMap.getNumberOfClusters();
		
		priorProb = new double[k];
		posteriorTargetProb = new double[k];
		posteriorOutlierProb = new double[k];
		
		computePriorProbabilities(fpDist,cMap);
		computePosteriorProbabilities(fpDist,cMap);
		
	}
	
	public double getPriorProb(int clusterIndex) {
		
		return priorProb[clusterIndex];
	}
	
	public double getPosteriorTargetProb(int clusterIndex) {
		
		return posteriorTargetProb[clusterIndex];
	}
	
	public double getPosteriorOutlierProb(int clusterIndex) {
		
		return posteriorOutlierProb[clusterIndex];
	}
	
	private void computePriorProbabilities(FeatureProbabilityDistributions fpDist, FeatureClustersMap cMap) {
		
		for(int l=0; l<k; l++) {
			Set<PairIndex> piSet = cMap.get(l);
			if(piSet!=null) {
				Iterator j = piSet.iterator();
				while(j.hasNext()) {
					PairIndex pi = (PairIndex)j.next();
					priorProb[l] += fpDist.getFeatureProb(pi.getRow(),pi.getColumn()); // Sum of p(w_t), for all the w_t in Wj
				}
			}
		}
	}	
	
	private void computePosteriorProbabilities(FeatureProbabilityDistributions fpDist, FeatureClustersMap cMap) {
		
		for(int l=0; l<k; l++) {
			Set<PairIndex> piSet = cMap.get(l);
			if(piSet!=null) {
				Iterator j = piSet.iterator();
				while(j.hasNext()) {
					PairIndex pi = (PairIndex)j.next();
					double ptProb = fpDist.getPosteriorTargetProb(pi.getRow(),pi.getColumn());  // p(T|w_t)
					double poProb = fpDist.getPosteriorOutlierProb(pi.getRow(),pi.getColumn()); // p(O|w_t)
					
					posteriorTargetProb[l] += (fpDist.getFeatureProb(pi.getRow(),pi.getColumn())/priorProb[l])*ptProb;  // p(T|Wj) = (p(w_t)/p(Wj))*p(T|w_t)
					posteriorOutlierProb[l] += (fpDist.getFeatureProb(pi.getRow(),pi.getColumn())/priorProb[l])*poProb; // p(O|Wj) = (p(w_t)/p(Wj))*p(O|w_t)
				}
			}
		}
	}
	
}
