package tadu.code.analyzer.view.tableviewer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
  
public class TableViewerLabelProvider implements ITableLabelProvider {  
  
    public String getColumnText(Object element, int columnIndex) {  
        PeopleEntity o = (PeopleEntity) element;  
        if(columnIndex == 0)  
            return o.getId().toString();  
        if(columnIndex == 1)  
            return o.getServletName();  
        if(columnIndex == 2)  
            return o.getServletURL();  
        if(columnIndex == 3)  
            return o.getChangedContent(); 
        return null;  
    }  
  
    public void addListener(ILabelProviderListener listener) {  
          
    }  
  
    public void dispose() {  
    }  
  
    public boolean isLabelProperty(Object element, String property) {  
        return false;  
    }  
  
    public void removeListener(ILabelProviderListener listener) {  
          
    }

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}  
}