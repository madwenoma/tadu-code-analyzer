package com.taduk.analyzer.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public final class TaduJavaProject {
	public static final String WEB_PRO_NAME = "tadu-web-analyzer";
	public static final String CORE_PRO_NAME = "tadu-core-analyzer";

	/**/
	public static final IWorkspaceRoot ROOT = ResourcesPlugin.getWorkspace().getRoot();
	
	public static final IProject WEB_IPROJECT = ROOT.getProject(Constants.WEB_PRO_NAME);
	public static final IProject CORE_IPROJECT = ROOT.getProject(Constants.CORE_PRO_NAME);
	/**/
	public static final IJavaProject WEB_JAVAPROJECT =  JavaCore.create(WEB_IPROJECT);
	public static final IJavaProject CORE_JAVAPROJECT = JavaCore.create(CORE_IPROJECT);

	private TaduJavaProject() {

	}
	
}
