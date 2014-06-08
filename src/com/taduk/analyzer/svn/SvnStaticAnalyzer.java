package com.taduk.analyzer.svn;

import com.taduk.analyzer.util.OSCheckUtil;



public class SvnStaticAnalyzer implements SvnAnalyzer{

	/**
	 * 解析静态资源文件，如css、js、img等
	 */
	@Override
	public ChangedResource analyzer(String proLocalPath, String filePath) {
		filePath = filePath.split(OSCheckUtil.LINE_BREAK)[0];
		return new ChangedResource(filePath);
	}

}
