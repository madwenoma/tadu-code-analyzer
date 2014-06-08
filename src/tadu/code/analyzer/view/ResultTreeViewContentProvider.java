package tadu.code.analyzer.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ResultTreeViewContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	@Override
	public Object[] getChildren(Object element) {
		ResultBean resultBean = (ResultBean)element;
		List<ResultBean> resultBeans = resultBean.getResultBeans();
		if (resultBeans == null || resultBeans.isEmpty()) {
			return new Object[0];
		} else {
			return resultBeans.toArray();
		}
	}

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof List) {
			@SuppressWarnings("unchecked")
			List<ResultBean> list = (List<ResultBean>) element;
			return list.toArray();
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		ResultBean resultBean = (ResultBean) element;
		List<ResultBean> resultBeans = resultBean.getResultBeans();
		return resultBeans != null && !resultBeans.isEmpty();
	}
}
