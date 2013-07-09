/*
  @(#) VertexFactory.java	1.0,	05/07/2013
  
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

import jung.StringIndexableVertex;

/**
 * Class that generates unique vertices.
 * 
 * @author Symeon Papadopoulos
 *
 */
public class VertexFactory implements RandomIndexableVertexFactory {

	private static long counter = 0;

	/**
	 * Create a new vertex object that is different from the previously generated ones.
	 */
	public StringIndexableVertex createRandomIndexableVertex() {
		
		StringIndexableVertex t = new StringIndexableVertex(String.valueOf(counter));
		counter++;
		return t;
	}

	/**
	 * Test the results of the tag factory.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		VertexFactory tfFactory = new VertexFactory();
		System.out.println(tfFactory.createRandomIndexableVertex());
		System.out.println(tfFactory.createRandomIndexableVertex());
	}

}
