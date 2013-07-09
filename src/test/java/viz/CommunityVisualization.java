/*
  @(#) CommunityVisualization.java	1.0,	05/07/2013
  
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import graph.Community;
import graph.GraphPartition;

//import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import jung.IndexableUndirectedSparseGraph;
import jung.StringIndexableVertex;

public class CommunityVisualization<V extends StringIndexableVertex, E> {

	private static final Dimension VIZ_DIM = new Dimension(600, 600);
	private static boolean one2one = false;
	private static Map<Integer, Integer> communityMap = null;
	
	/**
	 * Visualize a graph partition based on given parameters.
	 * 
	 * @param graphPartition Partition to visualize.
	 * @param vizualizationParameters Visualization parameters.
	 * @return A JComponent which contains the visualization.
	 */
	public JComponent getCommunityVisualizationComponent(
			final GraphPartition<V, E> graphPartition, 
			final PartitionVisualizationParameters vizualizationParameters){
		
		final List<Community<V,E>> communities = graphPartition.getCommunities();
		final IndexableUndirectedSparseGraph<V, E> graph =
			graphPartition.getReferenceGraph();
		
		/* count the number of communities with more than one member */
		int countNonSingletons = 0;
		communityMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < communities.size(); i++){
			if (communities.get(i).getNumberOfMembers() > 1){
				countNonSingletons++;
				communityMap.put(i, countNonSingletons);
			}
		}
		
		final List<CommunityVisualizationParameters> communityVizualizationParameters =
			vizualizationParameters.getCommunityVizualizationParams();
		if (countNonSingletons >= communityVizualizationParameters.size() && communities.size()>countNonSingletons){
			throw new IllegalArgumentException(
					"Too many communities too plot with default viz parameters! " +
					communities.size() + " " + countNonSingletons + " " + 
					communityVizualizationParameters.size());
		}
		
		one2one = false;
		if (communities.size() <= communityVizualizationParameters.size()){
			one2one = true;
		}
		
		Layout<V, E> layout = new CommunityLayout<V, E>(graph, graphPartition.getCommunities());
		layout.setSize(VIZ_DIM);
		VisualizationViewer<V, E> vv = new VisualizationViewer<V, E>(layout);
		vv.setPreferredSize(new Dimension((int)VIZ_DIM.getWidth()+100,
				(int)VIZ_DIM.getWidth()+100));
		vv.setBackground(Color.WHITE);
		vv.getRenderContext().setVertexFillPaintTransformer(
				new Transformer<V, Paint>(){
			public Paint transform(V t){
				int cIndex = graphPartition.getVertexCommunityIndex(t);
				if (cIndex < 0) {
					return vizualizationParameters.getNonAssignedNodeColor(); 
				}
				if (one2one){
					return communityVizualizationParameters.get(cIndex).getColor(); 
				} else {
					if (communities.get(cIndex).getNumberOfMembers() == 1) {
						return communityVizualizationParameters.get(0).getColor();
					} else {
						if (communityMap.get(cIndex) >= communityVizualizationParameters.size()){
							return vizualizationParameters.getNonAssignedNodeColor();
						}
						return communityVizualizationParameters.
							get(communityMap.get(cIndex)).getColor();
					}
				}
			}
		});
		vv.getRenderContext().setVertexShapeTransformer(
				new Transformer<V, Shape>(){
			public Shape transform(V t){
				int cIndex = graphPartition.getVertexCommunityIndex(t);
				if (cIndex < 0){
					return vizualizationParameters.getNonAssignedNodeShape();
				} else if (one2one){
					return communityVizualizationParameters.get(cIndex).getShape(); 
				}
				else {
					if (communities.get(cIndex).getNumberOfMembers()==1){
						return communityVizualizationParameters.get(0).getShape();
					} else {
						if (communityMap.get(cIndex) >= communityVizualizationParameters.size()){
							return vizualizationParameters.getNonAssignedNodeShape();
						}
						return communityVizualizationParameters.get(
								communityMap.get(cIndex)).getShape();
					}
				} 
			}
		});
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
		
		vv.getRenderContext().setEdgeStrokeTransformer(
				new Transformer<E, Stroke>(){
			public Stroke transform(E c){
				Pair<V> endPoints = graph.getEndpoints(c);
				V v1 = endPoints.getFirst();
				V v2 = endPoints.getSecond();
				int communityIndex1 = graphPartition.getVertexCommunityIndex(v1);
				int communityIndex2 = graphPartition.getVertexCommunityIndex(v2);
				
				if (communityIndex1 == communityIndex2){
					if (communityIndex1 < 0){
						return vizualizationParameters.getNonAssignedEdgeStroke();
					} else {
						if (communityIndex1 >= communityVizualizationParameters.size()){
							return vizualizationParameters.getNonAssignedEdgeStroke();
						}
						return communityVizualizationParameters.get(communityIndex1).getEdgeStroke();
					}
				} else {
					return vizualizationParameters.getInterCommunityStroke();
				}
			}
		});
		vv.getRenderContext().setEdgeDrawPaintTransformer(
				new Transformer<E, Paint>(){
			public Paint transform (E c){
				Pair<V> endPoints = graph.getEndpoints(c);
				V v1 = endPoints.getFirst();
				V v2 = endPoints.getSecond();
				int communityIndex1 = graphPartition.getVertexCommunityIndex(v1);
				int communityIndex2 = graphPartition.getVertexCommunityIndex(v2);
				
				if (communityIndex1 == communityIndex2){
					if (communityIndex1 < 0){
						return vizualizationParameters.getNonAssignedEdgeColor();
					} else {
						if (communityIndex1 >= communityVizualizationParameters.size()){
							return vizualizationParameters.getNonAssignedEdgeColor();
						}
						return communityVizualizationParameters.get(communityIndex1).getEdgeColor();
					}
				} else {
					return vizualizationParameters.getInterCommunityColor();
				}
			}
		});
		return vv;
	}
	
	/**
	 * Create a JFrame window which contains a graph visualization.
	 * 
	 * @param graphPartition Graph partition to visualize.
	 * @param vizualizationParams Visualization parameters.
	 */
	public void visualizeCommunities(	
			GraphPartition<V,E> graphPartition, 
			final PartitionVisualizationParameters vizualizationParams){
		
		JComponent vv = this.getCommunityVisualizationComponent(graphPartition, vizualizationParams);
		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	/**
	 * Visualize graph partition with default visualization parameters.
	 */
	public void plotPartition(GraphPartition<V, E> partition){
		visualizeCommunities(partition, PartitionVisualizationParameters.
				getDefaultPartitionVisualizationParameters(partition.getNumberOfCommunities()));
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
