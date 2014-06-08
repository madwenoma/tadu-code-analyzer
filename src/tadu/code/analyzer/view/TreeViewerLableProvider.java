package tadu.code.analyzer.view;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import tadu.code.analyzer.call.CalledMethodEntity;

public class TreeViewerLableProvider implements ILabelProvider {

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
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Image getImage(Object arg0) {
		return null;
	}

	@Override
	public String getText(Object element) {
		CalledMethodEntity entity = (CalledMethodEntity) element;
		return formatIMethod(entity.getMethod());
	}

	/**
	 * getCallersRecursive(IMethod, CalledMethodEntity):void -
	 * tadu.core.analyzer.call.CallHierarchyGenerator
	 * 
	 * @param method
	 * @return
	 */
	private String formatIMethod(IMethod method) {
		if (method == null)
			return "";
		try {
			String returnType = method.getReturnType();
			String[] parameterTypes = method.getRawParameterNames();
			IType type = method.getCompilationUnit().getTypes()[0];
			String className = type.getElementName();
			String packageStr = type.getPackageFragment().getElementName();
			return method.getElementName() + "(" + StringUtils.remove(StringUtils.remove(Arrays.toString(parameterTypes), ']'), '[') + ")" + ":"
					+ returnType + " - " + packageStr + "." + className;
		} catch (JavaModelException e) {
			return "";
		}
	}

}
