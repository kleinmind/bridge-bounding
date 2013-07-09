/*
  @(#) SyntheticCommunityParameters.java	1.0,	05/07/2013
  
  Bridge Bounding, https://github.com/kleinmind/bridge-bounding
  
  Copyright 2013 Symeon Papadopoulos

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package graph;

/**
 * 
 * @author Symeon Papadopoulos
 *
 */
public class SyntheticCommunityParameters {

	private int nrNodes = 0;
	private int nrCommunities = 0;
	private double sizeVariation = 1.0;
	private int minTotDegree = 0;
	private int maxTotDegree = 0;
	private double minPout = 0.0;
	private double maxPout = 0.0;
	
	public SyntheticCommunityParameters(int nodes, int communities, double szVariation,
			int minTotDegree, int maxTotDegree, double minPout, double maxPout){
		
		if ((minTotDegree < 0) || (maxTotDegree > nodes-1) || (maxTotDegree < 0) || (maxTotDegree > nodes-1)){
			throw new IllegalArgumentException("Community density should lie in the interval [0.0,1.0].");
		}
		if (minTotDegree > maxTotDegree){
			throw new IllegalArgumentException("Min Total Degree should be smaller than the max one!");
		}
		if ((minPout < 0.0) || (minPout > 1.0) || (maxPout < 0.0) || (maxPout > 1.0)){
			throw new IllegalArgumentException("Community out-density should lie in the interval [0.0,1.0].");
		}
		if (minPout > maxPout) {
			throw new IllegalArgumentException("Min Out Density should be smaller than the maximum one!");
		}
		
		this.nrNodes = nodes;
		this.nrCommunities = communities;
		this.sizeVariation = szVariation;
		this.minTotDegree = minTotDegree;
		this.maxTotDegree = maxTotDegree;
		this.minPout = minPout;
		this.maxPout = maxPout;
	}
	
	
	
	public SyntheticCommunityParameters(int nodes, int communities, double szVariation,
			int totDegree, double pout){
		
		if ((totDegree < 0) || (totDegree > nodes-1)){
			throw new IllegalArgumentException("Community density should lie in the interval [0.0,1.0].");
		}
		if ((pout < 0.0) || (pout > 1.0)){
			throw new IllegalArgumentException("Community out-density should lie in the interval [0.0,1.0].");
		}
		
		this.nrNodes = nodes;
		this.nrCommunities = communities;
		this.sizeVariation = szVariation;
		this.minTotDegree = totDegree;
		this.maxTotDegree = totDegree;
		this.minPout = pout;
		this.maxPout = pout;
	}
	
	public static SyntheticCommunityParameters getDefault(){
		return new SyntheticCommunityParameters(100, 4, 1.0, 15, 0.05);
	}
	
	public int getNrNodes() {
		return nrNodes;
	}
	public void setNrNodes(int nrNodes) {
		this.nrNodes = nrNodes;
	}
	public int getNrCommunities() {
		return nrCommunities;
	}
	public void setNrCommunities(int nrCommunities) {
		this.nrCommunities = nrCommunities;
	}
	public double getSizeVariation() {
		return sizeVariation;
	}
	public void setSizeVariation(double sizeVariation) {
		this.sizeVariation = sizeVariation;
	}
	public int getMinTotDegree() {
		return minTotDegree;
	}
	public void setMinTotDegree(int minTotDegree) {
		this.minTotDegree = minTotDegree;
	}
	public int getMaxTotDegree() {
		return maxTotDegree;
	}
	public void setMaxTotDegree(int maxTotDegree) {
		this.maxTotDegree = maxTotDegree;
	}
	public double getMinPout() {
		return minPout;
	}
	public void setMinPout(double minPout) {
		this.minPout = minPout;
	}
	public double getMaxPout() {
		return maxPout;
	}
	public void setMaxPout(double maxPout) {
		this.maxPout = maxPout;
	}



	@Override
	public String toString() {
		return nrNodes + "\t" + nrCommunities + "\t" + sizeVariation + "\t" + 
			minTotDegree + "\t" + maxTotDegree + "\t" + minPout + "\t" + maxPout;
	}

	
}
