/*
  @(#) CommunityVisualizationParameters.java	1.0,	05/07/2013
  
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

/**
 * Class for representing visualization parameters for a single community.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class CommunityVisualizationParameters {

	/* Color used for the nodes of the community. */
	private Color color = Color.BLACK;
	/* Shape used for the nodes of the community */
	private Shape shape = new Ellipse2D.Double(-6.0,  -6.0, 12.0, 12.0);
	/* Stroke used for the connections between the nodes of a community. */
	private Stroke edgeStroke = new BasicStroke(1.0f);
	/* Color for intra-community edges. */
	private Color edgeColor = Color.BLACK;

	
	public CommunityVisualizationParameters(){
	}
	public CommunityVisualizationParameters(Color c, Shape s){
		this.color = c;
		this.shape = s;
	}
	
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Shape getShape() {
		return shape;
	}
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	
	public Stroke getEdgeStroke() {
		return edgeStroke;
	}
	public void setEdgeStroke(Stroke edgeStroke) {
		this.edgeStroke = edgeStroke;
	}
	
	public Color getEdgeColor() {
		return edgeColor;
	}
	public void setEdgeColor(Color edgeColor) {
		this.edgeColor = edgeColor;
	}
	
}
