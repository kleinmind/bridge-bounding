package bbound;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import graph.Community;
import jung.IndexableGraph;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * Class implementing the local community detection method by Luo, Wang and Promislow
 * appearing in the paper "Exploring Local Community Structures in Large Networks", WI 2006.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class LWPCommunityDetector<V extends StringIndexableVertex,E> implements LocalCommunityDetector<V,E> {
	

	public Community<V,E> getCommunity(
			IndexableUndirectedSparseGraph<V, E> graph, V seed) {
		
		int cId = 1;
		Community<V,E> community = new Community<V,E>(cId, graph);
		community.addMember(seed);
		Set<V> neighbourSet = new HashSet<V>(graph.getNeighbors(seed));
		
		//int counter = 0;
		
		Set<V> Q = new HashSet<V>();
		
		do {
			Q = new HashSet<V>();
			double lastModularity = getLWPModularity(community);
			
			//System.out.println(++counter + " " + lastModularity);
			
			/* addition step */
			Iterator<V> nIter = neighbourSet.iterator();
			Set<V> toRemove = new HashSet<V>();
			while (nIter.hasNext()){
				V uj = nIter.next();
				if (toRemove.contains(uj)){
					continue;
				}
				community.addMember(uj);
				double newModularity = getLWPModularity(community);
				//System.out.println("\tADD " + newModularity + " " + community.getNumberOfMembers());
				if (newModularity > lastModularity){
					lastModularity = newModularity;
					Q.add(uj);
					toRemove.add(uj);
				} else {
					community.removeMember(uj);
				}
			}
			Iterator<V> removeIter = toRemove.iterator();
			while (removeIter.hasNext()){
				neighbourSet.remove(removeIter.next());
			}
			
			/* deletion step */
			Set<V> deleteQ = new HashSet<V>();
			do {
				deleteQ = new HashSet<V>();
				List<String> vsIDs = community.getMembers();
				for (int i = 0; i < vsIDs.size(); i++){
					String viID = vsIDs.get(i);
					community.removeMemberByID(viID);
					double newModularity = getLWPModularity(community);
					//System.out.println("\tDEL " + newModularity + " " + community.getNumberOfMembers());
					if ( (newModularity > lastModularity) && (community.isConnected()) ){
						lastModularity = newModularity;
						V vi = graph.getVertex(viID);
						deleteQ.add(vi);
						if (Q.contains(vi)){
							Q.remove(vi);
						}
					} else {
						community.addMemberByID(viID);
					}
				}
			} while (!deleteQ.isEmpty() );
			
			/* add vertices to neighbourSet */
			Iterator<V> nIterK = Q.iterator();
			while (nIterK.hasNext()){
				Iterator<V> candIterator = graph.getNeighbors(nIterK.next()).iterator();
				while (candIterator.hasNext()){
					V al = candIterator.next();
					if ((!community.contains(al)) && (!neighbourSet.contains(al))){
						neighbourSet.add(al);
					}
				}
			}
			
		} while (!Q.isEmpty());
		
		if (getLWPModularity(community) > 0.0 && community.contains(seed)){
			return community;
		} else {
			System.err.println("Empty community returned, because the output community does not" +
					" contain the seed node!");
			return new Community<V,E>(cId, graph);
		}		
	}

	/**
	 * Compute the LPW modularity measure introduced by Luo, Wang and Promislow. This method
	 * has been made public so that other algorithms can use the same measure (but a different
	 * search strategy).
	 * 
	 * @param community Input community.
	 * @return
	 */
	public double getLWPModularity(Community<V,E> community){
		/* check community validity*/
		if (!community.isValid()) throw new IllegalArgumentException(
				"You should provide a valid community as argument to the algorithm!");

		IndexableGraph<V, E> graph = community.getReferenceGraph();
		
		List<String> memberIDs = community.getMembers();
		int M = community.getNumberOfMembers();
		int indS = 0;
		int outdS = 0;
		for (int i = 0; i < M; i++){
			V currentMember = graph.getVertex(memberIDs.get(i));
			int currentMemberInDegree = 0;
			for (int j = 0; j < M; j++){
				if (i==j) continue;
				if (graph.findEdge(currentMember, graph.getVertex(memberIDs.get(j)))!= null){
					currentMemberInDegree++;
				}
			}
			/* update out- and in-degree counts of community */
			outdS += graph.degree(currentMember) - currentMemberInDegree;
			/* in-edges were counted twice so divide by 2 */
			indS += (int)Math.round(currentMemberInDegree/2.0); 
		}
		if ((outdS == 0) && (indS > 0)){
			return Double.MAX_VALUE;
		} else if ((outdS == 0) && (indS == 0)){
			/* depending on whether we consider an isolated node as a community or not
			 * the returned value should be 1.0 or 0.0 respectively */
			return 0.0;	
		} else {
			return (double)indS/(double)outdS;
		}
	}

}
