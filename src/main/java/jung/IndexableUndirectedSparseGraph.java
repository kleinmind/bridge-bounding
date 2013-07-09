/*
  @(#) IndexableUndirectedSparseGraph.java	1.0,	01/12/2008
  
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

import java.util.HashMap;
import java.util.Map;


import edu.uci.ics.jung.graph.UndirectedSparseGraph;



/**
 * This class extends the JUNG sparse undirected graph class 
 * to support lookup of nodes by some key.
 * 
 * @author Symeon Papadopoulos
 *
 * @param <V>	Vertex
 * @param <E>	Edge
 */
public class IndexableUndirectedSparseGraph<V, E> 
	extends UndirectedSparseGraph<V,E> implements IndexableGraph<V, E> {
	
	private static final long serialVersionUID = 1L;
	
	protected Map<String,V> vertexIDs;
	
	public IndexableUndirectedSparseGraph() {
		super();
		vertexIDs = new HashMap<String,V>();
	}

	public boolean addVertex(V vertex) {
		if (super.addVertex(vertex)) {
			if (vertex instanceof StringIndexableVertex){
				vertexIDs.put(((StringIndexableVertex)vertex).getID(), vertex);
				return true;
			} else {
				return false;
			}
		}
		else
			return false;
	}
	
	public boolean removeVertex(String id) {
		V vertex = getVertex(id);
		if (vertex == null)		
			return false;
		else {
			if (removeVertex(vertex)) {
				if (vertex instanceof StringIndexableVertex){
					vertexIDs.remove(((StringIndexableVertex)vertex).getID());
		        	return true;
				} else {
					return false;
				}
			}
			else
				return false;
		}	
	}

	public V getVertex(String id) {
		return vertexIDs.get(id); 
	}


	public boolean containsVertex(V vertex) {
		if (vertex instanceof StringIndexableVertex){
			return vertexIDs.containsKey(((StringIndexableVertex)vertex).getID()); // don't compare vertex objects but vertex ids!
		} else {
			if (this.vertices.containsKey(vertex)){
				return true;
			} else {
				return false;
			}
		}
    }
	
}
