package com.taduk.analyzer.svn;

import java.util.List;

/**
 * 解析的变动对象
 * @author LIFEI
 *
 */
public class ChangedJavaResource extends ChangedResource{
	
	private String classNameWithPkg;
	private List<String> methodNames; // 方法名
	private List<String> propertyNames; // 声明属性
	
	public List<String> getMethodNames() {
		return methodNames;
	}
	public void setMethodNames(List<String> methodNames) {
		this.methodNames = methodNames;
	}
	public List<String> getPropertyNames() {
		return propertyNames;
	}
	public void setPropertyNames(List<String> propertyNames) {
		this.propertyNames = propertyNames;
	}
	public String getClassNameWithPkg() {
		return classNameWithPkg;
	}
	public void setClassNameWithPkg(String classNameWithPkg) {
		this.classNameWithPkg = classNameWithPkg;
	}
	
}
