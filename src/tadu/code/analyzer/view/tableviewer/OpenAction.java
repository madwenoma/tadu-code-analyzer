package tadu.code.analyzer.view.tableviewer;

import org.eclipse.jface.action.Action;  
import org.eclipse.jface.dialogs.MessageDialog;  
import org.eclipse.jface.viewers.IStructuredSelection;  
import org.eclipse.jface.viewers.TableViewer;  
  
public class OpenAction extends Action {  
  
    private TableViewer tv;  
      
    public OpenAction(TableViewer tv) {  
        this.tv = tv;  
        setText("打开");  
    }  
      
    public void run() {  
        IStructuredSelection selection = (IStructuredSelection) tv.getSelection();  
        PeopleEntity o = (PeopleEntity) selection.getFirstElement();  
        if(o == null)  
            MessageDialog.openInformation(null, null, "请先选择记录");  
        else  
            MessageDialog.openInformation(null, null, o.getChangedContent());  
    }  
} 