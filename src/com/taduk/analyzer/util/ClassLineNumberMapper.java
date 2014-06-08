package com.taduk.analyzer.util;

import java.util.ArrayList;
import java.util.List;

public class ClassLineNumberMapper {
	public List<TypeLine> fieldLineNumbers = new ArrayList<TypeLine>();
	public List<TypeLine> importLineNumbers = new ArrayList<TypeLine>();
	public List<TypeLine> methodLineNumbers = new ArrayList<TypeLine>();
	public List<TypeLine> commentLineNumbers = new ArrayList<TypeLine>();
}