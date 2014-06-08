package tadu.code.analyzer.view.tableviewer;

import org.eclipse.jface.action.Action;  
import org.eclipse.jface.viewers.TableViewer;  
  
public class RefreshAction extends Action {  
  
    private TableViewer tv;  
      
    public RefreshAction(TableViewer tv) {  
        this.tv = tv;  
        setText("刷新");  
    }  
      
    public void run() {  
        tv.refresh();//表格的刷新方法,界面会重新读取数据并显示  
    }  
}