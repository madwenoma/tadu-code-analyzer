package tadu.code.analyzer.view;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import tadu.code.analyzer.call.CallHierarchyGenerator;
import tadu.code.analyzer.call.CalledMethodEntity;
import tadu.code.analyzer.call.TaduCoreMethodFilter;

import com.taduk.analyzer.svn.ChangedJavaResource;
import com.taduk.analyzer.svn.ChangedResource;
import com.taduk.analyzer.svn.SvnAnalyzerTools;
import com.taduk.analyzer.util.JDTUtils;
import com.taduk.analyzer.util.SvnKitUtil;
import com.taduk.analyzer.util.TaduJavaProject;
import com.taduk.analyzer.webapp.TaduServletMapper;

public class ResultTreeCodeAnalyzerView {

	protected Shell codeAnalyzerShell;
	private Text coreSvnUrlText;
	private Text webSvnUrlText;
	private Label label;
	private Menu menu;
	private MenuItem menuAbout;
	private Group group;
	private Label lblNewLabel;
	private Label callHierarchyLabel;
	private Tree callHierarchyTree;
	private TreeViewer callHierarchyTreeViewer;
	private Text tipConsoleText;
	private Text coreSvnStartRevision;
	private Text coreSvnEndRevision;
	private Text webSvnStartRevision;
	private Text webSvnEndRevision;
	private Tree resultURLTree;
	private TreeViewer resultURLTreeViewer;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ResultTreeCodeAnalyzerView window = new ResultTreeCodeAnalyzerView();
			window.open();
		} catch (Exception e) {
			//对于64位机，采用控制台输出，不能查看树形调用结构
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		codeAnalyzerShell.pack();
		codeAnalyzerShell.open();
		while (!codeAnalyzerShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		codeAnalyzerShell = new Shell();
		codeAnalyzerShell.setSize(610, 449);
		codeAnalyzerShell.setText("塔读代码变动分析工具");
		codeAnalyzerShell.setLayout(new GridLayout(10, false));
		
		Label coreSvnUrlLabel = new Label(codeAnalyzerShell, SWT.NONE);
		coreSvnUrlLabel.setText("core-svn-url");
		
		coreSvnUrlText = new Text(codeAnalyzerShell, SWT.BORDER);
		GridData gd_coreSvnUrlText = new GridData(SWT.FILL, SWT.CENTER, false, false, 7, 1);
		gd_coreSvnUrlText.widthHint = 306;
		coreSvnUrlText.setLayoutData(gd_coreSvnUrlText);
		
		coreSvnStartRevision = new Text(codeAnalyzerShell, SWT.BORDER);
		coreSvnStartRevision.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		coreSvnEndRevision = new Text(codeAnalyzerShell, SWT.BORDER);
		coreSvnEndRevision.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label webSvnUrlLabel = new Label(codeAnalyzerShell, SWT.NONE);
		webSvnUrlLabel.setText("web-svn-url");
		
		webSvnUrlText = new Text(codeAnalyzerShell, SWT.BORDER);
		webSvnUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 7, 1));
		
		webSvnStartRevision = new Text(codeAnalyzerShell, SWT.BORDER);
		webSvnStartRevision.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		webSvnEndRevision = new Text(codeAnalyzerShell, SWT.BORDER);
		webSvnEndRevision.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		
		Button startBtn = new Button(codeAnalyzerShell, SWT.NONE);
		startBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		startBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				ASTUtil.parserJavaFile("E:\\eccentric_personal_files\\tadu_work_center\\tadu_wordspace_indigo\\taducore_cios_druid_redisFailover\\src\\com\\tywire\\tadu\\facade\\AddTimelineFacade.java");
