/**
====================================================================
WaRSWap Software Application
====================================================================

AUTHORS
  Mitra Ansariola, Molly Megraw
  Department of Botany and Plant Pathology 
  2082 Cordley Hall
  Oregon State University
  Corvallis, OR 97331-2902
  
  E-mail:  megrawm@science.oregonstate.edu 
  http://bpp.oregonstate.edu/

Copyright (c) 2015 Oregon State University
All Rights Reserved. 
====================================================================

Permission to use, copy, modify, and distribute this software and its
documentation for educational, research and non-profit purposes, without fee,
and without a written agreement is hereby granted, provided that the above
copyright notice, this paragraph and the following three paragraphs appear in
all copies. 

Permission to incorporate this software into commercial products may be obtained
by contacting Oregon State University Office of Technology Transfer.

This software program and documentation are copyrighted by Oregon State
University. The software program and documentation are supplied "as is", without
any accompanying services from Oregon State University. OSU does not warrant
that the operation of the program will be uninterrupted or error-free. The
end-user understands that the program was developed for research purposes and is
advised not to rely exclusively on the program for any reason. 

IN NO EVENT SHALL OREGON STATE UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT,
INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF OREGON
STATE UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. OREGON STATE
UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
AND ANY STATUTORY WARRANTY OF NON-INFRINGEMENT. THE SOFTWARE PROVIDED HEREUNDER
IS ON AN "AS IS" BASIS, AND OREGON STATE UNIVERSITY HAS NO OBLIGATIONS TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
 */
package edu.osu.netmotifs.warswap.ui;

import static edu.osu.netmotifs.warswap.common.CONF.DIR_SEP;
import static edu.osu.netmotifs.warswap.common.CONF.ERROR_MSG_TYPE;
import static edu.osu.netmotifs.warswap.common.CONF.FAILE_STATUS;
import static edu.osu.netmotifs.warswap.common.CONF.GENE_Color;
import static edu.osu.netmotifs.warswap.common.CONF.GENE_STR;
import static edu.osu.netmotifs.warswap.common.CONF.INFO_MSG_TYPE;
import static edu.osu.netmotifs.warswap.common.CONF.MIR_Color;
import static edu.osu.netmotifs.warswap.common.CONF.MIR_STR;
import static edu.osu.netmotifs.warswap.common.CONF.MOTIFS_HTML_OUT_FILE_NAME;
import static edu.osu.netmotifs.warswap.common.CONF.MOTIFS_OUT_DIR;
import static edu.osu.netmotifs.warswap.common.CONF.MOTIFS_OUT_FILE_NAME;
import static edu.osu.netmotifs.warswap.common.CONF.NEWLINE;
import static edu.osu.netmotifs.warswap.common.CONF.OUT_DIR_NAME;
import static edu.osu.netmotifs.warswap.common.CONF.SL_Color;
import static edu.osu.netmotifs.warswap.common.CONF.TF_Color;
import static edu.osu.netmotifs.warswap.common.CONF.TF_STR;
import static edu.osu.netmotifs.warswap.common.CONF.pool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.Document;

import edu.osu.netmotifs.warswap.JWarswapMultiThread;
import edu.osu.netmotifs.warswap.common.CONF;
import edu.osu.netmotifs.warswap.common.CreateDirectory;
import edu.osu.netmotifs.warswap.common.Utils;
import edu.osu.netmotifs.warswap.common.exception.DuplicateItemException;
import edu.osu.netmotifs.warswap.common.exception.EdgeFileDoesNotExistException;
import edu.osu.netmotifs.warswap.common.exception.EdgeFileFormatException;
import edu.osu.netmotifs.warswap.common.exception.VertexEdgeDontMatchException;
import edu.osu.netmotifs.warswap.common.exception.VertexFileDoesNotExistException;
import edu.osu.netmotifs.warswap.common.exception.VertexFileFormatException;

/**
 * This class provides user interface for running WaRSwap Software Application
 * 
 * @author mitra
 * @Date 6/26/2015
 */
