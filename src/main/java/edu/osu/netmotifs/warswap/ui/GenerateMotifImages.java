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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.CreateDirectory;
import edu.osu.netmotifs.warswap.common.ScreenImage;
import edu.osu.netmotifs.warswap.common.Utils;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GenerateMotifImages {

	private HashMap<Integer, Color> colorsHash;
	private HashMap<Integer, Color> colPerGraphHash;
	private String motifsFile;
	private String imagesOutDir;
	private int motifSize = 3;
	private List<Graph<Integer, String>> graphList = new ArrayList<Graph<Integer, String>>();
	private String imageOutFile;
	private String htmOutFile;
	private String relativePathToImage;
	private int runstatus = 0;

	public GenerateMotifImages(HashMap<Integer, Color> colorHash,
			String motifsFile, int motifSize, String htmOutFile) {
		this.colorsHash = colorHash;
		this.motifsFile = motifsFile;
		this.motifSize = motifSize;
//		imagesOutDir = new File(motifsFile).getParent() + CONF.DIR_SEP + "images";
		imagesOutDir = new File(htmOutFile).getParent() + CONF.DIR_SEP + "images";
		CreateDirectory.createDir(imagesOutDir);
		this.htmOutFile = htmOutFile;
	}

	public GenerateMotifImages() {
		// TODO Auto-generated constructor stub
	}

	public void generateImagesSize3(Graph<Integer, String> g, String fileName) {
		Transformer<Integer, Point2D> locationTransformer = new TransformerMotif(motifSize);
		Layout<Integer, String> layout = new StaticLayout<Integer, String>(g,
				locationTransformer);
		layout.setSize(new Dimension(100, 100));
		BasicVisualizationServer<Integer, String> vv = new BasicVisualizationServer<Integer, String>(
				layout);
		vv.setPreferredSize(new Dimension(120, 120));
		Transformer<Integer, Paint> vertexPaint = new Transformer<Integer, Paint>() {
			public Paint transform(Integer i) {
				return colPerGraphHash.get(i);
			}
		};
		Transformer<Integer, Paint> vertexDrawPaint = new Transformer<Integer, Paint>() {
			public Paint transform(Integer i) {
				return colPerGraphHash.get(i);
			}
		};
		Transformer<Integer, Shape> vertexSize = new Transformer<Integer, Shape>() {
			public Shape transform(Integer i) {
				Ellipse2D circle = new Ellipse2D.Double(-10, -10, 20, 20);
				return AffineTransform.getScaleInstance(0.6, 0.6)
						.createTransformedShape(circle);
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		vv.getRenderContext().setVertexDrawPaintTransformer(vertexDrawPaint);
		vv.getRenderContext().setVertexShapeTransformer(vertexSize);

		JPanel jPanel = new JPanel();
		jPanel.add(vv);
		BufferedImage bufImage = null;
		bufImage = ScreenImage.createImage((JComponent) jPanel);
		
		imageOutFile = imagesOutDir + CONF.DIR_SEP + fileName + ".png";
		relativePathToImage = "images" + CONF.DIR_SEP + fileName + ".png"; 
		try {
			File outFile = new File(imageOutFile);
			ImageIO.write(bufImage, "png", outFile);
//			System.out.println("wrote image to " + outFile);
		} catch (Exception e) {
			System.err.println("writeToImageFile(): " + e.getMessage());
		}
	}

	public void createHtm(float zScoreCutoff,float pvalueCutoff,  int recPerPage, boolean referesh) throws Exception {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(motifsFile));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			colPerGraphHash = new HashMap<Integer, Color>();
			String headStr= "<head><style>\ntable, th, td {\nborder: 1px solid black;\nborder-collapse: collapse;\ntext-align: center;}\n</style></head>";
			String tableStr = "<table style=\"width:50%\"><tr bgcolor=\"#F1F1F1\"><th>Image</th><th>Z-score</th><th>P-Value</th><th>std-dev</th><th>Adj-Matrix</th></tr>";
			String seperator = "\t";
			if (motifsFile.endsWith("csv"))
				seperator = ",";
			
			line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
//				Thread.sleep(1000);
//				System.out.println(line);
				String[] parts = line.split(seperator);
				String adjMtx = parts[0];
				//String graphPart = parts[0].split("_")[1];
				Graph<Integer, String> g = new DirectedSparseGraph();
				int s = 0;
				colPerGraphHash.clear();
				for (int i = 0; i < motifSize; i++) {
					int vCode = Character.getNumericValue(adjMtx.charAt(i * (motifSize + 1)));
					colPerGraphHash.put(i, colorsHash.get(vCode));
					g.addVertex(i);
					for (int j = 0; j < motifSize; j++) {
						int d = Character.getNumericValue(adjMtx.charAt(i
								* motifSize + j));
						if (d == 1 && (i * motifSize + j) % (motifSize + 1) > 0)
							g.addEdge("E" + s++, i, j);
					}
				}
				graphList.add(g);
				if (!parts[1].equalsIgnoreCase(CONF.INFINIT) && !parts[2].equalsIgnoreCase(CONF.INFINIT) && !parts[3].equalsIgnoreCase(CONF.INFINIT)) {
					float zscore = Float.valueOf(parts[1]);
					float pValue = Float.valueOf(parts[2]);
					
					float stddev = Float.valueOf(parts[3]);
//				float std = Float.valueOf(parts[3]);
					
					if (zScoreCutoff != -1 || pvalueCutoff != -1) {
						if (zScoreCutoff != -1) {
							if (zscore < zScoreCutoff)
								continue;
						}
						if (pvalueCutoff != -1) {
							if (pValue > pvalueCutoff)
								continue;
						}
					}
					String imageFileName = parts[0];
					if (!referesh) {
						imageFileName += "_";
						for (int i = 0; i < colPerGraphHash.size(); i++) {
							imageFileName += colPerGraphHash.get(i);
						}
					}
					generateImagesSize3(g, imageFileName);
					File file = new File(imageOutFile);
					tableStr += "<tr>" + "<td><img src=\"" + relativePathToImage + "\" width=\"80\" height=\"80\" >" + "</td>" 
							+ "<td>" + parts[1] + "</td>" + "<td>" + parts[2] + "</td>" + "<td>" + parts[3] + "</td>" + "<td>" + adjMtx + "</td>" + "</tr>";
//					tableStr += "<tr>" + "<td><img src=\"" + imageOutFile + "\" width=\"80\" height=\"80\" >" + "</td>" 
//							+ "<td>" + parts[1] + "</td>" + "<td>" + parts[2] + "</td>" + "<td>" + parts[3] + "</td>" + "<td>" + adjMtx + "</td>" + "</tr>";
				}
			}
			Utils.printStrToFile(headStr+tableStr, htmOutFile);
			inputStream.close();
			bufferedReader.close();
			runstatus = 1;
		} catch (Exception e) {
			e.printStackTrace();
			runstatus = 1;
			throw e;
		}
	}
	
	
	public int getRunstatus() {
		return runstatus;
	}

	public void setRunstatus(int runstatus) {
		this.runstatus = runstatus;
	}

	public void generateHTML() throws IOException {
		String htmStr = "<html><head><title>Page Title</title></head><body><h1>This is a Heading</h1></body></html>";
		String tableStr = "<table style=\"width:100%\">";
		
		Utils.printStrToFile(htmStr, "d.htm");
	}

	public static void main(String[] args) {
		HashMap<Integer, Color> cHash = new HashMap<Integer, Color>();
		cHash.put(0, Color.BLUE);
		cHash.put(2, Color.BLACK);
		cHash.put(1, Color.RED);
		try {
			new GenerateMotifImages(cHash,
					"/home/mitra/workspace/uni-workspace/warswap_tool/warswap.subgraphsdddd.OUT", 3, "data/htmout.htm")
					.createHtm(1, 0, 10, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
