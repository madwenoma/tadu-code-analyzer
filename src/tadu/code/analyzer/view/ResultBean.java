package tadu.code.analyzer.view;

import java.util.List;

public class ResultBean {
	private String beanName;
	private List<ResultBean> resultBeans;
	
	public ResultBean(String beanName, List<ResultBean> resultBeans) {
		super();
		this.beanName = beanName;
		this.resultBeans = resultBeans;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public List<ResultBean> getResultBeans() {
		return resultBeans;
	}
	public void setResultBeans(List<ResultBean> resultBeans) {
		this.resultBeans = resultBeans;
	}
	@Override
	public String toString() {
		return "ResultBean [beanName=" + beanName + ", resultBeans="
				+ resultBeans + "]";
	}
	
}
