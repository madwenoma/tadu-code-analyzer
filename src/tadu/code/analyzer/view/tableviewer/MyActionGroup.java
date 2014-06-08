package tadu.code.analyzer.view.tableviewer;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.actions.ActionGroup;
  
public class MyActionGroup extends ActionGroup {  
  
    private TableViewer tv;  
      
    public MyActionGroup(TableViewer tv) {  
        this.tv = tv;  
    }  
      
    public void fillContextMenu(IMenuManager mgr) {  
        MenuManager menuManager = (MenuManager) mgr;  
        menuManager.add(new OpenAction(tv));  
        menuManager.add(new RefreshAction(tv));  
          
        Table table = tv.getTable();  
        Menu menu = menuManager.createContextMenu(table);  
        table.setMenu(menu);  
    }  
}