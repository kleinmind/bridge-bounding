/*
  @(#) PartitionVisualizationParameters.java	1.0,	05/07/2013
  
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for packaging the necessary parameters for visualizing a graph
 * partition into communities.
 * @author Symeon Papadopoulos
 *
 */
public class PartitionVisualizationParameters {

	private static final Color[] COLOR_PRIMITIVES = {
		Color.BLACK, Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE,
		Color.GRAY, Color.ORANGE, Color.PINK, Color.DARK_GRAY, Color.WHITE, Color.MAGENTA, 
		Color.CYAN, Color.LIGHT_GRAY
	};
	private static final Shape[] SHAPE_PRIMITIVES = {
		new Ellipse2D.Double(-5.0,  -5.0, 10.0, 10.0),
		new Rectangle2D.Double(-5.0, -5.0, 10.0, 10.0)
	};
	private static final float[] PHASE = {5.0f, 8.0f};
	
	/* List of community visualization */
	private List<CommunityVisualizationParameters> communityVizualizationParams;
	/* Stroke of edges that lie between different communities. */
	private Stroke interCommunityStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_BEVEL, 1.0f, PHASE, 1.0f);
	/* Color of edges that lie between different communities. */
	private Color interCommunityColor = Color.GRAY;
	
	/* Color of nodes which are not assigned to any community */
	private Color nonAssignedNodeColor = Color.WHITE;
	/* Shape of nodes which are not assigned to any community */
	private Shape nonAssignedNodeShape = new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0);
	/* Stroke used to draw edges between non-assigned nodes. */
	private Stroke nonAssignedEdgeStroke = new BasicStroke(1.0f);
	/* Color used to draw edges between non-assigned nodes. */
	private Color nonAssignedEdgeColor = Color.GRAY;
	
	/**
	 * Utility method for plotting with a preselected set of parameters.
	 * 
	 * @param n Number of communities to plot.
	 * @return A list of visualization parameters.
	 */
	public static PartitionVisualizationParameters
		getDefaultPartitionVisualizationParameters(int n){
		
		if (n > COLOR_PRIMITIVES.length * SHAPE_PRIMITIVES.length){
			//throw new IllegalArgumentException("Such a long list of default vizualization parameters is not supported!");
			n = COLOR_PRIMITIVES.length * SHAPE_PRIMITIVES.length;
		}
		
		PartitionVisualizationParameters partitionVisualizationParameters = 
			new PartitionVisualizationParameters();
		
		List<CommunityVisualizationParameters> params = 
			new ArrayList<CommunityVisualizationParameters>(n);
		
		for (int i = 0; i < COLOR_PRIMITIVES.length; i++){
			CommunityVisualizationParameters p = new CommunityVisualizationParameters();
			p.setColor(COLOR_PRIMITIVES[i]);
			p.setShape(SHAPE_PRIMITIVES[i%SHAPE_PRIMITIVES.length]);
			params.add(p);
			if (params.size()>=n){
				break;
			}
		}
		partitionVisualizationParameters.setCommunityVizualizationParams(params);
		return partitionVisualizationParameters;
	}

	public List<CommunityVisualizationParameters> getCommunityVizualizationParams() {
		return communityVizualizationParams;
	}
	public void setCommunityVizualizationParams(List<CommunityVisualizationParameters> vizParams) {
		this.communityVizualizationParams = vizParams;
	}

	public Stroke getInterCommunityStroke() {
		return interCommunityStroke;
	}
	public void setInterCommunityStroke(Stroke intraCommunityStroke) {
		this.interCommunityStroke = intraCommunityStroke;
	}

	public Color getInterCommunityColor() {
		return interCommunityColor;
	}
	public void setInterCommunityColor(Color interCommunityColor) {
		this.interCommunityColor = interCommunityColor;
	}

	public Color getNonAssignedNodeColor() {
		return nonAssignedNodeColor;
	}
	public void setNonAssignedNodeColor(Color nonAssignedNodeColor) {
		this.nonAssignedNodeColor = nonAssignedNodeColor;
	}

	public Shape getNonAssignedNodeShape() {
		return nonAssignedNodeShape;
	}
	public void setNonAssignedNodeShape(Shape nonAssignedNodeShape) {
		this.nonAssignedNodeShape = nonAssignedNodeShape;
	}

	public Color getNonAssignedEdgeColor() {
		return nonAssignedEdgeColor;
	}

	public void setNonAssignedEdgeColor(Color nonAssignedEdgeColor) {
		this.nonAssignedEdgeColor = nonAssignedEdgeColor;
	}

	public Stroke getNonAssignedEdgeStroke() {
		return nonAssignedEdgeStroke;
	}

	public void setNonAssignedEdgeStroke(Stroke nonAssignedEdgeStroke) {
		this.nonAssignedEdgeStroke = nonAssignedEdgeStroke;
	}
}
