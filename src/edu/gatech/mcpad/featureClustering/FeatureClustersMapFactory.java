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

import java.util.Set;
import java.util.Iterator;


public class FeatureClustersMapFactory {
	
	public static final int NUM_OF_CHARACTERS = 256;
	public static final int MAX_ITERATIONS = 100;
	
	private FeatureClustersMap cMap;
	private int k; //number of clusters
	
	public FeatureClustersMapFactory(FeatureProbabilityDistributions fpDist, int k, double lossThreshold, boolean randomInit) {
		
		this.k = k;
		if(randomInit)
			cMap = initializeRandomClusters(fpDist,k); //Random initialization
		else
			cMap = initializeClusters(fpDist,k); //"Divisive" initialization
		
		ClusterProbabilityDistributions cpDist = new ClusterProbabilityDistributions(fpDist, cMap);
		
		double loss = computeInformationLoss(cMap,fpDist,cpDist);
		/*
		System.out.println("LOSS = " + loss);
		int h = k-cMap.getNumberOfNonEmptyClusters();
		System.out.println("Empty Clusters = " + h);
		
		for(int clusterIndex=0; clusterIndex<k; clusterIndex++) {
			Set<PairIndex> piSet = cMap.get(clusterIndex);
			if(piSet!=null)
				System.out.println("Cluster " + clusterIndex + " : " + piSet.size());
		}
		*/
		
		int iterations = 0;
		while(loss > lossThreshold) {
			cMap = computeClusters(cMap,fpDist,cpDist,k);
			cpDist = new ClusterProbabilityDistributions(fpDist, cMap);
			loss = computeInformationLoss(cMap,fpDist,cpDist);
			iterations++;
			
			if(iterations>MAX_ITERATIONS)
				break;
			
			/*
			System.out.println("LOSS = " + loss);
			h = k-cMap.getNumberOfNonEmptyClusters();
			System.out.println("Empty Clusters = " + h);
			
			int count = 0;
			for(int clusterIndex=0; clusterIndex<k; clusterIndex++) {
				Set<PairIndex> piSet = cMap.get(clusterIndex);
				if(piSet!=null) {
					System.out.println("Cluster " + clusterIndex + " : " + piSet.size());
					count+=piSet.size();
				}
			}
			System.out.println("Number of mapped features = " + count);
			System.out.println();
			*/
		}
	}
	
	public FeatureClustersMap getFeatureClusterMap() {
		return cMap;
	}
	
	private FeatureClustersMap computeClusters(FeatureClustersMap map, FeatureProbabilityDistributions fpDist, ClusterProbabilityDistributions cpDist, int k) {
		
		FeatureClustersMap newMap = new FeatureClustersMap(k);
		for(int i=0; i<NUM_OF_CHARACTERS; i++) {
			for(int j=0; j<NUM_OF_CHARACTERS; j++) {
				int clusterIndex = computeNewClusterIndex(map,fpDist,cpDist,i,j); 
				newMap.put(i,j,clusterIndex);
			}
		}
		
		return newMap;
	}
	
	private FeatureClustersMap initializeClusters(FeatureProbabilityDistributions fpDist, int k) {
		
		FeatureClustersMap tmpMap = new FeatureClustersMap(k);
		
		int countT = 0; //count Target
		int countO = 0; //count Outlier
		for(int i=0; i<fpDist.getNumberOfRows(); i++) {
			for(int j=0; j<fpDist.getNumberOfColumns(); j++) {
				if(fpDist.getPosteriorTargetProb(i,j)>=fpDist.getPosteriorOutlierProb(i,j)) {
					int clusterIndex = countT % (int)Math.ceil(k/2.0);
					tmpMap.put(i,j,clusterIndex);
					countT++;
				}
				else {
					int clusterIndex = (int)Math.floor(k/2) + countO % (int)Math.ceil(k/2.0);
					tmpMap.put(i,j,clusterIndex);
					countO++;
				}
			}
		}
		
		return tmpMap;
	}	
	
	private FeatureClustersMap initializeRandomClusters(FeatureProbabilityDistributions fpDist, int k) {
		
		FeatureClustersMap tmpMap = new FeatureClustersMap(k);
		
		for(int i=0; i<fpDist.getNumberOfRows(); i++) {
			for(int j=0; j<fpDist.getNumberOfColumns(); j++) {
				int clusterIndex = (int)(Math.random()*k*10000) % k;
				tmpMap.put(i,j,clusterIndex);
			}
		}
		
		return tmpMap;
	}	
	
	private double computeInformationLoss(FeatureClustersMap map, FeatureProbabilityDistributions fpDist, ClusterProbabilityDistributions cpDist) {
		
		double loss = 0;
		for(int clusterIndex=0; clusterIndex < k; clusterIndex++) {
			Set<PairIndex> piSet = map.get(clusterIndex);
			if(piSet!=null) {
				Iterator j = piSet.iterator();
				while(j.hasNext()) {
					PairIndex pi = (PairIndex)j.next();
					int row = pi.getRow();
					int column = pi.getColumn();
					loss += fpDist.getFeatureProb(row,column)*computeKLDivergence(map,fpDist,cpDist,row,column,clusterIndex);
					
				}
			}
		}
		
		return loss;
	}
	
	private int computeNewClusterIndex(FeatureClustersMap map, FeatureProbabilityDistributions fpDist, ClusterProbabilityDistributions cpDist, int i, int j) {
		int minDivergenceCluster = Integer.MAX_VALUE;
		
		double div = Double.MAX_VALUE;
		double tmpDiv = Double.MAX_VALUE;
		for(int clusterIndex=0; clusterIndex < k; clusterIndex++) {
			tmpDiv = computeKLDivergence(map,fpDist,cpDist,i,j,clusterIndex);	
			
			if(tmpDiv<div) {
				div = tmpDiv;
				minDivergenceCluster = clusterIndex;
			}
		}
		
		return minDivergenceCluster;
	}
	
	private double computeKLDivergence(FeatureClustersMap map, FeatureProbabilityDistributions fpDist, ClusterProbabilityDistributions cpDist, int i, int j, int clusterIndex) {
		//Compute the Kullback-Leibler Divervenge
		
		double pTargetGivenWord = fpDist.getPosteriorTargetProb(i,j);
		double pOutlierGivenWord = fpDist.getPosteriorOutlierProb(i,j);
		double pTargetGivenCluster = cpDist.getPosteriorTargetProb(clusterIndex);
		double pOutlierGivenCluster = cpDist.getPosteriorOutlierProb(clusterIndex);
		
		double div = 0;
		div += pTargetGivenWord * log2(pTargetGivenWord/pTargetGivenCluster);
		div += pOutlierGivenWord * log2(pOutlierGivenWord/pOutlierGivenCluster);
	
		return div;
	}
	
	private double log2(double x) {
		return Math.log(x)/Math.log(2);
	}
	
}
