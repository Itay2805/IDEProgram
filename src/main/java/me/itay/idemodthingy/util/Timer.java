package me.itay.idemodthingy.util;

public class Timer {
	
	private long start;
	
	public Timer() {
		
		reset();
	}
	
	public void reset() {
		start = System.currentTimeMillis();
	}
	
	public float elapsed() {
		return (System.currentTimeMillis() - start) / 1000.0f;
	}
	
}
