package com.taduk.analyzer.util;

public class TypeLine {
	String typeName;
	int startLine;
	int endLine;

	public TypeLine(String typeName, int startLine, int endLine) {
		super();
		this.typeName = typeName;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	

}
