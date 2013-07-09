/*
  @(#) CommunityFactory.java	1.0,	05/07/2013
  
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
import java.util.Iterator;
import java.util.List;

import jung.WalkableWeightedEdge;
import jung.StringIndexableVertex;
import jung.IndexableGraph;
import jung.IndexableUndirectedSparseGraph;

/**
 * Class used for the parameterized generation of synthetic graphs. 
 * @author papadop
 *
 */
public class CommunityFactory {

	/**
	 * Create a graph consisting of a predetermined number of nodes connected with an 
	 * average density (quantified with the p parameter as in the 
	 * Erdos-Renyi graph generation model).
	 * 
	 * @param nrNodes Number of nodes of the synthetic graph.
	 * @param density Probability that an edge will occur between two arbitrary nodes. 
	 * @return Graph.
	 */
	public IndexableUndirectedSparseGraph<StringIndexableVertex, WalkableWeightedEdge> 
			generateGraph(int nrNodes, double density){
		if ( (density < 0.0) || (density > 1.0) ){
			throw new IllegalArgumentException(
					"Community density should lie in the interval [0.0,1.0].");
		}
		
		IndexableUndirectedSparseGraph<StringIndexableVertex, WalkableWeightedEdge> g = 
			new IndexableUndirectedSparseGraph<StringIndexableVertex, WalkableWeightedEdge>();
		if (nrNodes < 1){
			return g;
		}
		
		List<StringIndexableVertex> vertices = new ArrayList<StringIndexableVertex>(nrNodes);
		VertexFactory tagFactory = new VertexFactory();
		
		for (int i = 0; i < nrNodes; i++){
			StringIndexableVertex t = tagFactory.createRandomIndexableVertex();
			g.addVertex(t);
			vertices.add(t);
		}
		
		double thres = 1.0 - density;
		for (int i = 0; i < vertices.size()-1; i++){
			for (int j = i+1; j < vertices.size(); j++){
				double p = Math.random();
				if (p >= thres){
					List<StringIndexableVertex> endPoints = new ArrayList<StringIndexableVertex>(2);
					endPoints.add(vertices.get(i));
					endPoints.add(vertices.get(j));
					g.addEdge(new WalkableWeightedEdge(1), endPoints);
				}
			}
		}
		
		return g;
	}
	
