/*
  @(#) GraphPartition.java	1.0,	08/07/2013
  
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;


/**
 * This class is used to represent the result of a community detection (aka graph clustering)
 * method on a graph.
 * 
 * @author Symeon Papadopoulos
 *
 * @param <V, E>
 */
public class GraphPartition<V extends StringIndexableVertex, E> {
	
	/* Reference to the underlying graph */
	private IndexableUndirectedSparseGraph<V, E> referenceGraph = null;
	
	/* List of identified communities */
	private List<Community<V,E>> communities = new ArrayList<Community<V,E>>();
	
	
	public GraphPartition(){	
	}
	public GraphPartition(IndexableUndirectedSparseGraph<V, E> g){
		this.referenceGraph = g;
	}
	public GraphPartition(IndexableUndirectedSparseGraph<V, E> g, 
			List<Community<V,E>> communities){
		this.referenceGraph = g;
		this.communities = communities;
	}
	
	
	public IndexableUndirectedSparseGraph<V, E> getReferenceGraph() {
		return referenceGraph;
	}
	public void setReferenceGraph(IndexableUndirectedSparseGraph<V, E> referenceGraph) {
		this.referenceGraph = referenceGraph;
	}
	
	
	public int getNumberOfCommunities(){
		return communities.size();
	}
	public Community<V,E> getCommunity(int i){
		if (i >= communities.size()){
			throw new IllegalArgumentException("Community " + i + " does not exist in this partition!");
		}
		return communities.get(i);
	}
	public List<Community<V,E>> getCommunities() {
		return communities;
	}
	public void setCommunities(List<Community<V,E>> communities) {
		this.communities = communities;
	}
	public void addCommunity(Community<V,E> community) {
		communities.add(community);
	}
	
	/**
	 * Return the index of the community where a given vertex belongs.
	 *  
	 * @param v Input vertex for which we want to know the community.
	 * @return The community index of the vertex or -1 in case the vertex
	 * 			does not belong to any community.
	 */
	public int getVertexCommunityIndex(V v){
		for (int i = 0; i < communities.size(); i++) {
			if (communities.get(i).contains(v)){
				return i;
			}
		}
		return -1;
	}
	
	
	/**
	 * Generate a text file containing a description of the communities of the partition
	 * and their members.
	 * 
	 * @param file
	 */
	public void writePartitionToFile(String file){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append("GRAPH PARTITION FOR GRAPH: " + "G(" + 
					referenceGraph.getVertexCount() + ", " + referenceGraph.getEdgeCount()+")");
			writer.newLine(); writer.newLine();
			
			writer.append("COMMUNITIES: " + this.getNumberOfCommunities());
			writer.newLine(); writer.newLine();
			
			for (int i = 0; i < this.getNumberOfCommunities(); i++){
				List<String> members = this.getCommunity(i).getMembers();
				writer.append((i+1) + ".\tCOMMUNITY " + this.getCommunity(i).getId() + ": "+
						" " + members.size() + " tags");
				writer.newLine();
				int N = members.size() > 100 ? 100 : members.size();
				writer.append("\t");
				for (int x = 0; x < N-1; x++){
					writer.append(members.get(x).toString() + ", ");
				}
				writer.append(members.get(N-1).toString());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
