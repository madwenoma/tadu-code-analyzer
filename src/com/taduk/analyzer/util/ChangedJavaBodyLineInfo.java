package com.taduk.analyzer.util;

public class ChangedJavaBodyLineInfo {
	private int lineNumBeforeChange;
	private int lineNumCountBeforeChange;
	private int lineNumAfterChange;
	private int lineNumCountAfterChange;

	
	public int getLineNumBeforeChange() {
		return lineNumBeforeChange;
	}


	public void setLineNumBeforeChange(int lineNumBeforeChange) {
		this.lineNumBeforeChange = lineNumBeforeChange;
	}


	public int getLineNumCountBeforeChange() {
		return lineNumCountBeforeChange;
	}


	public void setLineNumCountBeforeChange(int lineNumCountBeforeChange) {
		this.lineNumCountBeforeChange = lineNumCountBeforeChange;
	}


	public int getLineNumAfterChange() {
		return lineNumAfterChange;
	}


	public void setLineNumAfterChange(int lineNumAfterChange) {
		this.lineNumAfterChange = lineNumAfterChange;
	}


	public int getLineNumCountAfterChange() {
		return lineNumCountAfterChange;
	}


	public void setLineNumCountAfterChange(int lineNumCountAfterChange) {
		this.lineNumCountAfterChange = lineNumCountAfterChange;
	}


	@Override
	public String toString() {
		return "ChangedJavaBodyLineInfo [lineNumBeforeChange="
				+ lineNumBeforeChange + ", lineNumCountBeforeChange="
				+ lineNumCountBeforeChange + ", lineNumAfterChange="
				+ lineNumAfterChange + ", lineNumCountAfterChange="
				+ lineNumCountAfterChange + "]";
	}
}