	/**
	 * Create a graph which contains a synthetic community structure. 
	 * 
	 * @param SyntheticCommunityParameters Parameters of the synthetic community mixture.
	 * @return GraphPartition	A synthetic community mixture.
	 */
	public GraphPartition<StringIndexableVertex, WalkableWeightedEdge> generateCommunityMixture(SyntheticCommunityParameters params){
		
		int avgSize = params.getNrNodes() / params.getNrCommunities();
		int minNodes = (int)Math.round(2*avgSize / (1.0+params.getSizeVariation()));
		int maxNodes = (int)Math.round(2*avgSize * params.getSizeVariation() / (1.0+params.getSizeVariation()) );
		
		GraphPartition<StringIndexableVertex, WalkableWeightedEdge> partition = new GraphPartition<StringIndexableVertex, WalkableWeightedEdge>();
		
		IndexableUndirectedSparseGraph<StringIndexableVertex, WalkableWeightedEdge> fullGraph = 
			new IndexableUndirectedSparseGraph<StringIndexableVertex, WalkableWeightedEdge>();
		List<Community<StringIndexableVertex, WalkableWeightedEdge>> communities = new ArrayList<Community<StringIndexableVertex, WalkableWeightedEdge>>(params.getNrCommunities());
		
		int countNodesSoFar = 0;
		int[] outDegrees = new int[params.getNrCommunities()];
		double[] outDegreePercent = new double[params.getNrCommunities()];
		
		for (int i = 0; i < params.getNrCommunities(); i++){
			
			int comNodes = minNodes + (int)Math.round(Math.random()*(maxNodes-minNodes));
			int totDegree = params.getMinTotDegree() + (int)Math.random()*(params.getMaxTotDegree()-params.getMinTotDegree());
			double outDensityPercentage = params.getMinPout() + Math.random()*(params.getMaxPout()-params.getMinPout());
			outDegrees[i] = (int)Math.round(outDensityPercentage * totDegree);
			outDegreePercent[i] = outDensityPercentage;
			
			if (i == params.getNrCommunities() - 1){
				comNodes = params.getNrNodes() - countNodesSoFar;
				if (comNodes < 1){
					System.err.println("WARNING: The last community was forced to have 1 node!");
					comNodes = 1;
				}
			}
			double inDensity = (double)(totDegree-outDegrees[i])/(double)comNodes;
			if (inDensity > 1.0){
				System.err.println("WARNING: Impossible community mixture specification!");
				inDensity = 1.0;
			}
			
			
			IndexableGraph<StringIndexableVertex, WalkableWeightedEdge> g = generateGraph(comNodes, inDensity);
			countNodesSoFar += comNodes;
			
			// add vertices to the full graph
			List<StringIndexableVertex> memberList = new ArrayList<StringIndexableVertex>(g.getVertices());
			for (int x = 0; x < memberList.size(); x++){
				fullGraph.addVertex(memberList.get(x));
			}
			// add edges of current community to the full graph
			for (int x = 0; x < memberList.size(); x++){
				Iterator<StringIndexableVertex> tIter = g.getNeighbors(memberList.get(x)).iterator();
				while (tIter.hasNext()){
					StringIndexableVertex toAdd = tIter.next();
					WalkableWeightedEdge e = fullGraph.findEdge(memberList.get(x), toAdd);
					if (e == null){
						List<StringIndexableVertex> endPoints = new ArrayList<StringIndexableVertex>(2);
						endPoints.add(memberList.get(x));
						endPoints.add(toAdd);
						fullGraph.addEdge(g.findEdge(memberList.get(x), toAdd), endPoints);
					}
				}
			}
			
			communities.add(
					new Community<StringIndexableVertex, WalkableWeightedEdge>(i, fullGraph, new ArrayList<StringIndexableVertex>(g.getVertices())));
			//offset += params.getNrNodes();
			
		}
		
		partition.setReferenceGraph(fullGraph);
		partition.setCommunities(communities);
		
		// add edges between communities
		for (int i = 0; i < params.getNrCommunities(); i++){
			List<String> memberIdList = communities.get(i).getMembers();
			//int nrOutNodes = fullGraph.getVertexCount() - tagList.size();
			//double pToHitOutNode = (double)outDegrees[i]/(double)(nrOutNodes);
			double pToHitOutNode = outDegreePercent[i];
			
			for (int x = 0; x < memberIdList.size(); x++){
				StringIndexableVertex thisVertex = fullGraph.getVertex(memberIdList.get(x));
				
				int countOutLinks = 0;
				// we must connect this vertex with another vertex of the graph
				// which doesn't belong to the community and is not already connected
				// to it
				Iterator<StringIndexableVertex> vertexIter = fullGraph.getVertices().iterator();
				while (vertexIter.hasNext()){
					StringIndexableVertex temp = vertexIter.next();
					
					// we don't want to connect it to nodes within the same community
					if (communities.get(i).contains(temp)){
						continue;
					}
					// we don't want to connect it with an already connected tag
					if (fullGraph.findEdge(thisVertex, temp) != null){
						continue;
					}
					double hitThres = 1.0 - pToHitOutNode;
					if (Math.random()>hitThres){
						List<StringIndexableVertex> endPoints = new ArrayList<StringIndexableVertex>(2);
						endPoints.add(thisVertex);
						endPoints.add(temp);
						fullGraph.addEdge(new WalkableWeightedEdge(1), endPoints);
						countOutLinks++;
						if (countOutLinks >= outDegrees[i]){
							break;
						}
					}
				}
			}
		}
		return partition;
	}
	
	/**
	 * Test the community generation process by creating a community mixture
	 * and visualizing it.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CommunityFactory cf = new CommunityFactory();
		GraphPartition<StringIndexableVertex, WalkableWeightedEdge> syntheticPartition = cf.
			generateCommunityMixture(SyntheticCommunityParameters.getDefault());
		syntheticPartition.writePartitionToFile("test.partition");
	}
}
