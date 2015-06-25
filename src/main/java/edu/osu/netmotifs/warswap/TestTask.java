package edu.osu.netmotifs.warswap;

import java.util.concurrent.Callable;

public class TestTask implements Callable<String> {

	private int i;
	
	public TestTask(int i) {
		this.i = i;
	}
	@Override
	public String call() throws Exception {
		System.out.println("Call " + i);
		return String.valueOf(i);
	}

}
