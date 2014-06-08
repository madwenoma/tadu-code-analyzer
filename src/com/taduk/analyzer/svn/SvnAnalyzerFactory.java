package com.taduk.analyzer.svn;

import java.util.HashMap;

/**
 * 
 * @author LIFEI
 * 
 */
public class SvnAnalyzerFactory {
	public static HashMap<String, SvnAnalyzer> svnAnalyzers = new HashMap<String, SvnAnalyzer>();

	static {
		SvnStaticAnalyzer staticAnalyzer = new SvnStaticAnalyzer();
		svnAnalyzers.put("java", new SvnJavaAnalyzer());
		svnAnalyzers.put("xml", new SvnXmlAnalyzer());
		svnAnalyzers.put("css", staticAnalyzer);
		svnAnalyzers.put("js", staticAnalyzer);
		svnAnalyzers.put("shtml", staticAnalyzer);
	}

	/**
	 * 根据某个文件的变动内容，找到文件后缀，进而找到对应的analyzer
	 * 
	 * @param svnChangedContent
	 * @return
	 */
	public static SvnAnalyzer createSvnAnalyzer(String fileSuffix) {
		return svnAnalyzers.get(fileSuffix);
	}
}
