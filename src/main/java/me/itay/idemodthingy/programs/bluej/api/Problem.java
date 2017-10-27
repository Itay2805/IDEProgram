package me.itay.idemodthingy.programs.bluej.api;

public class Problem {
	
	private int line, column, length;
	private String error;
	private int color;
	
	public Problem(int line, int column, int length, String error, int color) {
		this.line = line;
		this.column = column;
		this.length = length;
		this.error = error;
		this.color = color;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public int getLength() {
		return length;
	}

	public String getError() {
		return error;
	}

	public int getColor() {
		return color;
	}
	
}
