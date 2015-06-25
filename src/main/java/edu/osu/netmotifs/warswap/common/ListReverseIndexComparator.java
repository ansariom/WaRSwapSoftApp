package edu.osu.netmotifs.warswap.common;

import java.util.Comparator;
import java.util.List;

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

public class ListReverseIndexComparator implements Comparator<Integer> {
	private final List<Integer> array;

	public ListReverseIndexComparator(List<Integer> list) {
		array = list;
	}

	public Integer[] createIndexArray() {
		Integer[] indexes = new Integer[array.size()];
		for (int i = 0; i < array.size(); i++) {
			indexes[i] = i; // Autoboxing
		}
		return indexes;
	}

	@Override
	public int compare(Integer index1, Integer index2) {
		// Autounbox from Integer to int to use as array indexes
		return -1 * array.get(index1).compareTo(array.get(index2));
	}
}
