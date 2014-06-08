package tadu.code.analyzer.view.tableviewer;

import java.util.List;  

import org.eclipse.jface.viewers.IStructuredContentProvider;  
import org.eclipse.jface.viewers.Viewer;  
  
public class TableViewerContentProvider implements IStructuredContentProvider {  
  
    @SuppressWarnings("rawtypes")
	public Object[] getElements(Object inputElement) {  
        if(inputElement instanceof List)  
            return ((List) inputElement).toArray();  
        return new Object[0];  
    }  
  
    /** 
     * 当TableViewer对象被关闭时触发执行此方法 
     */  
    public void dispose() {  
  
    }  
  
    /** 
     * 当TableViewer再次调用setInput()时触发此方法执行 
     */  
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {  
  
    }  
  
}  