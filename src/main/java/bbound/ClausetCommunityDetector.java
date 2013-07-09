/*
  @(#) ClausetCommunityDetector.java	1.0,	08/07/2013
 
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * Class implementing the local community detection method by Clauset appearing in the paper 
 * "Finding local community structure in networks", Phys. Rev. E72(2), id. 026132, 2005.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class ClausetCommunityDetector<V extends StringIndexableVertex, E> implements LocalCommunityDetector<V,E> {

	private int targetCommunityMembers = 100;
	
	public void setTargetCommunityMembers(int k) {
		this.targetCommunityMembers = k;
	}
	

	public Community<V,E> getCommunity(
			IndexableUndirectedSparseGraph<V, E> graph, V seed) {
		
		int cId = 1;
		
		/* The community that will be identified. */
		Community<V,E> C = new Community<V,E>(cId, graph);
		C.addMember(seed);
		
		/* The set of vertices that are adjacent to the community border. Initialized as
		 * the neighbors of the seed node. */
		List<V> U = new ArrayList<V>();
		Set<String> Ulookup = new HashSet<String>();
		Iterator<V> nIter = graph.getNeighbors(seed).iterator();
		while (nIter.hasNext()){
			V thisNeighbour = nIter.next();
			U.add(thisNeighbour);
			Ulookup.add(thisNeighbour.getID());
		}
		/* The set of border nodes. Initialized as the seed node. */
		List<V> B = new ArrayList<V>();
		B.add(seed);
		int I = 0;
		int T = graph.degree(seed);
		double R = 0.0;
		
		int noProgressIter = 0;
		while ((C.getNumberOfMembers() < targetCommunityMembers) && (noProgressIter < 10)){
			
			int maxDeltaI = 0;
			int maxDeltaT = 0;
			double maxDeltaR = 0.0;
			int maxInd = -1;
			//System.out.println("STEP. R = " + R);
			
			/* estimate DR for each potential new member in the community based on deltaI and deltaT */
			for (int i = 0; i < U.size(); i++){
				V candidate = U.get(i);
				
				/* find the additional nodes that form the frontier of the new border */
				Set<String> extendedFrontier = new HashSet<String>();
				Iterator<V> extFrontierIter = graph.getNeighbors(candidate).iterator();
				int existingConnsOut = 0;
				while (extFrontierIter.hasNext()){
					V candidateNeighbour = extFrontierIter.next();
					if (!C.contains(candidateNeighbour) && !B.contains(candidateNeighbour)){
						if (!U.contains(candidateNeighbour)){
							extendedFrontier.add(candidateNeighbour.getID());
						} else {
							existingConnsOut++;
						}
					}
				}
				
				/* check which nodes will be removed from the boundary */
				List<V> toBeRemoved = new ArrayList<V>(B.size());
				Set<String> toBeRemovedLookup = new HashSet<String>();
				
				for (int x = 0; x < B.size(); x++){
					Iterator<V> inIter = graph.getNeighbors(B.get(x)).iterator();
					boolean remainsInBoundary = false;
					while (inIter.hasNext()){
						String currentNeighbourID = inIter.next().getID();
						/* for the node to remain in boundary we need to find at least one neighbor 
						 * apart from "candidate" that belongs to U */
						if (Ulookup.contains(currentNeighbourID) || 
								extendedFrontier.contains(currentNeighbourID)){
							if (!currentNeighbourID.equals(candidate.getID())){
								remainsInBoundary = true;
								break;
							}
						}
					}
					if (!remainsInBoundary) {
						toBeRemoved.add(B.get(x));
						toBeRemovedLookup.add(B.get(x).getID());
					}
				}
				
				/* estimate the number of connections for candidate that end up outside the border and community */
				int connsOut = existingConnsOut + extendedFrontier.size();
				
				/* marginal case where the current node will be immediately removed from boundary */
				if (connsOut == 0){
					toBeRemoved.add(candidate);
					toBeRemovedLookup.add(candidate.getID());
				}
					
				/* count the number of connections from the nodes to be kicked out of boundary that 
				 * ended up in the community but not to boundary */
				int kickedoutIn = 0;
				for (int x = 0; x < toBeRemoved.size(); x++){
					Iterator<V> potentialInConnectionIter = 
						graph.getNeighbors(toBeRemoved.get(x)).iterator();
					while (potentialInConnectionIter.hasNext()){
						V potentialInConnection = potentialInConnectionIter.next();
						if (C.contains(potentialInConnection) ||
								toBeRemovedLookup.contains(potentialInConnection.getID())) {
							kickedoutIn++;
						}
					}
				}
				
				
				/* count number of connections for candidate that end up within the border or community */
				int connsIn = graph.degree(candidate) - connsOut;
				
				int deltaI = connsIn - kickedoutIn;
				int deltaT = connsOut + connsIn - kickedoutIn;
				
				double deltaR = ((double)(deltaI + I)/ (double)(deltaT + T)) - R;
				
				//System.out.println("\t" + candidate + " " + deltaR);
				
				if (deltaR > maxDeltaR) {
					maxDeltaR = deltaR;
					maxInd = i;
					maxDeltaI = deltaI;
					maxDeltaT = deltaT;
				}
			}
			if (maxInd < 0) {
				noProgressIter++;
				continue;
			}
			noProgressIter = 0;
			
			/* update U */
			V toAdd = U.get(maxInd);
			U.remove(maxInd);
			Ulookup.remove(toAdd.getID());
			Iterator<V> newUIter = graph.getNeighbors(toAdd).iterator();
			while (newUIter.hasNext()){
				V newNode = newUIter.next();
				if (!C.contains(newNode) && !B.contains(newNode)){
					if (!U.contains(newNode)){
						U.add(newNode);
						Ulookup.add(newNode.getID());
					}
				}
			}
			
			/* update B and C */
			C.addMember(toAdd);
			List<V> newB = new ArrayList<V>(B.size());
			B.add(toAdd);
			for (int i = 0; i < B.size(); i++){
				Iterator<V> inIter = graph.getNeighbors(B.get(i)).iterator();
				boolean remainsInBoundary = false;
				while (inIter.hasNext()){
					V currentNeighbour = inIter.next();
					if (Ulookup.contains(currentNeighbour.getID())){
						remainsInBoundary = true;
						break;
					}
				}
				if (remainsInBoundary){
					newB.add(B.get(i));
				}
			}
			B = null;
			B = newB;
			
			/* update R, T and I */
			R += maxDeltaR;
			T += maxDeltaT;
			I += maxDeltaI;
		}
		
		return C;
	}

}
