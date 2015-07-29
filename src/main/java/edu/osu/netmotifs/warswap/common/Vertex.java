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

package edu.osu.netmotifs.warswap.common;

import java.util.HashMap;

public class Vertex {
	private int label;
	private byte color;

	private int inDegree = 0;
	HashMap<String, Integer> inDegLyrHash = new HashMap<String, Integer>();
	private int outDegree = 0;
	HashMap<String, Integer> outDegLyrHash = new HashMap<String, Integer>();
	private int usedOutDegCapasity = 0;
	private int outCap = 0;

	public int getOutCap() {
		return outCap;
	}

	public void decreaseOutCap() {
		outCap--;
	}

	public int getInDegree() {
		return inDegree;
	}

	public Vertex(int label, byte color) {
		this.label = label;
		this.color = color;
	}

	@Override
	public String toString() {
//		 return label + "_" + color + "_" + outDegree + "_" + inDegree + "_" +
//		 usedOutDegCapasity;
		return label + CONF.U_LINE + color;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}

	@Deprecated
	public int getOutDegree(byte color1, byte color2) {
		return outDegLyrHash.get(color1 + "_" + color2);
	}

	@Deprecated
	public void addOutDegree(byte color1, byte color2) {
		String key = color1 + "_" + color2;
		if (outDegLyrHash.get(key) == null)
			outDegLyrHash.put(key, 0);
		int oldValue = outDegLyrHash.get(key);
		outDegLyrHash.put(key, oldValue++);
	}

	@Deprecated
	public int getInDegree(byte color1, byte color2) {
		return inDegLyrHash.get(color1 + "_" + color2);
	}

	@Deprecated
	public void addInDegree(byte color1, byte color2) {
		String key = color1 + "_" + color2;
		if (inDegLyrHash.get(key) == null)
			inDegLyrHash.put(key, 0);
		int oldValue = inDegLyrHash.get(key);
		inDegLyrHash.put(key, oldValue++);
	}

	public int getOutDegree() {
		return outDegree;
	}

	public void setOutDegree(int outDegree) {
		this.outDegree = outDegree;
	}

	public int getUsedOutCapasity() {
		return usedOutDegCapasity;
	}

	public void setUsedCapasity(int usedCapasity) {
		this.usedOutDegCapasity = usedCapasity;
	}

	public void setInDegree(int inDegree) {
		this.inDegree = inDegree;
	}

	public void incrementUsedCapasity() {
		usedOutDegCapasity++;
	}

	public void incrementOutDeg() {
		outDegree++;
		outCap++;
	}
	public void incrementInDeg() {
		inDegree++;
	}

}
