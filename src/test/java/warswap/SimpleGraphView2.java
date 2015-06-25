/*
 * SimpleGraphView.java
 *
 * Created on March 8, 2007, 7:49 PM
 *
 * Copyright March 8, 2007 Grotto Networking
 */

package warswap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 *
 * @author Dr. Greg M. Bernstein
 */
public class SimpleGraphView2 {
    Graph<Integer, String> g;
    /** Creates a new instance of SimpleGraphView */
    public SimpleGraphView2() {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        // Note showing the use of a SparseGraph rather than a SparseMultigraph
        g = new DirectedSparseGraph();
        // Add some vertices. From above we defined these to be type Integer.
        g.addVertex((Integer)1);
        g.addVertex((Integer)2);
        g.addVertex((Integer)3); 
        // g.addVertex((Integer)1);  // note if you add the same object again nothing changes
        // Add some edges. From above we defined these to be of type String
        // Note that the default is for undirected edges.
        g.addEdge("1", 1, 2); // Note that Java 1.5 auto-boxes primitives
        g.addEdge("2", 2, 3);  
        g.addEdge("3", 2, 2);  
        g.addEdge("4", 3, 1);  
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SimpleGraphView2 sgv = new SimpleGraphView2(); // This builds the graph
        // Layout<V, E>, VisualizationComponent<V,E>
        
        Transformer<Integer, Point2D> locationTransformer = new Transformer<Integer, Point2D>() {

            @Override
            public Point2D transform(Integer vertex) {
            	switch (vertex.intValue()) {
            	case 1:
            		return new Point2D.Double(60, 20);
            	case 2:
            		return new Point2D.Double(30, 80);
				case 3:
					return new Point2D.Double(90, 80);

				}
            	return null;
            }
        };
        
        Layout<Integer, String> layout = new StaticLayout<Integer, String>(sgv.g, locationTransformer);
        layout.setSize(new Dimension(100,100));
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(120,120));       
        // Setup up a new vertex to paint transformer...
        Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
        	public Paint transform(Integer i) {
        		switch (i) {
        		case 1:
        			return Color.GREEN;
        		case 2:
        			return Color.BLUE;
        		case 3:
        			return Color.RED;
        			
        		}
        		return Color.GREEN;
        	}
        };  
        Transformer<Integer,Paint> vertexDrawPaint = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
            	switch (i) {
            	case 1:
            		return Color.GREEN;
            	case 2:
            		return Color.BLUE;
				case 3:
					return Color.RED;
				}
                return Color.GREEN;
            }
        };  
        Transformer<Integer,Shape> vertexSize = new Transformer<Integer,Shape>(){
            public Shape transform(Integer i){
                Ellipse2D circle = new Ellipse2D.Double(-10, -10, 20, 20);
                // in this case, the vertex is twice as large
               return AffineTransform.getScaleInstance(0.6, 0.6).createTransformedShape(circle);
            }
        };
        // Set up a new stroke Transformer for the edges
//        float dash[] = {10.0f};
//        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
//             BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//        Transformer<String, Stroke> edgeStrokeTransformer = new Transformer<String, Stroke>() {
//            public Stroke transform(String s) {
//                return edgeStroke;
//            }
//        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
//        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);     
        vv.getRenderContext().setVertexDrawPaintTransformer(vertexDrawPaint);
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
        
        JPanel  jPanel = new JPanel();
        jPanel.add(vv);
//        JFrame frame = new JFrame("Simple Graph View 2");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(jPanel);
//        
//        frame.pack();
//        frame.setVisible(true);     
//        
        BufferedImage bufImage = null;
			bufImage = ScreenImage.createImage((JComponent)jPanel);
 	   try {
 	       File outFile = new File("im.png");
 	       ImageIO.write(bufImage, "png", outFile);
 	       System.out.println("wrote image to im.png");
 	   } catch (Exception e) {
 	       System.out.println("writeToImageFile(): " + e.getMessage());
 	   }
    }
    
    private void writeToImageFile(String imageFileName) {

//    	   BufferedImage bufImage = ScreenImage.createImage((JComponent) jPanel1);
//    	   try {
//    	       File outFile = new File(imageFileName);
//    	       ImageIO.write(bufImage, "png", outFile);
//    	       System.out.println("wrote image to " + imageFileName);
//    	   } catch (Exception e) {
//    	       System.out.println("writeToImageFile(): " + e.getMessage());
//    	   }
    	}
    
}
