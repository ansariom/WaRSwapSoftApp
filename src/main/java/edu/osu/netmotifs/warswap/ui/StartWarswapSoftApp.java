/**
 * Copyright (C) 2015 
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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.CreateDirectory;

/**
 * This class provides user interface for running WaRSwap Software Application
 * @author mitra
 * @Date 6/26/2015
 */
public class StartWarswapSoftApp extends JFrame implements ActionListener, PropertyChangeListener {
	    /**
	 * 
	 */
	private static final long serialVersionUID = -7444783256972789874L;
		/**
	     * Creates new form NewWaRSwapUI
	     */
	    public StartWarswapSoftApp() {
	    	initComponents();
	    	initActions();
	    }

	    private void initActions() {
	    	initInOutActions();
	    	initOptions();
		}
	    
	    private void initOptions() {
	    	motifSizeCombo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (motifSizeCombo.getSelectedItem().toString()
							.equalsIgnoreCase("1")
							|| motifSizeCombo.getSelectedItem().toString()
									.equalsIgnoreCase("2")) {
						selfLoopCheck.setSelected(true);
					} else {
						selfLoopCheck.setEnabled(true);
					}
				}
			});			
		}

		/**
	     * initProperties and set actions for in/out buttons
	     */
	    private void initInOutActions() {
	    	ImageIcon icon = new ImageIcon("images/browse.jpg");
	    	Image img = icon.getImage() ;  
	    	Image newimg = img.getScaledInstance( 40, 20,  java.awt.Image.SCALE_SMOOTH ) ;  
	    	icon = new ImageIcon( newimg );
	    	
	    	inVtxBtn.setText("");
	    	inVtxBtn.setIcon(icon);
			inVtxBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File(inVtxTxt.getText());
					String fPath = ".";
					if (!file.isFile() && inVtxTxt.getText() != null && !inVtxTxt.getText().equalsIgnoreCase(""))
						fPath = file.getParent();
					else 
						fPath = file.getAbsolutePath();
					JFileChooser chooser = new JFileChooser(new File(fPath));
					int returnVal = chooser.showOpenDialog(StartWarswapSoftApp.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = chooser.getSelectedFile();
						String inPath = file.getAbsolutePath();
						inVtxTxt.setText(inPath);
					}
				}
			});
			
			
			inEdgBtn.setText("");
			inEdgBtn.setIcon(icon);
			inEdgBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File(inEdgTxt.getText());
					String fPath = ".";
					if (!file.isFile() && inEdgTxt.getText() != null && !inEdgTxt.getText().equalsIgnoreCase(""))
						fPath = file.getParent();
					else 
						fPath = file.getAbsolutePath();
					
					JFileChooser chooser = new JFileChooser(new File(fPath));
					int returnVal = chooser.showOpenDialog(StartWarswapSoftApp.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = chooser.getSelectedFile();
						String inPath = file.getAbsolutePath();
						inEdgTxt.setText(inPath);
						String outPathDir = new File(".").getAbsolutePath() + CONF.DIR_SEP + CONF.MOTIFS_OUT_DIR;
						CreateDirectory.createDir(outPathDir);
						outDirTxt.setText(outPathDir);
					}
				}
			});

			outDirBtn.setText("");
			outDirBtn.setIcon(icon);
			outDirBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new File(outDirTxt.getText());
					String fPath = ".";
					if (!file.isFile() && outDirTxt.getText() != null && !outDirTxt.getText().equalsIgnoreCase(""))
						fPath = file.getParent();
					else 
						fPath = file.getAbsolutePath();
					JFileChooser chooser = new JFileChooser(new File(fPath));
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    chooser.setAcceptAllFileFilterUsed(false);
					int returnVal = chooser.showOpenDialog(StartWarswapSoftApp.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = chooser.getSelectedFile();
						outDirTxt.setText(file.getAbsolutePath());
					}

				}
			});		}

		/**
	     * This method is called from within the constructor to initialize the form.
	     * WARNING: Do NOT modify this code. The content of this method is always
	     * regenerated by the Form Editor.
	     */
	    
	    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
	    private void initComponents() {

	        mainSplitPnl = new javax.swing.JSplitPane();
	        resSplitPnl = new javax.swing.JSplitPane();
	        filterPnl = new javax.swing.JPanel();
	        zscoreCheck = new javax.swing.JCheckBox();
	        zscoreTxt = new javax.swing.JTextField();
	        pvalCheck = new javax.swing.JCheckBox();
	        pvalTxt = new javax.swing.JTextField();
	        jPanel1 = new javax.swing.JPanel();
	        jLabel1 = new javax.swing.JLabel();
	        TFBtn = new javax.swing.JButton();
	        jLabel2 = new javax.swing.JLabel();
	        mirBtn = new javax.swing.JButton();
	        jLabel3 = new javax.swing.JLabel();
	        geneBtn = new javax.swing.JButton();
	        jLabel4 = new javax.swing.JLabel();
	        sloopBtn = new javax.swing.JButton();
	        reloadBtn = new javax.swing.JButton();
	        saveHtmBtn = new javax.swing.JButton();
	        resultScrPnl = new javax.swing.JScrollPane();
	        jPanel2 = new javax.swing.JPanel();
	        jPanel3 = new javax.swing.JPanel();
	        motifSizeLbl = new javax.swing.JLabel();
	        motifSizeCombo = new javax.swing.JComboBox();
	        selfLoopCheck = new javax.swing.JCheckBox();
	        randNetLbl = new javax.swing.JLabel();
	        randNetTxt = new javax.swing.JTextField();
	        inOutPnl = new javax.swing.JPanel();
	        inVtxLbl = new javax.swing.JLabel();
	        inVtxTxt = new javax.swing.JTextField();
	        inVtxBtn = new javax.swing.JButton();
	        inEdgLbl = new javax.swing.JLabel();
	        inEdgTxt = new javax.swing.JTextField();
	        inEdgBtn = new javax.swing.JButton();
	        outDirLbl = new javax.swing.JLabel();
	        outDirTxt = new javax.swing.JTextField();
	        outDirBtn = new javax.swing.JButton();
	        jPanel4 = new javax.swing.JPanel();
	        startBtn = new javax.swing.JButton();
	        jProgressBar1 = new javax.swing.JProgressBar();
	        jScrollPane1 = new javax.swing.JScrollPane();
	        reportArea = new javax.swing.JTextArea();
	        progressLbl = new javax.swing.JLabel();
	        closeBtn = new javax.swing.JButton();
	        helpBtn = new javax.swing.JButton();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setPreferredSize(new java.awt.Dimension(1100, 700));

	        mainSplitPnl.setDividerLocation(630);

	        resSplitPnl.setDividerLocation(210);
	        resSplitPnl.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
	        resSplitPnl.setToolTipText("");
	        resSplitPnl.setPreferredSize(new java.awt.Dimension(400, 538));

	        filterPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter results"));
	        filterPnl.setPreferredSize(new java.awt.Dimension(400, 320));
	        filterPnl.setRequestFocusEnabled(false);

	        zscoreCheck.setText("Z-score greater than ");

	        zscoreTxt.setText("2.00");

	        pvalCheck.setText("P-value less than ");

	        pvalTxt.setText("0.01");

	        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Color options"));

	        jLabel1.setText("TF");

	        TFBtn.setBackground(java.awt.Color.blue);

	        jLabel2.setText("MIR");

	        mirBtn.setBackground(java.awt.Color.red);

	        jLabel3.setText("GENE");

	        geneBtn.setBackground(java.awt.Color.black);

	        jLabel4.setText("Self-loop");

	        sloopBtn.setBackground(java.awt.Color.cyan);

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addComponent(jLabel1)
	                .addGap(4, 4, 4)
	                .addComponent(TFBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jLabel2)
	                .addGap(3, 3, 3)
	                .addComponent(mirBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jLabel3)
	                .addGap(4, 4, 4)
	                .addComponent(geneBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addComponent(jLabel4)
	                .addGap(3, 3, 3)
	                .addComponent(sloopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(0, 57, Short.MAX_VALUE))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(sloopBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jLabel4)
	                    .addComponent(geneBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jLabel3)
	                    .addComponent(mirBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(jLabel1)
	                        .addComponent(TFBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addComponent(jLabel2))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        reloadBtn.setText("Refresh");

	        saveHtmBtn.setText("Save");

	        javax.swing.GroupLayout filterPnlLayout = new javax.swing.GroupLayout(filterPnl);
	        filterPnl.setLayout(filterPnlLayout);
	        filterPnlLayout.setHorizontalGroup(
	            filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(filterPnlLayout.createSequentialGroup()
	                .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(filterPnlLayout.createSequentialGroup()
	                        .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(zscoreCheck)
	                            .addComponent(pvalCheck))
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                            .addComponent(zscoreTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addComponent(pvalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(0, 12, Short.MAX_VALUE))
	            .addGroup(filterPnlLayout.createSequentialGroup()
	                .addGap(0, 0, Short.MAX_VALUE)
	                .addComponent(reloadBtn)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(saveHtmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
	        );
	        filterPnlLayout.setVerticalGroup(
	            filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(filterPnlLayout.createSequentialGroup()
	                .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(zscoreCheck)
	                    .addComponent(zscoreTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(10, 10, 10)
	                .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(pvalCheck)
	                    .addComponent(pvalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(filterPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(saveHtmBtn)
	                    .addComponent(reloadBtn))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        resSplitPnl.setTopComponent(filterPnl);

	        resultScrPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Result browser"));
	        resSplitPnl.setRightComponent(resultScrPnl);

	        mainSplitPnl.setRightComponent(resSplitPnl);

	        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

	        motifSizeLbl.setText("Motif size");

	        motifSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7" }));
	        motifSizeCombo.setSelectedIndex(2);
	        motifSizeCombo.setToolTipText("");

	        selfLoopCheck.setSelected(true);
	        selfLoopCheck.setText("Consider self-loops");

	        randNetLbl.setText("No of random networks");

	        randNetTxt.setColumns(5);
	        randNetTxt.setText("2500");

	        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
	        jPanel3.setLayout(jPanel3Layout);
	        jPanel3Layout.setHorizontalGroup(
	            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel3Layout.createSequentialGroup()
	                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(jPanel3Layout.createSequentialGroup()
	                        .addComponent(motifSizeLbl)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(motifSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(96, 96, 96)
	                        .addComponent(selfLoopCheck))
	                    .addGroup(jPanel3Layout.createSequentialGroup()
	                        .addComponent(randNetLbl)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(randNetTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addGap(0, 0, Short.MAX_VALUE))
	        );
	        jPanel3Layout.setVerticalGroup(
	            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel3Layout.createSequentialGroup()
	                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(motifSizeLbl)
	                    .addComponent(motifSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(selfLoopCheck))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(randNetLbl)
	                    .addComponent(randNetTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addGap(0, 0, Short.MAX_VALUE))
	        );

	        inOutPnl.setBorder(javax.swing.BorderFactory.createTitledBorder("Input/output "));

	        inVtxLbl.setText("Vertex file");

	        inVtxTxt.setColumns(30);

	        inVtxBtn.setText("jButton1");

	        inEdgLbl.setText("Edge file");

	        inEdgTxt.setColumns(30);

	        inEdgBtn.setText("jButton1");

	        outDirLbl.setText("Output directory");

	        outDirTxt.setColumns(30);

	        outDirBtn.setText("jButton1");

	        javax.swing.GroupLayout inOutPnlLayout = new javax.swing.GroupLayout(inOutPnl);
	        inOutPnl.setLayout(inOutPnlLayout);
	        inOutPnlLayout.setHorizontalGroup(
	            inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(inOutPnlLayout.createSequentialGroup()
	                .addGroup(inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addGroup(inOutPnlLayout.createSequentialGroup()
	                        .addComponent(outDirLbl)
	                        .addGap(2, 2, 2)
	                        .addComponent(outDirTxt))
	                    .addGroup(inOutPnlLayout.createSequentialGroup()
	                        .addComponent(inVtxLbl)
	                        .addGap(3, 3, 3)
	                        .addComponent(inVtxTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(inOutPnlLayout.createSequentialGroup()
	                        .addComponent(inEdgLbl)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(inEdgTxt)))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(inVtxBtn)
	                    .addComponent(inEdgBtn)
	                    .addComponent(outDirBtn)))
	        );
	        inOutPnlLayout.setVerticalGroup(
	            inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(inOutPnlLayout.createSequentialGroup()
	                .addGroup(inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(inVtxLbl)
	                    .addComponent(inVtxTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(inVtxBtn))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(inEdgLbl)
	                    .addComponent(inEdgTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(inEdgBtn))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(inOutPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(outDirLbl)
	                    .addComponent(outDirTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(outDirBtn))
	                .addGap(0, 0, Short.MAX_VALUE))
	        );

	        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Run motif discovery"));

	        startBtn.setText("Start ");

	        reportArea.setColumns(20);
	        reportArea.setRows(5);
	        jScrollPane1.setViewportView(reportArea);

	        progressLbl.setText("1 out of 100 networks processed.");

	        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
	        jPanel4.setLayout(jPanel4Layout);
	        jPanel4Layout.setHorizontalGroup(
	            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel4Layout.createSequentialGroup()
	                .addGap(18, 18, 18)
	                .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(progressLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap(77, Short.MAX_VALUE))
	            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
	        );
	        jPanel4Layout.setVerticalGroup(
	            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel4Layout.createSequentialGroup()
	                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(jPanel4Layout.createSequentialGroup()
	                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(progressLbl))
	                    .addGroup(jPanel4Layout.createSequentialGroup()
	                        .addGap(6, 6, 6)
	                        .addComponent(startBtn)))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
	        );

	        closeBtn.setText("Close");

	        helpBtn.setText("Help");

	        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	        jPanel2.setLayout(jPanel2Layout);
	        jPanel2Layout.setHorizontalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(inOutPnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
	                        .addGap(0, 0, Short.MAX_VALUE)
	                        .addComponent(helpBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(closeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap())
	        );
	        jPanel2Layout.setVerticalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(inOutPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(closeBtn)
	                    .addComponent(helpBtn))
	                .addGap(0, 0, Short.MAX_VALUE))
	        );

	        mainSplitPnl.setLeftComponent(jPanel2);

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(mainSplitPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(mainSplitPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
	        );

	        pack();
	    }// </editor-fold>   

	    /**
	     * @param args the command line arguments
	     */
	    public static void main(String args[]) {
	        /* Set the Nimbus look and feel */
	        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
	        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
	         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
	         */
	        try {
	            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        } catch (ClassNotFoundException ex) {
	            java.util.logging.Logger.getLogger(StartWarswapSoftApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (InstantiationException ex) {
	            java.util.logging.Logger.getLogger(StartWarswapSoftApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (IllegalAccessException ex) {
	            java.util.logging.Logger.getLogger(StartWarswapSoftApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	            java.util.logging.Logger.getLogger(StartWarswapSoftApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
	        //</editor-fold>

	        /* Create and display the form */
	        java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new StartWarswapSoftApp().setVisible(true);
	            }
	        });
	    }

	    // Variables declaration - do not modify                     
	    private javax.swing.JButton TFBtn;
	    private javax.swing.JButton closeBtn;
	    private javax.swing.JPanel filterPnl;
	    private javax.swing.JButton geneBtn;
	    private javax.swing.JButton helpBtn;
	    private javax.swing.JButton inEdgBtn;
	    private javax.swing.JLabel inEdgLbl;
	    private javax.swing.JTextField inEdgTxt;
	    private javax.swing.JPanel inOutPnl;
	    private javax.swing.JButton inVtxBtn;
	    private javax.swing.JLabel inVtxLbl;
	    private javax.swing.JTextField inVtxTxt;
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JLabel jLabel3;
	    private javax.swing.JLabel jLabel4;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JPanel jPanel2;
	    private javax.swing.JPanel jPanel3;
	    private javax.swing.JPanel jPanel4;
	    private javax.swing.JProgressBar jProgressBar1;
	    private javax.swing.JScrollPane jScrollPane1;
	    private javax.swing.JSplitPane mainSplitPnl;
	    private javax.swing.JButton mirBtn;
	    private javax.swing.JComboBox motifSizeCombo;
	    private javax.swing.JLabel motifSizeLbl;
	    private javax.swing.JButton outDirBtn;
	    private javax.swing.JLabel outDirLbl;
	    private javax.swing.JTextField outDirTxt;
	    private javax.swing.JLabel progressLbl;
	    private javax.swing.JCheckBox pvalCheck;
	    private javax.swing.JTextField pvalTxt;
	    private javax.swing.JLabel randNetLbl;
	    private javax.swing.JTextField randNetTxt;
	    private javax.swing.JButton reloadBtn;
	    private javax.swing.JTextArea reportArea;
	    private javax.swing.JSplitPane resSplitPnl;
	    private javax.swing.JScrollPane resultScrPnl;
	    private javax.swing.JButton saveHtmBtn;
	    private javax.swing.JCheckBox selfLoopCheck;
	    private javax.swing.JButton sloopBtn;
	    private javax.swing.JButton startBtn;
	    private javax.swing.JCheckBox zscoreCheck;
	    private javax.swing.JTextField zscoreTxt;
	    // End of variables declaration                   
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}

}
