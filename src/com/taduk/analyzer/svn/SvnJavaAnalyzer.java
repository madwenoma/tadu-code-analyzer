package com.taduk.analyzer.svn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.taduk.analyzer.util.ASTUtil;
import com.taduk.analyzer.util.ChangedContentParseUtils;
import com.taduk.analyzer.util.ChangedJavaBodyLineInfo;
import com.taduk.analyzer.util.ClassLineNumberMapper;
import com.taduk.analyzer.util.OSCheckUtil;
import com.taduk.analyzer.util.TypeLine;

/**
 * 
 */
class AddedLine {
	private int lineNumInCurrentVersion;
	private String lineContent;

	public AddedLine(String lineContent, int lineNumInCurrentVersion) {
		this.lineContent = lineContent;
		this.lineNumInCurrentVersion = lineNumInCurrentVersion;
	}

	public int getLineNumInCurrentVersion() {
		return lineNumInCurrentVersion;
	}

	public void setLineNumInCurrentVersion(int lineNumInCurrentVersion) {
		this.lineNumInCurrentVersion = lineNumInCurrentVersion;
	}

	public String getLineContent() {
		return lineContent;
	}

	public void setLineContent(String lineContent) {
		this.lineContent = lineContent;
	}

	@Override
	public String toString() {
		return "AddedLine [lineContent=" + lineContent + "]";
	}
	
}

/**
 * 删除的变动，作如下处理：一个changed block中，将连续delete的多行看做一个整体，
 * 只记录这个连续删除块的第一行的上一行在当前version的行号 和这个连续删除块的最后一行的下一行在当前version的行号，
 * 如果这个连续删除块只有一行，那么就记录这一行的上下行号。
 */
class DeletedLine {
	private int preLineNumInCurrentVersion;
	private  String firstDeletedLineContent;
	private  int nextLineNumInCurrentVersion;
	private  String lastDeletedLineContent;
	private  String preLineContent;
	private  String nextUnDelLineContent;
	public int getPreLineNumInCurrentVersion() {
		return preLineNumInCurrentVersion;
	}
	public void setPreLineNumInCurrentVersion(int preLineNumInCurrentVersion) {
		this.preLineNumInCurrentVersion = preLineNumInCurrentVersion;
	}
	public String getFirstDeletedLineContent() {
		return firstDeletedLineContent;
	}
	public void setFirstDeletedLineContent(String firstDeletedLineContent) {
		this.firstDeletedLineContent = firstDeletedLineContent;
	}
	public int getNextLineNumInCurrentVersion() {
		return nextLineNumInCurrentVersion;
	}
	public void setNextLineNumInCurrentVersion(int nextLineNumInCurrentVersion) {
		this.nextLineNumInCurrentVersion = nextLineNumInCurrentVersion;
	}
	public String getLastDeletedLineContent() {
		return lastDeletedLineContent;
	}
	public void setLastDeletedLineContent(String lastDeletedLineContent) {
		this.lastDeletedLineContent = lastDeletedLineContent;
	}
	public String getPreLineContent() {
		return preLineContent;
	}
	public void setPreLineContent(String preLineContent) {
		this.preLineContent = preLineContent;
	}
	public String getNextUnDelLineContent() {
		return nextUnDelLineContent;
	}
	public void setNextUnDelLineContent(String nextUnDelLineContent) {
		this.nextUnDelLineContent = nextUnDelLineContent;
	}
	
}

public class SvnJavaAnalyzer implements SvnAnalyzer {

//	static final String HEADER_SEPARATOR = "===================================================================";
	private static final String LINE_CHANGE_IDENTIFIER 		= "@@";
	private static final String LINE_BREAK_IDENTIFIER 		= OSCheckUtil.LINE_BREAK;
	
