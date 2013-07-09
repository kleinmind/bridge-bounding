/*
 * @(#) CommunityLayout.java	1.0,	08/07/2013
 * 
 * Bridge Bounding, https://github.com/kleinmind/bridge-bounding
 */
/*
  @(#) CommunityLayout.java	1.0,	05/07/2013
  
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
package viz;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import graph.Community;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

public class CommunityLayout<V extends StringIndexableVertex, E> extends AbstractLayout<V, E> {

	List<Community<V, E>> communities = null;
	
	public CommunityLayout(IndexableUndirectedSparseGraph<V, E> g, 
			List<Community<V,E>> communities){
		super(g);
		this.communities = communities;
	}


	public void initialize() {
		if (this.communities == null) return;
		int N = communities.size();
		int Nh = (int)Math.floor(Math.sqrt((double)N));
		int Nw = N/Nh;
		if (Nw*Nh < N) Nw++;
		
		IndexableUndirectedSparseGraph<V, E> graph = (IndexableUndirectedSparseGraph<V, E>)getGraph();
		Dimension d = getSize();
		if (graph != null && d != null){
			Iterator<V> vIter = graph.getVertices().iterator();
			List<V> vertices = new ArrayList<V>(graph.getVertexCount());
			while (vIter.hasNext()){
				vertices.add(vIter.next());
			}
			Map<String, Integer> communityMap = new HashMap<String,Integer>();
			for (int i = 0; i < communities.size(); i++){
				List<String> members = communities.get(i).getMembers();
				for (int x = 0; x < members.size(); x++){
					communityMap.put(members.get(x),i);
				}
			}
			
			double W = d.getWidth();
			double H = d.getHeight();
			
			double rW = W/Nw;
			double rH = H/Nh;
			
			for (int i = 0; i < vertices.size(); i++){
				Point2D coord = transform(vertices.get(i));
				/* estimate center of community rectangle */
				Integer cIdx = communityMap.get(vertices.get(i).getID());
				int I = cIdx % Nw;
				if (I < 0) I = Nw-1;
				int J = cIdx / Nw;
				
				double x = I*rW + 0.5*rW + 0.9*(Math.random()-0.5)*rW;
				double y = J*rH + 0.5*rH + 0.9*(Math.random()-0.5)*rH;
				coord.setLocation(x, y);
			}
		}
	}


	public void reset() {
		initialize();
	}

}