//				List<IPath> ipaths = JDTUtils.doFileSearch(TaduJavaProject.WEB_PROJECT, "v2.60/space.css", "*.shtml");
				
				String coreSvnUrl = coreSvnUrlText.getText();
				String webSvnUrl = webSvnUrlText.getText();
				coreSvnUrl = "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core";
				webSvnUrl = "http://svn.kaiqi.com/tadu/tech/server/tadu-server/android/trunk/android";
				System.out.println(coreSvnUrl);
				System.out.println(webSvnUrl);
				
				//checkout coreSvnUrl和webSvnUrl指向的svn资源，
				String coreProjectPath = SvnKitUtil.checkoutCoreProject(coreSvnUrl);
				System.out.println("coreProjectPath:"+coreProjectPath);
				String webProjectpath = SvnKitUtil.checkoutWebProject(webSvnUrl);
				System.out.println("webProjectpath:"+webProjectpath);
				
				//修改项目的名称，避免冲突
				SvnKitUtil.modifiedProjectName(coreProjectPath);
				SvnKitUtil.modifiedProjectName(webProjectpath);
				
				//将coreSvnUrl和webSvnUrl checkout出的的svn项目Import到workspace下
				JDTUtils.importProject(coreProjectPath);
				JDTUtils.importProject(webProjectpath);
				
				//找到core包的方法调用堆栈，最后
				final List<ChangedResource> coreChangedResources = SvnAnalyzerTools.analyzeSvnDiffResult(coreProjectPath, coreSvnUrl, 76537, 76505);
				tipConsoleText.setText("开始分析svn变动");
				
				//初始化web.xml和servlet的映射类
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Class.forName(TaduServletMapper.class.getCanonicalName());
						} catch (ClassNotFoundException e) {
							//ignore
						}
					}
				}).start();
				
				
				Future<CoreThreadReturnObj> future = startCoreThread(coreChangedResources);
				
				HashMap<String, ResultBean> resultBeans = new HashMap<String, ResultBean>();
			
				/**
				 * web项目变动
				 */
				CallHierarchyGenerator webCallGenerator  = new CallHierarchyGenerator(TaduJavaProject.WEB_JAVAPROJECT);
				ArrayList<CalledMethodEntity> cmEntities = new ArrayList<CalledMethodEntity>();
				List<ChangedResource> webChangedResources = SvnAnalyzerTools.analyzeSvnDiffResult(webProjectpath, webSvnUrl, 78320, 78289);
				
				class StaticResourceMapper { //该类包含了一个静态资源，及其使用了该资源的shtml模板集合
					public String staticResource;//静态资源地址，如css的地址
					public List<IPath> shtmls;//该css 在哪些shtml模板里使用了
					public StaticResourceMapper(String staticResource, List<IPath> shtmls) {
						this.staticResource = staticResource;
						this.shtmls = shtmls;
					}
				}
				
				List<StaticResourceMapper> staticResourceMappers = new ArrayList<StaticResourceMapper>();
				
				for (ChangedResource changedResource : webChangedResources) {
					if(changedResource instanceof ChangedJavaResource) {
						ChangedJavaResource changedJavaResource = (ChangedJavaResource)changedResource;
						String classNameWithPkg = changedJavaResource.getClassNameWithPkg();
						CalledMethodEntity cmEntity = null;
						for (String methodName : changedJavaResource.getMethodNames()) {
							cmEntity = webCallGenerator.getCallersRecursive(classNameWithPkg, methodName);
							cmEntities.add(cmEntity);
						}
						//TODO webw项目的java代码变化 如何找到最外层的servlet
					} else {
						String staticResourcePath = changedResource.getResourcePath();
						//根据路径获取到文件名的上一级路径（因为通常是带版本号）
						String pathWithPreDir = getStaticFileName(staticResourcePath);
						
						//shtml变化了,直接找出包含此shtml的servlet类路径
						//TODO 这里没有考虑模板项目引用的情况。
						if (staticResourcePath.indexOf(".shtml") > 0) {
							List<IPath> shtmllInServlets = JDTUtils.doFileSearch(TaduJavaProject.WEB_IPROJECT, pathWithPreDir, "*.java");
							
							for (IPath iPath : shtmllInServlets) {//TODO 该处没有考虑某些模板在父类servlet或其他非servlet类中的情况，此两种情况获取url的时候就会为空
								String servletName = getSimpleFileName(iPath);
								///////-------------------------------------------------------
								ResultBean rBean = resultBeans.get(servletName); 
								ResultBean shtmlResultBean = new ResultBean(pathWithPreDir, null);
								if(rBean == null) {
									rBean = new ResultBean(servletName, asList(shtmlResultBean));
									resultBeans.put(servletName, rBean);
								} else {
									rBean.getResultBeans().add(shtmlResultBean);
								}
							}
							
						} else {
							//找出除了shtml之外的静态文件如js、css、png、都在哪些模板里调用了.
							//TODO 注意静态资源路径，查找的时候按照路径找
							staticResourceMappers.add(new StaticResourceMapper(pathWithPreDir, JDTUtils.doFileSearch(TaduJavaProject.WEB_IPROJECT, pathWithPreDir, "*.shtml")));
						}
					}
				}
		
				for (StaticResourceMapper srMapper : staticResourceMappers) {
					String staticResource = srMapper.staticResource;//css jpg等静态 文件
					for (IPath shtmlPath : srMapper.shtmls) {
						String shtmlSimplePath = getSimpleFileName(shtmlPath);
						List<IPath> shtmllInServlets = JDTUtils.doFileSearch(TaduJavaProject.WEB_IPROJECT, shtmlSimplePath, "*.java");
						HashSet<IPath> shtmllInServletsDistinct = new HashSet<IPath>(shtmllInServlets);//去重，去掉重复的servlets搜索结果
						for (IPath servletIPath : shtmllInServletsDistinct) {
							String servletName = getSimpleFileName(servletIPath);
							ResultBean rBean = resultBeans.get(servletName); 
							ResultBean staticResultBean = new ResultBean(staticResource, null);
							ResultBean shtmlResultBean = new ResultBean(shtmlSimplePath, asList(staticResultBean));
							if(rBean == null) {
								rBean = new ResultBean(servletName, asList(shtmlResultBean));
								resultBeans.put(servletName, rBean);
							} else {
								if(rBean.getResultBeans() != null && rBean.getResultBeans().size() > 0) {
									rBean.getResultBeans().add(shtmlResultBean);
								} else {
									rBean.setResultBeans(asList(shtmlResultBean));
								}
							}
						}
					}
				}
				
				try {
					CoreThreadReturnObj returnObj = future.get();
					for (SearchModel searchModel : returnObj.javaResultsOfinvokedCoreMethod) {
						for (SearchMatch searchMatch : searchModel.searchResults) {
							if (searchMatch.getResource().getFullPath().toString().indexOf("Action") > 0) {
								String matchPath = searchMatch.getResource().toString();
								String servletName =  StringUtils.substringAfterLast(matchPath, "/");
								
								SourceMethod sm = (SourceMethod)searchMatch.getElement();
								ResultBean rBean = resultBeans.get(servletName); 
								ResultBean searchMethodResultBean = new ResultBean(searchModel.changedOuterMethod.getElementName(), null);
								ResultBean invokeResultBean = new ResultBean(sm.getElementName(), asList(searchMethodResultBean));
								
								if(rBean == null) {
									rBean = new ResultBean(servletName, asList(invokeResultBean));
									resultBeans.put(servletName, rBean);
								} else {
									if(rBean.getResultBeans() != null && rBean.getResultBeans().size() > 0) {
										rBean.getResultBeans().add(invokeResultBean);
									} else {
										rBean.setResultBeans(asList(invokeResultBean));
									}
								}
							}
						}
					}
					cmEntities.addAll(returnObj.coreMethodInvokedHierarchy);
				} catch (Exception e1) {
				}
				
				resultURLTreeViewer.setContentProvider(new ResultTreeViewContentProvider());
				resultURLTreeViewer.setLabelProvider(new ResultTreeViewerLableProvider());
				resultURLTreeViewer.setInput(new ArrayList<ResultBean>(resultBeans.values()));
				
				callHierarchyTreeViewer.setContentProvider(new TreeViewContentProvider());
				callHierarchyTreeViewer.setLabelProvider(new TreeViewerLableProvider());
				callHierarchyTreeViewer.setInput(cmEntities);
				
				tipConsoleText.append("\n结束分析变动");
			}
			
			private <T> List<T> asList(T... a) {
				ArrayList<T> aList = new ArrayList<T>(a.length);
				for (T t : a) {
					aList.add(t);
				}
				return aList;
			}
			
			
			
			/**
			 * 启动一个线程去完成core包的方法查找，过滤外包方法，及其在web项目中调用情况。
			 * 返回一个future对象
			 * @param coreChangedResources
			 * @return
			 */
			private Future<CoreThreadReturnObj> startCoreThread(final List<ChangedResource> coreChangedResources) {
				ExecutorService threadPool 			= Executors.newSingleThreadExecutor();
				Future<CoreThreadReturnObj> future 	= threadPool.submit(new Callable<CoreThreadReturnObj>() {
					public CoreThreadReturnObj call() throws Exception {
						CallHierarchyGenerator coreCallGenerator = new CallHierarchyGenerator(TaduJavaProject.CORE_JAVAPROJECT);
						ArrayList<CalledMethodEntity> cmEntities = new ArrayList<CalledMethodEntity>();
						for (ChangedResource changedResource : coreChangedResources) {
							if (changedResource instanceof ChangedJavaResource) {
								ChangedJavaResource jr  = (ChangedJavaResource) changedResource;
								String classNameWithPkg = jr.getClassNameWithPkg();
								CalledMethodEntity cmEntity = null;
								for (String methodName : jr.getMethodNames()) {
									cmEntity = coreCallGenerator.getCallersRecursive(classNameWithPkg, methodName);
									// TODO 周一debug一下，是不是变动的方法本身，不会被加入到集合里？
									cmEntities.add(cmEntity);
								}
							}
						}
		
						// 找出core包中变动的代码 影响的最外层的方法的集合
						HashSet<IMethod> changedOuterMethodsInCore = new HashSet<IMethod>();
						for (CalledMethodEntity calledMethodEntity : cmEntities) {
							changedOuterMethodsInCore.addAll(TaduCoreMethodFilter.findOuterMethod(calledMethodEntity));
						}
		
						// search in web app
						List<SearchModel> result = new ArrayList<SearchModel>();
						for (IMethod changedOuterMethod : changedOuterMethodsInCore) {
							String methodName = changedOuterMethod.getElementName();
							result.add(new SearchModel(changedOuterMethod, distinctList(JDTUtils.doJavaSearch(TaduJavaProject.WEB_JAVAPROJECT, methodName))));
						}
						return new CoreThreadReturnObj(cmEntities, result);
						
					}
				});
				return future;
			}
			
			//去重，如果某个方法在同一个servlert中被调用了多次，经过去重，只保留一次，以方便结果展示（此处操作不会展示多次，除非要展示行号等具体信息）
			private List<SearchMatch> distinctList(List<SearchMatch> searchResults) {
				for (int i = 0; i < searchResults.size() - 1; i++) {
					for (int j = searchResults.size() - 1; j > i; j--) {
						SourceMethod sm1 = (SourceMethod)searchResults.get(j).getElement();
						SourceMethod sm2 = (SourceMethod)searchResults.get(i).getElement();
						if (sm1.getElementName().equals(sm2.getElementName())) {
							searchResults.remove(j);
						}
					}
				}
				return searchResults;
			}
			
			
			class SearchModel {
				public IMethod changedOuterMethod;
				public List<SearchMatch> searchResults;
				public SearchModel(IMethod changedOuterMethod,
						List<SearchMatch> searchResults) {
					super();
					this.changedOuterMethod = changedOuterMethod;
					this.searchResults = searchResults;
				}
				
				
			}
			
			class CoreThreadReturnObj {
				public ArrayList<CalledMethodEntity> coreMethodInvokedHierarchy; //core包变动方法的调用堆栈
				public List<SearchModel> javaResultsOfinvokedCoreMethod; //core变动的方法在web项目中的搜索调用结果
				public CoreThreadReturnObj(ArrayList<CalledMethodEntity> coreMethodInvokedHierarchy,
						List<SearchModel> javaResultsOfinvokedCoreMethod) {
					super();
					this.coreMethodInvokedHierarchy = coreMethodInvokedHierarchy;
					this.javaResultsOfinvokedCoreMethod = javaResultsOfinvokedCoreMethod;
				}
			}
			
			/**
			 * 根据ipath得到文件名
			 * @param iPath
			 * @return
			 */
			private String getSimpleFileName(IPath iPath) {
				return StringUtils.substringAfterLast(iPath.toString(), "/");
			}
			
			/**
			 * 该方法会放回静态资源路径的 文件名及上一级目录名
			 * 因为上一级目录名通常包含版本号
			 * @param staticResourcePath
			 * @return
			 */
			private String getStaticFileName(String staticResourcePath) {
				try {
					String[] searchPath = staticResourcePath.split("/");
					String pathWithPreDir = searchPath[searchPath.length - 2] + "/" +searchPath[searchPath.length - 1];
					return pathWithPreDir;
				} catch (Exception e) {
					return "";
				}
			}
		});
		startBtn.setText("开始分析");
		
		label = new Label(codeAnalyzerShell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gdLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 10, 3);
		gdLabel.widthHint = 586;
		label.setLayoutData(gdLabel);
		
		lblNewLabel = new Label(codeAnalyzerShell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		lblNewLabel.setText("分析结果:");
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(3));

		layout.addColumnData(new ColumnWeightData(100));

		layout.addColumnData(new ColumnWeightData(100));

		layout.addColumnData(new ColumnWeightData(200));
		
		resultURLTreeViewer = new TreeViewer(codeAnalyzerShell, SWT.BORDER);
		resultURLTree = resultURLTreeViewer.getTree();
		resultURLTree.setLinesVisible(true);
		resultURLTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 1));
		
		callHierarchyLabel = new Label(codeAnalyzerShell, SWT.NONE);
		callHierarchyLabel.setText("调用层级:");
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		
		callHierarchyTreeViewer = new TreeViewer(codeAnalyzerShell, SWT.BORDER);
		callHierarchyTree = callHierarchyTreeViewer.getTree();
		callHierarchyTree.setLinesVisible(true);
		callHierarchyTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 1));
		
		group = new Group(codeAnalyzerShell, SWT.NONE);
		group.setText("操作提示");
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gdGroup = new GridData(SWT.FILL, SWT.CENTER, true, true, 10, 1);
		gdGroup.heightHint = 106;
		gdGroup.widthHint = 598;
		group.setLayoutData(gdGroup);
		
		tipConsoleText = new Text(group, SWT.READ_ONLY | SWT.MULTI);
		
		menu = new Menu(codeAnalyzerShell, SWT.BAR);
		codeAnalyzerShell.setMenuBar(menu);
		
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText("菜单");
		
		Menu menu = new Menu(menuItem);
		menuItem.setMenu(menu);
		
		menuAbout = new MenuItem(menu, SWT.NONE);
		menuAbout.setText("关于");

	}

}
