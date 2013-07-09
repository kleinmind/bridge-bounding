/*
  @(#) LocalCommunityDetector.java	1.0,	08/07/2013
  
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

import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

/**
 * This interface defines the functionality of classes implementing local community
 * detection, i.e. producing a single community from a seed node.
 * 
 * @author Symeon Papadopoulos
 *
 */
public interface LocalCommunityDetector<V extends StringIndexableVertex, E> {

	/**
	 * Create a community containing the given seed node which is a member of 
	 * the input graph.
	 * 
	 * @param graph Input graph where the local community detection is applied.
	 * @param seed Seed node for initiating the local community detection method.
	 * @return The identified community around the input seed node.
	 */
	public Community<V,E> getCommunity(IndexableUndirectedSparseGraph<V, E> graph, V seed);
	
}
