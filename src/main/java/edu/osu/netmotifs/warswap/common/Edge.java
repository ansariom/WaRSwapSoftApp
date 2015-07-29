package edu.osu.netmotifs.warswap.common;

/** Copyright (C) 2015 
 * @author Mitra Ansariola 
 * 
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Contact info:  megrawm@science.oregonstate.edu

 */

public class Edge {
	private Vertex sourceV;
	private Vertex targetV;
	
	public Edge() {
	}
	public Edge(Vertex srcV, Vertex tgtV) {
		setSourceV(srcV);
		setTargetV(tgtV);
	}
	public Vertex getSourceV() {
		return sourceV;
	}
	public void setSourceV(Vertex sourceV) {
		this.sourceV = sourceV;
	}
	public Vertex getTargetV() {
		return targetV;
	}
	public void setTargetV(Vertex targetV) {
		this.targetV = targetV;
	}
	
	@Override
	public String toString() {
		return sourceV + "_" + targetV;
	}
	
	public static String getEdgeStr(Vertex sourceVertex, Vertex targetVertex) {
		return sourceVertex.getLabel() + "_" + targetVertex.getLabel();
	}
	
}
