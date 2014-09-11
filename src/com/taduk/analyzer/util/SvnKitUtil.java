package com.taduk.analyzer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.taduk.analyzer.svn.SvnXmlAnalyzer;

/**
 * svnKitApi工具类，提供svn的checkout、diff等功能 实现参考
 * http://wenku.baidu.com/view/2f93208483d049649b665881.html
 * 
 * @author LIFEI
 */
public class SvnKitUtil {

	private static final String PASSWORD = "111111";
	private static final String USERNAME = "lifei";
	
	private static ISVNAuthenticationManager authenticationManager;
	private static ISVNOptions options;
	private static SVNClientManager clientManager;

	static{
		DAVRepositoryFactory.setup();
		// 权限验证
		authenticationManager = SVNWCUtil
				.createDefaultAuthenticationManager(USERNAME, PASSWORD);
		options = SVNWCUtil.createDefaultOptions(true);
		// 创建clientManager对象
		clientManager = SVNClientManager.newInstance(options,
				authenticationManager);
	}
	
	public static String checkoutCoreProject(String coreSvnUrl) {
		return doCheckout(coreSvnUrl, TaduJavaProject.CORE_PRO_NAME);
	}

	public static String checkoutWebProject(String webSvnUrl) {
		return doCheckout(webSvnUrl, TaduJavaProject.WEB_PRO_NAME);
	}

