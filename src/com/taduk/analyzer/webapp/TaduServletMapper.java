package com.taduk.analyzer.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringUtils;

import com.taduk.analyzer.util.Constants;
import com.taduk.analyzer.util.TaduJavaProject;

/**
 * servlet映射类
 * 分析webapp项目里，每个servlet所包含的的一些信息，包括（父类）：facade及其所有调用的方法、url地址、包含的模板文件
 * 
 * @author LIFEI
 */

public class TaduServletMapper {

	private static final String SERVLET_XML_ELE = "servlet";
	private static final String URL_PATTERN_XML_ELE = "url-pattern";
	private static final String SERVLET_NAME_XML_ELE = "servlet-name";
	private static final String SERVLET_CLASS_XML_ELE = "servlet-class";
	private static final String SERVLET_MAPPING_XML_ELE = "servlet-mapping";
	private static List<SerlvetMapperBean> servletMappers = new ArrayList<SerlvetMapperBean>();
	
	private static final String WEBXML_PATH = "/WebRoot/WEB-INF/web.xml";
	
	static {
		try {
			servletMappers = getServletMappers();
		} catch (Exception e) {
		
		}
	}
	
	
	public static String getServletUrl(String servletClassName) {
		if(servletClassName.indexOf(".")> 0) {
			servletClassName = StringUtils.substringBefore(servletClassName, ".");
		}
		for (SerlvetMapperBean serlvetMapperBean : servletMappers) {
			if (serlvetMapperBean.getClassName().contains(servletClassName)) {
				return serlvetMapperBean.getUrlPattern();
			}
		}
		return "";
	}

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		System.out.println(getServletUrl("MessageAction"));
	}
	
	private static List<SerlvetMapperBean> getServletMappers() throws FileNotFoundException, XMLStreamException {
		HashMap<String, String> servletBeans = new HashMap<String, String>();
		HashMap<String, String> servletMappingBeans = new HashMap<String, String>();

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		String locationPath = TaduServletMapper.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		locationPath = locationPath.split("/")[1] + Constants.PROJECT_LOCAL_PATH + TaduJavaProject.WEB_PRO_NAME;
		String webXMLPath = locationPath + WEBXML_PATH;
		InputStream input = new FileInputStream(new File(webXMLPath));
		XMLStreamReader reader = inputFactory.createXMLStreamReader(input);
		boolean servletEleFlag = false;
		boolean servletMappingFlag = false;
		String servletName = "";
		String servletClass = "";
		String servletNameInServletMapping = "";
		String urlPattern = "";
		while(reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT) {
				String eleName = reader.getLocalName();
				if(SERVLET_XML_ELE.equals(eleName)) {
					servletEleFlag = true;
				}
				if(SERVLET_MAPPING_XML_ELE.equals(eleName)) {
					servletMappingFlag = true;
				}
				if(SERVLET_NAME_XML_ELE.equals(eleName)) {
					if (servletEleFlag) {
						servletName = reader.getElementText();
					}
					if (servletMappingFlag) {
						servletNameInServletMapping = reader.getElementText();
					}
				}
				if(SERVLET_CLASS_XML_ELE.equals(eleName)) {
					if (servletEleFlag) {
						servletClass =  reader.getElementText();
					}
				}
				if (URL_PATTERN_XML_ELE.equals(eleName)) {
					if (servletMappingFlag) { 
						urlPattern = reader.getElementText();
					}
				}
				
			}
			if (event == XMLStreamConstants.END_ELEMENT) {
				if (SERVLET_XML_ELE.equals(reader.getLocalName())) {
					if (servletEleFlag) {
						servletBeans.put(servletName, servletClass);
						servletEleFlag = false;
					}
				}

				if (SERVLET_MAPPING_XML_ELE.equals(reader.getLocalName())) {
					if (servletMappingFlag) {
						servletMappingBeans.put(servletNameInServletMapping, urlPattern);
						servletMappingFlag = false;
					}
				}
			}
		}
		List<SerlvetMapperBean> servletMappers = null;
		if (servletBeans.size() == servletMappingBeans.size()) {
//			Iterator<Entry<String, String>> it = servletBeans.entrySet().iterator();
			servletMappers = new ArrayList<SerlvetMapperBean>(servletBeans.size());
			SerlvetMapperBean servletMapper = null;
			for(Iterator<Entry<String, String>> it = servletBeans.entrySet().iterator();it.hasNext();){
				Entry<String, String> entry = it.next();
				String urlPatter = servletMappingBeans.get(entry.getKey());
				if (urlPatter != null && urlPattern != "") {
					servletMapper = new SerlvetMapperBean(urlPatter, entry.getValue());
					servletMappers.add(servletMapper);
				}
			}
		} 
		
		return servletMappers;
	}
	
	
}
