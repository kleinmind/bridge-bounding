/*
  @(#) WalkableWeightedEdge.java	1.0,	05/07/2013
  
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
package jung;

import java.util.HashMap;
import java.util.Map;

import bbound.NetworkTopologyMeasures;

/**
 * WalkableWeightedEdge represents a weighted relation between two objects. 
 * In addition it supports the association of different network-based measures
 * with it (through a map) as well as a counter of times that a graph visiting
 * process goes through it.
 * 
 * @version	1.0 05/07/2013
 * @author Symeon Papadopoulos
 *
 */
public class WalkableWeightedEdge {
	
	/* A frequency/intensity/weight value associated with the edge. */
	private int frequency = 0;
	
	/* Used by edge traversing algorithms to keep track of how
	 * many times they have passed through this edge. */
	private int timesVisited = 0;
	
	/*
	 * Map that associates values with network topology measures.
	 */
	private Map<NetworkTopologyMeasures, Double> associatedValues = 
		new HashMap<NetworkTopologyMeasures, Double>();
	
	public WalkableWeightedEdge(int freq){
		this.frequency = freq;
	}

	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public void increaseFrequency(){
		this.frequency++;
	}
	
	public void visit(){
		timesVisited++;
	}
	public int getTimesVisited(){
		return timesVisited;
	}
	
	public void setNetworkTopologyMeasure(NetworkTopologyMeasures measure, double value){
		associatedValues.put(measure, value);
	}
	public Double getNetworkTopologyMeasure(NetworkTopologyMeasures measure){
		return associatedValues.get(measure);
	}
}
