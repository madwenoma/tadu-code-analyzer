package tadu.code.analyzer.view;

import java.util.HashSet;

import com.taduk.analyzer.webapp.TaduServletMapper;

public class ServletResultBean {
	private static Long staticID = 0L;
	private Long id;
	public String servletClassName;
	private String url;
	public HashSet<String> changedContents = new HashSet<String>();
	
	
	
	public void addChangedContent(String changedContent) {
		this.changedContents.add(changedContent);
	}	
	
	public ServletResultBean(String servletClassName) {
		super();
		this.setId(staticID++);
		this.servletClassName = servletClassName;
		this.url = TaduServletMapper.getServletUrl(servletClassName);
	}

	@Override
	public String toString() {
		return "{" + servletClassName
				+ "} {" + url + "}, 发生变化的部分=" + changedContents + "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((servletClassName == null) ? 0 : servletClassName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServletResultBean other = (ServletResultBean) obj;
		if (servletClassName == null) {
			if (other.servletClassName != null)
				return false;
		} else if (!servletClassName.equals(other.servletClassName))
			return false;
		return true;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		ServletResultBean s1 = new ServletResultBean("BookAction");
		ServletResultBean s2 = new ServletResultBean("BookAction");
		HashSet<ServletResultBean> srbs = new HashSet<ServletResultBean>();
		srbs.add(s1);
		srbs.add(s2);
		System.out.println(srbs.size());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getServletClassName() {
		return servletClassName;
	}

	public void setServletClassName(String servletClassName) {
		this.servletClassName = servletClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HashSet<String> getChangedContents() {
		return changedContents;
	}

	public void setChangedContents(HashSet<String> changedContents) {
		this.changedContents = changedContents;
	}
	
	
}
