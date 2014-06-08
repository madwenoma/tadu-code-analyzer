package com.taduk.analyzer.svn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.taduk.analyzer.util.OSCheckUtil;
import com.taduk.analyzer.util.SvnKitUtil;

public class SvnAnalyzerTools {
	
	private SvnAnalyzerTools(){}
	
	/**
	 * 根据url取得当前最新版本号与上一次版本之间的差异
	 * 可通过cmd或者svnkit http://wenku.baidu.com/view/2f93208483d049649b665881.html
	 * 无论哪种都需要将项目checkout到本地副本
	 * （java文件变动、shtml、spring-xml、mybatis-xml、log4j-xml、jar、classpath、css、js、png、properties等）
	 * @param url
	 * @return 返回的是所有的变动组成的一个长字符串，按照其中Index"分隔的多个字符串块,比如：
	   
	    Index: E:/eccentric_personal_files/tadu_work_center/tadu_wordspace_indigo/taducore_cios_druid_redisFailover/test/com/tywire/tadu/test/facade/VirtualMoneyFacadeTest.java
		===================================================================
		--- E:/eccentric_personal_files/tadu_work_center/tadu_wordspace_indigo/taducore_cios_druid_redisFailover/test/com/tywire/tadu/test/facade/VirtualMoneyFacadeTest.java	(revision 75253)
		+++ E:/eccentric_personal_files/tadu_work_center/tadu_wordspace_indigo/taducore_cios_druid_redisFailover/test/com/tywire/tadu/test/facade/VirtualMoneyFacadeTest.java	(revision 75333)
		@@ -18,6 +18,6 @@
 
 			@Test
 			public void toReceiveVirtualMoney() {
		-		System.out.println(VirtualMoneyFacade.toReceiveVirtualMoney(3423432, 12, 2000));
		+		System.out.println(VirtualMoneyFacade.toReceiveVirtualMoney(3423432, 12, 2000, 1));
 			}
 		}
	 
	 */

	public static List<String> getChangedContentBlocks(String svnUrl, int startRevision, int endRevision) {
		String diffResultStr = SvnKitUtil.doDiff(svnUrl, startRevision, endRevision);
		if (StringUtils.isNotEmpty(diffResultStr)) {
			List<String> changedContentBlocks = new ArrayList<String>();
			diffResultStr = StringUtils.substringAfter(diffResultStr, "Index: ");
			String[] diffResultLines = diffResultStr.split("Index: ");
			changedContentBlocks = Arrays.asList(diffResultLines);
			return changedContentBlocks;
		}
		return null;
	}

	/**
	 * 解析svn的diff结果，找出所有有变动的文件：java、xml等，并返回一个集合
	 * @param svnDiffResult
	 */
	public static List<ChangedResource> analyzeSvnDiffResult(String proLocalPath, String svnURL,int startRevision, int endRevision) {
		List<String> svnChangedBlocks = getChangedContentBlocks(svnURL, startRevision, endRevision);
		List<ChangedResource> changeResources = new ArrayList<ChangedResource>();// TODO
		for (String block : svnChangedBlocks) {
			String fileSuffix = getFileSuffix(block);
			changeResources.add(SvnAnalyzerFactory.createSvnAnalyzer(fileSuffix).analyzer(proLocalPath, block));
		}
		return changeResources;
	}
	
	/**
	 * 获取文件后缀
	 * @param svnChangedContent
	 * @return
	 */
	private static String getFileSuffix(String svnChangedContent) {
		return StringUtils.substringAfterLast(StringUtils.substringBefore(svnChangedContent, OSCheckUtil.LINE_BREAK), ".");
	}
	
	public static void main(String[] args) {
		analyzeSvnDiffResult("", "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core", 76075, 76150);
	}
}
