/*
  @(#) BridgeBoundingDetector.java	1.0,	08/07/2013
 
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
package bbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * Class implementing the LocalCommunityDetector interface by employing the
 * Bridge Bounding technique.
 * @author Symeon Papadopoulos
 *
 */
public class BridgeBoundingDetector<V extends StringIndexableVertex, E> implements LocalCommunityDetector<V,E> {

	/* The network topology measure that will be used for estimating an
	 * edge's bridge-ness. */
	private final NetworkTopologyMeasures measure;
	
	/* The lower bridging threshold used by the algorithm. */
	private final double threshold;
	
	public BridgeBoundingDetector(NetworkTopologyMeasures measure, double threshold){
		this.measure = measure;
		this.threshold = threshold;
	}
	
	/**
	 * Detect the community containing the seed node based on the bridge bounding method.
	 */
	public Community<V,E> getCommunity(
			IndexableUndirectedSparseGraph<V, E> graph, V seed) {
		
		NetworkTopologyMeasuresCalculator<V,E> networkMeasureCalculator = 
			new NetworkTopologyMeasuresCalculator<V,E>(graph, measure);
		
		int cId = 1;
		Community<V,E> community = new Community<V,E>(cId, graph);
		
		Stack<V> frontier = new Stack<V>();
		frontier.push(seed);
		
		while (!frontier.isEmpty()){
			V vertexToAdd = frontier.pop();
			if (community.contains(vertexToAdd)) continue;
			community.addMember(vertexToAdd);
			
			List<V> neighbours = new ArrayList<V>(graph.getNeighbors(vertexToAdd));
			for (int i = 0; i < neighbours.size(); i++){
				V candidate = neighbours.get(i);
				if (community.contains(candidate)) continue;
				E edge = graph.findEdge(vertexToAdd, candidate);
				if (networkMeasureCalculator.calculateMeasure(edge) > threshold){
					continue;
				}
				frontier.push(candidate);
			}
		}
		return community;
	}
}
