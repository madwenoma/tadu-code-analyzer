package tadu.code.analyzer.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ResultTreeViewerLableProvider implements ILabelProvider{

	@Override
	public void addListener(ILabelProviderListener arg0) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
		
	}

	@Override
	public Image getImage(Object arg0) {
		return null;
	}

	@Override
	public String getText(Object element) {
		ResultBean resultBean = (ResultBean)element;
		return resultBean.getBeanName();
	}



}
