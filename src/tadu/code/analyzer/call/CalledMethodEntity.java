package tadu.code.analyzer.call;

import java.util.ArrayList;

import org.eclipse.jdt.core.IMethod;
/**
 * 方法调用堆栈的数据结构
 * @author LIFEI
 *
 */
public class CalledMethodEntity {
	private IMethod method;
	private ArrayList<CalledMethodEntity> calledMethodEntities;
	
	public IMethod getMethod() {
		return method;
	}
	public void setMethod(IMethod method) {
		this.method = method;
	}
	public ArrayList<CalledMethodEntity> getCalledMethodEntities() {
		return calledMethodEntities;
	}
	public void setCalledMethodEntities(
			ArrayList<CalledMethodEntity> calledMethodEntities) {
		this.calledMethodEntities = calledMethodEntities;
	}
	
	@Override
	public String toString() {
		return "CalledMethodEntity [method=" + method
				+ ", calledMethodEntities=" + calledMethodEntities + "]";
	}
	
}
