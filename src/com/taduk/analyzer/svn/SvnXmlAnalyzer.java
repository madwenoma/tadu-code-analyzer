package com.taduk.analyzer.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.taduk.analyzer.util.ChangedContentParseUtils;
import com.taduk.analyzer.util.ChangedJavaBodyLineInfo;
import com.taduk.analyzer.util.OSCheckUtil;
import com.taduk.analyzer.util.XMLParser;


/**
 * 解析xml
 * TODO 应该还要细分 spring配置、mybatis配置
 * @author LIUBING
 *
 */
public class SvnXmlAnalyzer implements SvnAnalyzer{
//	String basePath = "D:/ty/tadu_code_analyzer/project/analyzer_core/";

	@Override
	public ChangedResource analyzer(String proLocalPath, String changedContent) {
		String lineBreakIdentifier = OSCheckUtil.LINE_BREAK;
		String pathIdentifier = OSCheckUtil.PATH_IDENTIFIER;
		String[] strList = changedContent.split(lineBreakIdentifier);
		XMLParser xmlParser = new XMLParser(new File(proLocalPath + pathIdentifier + StringUtils.trimToNull(strList[0])));
		System.out.println(xmlParser.getElementList().size());
		
		boolean flag = true;
		ChangedJavaBodyLineInfo lineInfo = null;
		ChangedJavaResource javaResource = new ChangedJavaResource();
		Set<String> changeIds = new HashSet<String>();
		int number = 0;
		for (int i = 0; i < strList.length; i++) {
			if(strList[i].startsWith("@@") && strList[i].endsWith("@@")){
				flag = false;
				lineInfo = ChangedContentParseUtils.getLineInfo(strList[i]);
			}
			if(flag){
				continue;
			}

			boolean isDeleteLine = strList[i].startsWith("-");
			boolean isAddLine = strList[i].startsWith("+");

			String ids = null;
			if (isDeleteLine || isAddLine) {
				int deleteLineNumber = lineInfo.getLineNumBeforeChange() + number;
				ids = xmlParser.getTheChangeXMLId(deleteLineNumber);
			}

			if (ids != null) {
				String[] idList = ids.split(",");
				for (int j = 0; j < idList.length; j++) {
					changeIds.add(idList[j]);
				}
			}

			number++;
		}

		List<String> methodNameList = new ArrayList<String>(changeIds);

		javaResource.setMethodNames(methodNameList);
		javaResource.setClassNameWithPkg(xmlParser.getClassNameWithPkg());
		
		return javaResource;
	}
	
	public static void main(String[] args) {
		
	}

}
