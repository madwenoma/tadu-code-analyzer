package com.taduk.analyzer.util;

import org.apache.commons.lang.StringUtils;

public class ChangedContentParseUtils {
	
	private static final String ADDED_LINE_START_IDENTIFIER = "+";
	private static final String DEL_LINE_START_IDENTIFIER = "-";
	private static final String BLANK_STR = " ";
	
//	public static String getResourceFilePath(String changedContent) {
//		return StringUtils.substringBetween(changedContent, "Index: ", "\n");
//	}
	
	// @@ -200,7 +240,7 @@ 
	public static ChangedJavaBodyLineInfo getLineInfo(String descStr) {
		ChangedJavaBodyLineInfo info = new ChangedJavaBodyLineInfo();
		try {
			String tempStr = StringUtils.remove(descStr, '@');
			tempStr = StringUtils.remove(tempStr, DEL_LINE_START_IDENTIFIER);
			tempStr = StringUtils.remove(tempStr, ADDED_LINE_START_IDENTIFIER);
			String[] lineInfos = tempStr.trim().split(BLANK_STR);

			String[] lineInfo = lineInfos[0].split(",");
			info.setLineNumBeforeChange(Integer.parseInt(lineInfo[0]));
			info.setLineNumCountBeforeChange(Integer.parseInt(lineInfo[1]));

			lineInfo = lineInfos[1].split(",");
			info.setLineNumAfterChange(Integer.parseInt(lineInfo[0]));
			info.setLineNumCountAfterChange(Integer.parseInt(lineInfo[1]));

		} catch (Exception e) {
			// TODO: handle exception
		}
		return info;
	}
}
