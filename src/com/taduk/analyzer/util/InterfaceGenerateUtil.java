package com.taduk.analyzer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

public class InterfaceGenerateUtil {
	public static void main(String[] args) throws IOException {
		countMethod();
		
//		countMethod();
//		Collection<File> files = FileUtils.listFiles(new File("E:\\eccentric_personal_files\\tadu_work_center\\tadu_work_space\\tadu-dubbo-core-real\\src\\main\\java\\com\\tywire\\tadu\\facade"), new String[]{"java"}, true);
		/*Collection<File> files = FileUtils.listFiles(new File("E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\remote\\service\\charge"), new String[]{"java"}, true);
		for (File file : files) {
			ASTUtilForDubbo.generateInterface(file.getAbsolutePath());
		}*/
		modifyFacade();
//		ASTUtilForDubbo.generateInterface("E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore_cios_druid_redisFailover\\src\\com\\tywire\\tadu\\facade\\ActivityFacade.java");
	}
	
	private static void countMethod() {
		
		String[] packages = new String[]{
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\cache",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\cache\\key",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\cache\\queue",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\cache\\queue\\opt",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\cache\\refresh",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\core\\constants",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\core\\memcached",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\dao",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\facade",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\facade\\helper",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\mapper\\master",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\mapper\\slave",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\mybatis\\handlers",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\redis",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\service",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\utils",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\utils\\config",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\utils\\init",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\utils\\service",
				"E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\utils\\thread"
		};
		
		int totalMethodCountInDubboCore = 0;
		int totalClassCountInDubboCore = 0;
		for (String packagePath : packages) {
			Collection<File> files = FileUtils.listFiles(new File(packagePath), new String[]{"java"}, false);
//		
			int totalMethod = 0;
			int totalClassCount = 0;
			for (File file : files) {
				int[] countResult = ASTUtilForDubbo.countMethod(file.getAbsolutePath());
				totalMethod += countResult[1];
				totalClassCount +=  countResult[0];
			}
			System.out.println(packagePath + "					类总数：" + totalClassCount + "				方法总数：" + totalMethod);
			totalMethodCountInDubboCore += totalMethod;
			totalClassCountInDubboCore += totalClassCount;
		}
		
		System.out.println("方法总数：" + totalMethodCountInDubboCore);
		System.out.println("类总数：" + totalClassCountInDubboCore);
	}
	
	
	/**
	 * 修改内容：加入implements和方法去除static，public static的还要加入override注解
	 * @throws IOException
	 */
	private static void modifyFacade() throws IOException {
//		Collection<File> facadeFiles = FileUtils.listFiles(new File("E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore_cios_druid_redisFailover\\src\\com\\tywire\\tadu\\facade"), new String[]{"java"}, false);
		Collection<File> facadeFiles = FileUtils.listFiles(new File("E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore\\src\\com\\tywire\\tadu\\remote\\service\\charge"), new String[]{"java"}, false);
		List<String> newLines = new ArrayList<String>();
		for (File facadeFile : facadeFiles) {
			String simpleFileName = StringUtils.substringBefore(facadeFile.getName(), ".");
			for (LineIterator lines = FileUtils.lineIterator(facadeFile); lines.hasNext();) {
				String line =  lines.next();
				if(isFacadeClass(line)) {
					newLines.add("@Component");
					line = StringUtils.replace(line, "{", " implements I" + simpleFileName + "{");
				}
				if(isPublicStaticMethod(line)) {
					newLines.add("	@Override");
					line = StringUtils.replace(line, "public static", "public");
				} else if (isPrivateStaticMethod(line)) {
					line = StringUtils.replace(line, "private static", "private");
				}
				newLines.add(line);
			}
			try {
				IOUtils.writeLines(newLines, null, new FileOutputStream(new File("c://newFacades//"  + simpleFileName + ".java")), "utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(1);
			newLines.clear();
		}
		
	}
	
	private static boolean isPublicStaticMethod(String line) {
		return line.contains("(") && line.contains("public static") && !line.contains("=") &&  !line.contains("//") &&  !line.contains("*");
	}
	
	private static boolean isPrivateStaticMethod(String line) {
		return line.contains("(") && line.contains("private static") && !line.contains("=") &&  !line.contains("//") &&  !line.contains("*");
	}


	private static boolean isFacadeClass(String classLineStr) {
		return classLineStr.contains("public")  && classLineStr.contains("class");
	}
	
}
