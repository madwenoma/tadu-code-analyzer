package tadu.code.analyzer.view;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import tadu.code.analyzer.call.CalledMethodEntity;

public class TreeViewContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	@Override
	public Object[] getChildren(Object element) {
		CalledMethodEntity entity = (CalledMethodEntity) element;
		List<CalledMethodEntity> enetities = entity.getCalledMethodEntities();
		if (enetities == null || enetities.isEmpty()) {
			return new Object[0];
		} else {
			return enetities.toArray();
		}
	}

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof List) {
			@SuppressWarnings("unchecked")
			List<CalledMethodEntity> list = (List<CalledMethodEntity>) element;
			return list.toArray();
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * 判断某节点是否有子结点
	 */
	@Override
	public boolean hasChildren(Object element) {
		CalledMethodEntity entity = (CalledMethodEntity) element;
		List<CalledMethodEntity> enetities = entity.getCalledMethodEntities();
		return enetities != null && !enetities.isEmpty();
	}

}
