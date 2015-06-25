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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

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



public class StartWarswapUI extends javax.swing.JFrame implements
		PropertyChangeListener, ActionListener {
	private static Logger logger = Logger.getLogger(StartWarswapUI.class);

	private StartWarswapUI startWarswapUI;
	private String outFile = CONF.OUT_DIR_NAME;
	private HashMap<String, String> vTypeHash = new HashMap<String, String>();
	HashMap<Integer, String> vHash = new HashMap<Integer, String>();
	private int maxVId = 0;
	private int maxEId = 0;

	/**
	 * Creates new form StartwrUI
	 */
	public StartWarswapUI() {
		startWarswapUI = this;
		initComponents();
		vTypeHash.put(CONF.TF_STR, "0");
		vTypeHash.put(CONF.MIR_STR, "1");
		vTypeHash.put(CONF.GENE_STR, "2");
	}

	private long startTime;
	private Task task;
	

	class Task extends SwingWorker<String, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		private boolean isRunning = false;

		@Override
		public String doInBackground() throws Exception {
			int progress = 0;
			startTime = System.currentTimeMillis();
			updateTimeLabel();
			setProgress(progress);
			String eFileIn = eFileInPath.getText();
			String inDir = new File(eFileIn).getParent();
			String fileName = new File(eFileIn).getName() + ".out";
			String vFileIn = vertexFileInPath.getText();
			
			int motifSize = Integer.valueOf(subgraphSize
					.getSelectedItem().toString());
			
			JWarswapMultiThread jWarswapMultiThread = null;
			try {
				inputVertexFormatCheck(vFileIn);
				inputEdgeFormatCheck(eFileIn);
				jWarswapMultiThread = new JWarswapMultiThread(eFileIn, vFileIn,
						inDir, CONF.OUT_DIR_NAME, motifSize);
			} catch (Exception e) {
				reportArea.append("Failed: " + e.getMessage() + "\n");
//				e.printStackTrace();
				logger.error(e.getStackTrace().toString());
				throw e;
			} 
				
			int nOfRandNets = Integer.valueOf(randNetNum.getText());
			jWarswapMultiThread.setNoOfIterations(nOfRandNets);
			if (csv.isSelected())
				outputPath.setText(outputPath.getText() + ".csv");
			jWarswapMultiThread.setSignOutFile(outputPath.getText());
			jWarswapMultiThread.setSelfLoops(hasSelfloops.isSelected());

			reportArea.setText("Randomization & Enumeration ...\n");
			startTime = System.currentTimeMillis();
			try {
			CONF.pool.submit(jWarswapMultiThread);
			isRunning = true;
			}catch(Throwable t) {
//				t.printStackTrace();
				logger.error(t.getMessage());
			}
			while (progress < 100) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
//					ignore.printStackTrace();
					logger.error(ignore.getMessage());
				}
				updateTimeLabel();
				if (CONF.FAILE_STATUS.equalsIgnoreCase(jWarswapMultiThread
						.getStatus())) {
					progress = 0;
					setProgress(1);
					reportArea.append("Failed: "
							+ jWarswapMultiThread.getErrorMsg() + "\n");
					break;
				}
				progress = (((jWarswapMultiThread.getFinishedJobs() * 100) / nOfRandNets));
				setProgress(Math.min(progress, 100));
				int progressNo = jWarswapMultiThread.getFinishedJobs();
				if (progressNo > Integer.valueOf(randNetNum.getText()))
					progressNo = Integer.valueOf(randNetNum.getText());
				progressLabel.setText(progressNo
						+ " out of " + randNetNum.getText()
						+ " networks processed.");
				progressLabel.setForeground(Color.black);
			}
			if (progress >= 100) {
				reportArea.append("Randomization finished! \nMotif extraction ...\n");
				progress = 100;
			}
			while (!jWarswapMultiThread.isDone()
					|| CONF.FAILE_STATUS.equalsIgnoreCase(jWarswapMultiThread
							.getStatus())) {
				try {
					updateTimeLabel();
					Thread.sleep(1000);
				} catch (InterruptedException ignore) {
//					ignore.printStackTrace();
					logger.error(ignore.getMessage());
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
				CONF.pool.take().get();
			} catch (Exception e) {
//				e.printStackTrace();
				logger.error(e.getMessage());
			}
			Toolkit.getDefaultToolkit().beep();
			startBtn.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			if (getProgress() == 100) {
				reportArea.append("Done!\n");
				reportArea.append("Output file is located at: " + outputPath.getText() + "\n");
				reportArea.append("To get HTML output click on \"Export HTML\" button\n");
				exportHtmBtn.setEnabled(true);
			}
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
						throw new DuplicateItemException("Input edge file format error : douplicate entry ( " + edgeKey + " )");
						
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

	public void updateTimeLabel() {
		timeLabel.setText("Time Elapsed: " + getTimeElapsed());
	}

	public String getTimeElapsed() {
		long elapsedTime = System.currentTimeMillis() - startTime;
		elapsedTime = elapsedTime / 1000;

		String seconds = Integer.toString((int) (elapsedTime % 60));
		String minutes = Integer.toString((int) ((elapsedTime % 3600) / 60));
		String hours = Integer.toString((int) (elapsedTime / 3600));

		if (seconds.length() < 2)
			seconds = "0" + seconds;

		if (minutes.length() < 2)
			minutes = "0" + minutes;

		if (hours.length() < 2)
			hours = "0" + hours;

		return hours + ":" + minutes + ":" + seconds;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		subgraphSize = new javax.swing.JComboBox();
		jLabel2 = new javax.swing.JLabel();
		randNetNum = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		eFileInPath = new javax.swing.JTextField();
		inputBtn = new javax.swing.JButton();
		jPanel4 = new javax.swing.JPanel();
		jLabel4 = new javax.swing.JLabel();
		outputPath = new javax.swing.JTextField();
		outputBtn = new javax.swing.JButton();
		asci = new javax.swing.JRadioButton();
		csv = new javax.swing.JRadioButton();
		jPanel5 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		hasSelfloops = new javax.swing.JCheckBox();
		startBtn = new javax.swing.JButton();
		jLabel5 = new javax.swing.JLabel();
		vertexInBtn = new javax.swing.JButton();
		jLabel6 = new javax.swing.JLabel();
		totalProgress = new javax.swing.JProgressBar(0, 100);
		totalProgress.setStringPainted(true);
		vertexFileInPath = new javax.swing.JTextField();
		// netProgress = new javax.swing.JProgressBar(0, 100);
		// netProgress.setStringPainted(true);
		timeLabel = new javax.swing.JLabel();
		jPanel7 = new javax.swing.JPanel();
		exportHtmBtn = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		reportArea = new javax.swing.JTextArea();
		closeBtn = new javax.swing.JButton();
		helpBtn = new javax.swing.JButton();
		progressLabel = new JLabel();

		exportHtmBtn.setEnabled(false);
		exportHtmBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						ExportHtmlUI exportHtmlUI = new ExportHtmlUI(outputPath
								.getText() + ".htm", Integer
								.valueOf(subgraphSize.getSelectedItem()
										.toString()), outputPath.getText(),
								hasSelfloops.isSelected(), startWarswapUI);
						Toolkit tk = Toolkit.getDefaultToolkit();
						Dimension screenSize = tk.getScreenSize();
						final int WIDTH = screenSize.width;
						final int HEIGHT = screenSize.height;
						exportHtmlUI.setLocation(WIDTH / 7, HEIGHT / 4);
						exportHtmlUI.setVisible(true);
					}
				});
			}
		});

		vertexInBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(vertexFileInPath.getText());
				String fPath = ".";
				if (!file.isFile() && vertexFileInPath.getText() != null && !vertexFileInPath.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else 
					fPath = file.getAbsolutePath();
				JFileChooser chooser = new JFileChooser(new File(fPath));
				int returnVal = chooser.showOpenDialog(StartWarswapUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					String inPath = file.getAbsolutePath();
					vertexFileInPath.setText(inPath);
				}
			}
		});
		inputBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(eFileInPath.getText());
				String fPath = ".";
				if (!file.isFile() && eFileInPath.getText() != null && !eFileInPath.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else 
					fPath = file.getAbsolutePath();
				
				JFileChooser chooser = new JFileChooser(new File(fPath));
				int returnVal = chooser.showOpenDialog(StartWarswapUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					String inPath = file.getAbsolutePath();
					eFileInPath.setText(inPath);
					String outPathDir = new File(".").getAbsolutePath() + CONF.DIR_SEP + CONF.MOTIFS_OUT_DIR;
					CreateDirectory.createDir(outPathDir);
					outputPath.setText(outPathDir + CONF.DIR_SEP + CONF.MOTIFS_OUT_FILE_NAME);
				}
			}
		});

		outputBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(outputPath.getText());
				String fPath = ".";
				if (!file.isFile() && outputPath.getText() != null && !outputPath.getText().equalsIgnoreCase(""))
					fPath = file.getParent();
				else 
					fPath = file.getAbsolutePath();
				JFileChooser chooser = new JFileChooser(new File(fPath));
				int returnVal = chooser.showOpenDialog(StartWarswapUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					outputPath.setText(file.getAbsolutePath());
				}

			}
		});

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("CONFIG"));

		jLabel1.setText("Subgraph Size: ");

		subgraphSize.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
		subgraphSize.setSelectedIndex(2);
		subgraphSize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (subgraphSize.getSelectedItem().toString()
						.equalsIgnoreCase("1")
						|| subgraphSize.getSelectedItem().toString()
								.equalsIgnoreCase("2")) {
					hasSelfloops.setSelected(true);
					// hasSelfloops.setEnabled(false);
				} else {
					hasSelfloops.setEnabled(true);
				}
			}
		});

		jLabel2.setText("Number of random networks: ");

		randNetNum.setText("2500");

		hasSelfloops.setText("Detect Self-loops");
		hasSelfloops.setSelected(true);
		hasSelfloops.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (subgraphSize.getSelectedItem().toString()
						.equalsIgnoreCase("1")
						|| subgraphSize.getSelectedItem().toString()
								.equalsIgnoreCase("2")) {
					hasSelfloops.setSelected(true);
				}
			}
		});

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addGap(1, 1, 1)
										.addComponent(
												subgraphSize,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(96, 96, 96)
										.addComponent(hasSelfloops)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(jLabel2)
										.addGap(1, 1, 1)
										.addComponent(
												randNetNum,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												95,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(23, 23, 23)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(jLabel1)
						.addComponent(subgraphSize,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(jLabel2)
						.addComponent(randNetNum,
								javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(hasSelfloops)));

		jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("INPUT"));

		jLabel3.setText("Edges: ");

		inputBtn.setText("‌Browse");

		vertexInBtn.setText("Browse");

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel3Layout
										.createSequentialGroup()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addContainerGap(
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(
																				jLabel3)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED))
														.addGroup(
																jPanel3Layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				jLabel6)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)))
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																eFileInPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																189,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																vertexFileInPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																189,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(inputBtn)
														.addComponent(
																vertexInBtn))
										.addContainerGap()));
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
														.addComponent(jLabel3)
														.addComponent(
																eFileInPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(inputBtn))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel6)
														.addComponent(
																vertexFileInPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																vertexInBtn))
										.addGap(0, 0, Short.MAX_VALUE)));

		jPanel4.setBorder(javax.swing.BorderFactory
				.createTitledBorder("OUTPUT"));

		jLabel4.setText("Output:");

		outputBtn.setText("Browse");

		asci.setText("ASCII-TEXT");
		asci.setSelected(true);

		csv.setText("CSV");
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(asci);
		buttonGroup.add(csv);

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
										.addComponent(jLabel4)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												outputPath,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												188,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(outputBtn)
										.addGap(0, 51, Short.MAX_VALUE))
						.addGroup(
								jPanel4Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel4Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(asci)
														.addComponent(csv))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
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
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																outputPath,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel4)
														.addComponent(outputBtn))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(asci)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(csv)));

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
										.addComponent(
												jPanel3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel4,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		jPanel2Layout
				.setVerticalGroup(jPanel2Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel2Layout
										.createSequentialGroup()
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												jPanel2Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																jPanel3,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																jPanel4,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))));

		jPanel5.setBorder(javax.swing.BorderFactory
				.createTitledBorder("RUN ALGORITHM"));

		startBtn.setText("Start");
		startBtn.setActionCommand("start");
		startBtn.addActionListener(this);

		jLabel5.setText("Progress");
		jLabel5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

		jLabel6.setText("Vertices: ");
		jLabel6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

		timeLabel.setText("");

