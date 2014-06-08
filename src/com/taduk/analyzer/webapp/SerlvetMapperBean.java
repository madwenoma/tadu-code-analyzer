package com.taduk.analyzer.webapp;


public class SerlvetMapperBean {
	private String urlPattern;
	private String className;
	
	public SerlvetMapperBean() {
		super();
	}

	public SerlvetMapperBean(String urlPattern, String className) {
		super();
		this.urlPattern = urlPattern;
		this.className = className;
	}


	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}