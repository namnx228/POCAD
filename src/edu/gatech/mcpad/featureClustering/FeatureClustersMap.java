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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

public class FeatureClustersMap implements Serializable {
	
	static final long serialVersionUID = 4445504246415083109L;
	
	private Map<PairIndex,Integer> clustersMap = new HashMap<PairIndex,Integer>();
	private Map<Integer,Set<PairIndex>> inverseClustersMap = new HashMap<Integer,Set<PairIndex>>();
	private int k;
	
	FeatureClustersMap(int k) {
		this.k = k;
	}
	
	public void put(int i, int j, int clusterIndex) {
		clustersMap.put(new PairIndex(i,j),new Integer(clusterIndex));
		
		Set<PairIndex> piSet = inverseClustersMap.get(new Integer(clusterIndex));
		if(piSet!=null) {
			piSet.add(new PairIndex(i,j));
		}
		else {
			piSet = new HashSet<PairIndex>();
			piSet.add(new PairIndex(i,j));
			inverseClustersMap.put(new Integer(clusterIndex),piSet);
		}
	}
	
	public int get(int i, int j) {
		Integer c = (Integer)clustersMap.get(new PairIndex(i,j));
		return c.intValue();
	}
	
	public int get(PairIndex pi) {
		Integer c = (Integer)clustersMap.get(pi);
		return c.intValue();
	}	
	
	public Set<PairIndex> get(int clusterIndex) {
		return (Set<PairIndex>)inverseClustersMap.get(new Integer(clusterIndex));
	}
	
	public int getNumberOfClusters() {
		return k;
	}
	
	public int getNumberOfNonEmptyClusters() {
		return inverseClustersMap.size();
	}
	
	public int getNumerOfFeaturesPerCluster(int clusterIndex) {
		return ((Set<PairIndex>)inverseClustersMap.get(new Integer(clusterIndex))).size();
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		for(int i=0; i<k; i++) {
			Set pairSet = (Set<PairIndex>)inverseClustersMap.get(new Integer(i));
			if(pairSet!=null) {
				buff.append("Cluster["+i+"] : "+pairSet.size());
			}
			else 
				buff.append("Cluster["+i+"] : 0");
			buff.append("\r\n");
		}
		
		return buff.toString();
	}
	
}