	private static final String ADDED_LINE_START_IDENTIFIER = "+";
	private static final String DEL_LINE_START_IDENTIFIER 	= "-";
	private static final String EMPTY_STR 					= "";
	
	
	/**
	 * 解析java文件变动
	 */
	@Override
	public ChangedResource analyzer(String proLocalPath, String changedContentStr) {
		String resourceFilePath 		= StringUtils.substringBefore(changedContentStr, LINE_BREAK_IDENTIFIER);
		String classNameWithPkg 		= getClassNameWithPkg(resourceFilePath);
		String[] changedContentLines 	= changedContentStr.split(LINE_BREAK_IDENTIFIER);
		
		List<Integer> splitLines 		= new ArrayList<Integer>();
		List<AddedLine> addedLines 		= new ArrayList<AddedLine>();
		List<DeletedLine> deletedLines 	= new ArrayList<DeletedLine>();

		for (int p = 0; p < changedContentLines.length; p++) {
			if (changedContentLines[p].startsWith(LINE_CHANGE_IDENTIFIER)) {
				splitLines.add(p);
			}
		}
		splitLines.add(changedContentLines.length);
		
//		StringBuilder strBuilder = new StringBuilder();
		AddedLine addedLine = null;
		DeletedLine deletedLine = null;
		ChangedJavaBodyLineInfo info = null;
		
		for (int i = 0; i < splitLines.size() - 1; i++) {
			boolean delLineFlag = false;
			String lastDelLineContent = EMPTY_STR;
			String preLineBeforeFirstDelLine = EMPTY_STR;
			for (int lineTemp = 0, k = 0, j = splitLines.get(i); j < splitLines.get(i + 1); j++) {
				String changedContent = changedContentLines[j];
//				strBuilder.append(changedContent + LINE_BREAK_IDENTIFIER);
				if (changedContent.startsWith(LINE_CHANGE_IDENTIFIER)) {
					info = ChangedContentParseUtils.getLineInfo(changedContent);
				} else if (info != null) {
					if (changedContent.startsWith(DEL_LINE_START_IDENTIFIER)) {
						lastDelLineContent = changedContent;
						if (delLineFlag) {
							continue;
						}
						deletedLine = new DeletedLine();
						deletedLine.setPreLineContent(preLineBeforeFirstDelLine);
						deletedLine.setFirstDeletedLineContent(changedContent);
						System.out.println("删除块的第一行的上一个未删除行在当前version的line num：" + lineTemp);
						deletedLine.setPreLineNumInCurrentVersion(lineTemp);
						delLineFlag = true;
						preLineBeforeFirstDelLine = EMPTY_STR;
						k--;
					} else {
						lineTemp = info.getLineNumAfterChange() + k;
						preLineBeforeFirstDelLine = changedContent;
						if (delLineFlag) {
							deletedLine.setNextLineNumInCurrentVersion(lineTemp);
							deletedLine.setNextUnDelLineContent(changedContent);
							deletedLine.setLastDeletedLineContent(lastDelLineContent);
							System.out.println("最后一个删除行的内容：" + lastDelLineContent);
							deletedLines.add(deletedLine);
							lastDelLineContent = "";
						}
						if (changedContent.startsWith(ADDED_LINE_START_IDENTIFIER)){
							addedLine = new AddedLine(changedContent, lineTemp);
							addedLines.add(addedLine);
						}
						System.out.println("line num in currentFile:" + lineTemp);
						delLineFlag = false;
					}
					k++;
				}

			}
		}

		/*删除added 的import行*/
		Iterator<AddedLine> ite = addedLines.iterator();
		while (ite.hasNext()) {
			String tempStr = ite.next().getLineContent();
			if (tempStr.contains("+import ")) {
				ite.remove();
			}
		}
		ClassLineNumberMapper mapper = ASTUtil.parserJavaFile(proLocalPath + "/" + resourceFilePath);
//			List<TypeLine> importLineNumbers = mapper.importLineNumbers;
//			List<TypeLine> fieldLineNumbers = mapper.fieldLineNumbers;
		List<TypeLine> methodLineNumInfos = mapper.methodLineNumbers;
		List<TypeLine> commentLineNumInfos = mapper.commentLineNumbers;
		/*被影响的method*/
		HashSet<String> effectMethodNames = new HashSet<String>();
		/*验证add的行*/
		for (AddedLine addedLineInfo : addedLines) {
//			if (CollectionUtils.select(commentLineNumInfos, new PredicateImpl("typeName", addedLineStr.getLineContent())).size()>0){
//				continue;
//			}
			int addedLineNum = addedLineInfo.getLineNumInCurrentVersion();
			boolean commenLineFlag = false;
			for (TypeLine commentLineInfo : commentLineNumInfos) {
				int startCommentLineNum = commentLineInfo.getStartLine();
				int endCommentLineNum = commentLineInfo.getEndLine();
				if(addedLineNum >= startCommentLineNum && addedLineNum <= endCommentLineNum) { 
					String commentLineStr = commentLineInfo.getTypeName().trim();
					String addedLineContent = StringUtils.remove(addedLineInfo.getLineContent(), "+").trim();
					if(commentLineStr.equals(addedLineContent) || commentLineStr.contains(addedLineContent)) {
						commenLineFlag = true;
					}
				}
			}
			if (commenLineFlag) 
				continue;
			for (TypeLine methodLineInfo : methodLineNumInfos) {
				int startLineNum = methodLineInfo.getStartLine();
				int endLineNum = methodLineInfo.getEndLine();
				if(addedLineNum >= startLineNum && addedLineNum <= endLineNum) {
					System.out.println(addedLineInfo + " 行在处于方法体：" + methodLineInfo.getTypeName());
					effectMethodNames.add(methodLineInfo.getTypeName());
					continue;
				}	
			}
		}
		//TODO 删除行的判断逻辑？
		System.out.println(effectMethodNames);
		ChangedJavaResource changedJavaResource = new ChangedJavaResource();
		changedJavaResource.setClassNameWithPkg(classNameWithPkg);
		changedJavaResource.setResourcePath(resourceFilePath);
		changedJavaResource.setMethodNames(new ArrayList<String>(effectMethodNames));
		return changedJavaResource;
	}

	/**
	 * /src/com/tywire/tadu/facade/FlashSaleFacade.java
	 * @param resourceFilePath
	 * @return
	 */
	public String getClassNameWithPkg(String resourceFilePath) {
		String javaPkgPath = StringUtils.substringBetween(resourceFilePath, "src/", ".java");
		String classNameWithPkg = StringUtils.replace(javaPkgPath, "/", ".");
		return classNameWithPkg;
	}
	

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = FileUtils.getFile("c:/1.txt");
		String fileContent = IOUtils.toString(new FileInputStream(file), Charsets.UTF_8);
		System.out.println(fileContent);
		System.out.println(new SvnJavaAnalyzer().analyzer("", fileContent));
	}

}
