/*
  @(#) IndexableVertex.java	1.0,	01/12/2008
  
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

/**
 * IndexableVertex is used instead of a simple vertex in a graph
 * when there is a need for looking-up vertices in the graph based
 * on some key.
 * 
 * @version 1.0 18/12/2008
 * @author Symeon Papadopoulos
 *
 */
public class StringIndexableVertex {

	private String index = null;

	public StringIndexableVertex(String idx){
		this.index = idx;
	}
	
	public String getID(){
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StringIndexableVertex other = (StringIndexableVertex) obj;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return index;
	}
	
	
}