public class StartWarswapSoftApp extends JFrame implements ActionListener,
		PropertyChangeListener {

	private static final long serialVersionUID = -7444783256972789874L;

	private HashMap<String, String> vTypeHash = new HashMap<String, String>();
	HashMap<Integer, String> vHash = new HashMap<Integer, String>();
	HashMap<Integer, Color> colorHash = new HashMap<Integer, Color>();
	private int maxVId = 0;
	private int maxEId = 0;
	private int motifSize = 3;
	private String motifOutputFile = "";
	protected StartWarswapSoftApp thisClass;
	private SaveOptionsUI saveOptionsUI;
	
	private Task task;

	private String htmlOutFile;
	

	/**
	 * This inner class runs motif discovery in separate thread. 
	 * Input options will be checked and feeded into main algorithm
	 * to start randomization and enumeration and motif calculations 
	 * @author mitra
	 *
	 */
	class Task extends SwingWorker<String, Void> {
		
		private boolean isRunning = false;

		@Override
		public String doInBackground() throws Exception {
			int progress = 0;
			setProgress(progress);
			
			String inEdgeFile = inEdgTxt.getText();
			String inDir = new File(inEdgeFile).getParent();
			String inVtxFile = inVtxTxt.getText();
			motifSize = Integer.valueOf(motifSizeCombo.getSelectedItem().toString());
			int nOfRandNets = Integer.valueOf(randNetTxt.getText());
			motifOutputFile = outDirTxt.getText() + DIR_SEP + MOTIFS_OUT_FILE_NAME;
			
			// Input options check
			JWarswapMultiThread jWarswapMultiThread = null;
			try {
				inputVertexFormatCheck(inVtxFile);
				inputEdgeFormatCheck(inEdgeFile);
				jWarswapMultiThread = new JWarswapMultiThread(inEdgeFile, inVtxFile,
						inDir, OUT_DIR_NAME, motifSize, selfLoopCheck.isSelected());
			} catch (Exception e) {
				updateReportConsole(ERROR_MSG_TYPE, e.getMessage());
				e.printStackTrace();
				throw e;
			} 
				
			// Initialize main thread with configured options
			jWarswapMultiThread.setNoOfIterations(nOfRandNets);
			jWarswapMultiThread.setSignOutFile(motifOutputFile);

			reportArea.setText("");
			updateReportConsole(INFO_MSG_TYPE, "Motif discovery started ...");
			updateReportConsole(INFO_MSG_TYPE, "Input vertex file: " + inVtxFile);
			updateReportConsole(INFO_MSG_TYPE, "Input edge file: " + inEdgeFile);
			updateReportConsole(INFO_MSG_TYPE, "Motif size: " + motifSize);
			
			try {
				pool.submit(jWarswapMultiThread);
				isRunning = true;
			}catch(Throwable t) {
				t.printStackTrace();
			}
			
			// Wait until search completes
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
					ignore.printStackTrace();
				}
				
				if (FAILE_STATUS.equalsIgnoreCase(jWarswapMultiThread.getStatus())) {
					progress = 0;
					updateReportConsole(ERROR_MSG_TYPE, jWarswapMultiThread.getErrorMsg());
					break;
				}
				
				progress = (((jWarswapMultiThread.getFinishedJobs() * 100) / nOfRandNets));
				setProgress(Math.min(progress, 100));
				int progressNo = jWarswapMultiThread.getFinishedJobs();
				if (progressNo > Integer.valueOf(randNetTxt.getText()))
					progressNo = Integer.valueOf(randNetTxt.getText());
				
				progressLbl.setText(progressNo	+ " out of " + randNetTxt.getText()
						+ " networks processed.");
				progressLbl.setForeground(Color.black);
			}
			
			if (progress >= 100) {
				updateReportConsole(INFO_MSG_TYPE, "Randomization and enumeration of subgraphs completed!");
				updateReportConsole(INFO_MSG_TYPE, "Waiting for extracting motif information ...");
				progress = 100;
			}
			while (!jWarswapMultiThread.isDone() || FAILE_STATUS.equalsIgnoreCase(jWarswapMultiThread.getStatus())) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
					ignore.printStackTrace();
				}
			}
			setProgress(progress);
			return String.valueOf(progress);
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			try {
				get();
				pool.take().get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Toolkit.getDefaultToolkit().beep();
			activateUIItems();
			setCursor(null); // turn off the wait cursor
			if (getProgress() == 100) {
				updateReportConsole(INFO_MSG_TYPE, "Motif discovery completed!");
				updateReportConsole(INFO_MSG_TYPE, "Output files are generated and located at: " + outDirTxt.getText() + "\n");
			}
			loadHTML();
		}
		
		public void inputVertexFormatCheck(String vertexFile) throws Exception {
			vHash.clear();
			maxVId = 0;
			//1- If it is a valid file
			File vFile = new File(vertexFile);
			if (!vFile.isFile() || vFile.isDirectory())
				throw new VertexFileDoesNotExistException();
				
			//2- If it contains two columns with desired labels (TF,MIR,GENE)
			InputStream inputStream = new FileInputStream(vFile);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] vParts = line.split("\t");
				if (vParts.length != 2)
					throw new VertexFileFormatException();
				try {
					int vId = Integer.valueOf(vParts[0]);
					if (vId > maxVId)
						maxVId = vId;
					if (vHash.get(vId) != null)
						throw new DuplicateItemException("Input vertex file format error: duplicate entry ( " + vId + " )!");
					vHash.put(vId, "-");
				} catch (NumberFormatException e) {
					throw new VertexFileFormatException();
				}
				if (vTypeHash.get(vParts[1]) == null)
					throw new VertexFileFormatException();
			}
			bufferedReader.close();
			inputStream.close();
		}
		
		
		public void inputEdgeFormatCheck(String edgeFile) throws Exception {
			//1- If it is a valid file
			InputStream inputStream = null;
			BufferedReader bufferedReader = null;
			try {
				maxEId = 0;
				HashMap<String, String> eHashMap = new HashMap<String, String>();
				File eFile = new File(edgeFile);
				if (!eFile.isFile() || eFile.isDirectory())
					throw new EdgeFileDoesNotExistException();
					
				//2- If it contains two columns with desired labels (TF,MIR,GENE)
				inputStream = new FileInputStream(eFile);
				bufferedReader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String[] eParts = line.split("\t");
					if (eParts.length != 2) 
						throw new EdgeFileFormatException();
					int v1 = 0, v2 = 0;
					try {
						v1 = Integer.valueOf(eParts[0]);
						v2 = Integer.valueOf(eParts[1]);
					} catch (NumberFormatException e) {
						throw new EdgeFileFormatException();
					}
					if (vHash.get(v1) == null || vHash.get(v2) == null) 
						throw new VertexEdgeDontMatchException();
					String edgeKey = v1 + "_" + v2;
					if (eHashMap.get(edgeKey) != null) 
						throw new DuplicateItemException("Input edge file format error : duplicate entry ( " + edgeKey + " )");
						
					if (v1 > maxEId)
						maxEId = v1;
					if (v2 > maxEId)
						maxEId = v2;
				}
				if (maxEId != maxVId) {
					bufferedReader.close();
					throw new VertexEdgeDontMatchException();
				}

			} catch (Exception e) {
				throw e;
			} finally {
				inputStream.close();
				bufferedReader.close();
			}
		}
	}

	private void updateReportConsole(String type, String message) {
		reportArea.append(Calendar.getInstance().getTime() + "-" + type + ": " + message + NEWLINE);
	}
	
	public void loadHTMLPanel() {
		try {
			Document doc = htmlResultPane.getDocument();
			doc.putProperty(Document.StreamDescriptionProperty, null);
			URL url = new File(htmlOutFile).toURI().toURL();
			htmlResultPane.setPage(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadHTML() {
		float zscore = -1, pval = -1;
		htmlOutFile = outDirTxt.getText() + DIR_SEP + MOTIFS_HTML_OUT_FILE_NAME;
		try {
			if (zscoreCheck.isSelected())
				zscore = Float.valueOf(zscoreTxt.getText());
			if (pvalCheck.isSelected())
				pval = Float.valueOf(pvalTxt.getText());
			GenerateMotifImages generateMotifImages = new GenerateMotifImages(colorHash, motifOutputFile, motifSize, 
					htmlOutFile);
			generateMotifImages.createHtm(zscore, pval, 25, true);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		loadHTMLPanel();
	}
	
	public void saveMotifs(File saveFile, String filetype) {
		saveOptionsUI.dispose();
		
		float zscore = -1, pval = -1;
		try {
			if (zscoreCheck.isSelected())
				zscore = Float.valueOf(zscoreTxt.getText());
			if (pvalCheck.isSelected())
				pval = Float.valueOf(pvalTxt.getText());
			
			if (CONF.TEXT_FILE_TYPE.equalsIgnoreCase(filetype))
				new GenerateMotifsTextOutput().saveAsTxtFile(motifOutputFile, saveFile.getAbsolutePath(), CONF.TEXT_FILE_TYPE, zscore, pval);
			else if (CONF.CSV_FILE_TYPE.equalsIgnoreCase(filetype))
				new GenerateMotifsTextOutput().saveAsTxtFile(motifOutputFile, saveFile.getAbsolutePath(), CONF.CSV_FILE_TYPE, zscore, pval);
			else {
				GenerateMotifImages generateMotifImages = new GenerateMotifImages(colorHash, motifOutputFile, motifSize, 
						saveFile.getAbsolutePath());
				generateMotifImages.createHtm(zscore, pval, 25, false);
			}
			
            updateReportConsole(INFO_MSG_TYPE, "motifs saved to file: " + motifOutputFile);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public StartWarswapSoftApp() {
		vTypeHash.put(TF_STR, "0");
		vTypeHash.put(MIR_STR, "1");
		vTypeHash.put(GENE_STR, "2");
		
		colorHash.put(Integer.parseInt(String.valueOf(TF_Color)), Color.BLUE);
		colorHash.put(Integer.parseInt(String.valueOf(MIR_Color)), Color.RED);
		colorHash.put(Integer.parseInt(String.valueOf(GENE_Color)), Color.BLACK);
		colorHash.put(Integer.parseInt(String.valueOf(SL_Color)), Color.CYAN);
		
		initComponents();
		initActions();
		initOptions();
	}

	private void initActions() {
		initInOutActions();
		initOptions();
		initButtonIcons();
		initFilterActions();
	}
	
	private void activateUIItems() {
		mirBtn.setEnabled(true);
		TFBtn.setEnabled(true);
		geneBtn.setEnabled(true);
		sloopBtn.setEnabled(true);
		zscoreCheck.setEnabled(true);
		pvalCheck.setEnabled(true);
		saveHtmBtn.setEnabled(true);
		reloadBtn.setEnabled(true);	
		startBtn.setEnabled(true);
	}
	
	private void deactivateUIItems() {
		mirBtn.setEnabled(false);
		TFBtn.setEnabled(false);
		geneBtn.setEnabled(false);
		sloopBtn.setEnabled(false);

		zscoreCheck.setEnabled(false);
		zscoreTxt.setEnabled(false);

		pvalTxt.setEnabled(false);
		pvalCheck.setEnabled(false);
		
		saveHtmBtn.setEnabled(false);
		reloadBtn.setEnabled(false);
		
		startBtn.setEnabled(false);
	}


	private void initOptions() {
//		motifSizeCombo.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (motifSizeCombo.getSelectedItem().toString()
//						.equalsIgnoreCase("1")
//						|| motifSizeCombo.getSelectedItem().toString()
//								.equalsIgnoreCase("2")) {
//					selfLoopCheck.setSelected(true);
//				} else {
//					selfLoopCheck.setEnabled(true);
//				}
//			}
//		});

		selfLoopCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int s = motifSizeCombo.getSelectedIndex();
				String ss = (String) motifSizeCombo.getSelectedItem();
				if (!selfLoopCheck.isSelected()) {
					motifSizeCombo.setModel(new javax.swing.DefaultComboBoxModel<String>(
							new String[] { "2", "3", "4", "5", "6", "7", "8" }));
					motifSizeCombo.setSelectedItem(ss);
//					motifSizeCombo.setSelectedIndex(1);
				} else {
					motifSizeCombo.setModel(new javax.swing.DefaultComboBoxModel<String>(
							new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
					motifSizeCombo.setSelectedItem(ss);
				}
				
//				if (motifSizeCombo.getSelectedItem().toString()
//						.equalsIgnoreCase("1")
//						|| motifSizeCombo.getSelectedItem().toString()
//								.equalsIgnoreCase("2")) {
//					selfLoopCheck.setSelected(true);
//				}
			}
		});

	}

	private void initFilterActions() {
		htmlResultPane.setEditable(false);
		
		sloopBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(
						StartWarswapSoftApp.this, "Choose color", Color.CYAN);
				colorHash.put(Integer.parseInt(String.valueOf(SL_Color)),
						newColor);
				sloopBtn.setBackground(newColor);
			}
		});
		TFBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(StartWarswapSoftApp.this,
						"Choose color", Color.BLUE);
				colorHash.put(Integer.parseInt(String.valueOf(TF_Color)),
						newColor);
				TFBtn.setBackground(newColor);
			}
		});
		mirBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(StartWarswapSoftApp.this,
						"Choose color", Color.RED);
				colorHash.put(Integer.parseInt(String.valueOf(MIR_Color)),
						newColor);
				mirBtn.setBackground(newColor);
			}
		});

		geneBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = JColorChooser.showDialog(StartWarswapSoftApp.this,
						"Choose color", Color.BLACK);
				colorHash.put(
						Integer.parseInt(String.valueOf(GENE_Color)),
						newColor);
				geneBtn.setBackground(newColor);
			}
		});
		
		mirBtn.setEnabled(false);
		TFBtn.setEnabled(false);
		geneBtn.setEnabled(false);
		sloopBtn.setEnabled(false);

		zscoreCheck.setEnabled(true);
		zscoreCheck.setSelected(true);
		zscoreTxt.setEnabled(true);

		pvalTxt.setEnabled(true);
		pvalCheck.setEnabled(true);
		pvalCheck.setSelected(true);
		
		saveHtmBtn.setEnabled(false);
		reloadBtn.setEnabled(false);
		
		helpBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File("userManual.pdf"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		zscoreCheck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (zscoreCheck.isSelected())
					zscoreTxt.setEnabled(true);
				else
					zscoreTxt.setEnabled(false);
			}
		});
		
		pvalCheck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pvalCheck.isSelected())
					pvalTxt.setEnabled(true);
				else
					pvalTxt.setEnabled(false);
			}
		});
		
		ImageIcon icon = new ImageIcon("images/save.jpg");
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(20, 20,
				java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		saveHtmBtn.setIcon(icon);
		saveHtmBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				JFileChooser chooser = new JFileChooser(outDirTxt.getText());
//				FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML FILES", "htm", "html");
//				chooser.setFileFilter(filter);
//				
//				int returnVal = chooser.showSaveDialog(StartWarswapSoftApp.this);
//				if (returnVal == JFileChooser.APPROVE_OPTION) {
//					File file = chooser.getSelectedFile();
//					saveHTML(file);
//				}
				if (outDirTxt.getText().isEmpty() || !(new File(outDirTxt.getText()).isDirectory()) ) {
					JOptionPane.showMessageDialog(thisClass, "Output directory is empty or not valid!");
				}
				saveOptionsUI = new SaveOptionsUI(outDirTxt.getText(), thisClass);
            	Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screenSize = tk.getScreenSize();
            	final int WIDTH = screenSize.width;
				final int HEIGHT = screenSize.height;
				saveOptionsUI.setLocation(WIDTH / 3, HEIGHT / 3);
				saveOptionsUI.setVisible(true);
			}
		});
		
		icon = new ImageIcon("images/refresh.jpg");
		img = icon.getImage();
		newimg = img.getScaledInstance(20, 20,
				java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		reloadBtn.setIcon(icon);
		
		reloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadHTML();
			}
		});
	}

	private void initButtonIcons() {

		ImageIcon icon = new ImageIcon("images/start.jpg");
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(20, 20,
				java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);

		startBtn.setText("Start");
		startBtn.setActionCommand("start");
		startBtn.addActionListener(this);
		startBtn.setIcon(icon);

		icon = new ImageIcon("images/close.jpg");
		img = icon.getImage();
		newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);

		closeBtn.setIcon(icon);
		closeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane
						.showConfirmDialog(null,
								"Do you want to close the application?",
								"Confirm", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		icon = new ImageIcon("images/help.jpg");
		img = icon.getImage();
		newimg = img.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		helpBtn.setIcon(icon);

	}

	/**
	 * initProperties and set actions for in/out buttons
	 */
	private void initInOutActions() {
		ImageIcon icon = new ImageIcon("images/browse.jpg");
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(40, 20,
				java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);

		inVtxBtn.setText("");
		inVtxBtn.setIcon(icon);
		inVtxBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(inVtxTxt.getText());
				String fPath = ".";
				if (!file.isFile() && inVtxTxt.getText() != null
						&& !inVtxTxt.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else
					fPath = file.getAbsolutePath();
				JFileChooser chooser = new JFileChooser(new File(fPath));
				int returnVal = chooser
						.showOpenDialog(StartWarswapSoftApp.this);
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
				if (!file.isFile() && inEdgTxt.getText() != null
						&& !inEdgTxt.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else
					fPath = file.getAbsolutePath();

				JFileChooser chooser = new JFileChooser(new File(fPath));
				int returnVal = chooser
						.showOpenDialog(StartWarswapSoftApp.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					String inPath = file.getAbsolutePath();
					inEdgTxt.setText(inPath);
					String outPathDir = new File(".").getAbsolutePath()
							+ DIR_SEP + MOTIFS_OUT_DIR;
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
				if (!file.isFile() && outDirTxt.getText() != null
						&& !outDirTxt.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else
					fPath = file.getAbsolutePath();
				JFileChooser chooser = new JFileChooser(new File(fPath));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser
						.showOpenDialog(StartWarswapSoftApp.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					outDirTxt.setText(file.getAbsolutePath());
				}

			}
		});
	}

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
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		motifSizeLbl = new javax.swing.JLabel();
		motifSizeCombo = new javax.swing.JComboBox<String>();
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
		progressBar = new javax.swing.JProgressBar();
		jScrollPane1 = new javax.swing.JScrollPane();
		reportArea = new javax.swing.JTextArea();
		progressLbl = new javax.swing.JLabel();
		closeBtn = new javax.swing.JButton();
		helpBtn = new javax.swing.JButton();
		htmlResultPane = new JEditorPane();
		resultScrPnl = new javax.swing.JScrollPane(htmlResultPane);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new java.awt.Dimension(1100, 700));

		mainSplitPnl.setDividerLocation(630);

		resSplitPnl.setDividerLocation(240);
		resSplitPnl.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		resSplitPnl.setToolTipText("");
		resSplitPnl.setPreferredSize(new java.awt.Dimension(400, 538));

		filterPnl.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Filter results"));
		filterPnl.setPreferredSize(new java.awt.Dimension(400, 320));
		filterPnl.setRequestFocusEnabled(false);

		zscoreCheck.setText("Z-score greater than ");

		zscoreTxt.setText("2.00");

		pvalCheck.setText("P-value less than ");

		pvalTxt.setText("0.01");

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Color options"));

		jLabel1.setText("TF");

		TFBtn.setBackground(java.awt.Color.blue);

		jLabel2.setText("MIR");

		mirBtn.setBackground(java.awt.Color.red);

		jLabel3.setText("GENE");

		geneBtn.setBackground(java.awt.Color.black);

		jLabel4.setText("Self-loop");

		sloopBtn.setBackground(java.awt.Color.cyan);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout
						.createSequentialGroup()
						.addComponent(jLabel1)
						.addGap(4, 4, 4)
						.addComponent(TFBtn,
								javax.swing.GroupLayout.PREFERRED_SIZE, 23,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jLabel2)
						.addGap(3, 3, 3)
						.addComponent(mirBtn,
								javax.swing.GroupLayout.PREFERRED_SIZE, 23,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jLabel3)
						.addGap(4, 4, 4)
						.addComponent(geneBtn,
								javax.swing.GroupLayout.PREFERRED_SIZE, 23,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(jLabel4)
						.addGap(3, 3, 3)
						.addComponent(sloopBtn,
								javax.swing.GroupLayout.PREFERRED_SIZE, 23,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 57, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel1Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																sloopBtn,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																21,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4)
														.addComponent(
																geneBtn,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																21,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel3)
														.addComponent(
																mirBtn,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																21,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(
																jPanel1Layout
																		.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				jLabel1)
																		.addComponent(
																				TFBtn,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				21,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(jLabel2))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		reloadBtn.setText("Refresh");

		saveHtmBtn.setText("Save");

		javax.swing.GroupLayout filterPnlLayout = new javax.swing.GroupLayout(
				filterPnl);
		filterPnl.setLayout(filterPnlLayout);
		filterPnlLayout
				.setHorizontalGroup(filterPnlLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								filterPnlLayout
										.createSequentialGroup()
										.addGroup(
												filterPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																filterPnlLayout
																		.createSequentialGroup()
																		.addGroup(
																				filterPnlLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								zscoreCheck)
																						.addComponent(
																								pvalCheck))
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				filterPnlLayout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								zscoreTxt,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								pvalTxt,
																								javax.swing.GroupLayout.PREFERRED_SIZE,
																								javax.swing.GroupLayout.DEFAULT_SIZE,
																								javax.swing.GroupLayout.PREFERRED_SIZE)))
														.addComponent(
																jPanel1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(0, 12, Short.MAX_VALUE))
						.addGroup(
								filterPnlLayout
										.createSequentialGroup()
										.addGap(0, 0, Short.MAX_VALUE)
										.addComponent(reloadBtn)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												saveHtmBtn,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												96,
												javax.swing.GroupLayout.PREFERRED_SIZE)));
		filterPnlLayout
				.setVerticalGroup(filterPnlLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								filterPnlLayout
										.createSequentialGroup()
										.addGroup(
												filterPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																zscoreCheck)
														.addComponent(
																zscoreTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(10, 10, 10)
										.addGroup(
												filterPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(pvalCheck)
														.addComponent(
																pvalTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												filterPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																saveHtmBtn)
														.addComponent(reloadBtn))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		resSplitPnl.setTopComponent(filterPnl);

		resultScrPnl.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Result browser"));
		resSplitPnl.setRightComponent(resultScrPnl);

		mainSplitPnl.setRightComponent(resSplitPnl);

		jPanel3.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Options"));

		motifSizeLbl.setText("Motif size");

		motifSizeCombo.setModel(new javax.swing.DefaultComboBoxModel<String>(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
		motifSizeCombo.setSelectedIndex(2);
		motifSizeCombo.setToolTipText("");

		selfLoopCheck.setSelected(true);
		selfLoopCheck.setText("Consider self-loops");

		randNetLbl.setText("No of random networks");

		randNetTxt.setColumns(5);
		randNetTxt.setText("2500");

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addComponent(
																				motifSizeLbl)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				motifSizeCombo,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGap(96,
																				96,
																				96)
																		.addComponent(
																				selfLoopCheck))
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addComponent(
																				randNetLbl)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				randNetTxt,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(0, 0, Short.MAX_VALUE)));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																motifSizeLbl)
														.addComponent(
																motifSizeCombo,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																selfLoopCheck))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																randNetLbl)
														.addComponent(
																randNetTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(0, 0, Short.MAX_VALUE)));

		inOutPnl.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Input/output "));

		inVtxLbl.setText("Vertex file");

		inVtxTxt.setColumns(30);

		inVtxBtn.setText("jButton1");

		inEdgLbl.setText("Edge file");

		inEdgTxt.setColumns(30);

		inEdgBtn.setText("jButton1");

		outDirLbl.setText("Output directory");

		outDirTxt.setColumns(30);

		outDirBtn.setText("jButton1");

		javax.swing.GroupLayout inOutPnlLayout = new javax.swing.GroupLayout(
				inOutPnl);
		inOutPnl.setLayout(inOutPnlLayout);
		inOutPnlLayout
				.setHorizontalGroup(inOutPnlLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								inOutPnlLayout
										.createSequentialGroup()
										.addGroup(
												inOutPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addGroup(
																inOutPnlLayout
																		.createSequentialGroup()
																		.addComponent(
																				outDirLbl)
																		.addGap(2,
																				2,
																				2)
																		.addComponent(
																				outDirTxt))
														.addGroup(
																inOutPnlLayout
																		.createSequentialGroup()
																		.addComponent(
																				inVtxLbl)
																		.addGap(3,
																				3,
																				3)
																		.addComponent(
																				inVtxTxt,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				438,
																				javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																inOutPnlLayout
																		.createSequentialGroup()
																		.addComponent(
																				inEdgLbl)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				inEdgTxt)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												inOutPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(inVtxBtn)
														.addComponent(inEdgBtn)
														.addComponent(outDirBtn))));
		inOutPnlLayout
				.setVerticalGroup(inOutPnlLayout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								inOutPnlLayout
										.createSequentialGroup()
										.addGroup(
												inOutPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(inVtxLbl)
														.addComponent(
																inVtxTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(inVtxBtn))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												inOutPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(inEdgLbl)
														.addComponent(
																inEdgTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(inEdgBtn))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												inOutPnlLayout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(outDirLbl)
														.addComponent(
																outDirTxt,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(outDirBtn))
										.addGap(0, 0, Short.MAX_VALUE)));

		jPanel4.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Run motif discovery"));

		startBtn.setText("Start ");

		reportArea.setColumns(20);
		reportArea.setRows(5);
		jScrollPane1.setViewportView(reportArea);

		progressLbl.setText("");

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
				.setHorizontalGroup(jPanel4Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel4Layout
										.createSequentialGroup()
										.addGap(18, 18, 18)
										.addComponent(
												startBtn,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												111,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel4Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																progressBar,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																390,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																progressLbl,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																331,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(77, Short.MAX_VALUE))
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.Alignment.TRAILING));
		jPanel4Layout
				.setVerticalGroup(jPanel4Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel4Layout
										.createSequentialGroup()
										.addGroup(
												jPanel4Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel4Layout
																		.createSequentialGroup()
																		.addComponent(
																				progressBar,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				progressLbl))
														.addGroup(
																jPanel4Layout
																		.createSequentialGroup()
																		.addGap(6,
																				6,
																				6)
																		.addComponent(
																				startBtn)))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												272, Short.MAX_VALUE)));

		closeBtn.setText("Close");

		helpBtn.setText("Help");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout
				.setHorizontalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jPanel3,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																inOutPnl,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jPanel4,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																jPanel2Layout
																		.createSequentialGroup()
																		.addGap(0,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				helpBtn,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				94,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				closeBtn,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				97,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addComponent(
												jPanel3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												inOutPnl,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel4,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(closeBtn)
														.addComponent(helpBtn))
										.addGap(0, 0, Short.MAX_VALUE)));

		mainSplitPnl.setLeftComponent(jPanel2);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainSplitPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 1021,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainSplitPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 669,
				Short.MAX_VALUE));

		pack();
	}// </editor-fold>

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(
					StartWarswapSoftApp.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(
					StartWarswapSoftApp.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(
					StartWarswapSoftApp.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(
					StartWarswapSoftApp.class.getName()).log(
					java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				StartWarswapSoftApp startWarswapUI = new StartWarswapSoftApp();
				startWarswapUI.setTitle("WaRSwap Software Application");
				startWarswapUI.setLocationByPlatform(true);
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screenSize = tk.getScreenSize();
				final int WIDTH = screenSize.width;
				final int HEIGHT = screenSize.height;
				startWarswapUI.setLocation(WIDTH / 8, HEIGHT / 4);
				startWarswapUI.setVisible(true);
				startWarswapUI.setThisClass(startWarswapUI);
			}
		});
	}

	protected void setThisClass(StartWarswapSoftApp startWarswapUI) {
		thisClass = startWarswapUI;
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
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSplitPane mainSplitPnl;
	private javax.swing.JButton mirBtn;
	private javax.swing.JComboBox<String> motifSizeCombo;
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
	private JEditorPane htmlResultPane = new JEditorPane();

	// End of variables declaration
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("start")) {
			String eFileIn = inEdgTxt.getText();
			if (eFileIn == null || eFileIn.equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Edge input file is empty!");
				return;
			}
			String vFileIn = inVtxTxt.getText();
			if (vFileIn == null || vFileIn.equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Vertex input file is empty!");
				return;
			}
			if (eFileIn.equalsIgnoreCase(vFileIn)) {
				JOptionPane.showMessageDialog(StartWarswapSoftApp.this,
						"Edge and Vertex files are the same!");
				return;
			}
			if (!Utils.isNumeric(randNetTxt.getText())) {
				JOptionPane.showMessageDialog(StartWarswapSoftApp.this, "Random Number is not Numeric!!");
				return;
			}
			if (outDirTxt.getText() == null || outDirTxt.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(StartWarswapSoftApp.this, "Error! Outpit path is empty!!");
				return;
			}
//			if (new File(outDirTxt.getText()).exists()) {
//				int response = JOptionPane.showConfirmDialog(StartWarswapSoftApp.this,
//						"Warning! File exists! Do you want to overwrite it? ", "Warning", 
//						JOptionPane.YES_NO_OPTION,
//						JOptionPane.QUESTION_MESSAGE);
//				if (response == 1)
//					return;
//			}
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			deactivateUIItems();
			try {
				task = new Task();
				task.addPropertyChangeListener(this);
				task.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
				task.cancel(true);
			}
		}

	}

}
