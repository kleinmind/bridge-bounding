/*
  @(#) SimpleSyntheticTest.java	1.0,	05/07/2013
  
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

import viz.CommunityVisualization;

import jung.StringIndexableVertex;
import jung.WalkableWeightedEdge;
import graph.Community;
import graph.CommunityFactory;
import graph.GraphPartition;
import graph.SyntheticCommunityParameters;

public class SimpleSyntheticTest {

	
	public static void partitionVizualizationTest(){
		/* Generate a synthetic community mixture. */
		CommunityFactory cf = new CommunityFactory();
		GraphPartition<StringIndexableVertex, WalkableWeightedEdge> partition = cf.
			generateCommunityMixture(SyntheticCommunityParameters.getDefault());
		CommunityVisualization<StringIndexableVertex, WalkableWeightedEdge> viz =
				new CommunityVisualization<StringIndexableVertex, WalkableWeightedEdge>();
		viz.plotPartition(partition);
	}
	
	public static void syntheticTest(){
		/* Generate a synthetic community mixture. */
		CommunityFactory cf = new CommunityFactory();
		GraphPartition<StringIndexableVertex, WalkableWeightedEdge> partition = cf.
			generateCommunityMixture(SyntheticCommunityParameters.getDefault());
		
		/* Identify the local communities containing an arbitrary input node from each reference community. */
		List<StringIndexableVertex> testVertices = new ArrayList<StringIndexableVertex>();
		for (Community<StringIndexableVertex, WalkableWeightedEdge> community : partition.getCommunities()){
			testVertices.add(new StringIndexableVertex(community.getMembers().get(0)));
		}
		
		LocalCommunityDetector<StringIndexableVertex, WalkableWeightedEdge> bbcd = 
				new BridgeBoundingDetector<StringIndexableVertex, WalkableWeightedEdge>(NetworkTopologyMeasures.ELB2, 0.5);
		
		for (StringIndexableVertex v : testVertices){
			Community<StringIndexableVertex, WalkableWeightedEdge> foundCommunity = 
					bbcd.getCommunity(partition.getReferenceGraph(), v);	
			
			System.out.println("Reference community for vertex " + v + ": " + partition.getCommunity(partition.getVertexCommunityIndex(v)).getMembers());
			System.out.println("Detected community for vertex " + v + ": " + foundCommunity.getMembers());
		}
		
	}
	
	public static void main(String[] args) {
		syntheticTest();
	}
}
