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

package edu.osu.netmotifs.warswap.ui;

import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;


public class TransformerMotif implements Transformer<Integer, Point2D> {
	private int motifSize = 3;
	
	public TransformerMotif(int motifSize) {
		this.motifSize = motifSize;
	}

	@Override
	public Point2D transform(Integer vertex) {
		if (motifSize <=3 ) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(60, 20);
			case 1:
				return new Point2D.Double(30, 80);
			case 2:
				return new Point2D.Double(90, 80);
				
			}
		} else if (motifSize == 4) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(30, 20);
			case 1:
				return new Point2D.Double(90, 20);
			case 2:
				return new Point2D.Double(30, 80);
			case 3:
				return new Point2D.Double(90, 80);
			}
		} else if (motifSize == 5) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(60, 15);
			case 1:
				return new Point2D.Double(30, 45);
			case 2:
				return new Point2D.Double(90, 45);
			case 3:
				return new Point2D.Double(30, 85);
			case 4:
				return new Point2D.Double(90, 85);
			}
		} else if (motifSize == 6) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(15, 20);
			case 1:
				return new Point2D.Double(55, 20);
			case 2:
				return new Point2D.Double(95, 20);
			case 3:
				return new Point2D.Double(15, 80);
			case 4:
				return new Point2D.Double(55, 80);
			case 5:
				return new Point2D.Double(95, 80);
			}
		} else if (motifSize == 7) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(35, 15);
			case 1:
				return new Point2D.Double(75, 15);
			case 2:
				return new Point2D.Double(15, 45);
			case 3:
				return new Point2D.Double(55, 45);
			case 4:
				return new Point2D.Double(95, 45);
			case 5:
				return new Point2D.Double(35, 85);
			case 6:
				return new Point2D.Double(75, 85);
			}
		}else if (motifSize == 8) {
			switch (vertex.intValue()) {
			case 0:
				return new Point2D.Double(35, 15);
			case 1:
				return new Point2D.Double(75, 15);
			case 2:
				return new Point2D.Double(15, 45);
			case 3:
				return new Point2D.Double(55, 45);
			case 4:
				return new Point2D.Double(95, 45);
			case 5:
				return new Point2D.Double(15, 85);
			case 6:
				return new Point2D.Double(55, 85);
			case 7:
				return new Point2D.Double(95, 85);
			}
		}
		return null;
	}

}
