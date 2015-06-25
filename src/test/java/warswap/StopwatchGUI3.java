package warswap;

	import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

	public class StopwatchGUI3 extends JFrame {

	    private static final long serialVersionUID = 3545053785228009472L;

	    // GUI Components
	    private JPanel panel;
	    private JLabel timeLabel;

	    private JPanel buttonPanel;
	    private JButton startButton;
	    private JButton resetButton;
	    private JButton stopButton;

	    // Properties of Program.
	    private byte centiseconds = 0;
	    private byte seconds = 30;
	    private short minutes = 0;

	    private DecimalFormat timeFormatter;

	    private Timer timer;

	    public StopwatchGUI3() {
	        panel = new JPanel();
	        panel.setLayout(new BorderLayout());

	        timeLabel = new JLabel();
	        timeLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
	        timeLabel.setHorizontalAlignment(JLabel.CENTER);
	        panel.add(timeLabel);

	        buttonPanel = new JPanel();
	        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

	        startButton = new JButton("Start");
	        startButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {

	                timer.start();

	            }
	        });
	        buttonPanel.add(startButton);

	        resetButton = new JButton("Reset");
	        resetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {

	                timer.stop();

	                centiseconds = 0;
	                seconds = 30;
	                minutes = 0;

	                timeLabel.setText(timeFormatter.format(minutes) + ":"
	                        + timeFormatter.format(seconds) + "."
	                        + timeFormatter.format(centiseconds));
	            }
	        });

	        buttonPanel.add(resetButton);

	        stopButton = new JButton("Stop");
	        stopButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                timer.stop();
	            }
	        });

	        buttonPanel.add(stopButton);

	        panel.add(buttonPanel, BorderLayout.SOUTH);

	        timeFormatter = new DecimalFormat("00");

	        timer = new Timer(10, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (centiseconds > 0) {
	                    centiseconds--;
	                } else {
	                    if (seconds == 0 && minutes == 0) {
	                        timer.stop();
	                    } else if (seconds > 0) {
	                        seconds--;
	                        centiseconds = 99;
	                    } else if (minutes > 0) {
	                        minutes--;
	                        seconds = 59;
	                        centiseconds = 99;
	                    }
	                }
	                timeLabel.setText(timeFormatter.format(minutes) + ":"
	                        + timeFormatter.format(seconds) + "."
	                        + timeFormatter.format(centiseconds));
	            }
	        });

	        timeLabel.setText(timeFormatter.format(minutes) + ":"
	                + timeFormatter.format(seconds) + "."
	                + timeFormatter.format(centiseconds));

	        add(panel);

	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	        setTitle("StopwatchGUI.java");

	        pack();
	        setVisible(true);
	    }

	    public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                } catch (Exception ex) {
	                }

	                new StopwatchGUI3();
	            }
	        });
	    }
	}
