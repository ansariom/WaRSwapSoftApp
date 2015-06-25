package edu.osu.netmotifs.warswap.significance;

public class SubGraph {
	private long count = 0;
	private long realCount = 0;
	
	public long getRealCount() {
		return realCount;
	}
	public void setRealCount(long realCount) {
		this.realCount = realCount;
	}
	private String subgId;
	private double frequency;
	private double sd;
	private double zscore;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getSubgId() {
		return subgId;
	}
	public void setSubgId(String subgId) {
		this.subgId = subgId;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public double getSd() {
		return sd;
	}
	public void setSd(double sd) {
		this.sd = sd;
	}
	public double getZscore() {
		return zscore;
	}
	public void setZscore(double zscore) {
		this.zscore = zscore;
	}
}