//		System.out.println(javax.swing.UIManager.getDefaults()
//				.getColor("Button.background").getRGB());
		progressLabel.setForeground(new Color(javax.swing.UIManager
				.getDefaults().getColor("Button.background").getRGB()));
		progressLabel.setText("1 out of 1000 networks processed");

		javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout
				.setHorizontalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												startBtn,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												88,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(34, 34, 34)
										.addComponent(jLabel5)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																progressLabel)
														.addGroup(
																jPanel6Layout
																		.createSequentialGroup()
																		.addComponent(
																				totalProgress,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				329,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				timeLabel)))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel5)
														.addComponent(startBtn)
														.addComponent(
																totalProgress,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																27,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(timeLabel))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(progressLabel)
										.addContainerGap()));
		exportHtmBtn.setText("Export HTML");

		reportArea.setColumns(20);
		reportArea.setRows(5);
		jScrollPane1.setViewportView(reportArea);

		javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(
				jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout
				.setHorizontalGroup(jPanel7Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel7Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(exportHtmBtn)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPane1)
										.addContainerGap()));
		jPanel7Layout.setVerticalGroup(jPanel7Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel7Layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(exportHtmBtn)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
				.addGroup(
						jPanel7Layout
								.createSequentialGroup()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 10, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		jPanel5Layout
				.setVerticalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel5Layout
										.createSequentialGroup()
										.addComponent(
												jPanel6,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel7,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		closeBtn.setText("Close");
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
		helpBtn.setText("Help");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												false)
												.addComponent(
														jPanel5,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														jPanel2,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))
								.addGap(0, 0, Short.MAX_VALUE))
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(helpBtn,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										86,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(closeBtn,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										85,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(jPanel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jPanel5,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(closeBtn)
												.addComponent(helpBtn))));

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
			java.util.logging.Logger.getLogger(StartWarswapUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(StartWarswapUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(StartWarswapUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(StartWarswapUI.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				StartWarswapUI startWarswapUI = new StartWarswapUI();
				startWarswapUI.setTitle("WaRSwap Software Application");
				startWarswapUI.setLocationByPlatform(true);
				Toolkit tk = Toolkit.getDefaultToolkit();
				Dimension screenSize = tk.getScreenSize();
				final int WIDTH = screenSize.width;
				final int HEIGHT = screenSize.height;
				startWarswapUI.setLocation(WIDTH / 7, HEIGHT / 4);
				startWarswapUI.setVisible(true);

			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JRadioButton asci;
	private javax.swing.JButton closeBtn;
	private javax.swing.JRadioButton csv;
	private javax.swing.JButton exportHtmBtn;
	private javax.swing.JButton helpBtn;
	private javax.swing.JButton inputBtn;
	private javax.swing.JButton outputBtn;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextField eFileInPath;
	// private javax.swing.JProgressBar netProgress;
	private javax.swing.JTextField outputPath;
	private javax.swing.JTextField randNetNum;
	private javax.swing.JTextArea reportArea;
	private javax.swing.JButton startBtn;
	private javax.swing.JComboBox subgraphSize;
	private javax.swing.JLabel timeLabel;
	private javax.swing.JProgressBar totalProgress;
	private javax.swing.JTextField vertexFileInPath;
	private javax.swing.JButton vertexInBtn;
	private javax.swing.JCheckBox hasSelfloops;
	private javax.swing.JLabel progressLabel;

	// End of variables declaration
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			totalProgress.setValue(progress);
			// reportArea.append(String.format("...\n",
			// task.getProgress()));
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("start")) {
			String eFileIn = eFileInPath.getText();
			if (eFileIn == null || eFileIn.equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Edge input file is empty!");
				return;
			}
			String vFileIn = vertexFileInPath.getText();
			if (vFileIn == null || vFileIn.equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null,
						"Vertex input file is empty!");
				return;
			}
			if (eFileIn.equalsIgnoreCase(vFileIn)) {
				JOptionPane.showMessageDialog(startWarswapUI,
						"Edge and Vertex files are the same!");
				return;
			}
			if (!Utils.isNumeric(randNetNum.getText())) {
				JOptionPane.showMessageDialog(startWarswapUI, "Random Number is not Numeric!!");
				return;
			}
			if (outputPath.getText() == null || outputPath.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(startWarswapUI, "Error! Outpit path is empty!!");
				return;
			}
			if (new File(outputPath.getText()).exists()) {
				int response = JOptionPane.showConfirmDialog(startWarswapUI,
						"Warning! File exists! Do you want to overwrite it? ", "Warning", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == 1)
					return;
			}
//			System.out.println("----");
			startBtn.setEnabled(false);
			exportHtmBtn.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				task = new Task();
				task.addPropertyChangeListener(this);
				task.execute();
			} catch (Exception ex) {
//				ex.printStackTrace();
				logger.error(ex.getMessage());
				task.cancel(true);
			}
		}
	}
}
