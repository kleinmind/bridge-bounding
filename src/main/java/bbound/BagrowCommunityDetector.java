/*
  @(#) BagrowCommunityDetector.java	1.0,	08/07/2013
 
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * Class implementing the local community detection method by Bagrow appearing in 
 * the paper "Evaluating Local Community Methods in Networks", J. Stat. Mech.  (2008).
 * 
 * @author Symeon Papadopoulos
 *
 */
public class BagrowCommunityDetector<V extends StringIndexableVertex,E> implements LocalCommunityDetector<V,E> {

	private int maxCommunitySize = 500;
	

	public Community<V,E> getCommunity(
			IndexableUndirectedSparseGraph<V, E> graph, V seed) {
		
		int cId = 1;
		
		/* The community that will be identified. */
		Community<V,E> C = new Community<V,E>(cId, graph);
		C.addMember(seed);
		
		/* set of nodes surrounding the community (agglomeration candidates) */
		Set<V> U = new HashSet<V>();
		Iterator<V> nIter = graph.getNeighbors(seed).iterator();
		while (nIter.hasNext()){
			U.add(nIter.next());
		}
		
		//int counter = 0;
		int Mprev = 0;
		int countCusps = 0;
		/* tendency can be either upward (1) or downward (0) */
		int prevTendency = 1; 
		while ((C.getNumberOfMembers() < maxCommunitySize) && (countCusps < 2)) {
			
			//System.out.println(++counter + " " + C.getNumberOfMembers());
			
			/* go through candidates one-by-one and select the one to agglomerate based 
			 * on its outwardness (we want minimun outwardness. If outwardness = -1, then
			 * don't perform any further check */
			double minOutwardness = 1;
			V selectedCandidate = null;
			
			Iterator<V> candIter = U.iterator();
			while (candIter.hasNext()){
				V candidate = candIter.next();
				Iterator<V> candNeighborIter = graph.getNeighbors(candidate).iterator();
				int kin = 0;
				while (candNeighborIter.hasNext()){
					if (C.contains(candNeighborIter.next())){
						kin++;
					}
				}
				int k = graph.degree(candidate);
				double outwardness = 1.0 - ((2.0*kin)/(double)k);
				if (outwardness<-0.999999){
					selectedCandidate = candidate;
					break;
				} else {
					if (outwardness < minOutwardness){
						minOutwardness = outwardness;
						selectedCandidate = candidate;
					}
				}
			}
			
			if (selectedCandidate == null) break;
			
			C.addMember(selectedCandidate);
			U.remove(selectedCandidate);
			Iterator<V> toAddIter = graph.getNeighbors(selectedCandidate).iterator();
			while (toAddIter.hasNext()){
				V toAdd = toAddIter.next();
				if (!C.contains(toAdd)) {
					U.add(toAdd);
				}
			}
			
			// stopping criterion (check whether Mout has reached a local minimum
			int m = calculateMout(C, graph);
			int tendency = 0;
			if (m > Mprev){
				tendency = 1;	
			}
			if (tendency != prevTendency){
				countCusps++;
				prevTendency = tendency;
			}
			Mprev = m;
			if (countCusps > 1){
				C.removeMember(selectedCandidate);
				break;
			}
		}
		
		return C;
	}
	
	private int calculateMout(Community<V,E> c, IndexableUndirectedSparseGraph<V,E> graph){
		List<String> members = c.getMembers();
		int mout = 0;
		for (int i = 0; i < members.size(); i++){
			V current = graph.getVertex(members.get(i));
			Iterator<V> nIter = graph.getNeighbors(current).iterator();
			while (nIter.hasNext()){
				if (!c.contains(nIter.next())){
					mout++;
				}
			}
		}
		return mout;
	}
}
