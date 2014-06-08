package tadu.code.analyzer.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

public class CodeAnalyzerViewDia extends Dialog {

	public CodeAnalyzerViewDia(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * 
	 */

	protected Shell codeAnalyzerShell2;
	private Text coreSvnUrlText;
	private Text webSvnUrlText;
	private Label label;
	private Group group;
	private Label lblNewLabel;
	private List list;
	private Label label_1;
	private Tree tree;
	private TreeViewer treeViewer;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CodeAnalyzerViewDia window = new CodeAnalyzerViewDia(new Shell());
			window.openD();
		} catch (Exception e) {
			// 对于64位机，采用控制台输出，不能查看树形调用结构
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void openD() {
		Display display = Display.getDefault();
		createContents();
		// 居中

		codeAnalyzerShell2.pack();
		codeAnalyzerShell2.open();
		while (!codeAnalyzerShell2.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * 改写这个父类Dialog的方法可以改变窗口的默认式样
	 */
	protected int getShellStyle() {
		/*
		 * super.getShellStyle()得到原有的式样 SWT.RESIZE：窗口可以变大小 SWT.MAX：窗口可最大化、最小化
		 */
		return super.getShellStyle() | SWT.RESIZE | SWT.MAX;
	}

	/**
	 * 改写父类创建按钮的方法，使其失效
	 */
	@Override
	protected Button createButton(Composite parent, int id, String label,
			boolean defaultButton) {
		return null;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite codeAnalyzerShell = new Composite(parent, SWT.NONE);

		codeAnalyzerShell.setSize(610, 449);
		codeAnalyzerShell.setLayout(new GridLayout(9, false));

		Label coreSvnUrlLabel = new Label(codeAnalyzerShell, SWT.NONE);
		coreSvnUrlLabel.setText("core-svn-url");

		coreSvnUrlText = new Text(codeAnalyzerShell, SWT.BORDER);
		coreSvnUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 8, 1));

		Label webSvnUrlLabel = new Label(codeAnalyzerShell, SWT.NONE);
		webSvnUrlLabel.setText("web-svn-url");

		webSvnUrlText = new Text(codeAnalyzerShell, SWT.BORDER);
		webSvnUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 8, 1));
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);

		Button startBtn = new Button(codeAnalyzerShell, SWT.NONE);
		startBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		startBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(coreSvnUrlText.getText());
				System.out.println(webSvnUrlText.getText());

			}
		});
		startBtn.setText("开始分析");

		label = new Label(codeAnalyzerShell, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd_label = new GridData(SWT.FILL, SWT.CENTER, true, false, 9,
				3);
		gd_label.widthHint = 586;
		label.setLayoutData(gd_label);

		lblNewLabel = new Label(codeAnalyzerShell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false,
				false, 1, 1));
		lblNewLabel.setText("分析结果:");
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);

		list = new List(codeAnalyzerShell, SWT.BORDER);
		list.setItems(new String[] { "1", "2", "3", "4", "5", "6" });
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 9, 1));

		label_1 = new Label(codeAnalyzerShell, SWT.NONE);
		label_1.setText("调用层级:");
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);
		new Label(codeAnalyzerShell, SWT.NONE);

		treeViewer = new TreeViewer(codeAnalyzerShell, SWT.BORDER);
		tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 9, 1));

		group = new Group(codeAnalyzerShell, SWT.NONE);
		group.setText("操作提示");
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_group = new GridData(SWT.FILL, SWT.FILL, false, true, 9, 1);
		gd_group.heightHint = 106;
		gd_group.widthHint = 598;
		group.setLayoutData(gd_group);

		
		codeAnalyzerShell.setLocation(
				Display.getCurrent().getClientArea().width / 2
						- codeAnalyzerShell.getShell().getSize().x / 2, Display
						.getCurrent().getClientArea().height
						/ 2
						- codeAnalyzerShell.getSize().y / 2);
		return codeAnalyzerShell;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {

	}
}
