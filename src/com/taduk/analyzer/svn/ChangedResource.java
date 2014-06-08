package com.taduk.analyzer.svn;


public class ChangedResource  {
	private String resourcePath;

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public ChangedResource(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	public ChangedResource() {
		
	}

}
