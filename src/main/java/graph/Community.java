/*
  @(#) Community.java	1.0,	01/12/2008
  
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import jung.IndexableGraph;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;


/**
 * Data class that models a node community.  
 * The community is identified by an id field and is always defined
 * with reference to a Graph object (which shouldn't be modified)
 * To specify the members of the community, a list of objects is
 * necessary. Obviously, the objects need to be contained in the graph.
 *  
 *   
 * @author Symeon Papadopoulos
 *
 */
public class Community<V extends StringIndexableVertex, E> {

	private int id = -1;
	private String name = null;
	private Set<String> members = new HashSet<String>(10);
	private final IndexableGraph<V, E> referenceGraph;
	
	/**
	 * When this constructor is used, it is assumed that a new
	 * empty community is needed
	 * @param id
	 * @param graph
	 */
	public Community(int id, IndexableGraph<V, E> graph){
		if (id < 0){
			throw new IllegalArgumentException("Community id should be a non-negative integer!");
		}
		this.id = id;
		this.referenceGraph = graph;
	}

	/**
	 * When this constructor is used, it is assumed that an already
	 * existing community is needed, so NO tag network initialization
	 * takes place. 
	 * 
	 * @param id
	 * @param net
	 * @param members
	 */
	public Community(int id, IndexableGraph<V, E> net, List<V> members){
		if (id < 0){
			throw new IllegalArgumentException("Community id should be a non-negative integer!");
		}
		this.id = id;
		this.referenceGraph = net;
		for (int i = 0; i < members.size(); i++){
			addMember(members.get(i));
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}
	
	public void addMember(V t){
		if (referenceGraph == null){
			throw new IllegalStateException("The community object has not been properly initialized!");
		}
		if (!referenceGraph.containsVertex(t)){
			throw new IllegalStateException("You attempt to add to the community a member, which does not exist in the graph!");
		}
		if (members.contains(t.getID())){
			//System.out.println(t + " --> " + members);
			return;
		} 
		members.add(t.getID());
	}
	public void addMemberByID(String tID){
		if (referenceGraph == null){
			throw new IllegalStateException("The community object has not been properly initialized!");
		}
		if (referenceGraph.getVertex(tID) == null){
			throw new IllegalStateException("You attempt to add to the community a member, which does not exist in the graph!");
		}
		if (members.contains(tID)){
			return;
		}
		members.add(tID);
	}
	
	public void removeMember(V t){
		if (members.contains(t.getID())){
			//System.out.println(t + " removed!");
			members.remove(t.getID());
		}
	}
	public void removeMemberByID(String tID){
		if (members.contains(tID)){
			members.remove(tID);
		}
	}
	
	public int getNumberOfMembers(){
		return members.size();
	}
	public List<String> getMembers(){
		List<String> comElements = new ArrayList<String>(members);
		return comElements;
	}
	public void setMembers(List<V> comElements){
		for (int i = 0; i < comElements.size(); i++){
			addMember(comElements.get(i));
		}
	}
	
	public boolean contains(StringIndexableVertex t){
		return members.contains(t.getID());
	}
	
	public boolean containsID(String tID){
		return members.contains(tID);
	}
	
	/**
	 * Check whether the community is connected (single-component).
	 * @return true if connected, false otherwise
	 */
	public boolean isConnected(){
		if (members.size() < 1) return false;
		
		/* visit all possible nodes starting from an arbitrary node of the community */
		Set<V> reachableNodes = new HashSet<V>();
		Set<String> visited = new HashSet<String>();
		V seed = referenceGraph.getVertex(members.iterator().next());
		Stack<V> frontier = new Stack<V>();
		frontier.push(seed);
		while (!frontier.isEmpty()){
			V cand = frontier.pop();
			visited.add(cand.getID());
			reachableNodes.add(cand);
			Iterator<V> nIter = referenceGraph.getNeighbors(cand).iterator();
			while(nIter.hasNext()){
				V ni = nIter.next();
				if (visited.contains(ni.getID())){
					continue;
				}
				if (members.contains(ni.getID())){
					if (!reachableNodes.contains(ni)){
						reachableNodes.add(ni);
					}
					frontier.add(ni);
				}
			}
		}
		/* if the reachable nodes (starting by a seed) are not equal to all community
		 * nodes then the community is not connected */
		if (reachableNodes.size() < members.size()){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Check that all IDs contained in the community correspond to actual
	 * nodes in the underlying graph
	 * @return true if community is valid, false otherwise
	 */
	public boolean isValid(){
		Iterator<String> idIter = members.iterator();
		while (idIter.hasNext()){
			if (referenceGraph.getVertex(idIter.next()) == null){
				return false;
			}
		}
		return true;
	}
	
	public IndexableGraph<V, E> getReferenceGraph(){
		return referenceGraph;
	}
	
	/**
	 * This method is relatively expensive, since each time it is
	 * called it creates a fresh copy of the subgraph containing all 
	 * its members.
	 * @return A copy of the graph containing all members and edges.
	 */
	public IndexableGraph<V, E> getCommunityCopy(){
		IndexableGraph<V, E> com = new IndexableUndirectedSparseGraph<V, E>();
		Iterator<String> mIter = members.iterator();
		while (mIter.hasNext()){
			V m = referenceGraph.getVertex(mIter.next());
			com.addVertex(m);
		}
		mIter = members.iterator();
		while (mIter.hasNext()){
			V thisVertex = com.getVertex(mIter.next());
			Collection<V> neighbors =  referenceGraph.getNeighbors(thisVertex);
			if (neighbors != null){
				Iterator<V> nIter = neighbors.iterator();
				while (nIter.hasNext()){
					V currentNeighbor = nIter.next();
					if (members.contains(currentNeighbor.getID())){
						E e = com.findEdge(thisVertex, currentNeighbor);
						if (e == null){
							e = referenceGraph.findEdge(thisVertex, currentNeighbor);
							List<V> edgePoints = new ArrayList<V>(2);
							edgePoints.add(thisVertex);
							edgePoints.add(currentNeighbor);
							com.addEdge(e, edgePoints);
						}
					}
				}
			}
		}
		return com;
	}
}
