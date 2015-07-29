package warswap;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class TimeElapsed extends JFrame {
	JLabel time;

	long startTime = System.currentTimeMillis();

	TimeElapsed() {
		setSize(380, 200);
		setTitle("http://simpleandeasycodes.blogspot.com/");
		setLocation(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());

		time = new JLabel("");

		time.setFont(new Font("SansSerif", Font.BOLD, 36));

		time.setForeground(Color.MAGENTA);

		add(time);

		// starting new Thread which will update time
		new Thread(new Runnable() {
			public void run() {
				try {
					updateTime();
				} catch (Exception ie) {
				}
			}
		}).start();
	}

	public void updateTime() {
		try {
			while (true) {
				// geting Time in desire format
				time.setText(getTimeElapsed());
				// Thread sleeping for 1 sec
				Thread.currentThread().sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Exception in Thread Sleep : " + e);
		}
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

	public static void main(String[] args) {
		JFrame obj = new TimeElapsed();
		obj.setVisible(true);
	}
}
