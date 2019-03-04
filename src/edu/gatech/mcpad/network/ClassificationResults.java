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

package edu.gatech.mcpad.network;


public class ClassificationResults {

	private static String NEW_LINE = System.getProperty("line.separator");
	
	private int numOfAnalyzedPackets = 0;
	
    /* numOfTestedPayloads differs from numOfAnalyzedPackets because if the
     * payload does not contain at least a n-gram of length maxNu+2, 
     * the payload will not be tested.
     */
	private int numOfTestedPayloads = 0;
	
	private int avgProbAttacks = 0; 
	private int prodProbAttacks = 0; 
	private int minProbAttacks = 0; 
	private int maxProbAttacks = 0; 
	private int majVotingAttacks = 0;

	protected ClassificationResults() {

	}

	public void incrementAnalyzedPackets(){
		numOfAnalyzedPackets++;
	}
	
	public void incrementTestedPackets(){
		numOfTestedPayloads++;
	}

	public void incrementNumDetectedAttacksAvgProb() {
		avgProbAttacks++;
	}
	
	public void incrementNumDetectedAttacksProdProb() {
		prodProbAttacks++;
	}
	
	public void incrementNumDetectedAttacksMinProb() {
		minProbAttacks++;
	}
	
	public void incrementNumDetectedAttacksMaxProb() {
		maxProbAttacks++;
	}

	public void incrementNumDetectedAttacksMajVoting(){
		majVotingAttacks++;
	}

	public int numDetectedAttacksAvgProb() {
		return avgProbAttacks;
	}
	
	public int numDetectedAttacksProdProb() {
		return prodProbAttacks;
	}
	
	public int numDetectedAttacksMinProb() {
		return minProbAttacks;
	}
	
	public int numDetectedAttacksMaxProb() {
		return maxProbAttacks;
	}
	
	public int numDetectedAttacksMajVoting() {
		return majVotingAttacks;
	}

	public int numOfAnalyzedPackets() {
		return numOfAnalyzedPackets;
	}
	
	public int numOfTestedPayloads() {
		return numOfTestedPayloads;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		int n = numOfAnalyzedPackets();
		sb.append("Number of analyzed packets: " + n);
		sb.append(NEW_LINE);
		sb.append("Number of tested payloads: " + numOfTestedPayloads());
		sb.append(NEW_LINE);
		sb.append("DETECTION RATES : ");
		sb.append(NEW_LINE);
		sb.append("Majority Voting : " + numDetectedAttacksMajVoting() + " ( " + numDetectedAttacksMajVoting()*100/(double)n + "% ) ");
		sb.append(NEW_LINE);
		sb.append("Average of Probabilities : " + numDetectedAttacksAvgProb() + " ( " + numDetectedAttacksAvgProb()*100/(double)n + "% ) ");
		sb.append(NEW_LINE);
		sb.append("Product of Probability : " + numDetectedAttacksProdProb() + " ( " + numDetectedAttacksProdProb()*100/(double)n + "% ) ");
		sb.append(NEW_LINE);
		sb.append("Minimum Probability : " + numDetectedAttacksMinProb() + " ( " + numDetectedAttacksMinProb()*100/(double)n + "% ) ");
		sb.append(NEW_LINE);
		sb.append("Maximum Probability : " + numDetectedAttacksMaxProb() + " ( " + numDetectedAttacksMaxProb()*100/(double)n + "% ) ");
		sb.append(NEW_LINE);
		
		return sb.toString();
	}
	
	public void printStatistics(){
		int n = numOfAnalyzedPackets();
		System.out.println("Number of analyzed packets: " + n);
		System.out.println("Number of tested payloads: " + numOfTestedPayloads());
		System.out.println("DETECTION RATES : ");
		System.out.println("Majority Voting : " + numDetectedAttacksMajVoting() + " ( " + numDetectedAttacksMajVoting()*100/n + "% ) ");
		System.out.println("Average of Probabilities : " + numDetectedAttacksAvgProb() + " ( " + numDetectedAttacksAvgProb()*100/n + "% ) ");
		System.out.println("Product of Probability : " + numDetectedAttacksProdProb() + " ( " + numDetectedAttacksProdProb()*100/n + "% ) ");
		System.out.println("Minimum Probability : " + numDetectedAttacksMinProb() + " ( " + numDetectedAttacksMinProb()*100/n + "% ) ");
		System.out.println("Maximum Probability : " + numDetectedAttacksMaxProb() + " ( " + numDetectedAttacksMaxProb()*100/n + "% ) ");
	}
	
}
