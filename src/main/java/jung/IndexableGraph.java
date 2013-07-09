/*
  @(#) IndexableGraph.java	1.0,	01/12/2008
  
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
package jung;

import edu.uci.ics.jung.graph.Hypergraph;

/**
 * 
 *
 * @author Symeon Papadopoulos
 *
 * @param <V>
 * @param <E>
 */
public interface IndexableGraph<V, E> extends Hypergraph<V,E>{

	public V getVertex(String id);
	public boolean removeVertex(String id);
	
}
