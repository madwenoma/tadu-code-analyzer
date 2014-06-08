package com.taduk.analyzer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * 封装了ecliopse运行时的几个功能
 * 		导入项目
 * 		设置项目关联
 * 		搜索：java搜索和file搜索
 * @author LIFEI
 *
 */
public class JDTUtils {

	/**
	 * 导入项目
	 * 
	 * @param projectPath
	 *            如：E:/eccentric_personal_files/git-folder/pro-for-learn-test"
	 */
	public static void importProject(final String projectPath) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					IPath projectDotProjectFile = new Path(projectPath + "/.project");
					IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
					IProject project = workspace.getRoot().getProject(projectDescription.getName());
					JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(), null);
					// project.create(null);
				} catch (CoreException e) {
					e.printStackTrace();
					// TODO 如果导入项目失败，插件如何表现？（后续的查找方法调用不能工作）
				}
			}
		};
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(runnable);
	}
	
	/**
	 * 调用示例：
	 * @param project
	 * @param searchContent
	 * @param fileNamePatterns  如 *.java
	 * @return
	 */
	public static List<IPath> doFileSearch(IProject project, String searchContent, String fileNamePatterns) {
		final List<IPath> matchFiles = new ArrayList<IPath>();
		Pattern pattern = Pattern.compile(searchContent);
		TextSearchRequestor requestor = new TextSearchRequestor() {
			public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) {
				matchAccess.getFile().getLocation();
				matchFiles.add(matchAccess.getFile().getLocation());
				return true;
			}

			public boolean acceptFile(IFile file) throws CoreException {
				return true;
			}
		};
		
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(new IResource[] { project }, new String[]{fileNamePatterns}, true);
		TextSearchEngine tsEngine = TextSearchEngine.create();
		tsEngine.search(scope, requestor, pattern, null);
		return matchFiles;
	}
	
	
	
	/**
	 * java type search
	 * @param projectName
	 * @param searchContent
	 * @return
	 * @throws JavaModelException
	 */
	public static List<SearchMatch> doJavaSearch(IJavaProject javaProject, final String searchContent) {
		final List<SearchMatch> matchs = new ArrayList<SearchMatch>();

		IPackageFragment[] packages;
		try {
			packages = javaProject.getPackageFragments();

		SearchPattern pattern = SearchPattern.createPattern(searchContent,
				IJavaSearchConstants.METHOD,
				IJavaSearchConstants.REFERENCES,
				SearchPattern.R_PREFIX_MATCH);

		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(packages);
		//define a result collector
		SearchRequestor requestor = new SearchRequestor() {
			public void acceptSearchMatch(SearchMatch match) {
				System.out.println(match.getElement());
//				if (match.getResource().getName().lastIndexOf(searchContent) > -1)
				matchs.add(match);
			}
		};

		//start searching
		SearchEngine searchEngine = new SearchEngine();
			searchEngine.search(pattern,
					new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
					requestor, null /* progress monitor */);
		} catch (CoreException e) {
		}
		return matchs;
	}
	
	
	///////////////////////////////本方法仅供参考//////////////////////////////////////////
	public static List<SearchMatch> searchClass(final String clsName) {
		final List<SearchMatch> matchs = new ArrayList<SearchMatch>();
		SearchRequestor requestor = new SearchRequestor() {
			@Override
			public void acceptSearchMatch(SearchMatch match)
					throws CoreException {
				if (match.getResource().getName().lastIndexOf(clsName) > -1)
					matchs.add(match);
			}
		};
		SearchPattern pattern = SearchPattern.createPattern(clsName,
				IJavaSearchConstants.CLASS, IJavaSearchConstants.DECLARATIONS,
				SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE);

		IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		try {
			new SearchEngine().search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope, requestor, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matchs;
	}


	/**
	 * 重新设置项目的classpath，加入dependency project
	 * （未使用）
	 * @throws JavaModelException
	 */
	public static void importProjectWithLinkSource() throws JavaModelException {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tadu-android");
					IJavaProject javaProject = JavaCore.create(project);
					try {
						IClasspathEntry requiredProject = JavaCore.newProjectEntry(new Path("/taducore"), true); // exported
						IClasspathEntry[] entries = javaProject.getRawClasspath();
						IClasspathEntry[] newClasspath = Arrays.copyOf(entries, entries.length + 1);
						newClasspath[entries.length] = requiredProject;		
						javaProject.setRawClasspath(newClasspath, new NullProgressMonitor ());
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					/*	IClasspathEntry varEntry = JavaCore.newVariableEntry(new Path(
							"HOME/foo.jar"), // library location
							new Path("SRC_HOME/foo_src.zip"), // source archive location
							new Path("SRC_ROOT"), // source archive root path
							true); // exported
					JavaCore.getClasspathVariableNames();
					try {
						JavaCore.setClasspathVariable("HOME", new Path("d:/myInstall"), null);
						javaProject.setRawClasspath(newClasspath, null);
					} catch (JavaModelException e) {
						e.printStackTrace();
					} */
				}
			};
			IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getDisplay().syncExec(runnable);
		}
}
