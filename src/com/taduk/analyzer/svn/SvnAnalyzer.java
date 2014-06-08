package com.taduk.analyzer.svn;



/**
 * 根据svn地址找出当前版本与上一版本的差别，
 * 输入：项目svn地址（2个？core与业务项目？）
 * 输出：基本上结果就是所有变动的类及方法（集合），
 * 分别对应：
 * java文件改动 	----   	java类变动的内容，类和方法
 * shtml		----   	引用改模板的java类（web项目）。包含关系怎么处理？
 * spring-xml   ----   	mybatis、配置文件、数据库、事务等，前两个输出变动的类和方法，后两个可以忽略
 * mybatis-xml  ----   	映射文件，解析xml，找出变动的类对应的具体的java类和方法
 * log4j-xml    ----   	略
 * jar          ----   	略
 * classpath    ----   	略
 * 
 * css          ----   	应用的模板，对应的java类
 * js           ----   	同上
 * img          ----   	同上
 * 
 * properties   ----   	搜索修改了的配置项，在项目中对应的java类和方法及其调用
 * 目录变更？          		
 *    新增               ----		新增文件
 *    改名		----	
 *    删除		----
 * 
 * 
 * 
 * @author LIFEI
 * 
 *
 */
public interface SvnAnalyzer {
	
	ChangedResource analyzer(String proLocalPath, String changedContent);
}