	/**
	 * 检出svnUrl指定路径下的项目 检出的项目保存在C:/ty/tadu_code_analyzer/project/目录下
	 * 项目以svn路径中对应的项目名来命名
	 * 
	 * @param svnUrl
	 * @return
	 */
	// 对传入的svn路径进行合法性验证
	private static String doCheckout(String svnUrl, String projectName) {
		if (svnUrl == null || "".equals(svnUrl)) {
			System.out.println("传入的svn路径为空，请检查你的svn路径无误后再操作！！！");
			return null;
		}
		SVNURL svnurl = null;
		try {
			svnurl = SVNURL.parseURIEncoded(svnUrl);
		} catch (SVNException e) {
			System.out.println("在解析svn路径的时候出错，请检查你的svn路径无误后再操作！！！");
			return null;
		}

		// 创建svnUpdateClient
		SVNUpdateClient svnUpdateClient = clientManager.getUpdateClient();
		svnUpdateClient.setIgnoreExternals(false);
		
		//创建项目检出保存的路径
		//TODO 如果项目本地已经签出一次，下次签出只update，注意：update之后要清除一些缓存，如taduservletmapper等
		String locationPath = SvnKitUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		locationPath = locationPath.split("/")[1] + "/ty/tadu_code_analyzer/project/" + projectName;

		File file = new File(locationPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		boolean hasFile = file.list().length > 0;
		//如果对应的目录下面已经有文件了，就进行更新操作；否则进行checkout操作
		if(hasFile){
			try {
				System.out.println("项目SVN正在更新，请稍后....");
				svnUpdateClient.doUpdate(file, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
				System.out.println("项目SVN更新完成。");
			} catch (SVNException e) {
				System.out.println("在更新项目的时候出错，请检查你的svn路径无误或者对应的项目存在后再操作！！！");
				e.printStackTrace();
			}
		}else{
			try {
				
				// 开始检出
				System.out.println("项目正在检出，请稍后....");
				svnUpdateClient.doCheckout(svnurl, file, null, SVNRevision.HEAD,
						SVNDepth.INFINITY, false);
				System.out.println("项目检出完成。");
			} catch (SVNException e) {
				System.out.println("在检出项目的时候出错，请检查你的svn路径无误或者对应的项目存在后再操作！！！");
				return null;
			}
		}
		
		return locationPath;
	}

	/**
	 * 检出两个版本之间的标准差异文件之间的修改内容
	 * 
	 * @param svnUrl
	 *            svn地址
	 * @param startRevision
	 *            开始版本
	 * @param endRevision
	 *            结束版本
	 */
	public static String doDiff(String svnUrl, int startRevision,
			int endRevision) {
		if (startRevision < 0 || endRevision < 0) {
			return "";
		}
		SVNURL svnurl = null;
		try {
			// svn路径
			// "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core"
			svnurl = SVNURL.parseURIEncoded(svnUrl);
		} catch (SVNException e) {
			System.out.println("在解析svn路径的时候出错，请检查你的svn路径无误后再操作！！！");
			return "";
		}

		// 获取比较的起始和结束的版本号
		SVNRevision sRevision = SVNRevision.create(startRevision);
		SVNRevision eRevision = SVNRevision.create(endRevision);

		String locationPath = SvnKitUtil.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		locationPath = locationPath.split("/")[1];
		SVNDiffClient diffClient = clientManager.getDiffClient();
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();//
		try {
			diffClient.doDiff(svnurl, sRevision, svnurl, eRevision,
					SVNDepth.INFINITY, false, resultStream);
			String diffStr = resultStream.toString();
			return diffStr;
		} catch (SVNException e) {
			// TODO LOG
			return "";
		}
	}

	/**
	 * 获取svn 指定版本的某个文件的内容
	 * 
	 * @param svnUrl
	 *            svn路径
	 * @param filePath
	 *            文件的路径，可以使用doDiff()方法里面的Index:后面的那段路径
	 * @param version
	 *            获取指定文件的版本
	 * @return
	 */
	public static String getRepositoryFile(String svnUrl, String filePath,
			int version) {
		SVNURL svnurl = null;
		// 定义版本库。
		SVNRepository repository = null;
		try {
			// svn路径
			// "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core"
			svnurl = SVNURL.parseURIEncoded(svnUrl);
			repository = SVNRepositoryFactory.create(svnurl);
		} catch (SVNException e) {
			System.out.println("在解析svn路径的时候出错，请检查你的svn路径无误后再操作！！！");
			return "";
		}

		repository.setAuthenticationManager(authenticationManager);

		// 此变量用来存放要查看的文件的属性名/属性值列表。
		SVNProperties fileProperties = new SVNProperties();
		// 此输出流用来存放要查看的文件的内容。
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			SVNNodeKind nodeKind = repository.checkPath(filePath, version);
			if (nodeKind == SVNNodeKind.FILE) {
				// 获取要查看文件的内容和属性，结果保存在baos和fileProperties变量中。
				repository.getFile(filePath, version, fileProperties, baos);
				String result = baos.toString();
				try {
					baos.close();
					baos = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return result;
			}

		} catch (SVNException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 修改项目的.project文件中projectName
	 * 
	 * @param projectPath
	 */
	public static void modifiedProjectName(String projectPath) {
		if (projectPath == null || "".equals(projectPath)) {
			return;
		}
		projectPath += "/.project";
		String[] strs = projectPath.split("/");
		String newProjectName = strs[strs.length - 2];

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			// 通过Document修改.project文件中<name>标签中的项目名字进行修改
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = null;
			try {
				document = builder.parse(new File(projectPath));
				document.normalize();
				Element element = document.getDocumentElement();
				if (element.hasChildNodes()) {
					NodeList firstChilds = element.getChildNodes();
					for (int i = 0; i < firstChilds.getLength(); i++) {
						Node node = firstChilds.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE
								&& node.getNodeName() == "name") {
							// System.out.println(node.getNodeName() + ", "+
							// node.getFirstChild().getNodeValue());
							node.getFirstChild().setNodeValue(newProjectName);
							// System.out.println(node.getNodeName() + ", "+
							// node.getFirstChild().getNodeValue());
							break;
						}
					}
				}
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 保存上面的document的修改
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource domSource = new DOMSource(document);
				StreamResult result = new StreamResult(new File(projectPath));
				try {
					transformer.transform(domSource, result);
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		String svnUrl = "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core";
		// modifiedProjectName("D:/ty/tadu_code_analyzer/project/analyzer_api/.project");
		// String str =
		// doDiff("http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core",
		// 76075, 76150);
		// importProject("D:/ty/tadu_code_analyzer/project/analyzer_core");
		// System.out.println(str);
//		File file = new File("D:/ty/tadu_code_analyzer/project/tadu-core-analyzer");
//		System.out.println(file.exists());
		String result = checkoutCoreProject("http://svn.kaiqi.com/tadu/tech/server/tadu-server/code-analyzer/trunk/tadu-code-analyzer");
		System.out.println(result);
//		String result = doDiff(
//				"http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core",
//				60177, 61006);
		// System.out.println(result);
//		String[] resultList = result.split("Index:");
		// for(int i=0; i<resultList.length; i++){
		// System.out.println(resultList[i]);
		// System.out.println();
		// }

//		new SvnXmlAnalyzer().analyzer("", resultList[4]);

		// getRepositoryFile(svnUrl,
		// "src/com/tywire/tadu/mapper/slave/DuoMi_Slave.xml", 60177);

		// doCheckout(
		// "http://svn.kaiqi.com/tadu/tech/server/tadu-server/core/trunk/core");
	}
}
