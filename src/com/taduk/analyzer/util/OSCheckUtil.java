package com.taduk.analyzer.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class OSCheckUtil { 
	
	
	private static final String LINE_BREAK_IDENTIFIER_MAC = "\n";
	private static final String LINE_BREAK_IDENTIFIER_NORMAL = "\r\n";
	
	private static final String PATH_IDENTIFIER_MAC = "/";
	private static final String PATH_IDENTIFIER_NORMAL = "\\";

	public static final String LINE_BREAK = isMac() ? LINE_BREAK_IDENTIFIER_MAC : LINE_BREAK_IDENTIFIER_NORMAL;
	
	public static final String PATH_IDENTIFIER =  isMac() ? PATH_IDENTIFIER_MAC : PATH_IDENTIFIER_NORMAL;

	private static boolean isMac(){
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		return StringUtils.isNotEmpty(os) && os.indexOf("Mac") > 0;
	}
	
	public static void main(String[] args) {
		System.out.println(LINE_BREAK);
	}
}
