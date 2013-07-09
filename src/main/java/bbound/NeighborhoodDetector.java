/*
  @(#) NeighborhoodDetector.java	1.0,	08/07/2013
  
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

import java.util.Collection;
import java.util.Iterator;

import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.graph.Graph;
import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * Class implementing the LocalCommunityDetector interface as a n-hop neighborhood
 * identification problem.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class NeighborhoodDetector<V extends StringIndexableVertex,E> implements LocalCommunityDetector<V,E> {

	/* Number of hops to allowed to reach nodes belonging to the neighbourhood. */
	private int numberOfHops = 1;
	
	/**
	 * Create the n-hop neighborhood around the input seed node. 
	 */
	public Community<V,E> getCommunity(
			IndexableUndirectedSparseGraph<V, E> graph, V seed) {
		if (!graph.containsVertex(seed)){
			throw new IllegalArgumentException("Input graph does not contain seed node!");
		}
		
		KNeighborhoodFilter<V, E> filter = 
			new KNeighborhoodFilter<V, E>(seed, numberOfHops, 
					KNeighborhoodFilter.EdgeType.IN_OUT);
		Graph<V, E> neighbourhoodGraph = filter.transform(graph);
		
		Collection<V> neighbourCollection = neighbourhoodGraph.getVertices();//graph.getNeighbors(seed);
		Iterator<V> neighbourIter = neighbourCollection.iterator();
		Community<V,E> neighbourhood = new Community<V,E>(1, graph);
		neighbourhood.setName(seed.getID().toString());
		
		while (neighbourIter.hasNext()){
			V v = neighbourIter.next();
			neighbourhood.addMember(v);
		}
		return neighbourhood;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}
	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}

	
}
