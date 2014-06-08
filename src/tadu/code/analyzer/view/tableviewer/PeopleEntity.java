package tadu.code.analyzer.view.tableviewer;


public class PeopleEntity {  
  
    private Long id;  
  
    private String servletName;  
  
    private String servletURL;  
  
    private String changedContent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public String getServletURL() {
		return servletURL;
	}

	public void setServletURL(String servletURL) {
		this.servletURL = servletURL;
	}

	public String getChangedContent() {
		return changedContent;
	}

	public void setChangedContent(String changedContent) {
		this.changedContent = changedContent;
	}  
  
}