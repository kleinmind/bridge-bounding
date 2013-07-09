/*
  @(#) NetworkTopologyMeasuresCalculator.java	1.0,	08/07/2013
  
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.util.Pair;
import jung.WalkableWeightedEdge;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * This class calculates a series of predefined network topology measures
 * around a given edge or node.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class NetworkTopologyMeasuresCalculator<V extends StringIndexableVertex, E> {

	private final NetworkTopologyMeasures measure;
	private IndexableUndirectedSparseGraph<V, E> referenceGraph = null;
	private final double alpha = 0.5;
	
	public NetworkTopologyMeasuresCalculator(
			IndexableUndirectedSparseGraph<V,E> g, NetworkTopologyMeasures measure){
		this.referenceGraph = g;
		this.measure = measure;
	}
	
	public double calculateMeasure(E edge){
		if (NetworkTopologyMeasures.ELB.equals(measure)){
			return calculateElb(edge);
		} else if (NetworkTopologyMeasures.ELB2.equals(measure)) {
			return calculateElb2(edge);
		} else {
			throw new IllegalArgumentException("Unsupported network measure!");
		}
	}
	
	/**
	 * Calculate edge local bridging (equivalent to: 1.0-edge clustering coefficient)
	 * 
	 * @param edge
	 * @return
	 */
	private double calculateElb(E edge){
		if (edge instanceof WalkableWeightedEdge){
			Double elb = ((WalkableWeightedEdge)edge).getNetworkTopologyMeasure(NetworkTopologyMeasures.ELB);
			if (elb != null){
				return elb;
			}
		}
		
		Pair<V> endpoints = referenceGraph.getEndpoints(edge);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		int deg1 = referenceGraph.degree(v1);
		int deg2 = referenceGraph.degree(v2);
		int denominator = Math.min(deg1-1, deg2-1);
		if (denominator == 1){
			return 1.0;
		}
		
		Set<V> neighborhood1Set = new HashSet<V>(referenceGraph.getNeighbors(v1));
		List<V> neighborhood2 = new ArrayList<V>(referenceGraph.getNeighbors(v2));
		
		int countCommon = 0;
		for (int i = 0; i < neighborhood2.size(); i++) {
			if (neighborhood1Set.contains(neighborhood2.get(i))){
				countCommon++;
			}
		}
		double elb = (1.0-(double)countCommon / (double)denominator);
		if (edge instanceof WalkableWeightedEdge){
			((WalkableWeightedEdge)edge).setNetworkTopologyMeasure(NetworkTopologyMeasures.ELB, elb);
		}
		return elb;
	}
	
	/**
	 * Calculate 2nd order edge local bridging (average between the local bridging of
	 * the edge and the bridging values of the surrounding edges)
	 * 
	 * @param edge
	 * @return
	 */
	private double calculateElb2(E edge){
		if (edge instanceof WalkableWeightedEdge){
			Double elb2 = ((WalkableWeightedEdge)edge).getNetworkTopologyMeasure(NetworkTopologyMeasures.ELB2);
			if (elb2 != null){
				return elb2;
			}
		}
		
		Pair<V> endpoints = referenceGraph.getEndpoints(edge);
		double thisElb = calculateElb(edge);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		List<E> edges1 = new ArrayList<E>(referenceGraph.getIncidentEdges(v1));
		List<E> edges2 = new ArrayList<E>(referenceGraph.getIncidentEdges(v2));
		
		double sum = 0.0;
		for (int i = 0; i < edges1.size(); i++){
			sum += calculateElb(edges1.get(i));
		}
		for (int i = 0; i < edges2.size(); i++){
			sum += calculateElb(edges2.get(i));
		}
		double elb2 = alpha*thisElb + (1.0-alpha)*sum/(edges1.size()+edges2.size());
		if (edge instanceof WalkableWeightedEdge){
			((WalkableWeightedEdge)edge).setNetworkTopologyMeasure(NetworkTopologyMeasures.ELB2, elb2);
		}
		return elb2;
	}
	
}
