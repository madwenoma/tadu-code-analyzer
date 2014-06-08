package com.taduk.analyzer.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author LIUBING
 *
 */
public class XMLParser {
	private List<MyElement> elementList = new ArrayList<MyElement>();
	public List<MyElement> getElementList() {
		return elementList;
	}

	public void setElementList(List<MyElement> elementList) {
		this.elementList = elementList;
	}

	public XMLParser(File file) {
		doParser(file);
	}
	
	private void doParser(File file) {
		long s = System.currentTimeMillis();
		
		try {
			parserXML(file);
			getElementLineNumber(file, elementList);
			for(MyElement e : elementList){
				System.out.println(e.getNodeName() +", startLineNumber:"+e.getStartLineNumber()+" ,endLineNumber:"+e.getEndLineNumber());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		long e = System.currentTimeMillis();
		System.out.println("总共花费:"+(e-s));
	}


	public static void main(String[] args) {
		new XMLParser(new File(""));
	}

	/**
	 * 获取改变的节点的id
	 * @param changeLineNumber
	 * @return
	 */
	public String getTheChangeXMLId(int changeLineNumber){
		for(MyElement e : elementList){
			boolean isInElement = e.containInElement(changeLineNumber);
			if(isInElement){
				String nodeName = e.getNodeName();
				String id = e.getAttributes().get("id");
				if(nodeName.equalsIgnoreCase("sql")){
					return getRefSqlElementIds(id);
				} else if(nodeName.equalsIgnoreCase("resultMap")){
					return getRefResultMapElementIds(id);
				} else {
					return id;
				}
			}
		}
		return null;
	}


	/**
	 * 获取引用了resultMap的那些兄弟节点
	 * @param resultMap
	 * @return
	 */
	private String getRefResultMapElementIds(String resultMap) {
		String ids = "";
		for(MyElement e : elementList){
			String value = e.getAttributes().get("resultMap");
			if(resultMap.equals(value)){
				ids = ids + e.getAttributes().get("id") + ",";
			}
		}
		
		return ids == null? null : ids.substring(0, ids.length()-1);
	}
	
	/**
	 * 获取引用了sql节点的那些兄弟节点
	 * @param sqlId
	 * @return
	 */
	private String getRefSqlElementIds(String sqlId) {
		String ids = "";
		for(MyElement e : elementList){
			String value = e.getAttributes().get("refid");
			if(sqlId.equals(value)){
				ids = ids + e.getAttributes().get("id") + ",";
			}
		}
		
		return ids == null? null : ids.substring(0, ids.length()-1);
	}
	
	/**
	 * 获取类名，包含了包的全路径
	 * @return
	 */
	public String getClassNameWithPkg(){
		if(this.elementList.size()>0){
			return this.elementList.get(0).getRootName();
		}
		
		return null;
	}

	/**
	 * 获取某个xml文件中对应的一级节点的起始行号
	 * @param file
	 * @param elementList
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void getElementLineNumber(File file, List<MyElement> elementList)
			throws FileNotFoundException, IOException {
		//创建读取文件行号的对象
		LineNumberReader numberReader = new LineNumberReader(new FileReader(file));
		//保存读取每行数据的变量
		String lineStr = null;
		//当前读取到的行号
		int currentLine = 0;
		//指向elementList列表的下标
		int index = 0;
		
		//以下的变量用来保存当前行和前2行的数据，为了排除一些特殊情况使用
		//boolean变量是保存对应每行数据中是否存在那些特殊情况的值
		String tempFirst = null;
		String tempSecond = null;
		String tempThrid = null;
		boolean beforeFirst = false;
		boolean beforeSecond = false;
		boolean beforeThrid = false;
		boolean afterFirst = false;
		boolean afterSecond = false;
		boolean afterThrid = false;
		
		//处理一种特殊，当某个标签分成了两行显示的时候，需要做特殊处理
		boolean twoRowCase = false;
		
		//开始循环读取文件内容
		while((lineStr = numberReader.readLine()) != null){
			if(index < 0 || index >= elementList.size()){
				return;
			}
			MyElement element = elementList.get(index);
			currentLine = numberReader.getLineNumber();
			
			//默认将前3行的数据赋值给定义好的变量
			if(currentLine == 1){
				tempFirst = lineStr;
			}else if(currentLine == 2){
				tempSecond = lineStr;
			}else if(currentLine == 3){
				tempThrid = lineStr;
			}else{
				tempThrid = tempSecond;
				tempSecond = tempFirst;
				tempFirst = lineStr;
			}
			//匹配节点名是否存在
			boolean nodeNameFlag = false;
			//当节点的起始位置已经确认之后，只需要判断节点的结束位置，所以匹配的方式不一致
			if(element.isStartFlag()){
				nodeNameFlag = lineStr.contains("</"+element.getNodeName()+">") || lineStr.contains("</"+element.getNodeName()+" >") 
						|| lineStr.contains("</ "+element.getNodeName()+">");
			}else{
				nodeNameFlag = lineStr.contains(element.getNodeName())&&
						lineStr.contains("<");
			}
			
			//对于select节点，获取该节点下存在<include refid="book.columns" />的refid
			if("select".equalsIgnoreCase(element.getNodeName())){
				boolean flag = lineStr.contains("<!--");
				boolean containRefId = lineStr.contains("<include");
				if(!flag && containRefId){
					int begin = lineStr.indexOf("<include");
					int end = lineStr.indexOf("/>");
					lineStr = lineStr.substring(begin+8, end);
//					System.out.println(lineStr);
					String[] strList = lineStr.split("=");
					strList[1] = strList[1].trim();
					strList[1] = strList[1].substring(1, strList[1].length()-1);
					elementList.get(index).getAttributes().put(strList[0].trim(), strList[1].trim());
				}
			}
		
			if(nodeNameFlag || twoRowCase){
				//当起始节点没有找到之前，走的是下面的逻辑；否则走的是else逻辑
				if(!element.isStartFlag()){
					//从这个if到下面的那个if都是为了排除某些节点被加了注释的情况，因为加了注释不住处理
					if(currentLine > 1){
						beforeFirst = tempFirst.contains("<!--");
						afterFirst = tempFirst.contains("-->");
						
						if(!beforeFirst && currentLine > 2){
							beforeSecond = tempSecond.contains("<!--");
							afterSecond = tempSecond.contains("-->");
						}
						
						if(!beforeFirst && !beforeSecond && currentLine > 3){
							beforeThrid = tempThrid.contains("<!--");
							afterThrid = tempThrid.contains("-->");
						}
					}
					//同上，如果存在就继续，并且将变量重新初始化
					if((beforeFirst && !afterFirst) || (beforeSecond && !afterSecond) || (beforeThrid && !afterThrid)){
						beforeFirst = false;
						afterFirst = false;
						beforeSecond = false;
						afterSecond = false;
						beforeThrid = false;
						afterThrid = false;
						continue;
					}
					
					//下面再根据节点里面的属性名和属性值来进一步的做判断
					boolean attributeFlag = false;
					String keys = element.keys();
					if(keys != null){
						String[] strs = keys.split(",");
						for(int i=0; i<strs.length; i++){
							String value = element.value(strs[i]);
							//如果twoRowCase为true，就表示当前的节点存在多行显示的情况，
							//这个时候只要有一条属性匹配上，就可以认为属性匹配成功
							//否则就使用id来进行匹配
							if(twoRowCase){
								if(lineStr.contains(strs[i]) && lineStr.contains(value)){
									attributeFlag = true;
								}
							}else{
								if("id".equalsIgnoreCase(strs[i])){
									attributeFlag = lineStr.contains(strs[i]) && lineStr.contains(value);
									break;
								}
							}
						}
					}
					
					//再次判断当前行的结尾是否存在节点的结束标签，如果有的话，那就只是一行
					//否则，就有可能是两行了
					boolean isEndArr = lineStr.contains(">");
					if(isEndArr){
						if(twoRowCase){
							//设置当前标签的起始行号
							if(attributeFlag){
								element.setStartLineNumber(currentLine-1);
								element.setStartFlag(true);
								twoRowCase = false;
							}
						}else{
							//设置当前标签的起始行号
							if(nodeNameFlag && attributeFlag){
								element.setStartLineNumber(currentLine);
								element.setStartFlag(true);
							}
						}
					}else{
						twoRowCase = true;
					}
					
					//判断当前标签是否属于单标签
					boolean isSingleElement = lineStr.contains("/>");
					if(isSingleElement){
						//设置当前标签的结束行号
						element.setEndLineNumber(currentLine);
						element.setEndFlag(true);
						index++;
					}
				}else{
					//设置当前标签的结束行号
					element.setEndLineNumber(currentLine);
					element.setEndFlag(true);
					index++;
				}
			}
		}
	}
	
	private List<MyElement> parserXML(File file) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		document.normalize();
		Element element = document.getDocumentElement();
		String rootName = element.getAttribute("namespace");
		NodeList list = element.getChildNodes();
		
		for(int i=0; i<list.getLength(); i++){
			Node node = list.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				MyElement myElement = new MyElement();
				myElement.setRootName(rootName);
				myElement.setNodeName(node.getNodeName());
				NamedNodeMap attributes = ((Element)node).getAttributes();
				
				for(int j=0; j<attributes.getLength(); j++){
					Node n = attributes.item(j);
					myElement.getAttributes().put(n.getNodeName(), n.getNodeValue());
				}
				
				elementList.add(myElement);
			}
		}
		
		return elementList;
	}
	
	
	
	class MyElement {
		private String nodeName;
		private String rootName;
		private Map<String, String> attributes = new HashMap<String, String>();
		private int startLineNumber;
		private int endLineNumber;
		private boolean startFlag = false;
		private boolean endFlag = false;
		
		public String getRootName() {
			return rootName;
		}
		public void setRootName(String rootName) {
			this.rootName = rootName;
		}
		public boolean isStartFlag() {
			return startFlag;
		}
		public void setStartFlag(boolean startFlag) {
			this.startFlag = startFlag;
		}
		public boolean isEndFlag() {
			return endFlag;
		}
		public void setEndFlag(boolean endFlag) {
			this.endFlag = endFlag;
		}
		public int getStartLineNumber() {
			return startLineNumber;
		}
		public void setStartLineNumber(int startLineNumber) {
			this.startLineNumber = startLineNumber;
		}
		public int getEndLineNumber() {
			return endLineNumber;
		}
		public void setEndLineNumber(int endLineNumber) {
			this.endLineNumber = endLineNumber;
		}
		public String getNodeName() {
			return nodeName;
		}
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}
		public Map<String, String> getAttributes() {
			return attributes;
		}
		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}
		
		public boolean containInElement(int lineNumber){
			boolean flag = false;
			if(lineNumber >= startLineNumber && lineNumber <= endLineNumber){
				flag = true;
			}
			return flag;
		}
		
		public String keys(){
			if(attributes.size() < 1){
				return null;
			}
			
			String result = "";
			for(Entry<String, String> entry : attributes.entrySet()){
				result = result + entry.getKey() + ",";
			}
			return result.substring(0, result.length()-1);
		}
		
		public String value(String key){
			if(attributes.size() < 1){
				return null;
			}
			String value = attributes.get(key);
			return value;
		}
	}
}
