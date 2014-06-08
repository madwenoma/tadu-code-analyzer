package tadu.code.analyzer.view.tableviewer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
  
public class ShowTable {  
  
    public static void main(String[] args) {  
        Display display = new Display();  
        Shell shell = new Shell(display);  
        shell.setLayout(new FillLayout());  
        TableViewer tv = new TableViewer(shell, SWT.MULTI | SWT.BORDER  
                | SWT.FULL_SELECTION);  
        Table table = tv.getTable();  
        table.setHeaderVisible(true);  
        table.setLinesVisible(true);  
        TableLayout layout = new TableLayout();  
        table.setLayout(layout);  
  
        layout.addColumnData(new ColumnWeightData(3));  
        new TableColumn(table, SWT.NONE).setText("ID");  
  
        layout.addColumnData(new ColumnWeightData(10));  
        new TableColumn(table, SWT.NONE).setText("servlet-name");  
  
        layout.addColumnData(new ColumnWeightData(10));  
        new TableColumn(table, SWT.NONE).setText("servlet-url");  
  
        layout.addColumnData(new ColumnWeightData(20));  
        new TableColumn(table, SWT.NONE).setText("变动内容");  
  
        tv.setContentProvider(new TableViewerContentProvider());  
  
        tv.setLabelProvider(new TableViewerLabelProvider());  
        Object data = PeopleFactory.getPeoples();  
  
        tv.setInput(data);  
        ShowTable show = new ShowTable();  
        show.addListener(tv);  
          
        MyActionGroup actionGroup = new MyActionGroup(tv);  
        actionGroup.fillContextMenu(new MenuManager());  
        shell.open();  
        while (!shell.isDisposed()) {  
            if (!display.readAndDispatch())  
                display.sleep();  
        }  
    }  
      
    private void addListener(TableViewer tv) {  
        tv.addDoubleClickListener(new IDoubleClickListener(){  
  
            public void doubleClick(DoubleClickEvent event) {  
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();  
                PeopleEntity o = (PeopleEntity) selection.getFirstElement();  
                  
                MessageDialog.openInformation(null, "提示", o.getServletName());  
            }  
              
        });  
          
        //选择事件(单击)  
        tv.addSelectionChangedListener(new ISelectionChangedListener() {  
  
            public void selectionChanged(SelectionChangedEvent event) {  
//              IStructuredSelection selection = (IStructuredSelection) event.getSelection();  
//              PeopleEntity o = (PeopleEntity) selection.getFirstElement();  
//              MessageDialog.openInformation(null, "提示", o.getName() + "年龄是" + o.getAge());  
            }  
              
        });  
    }  
}  