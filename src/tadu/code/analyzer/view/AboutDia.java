package tadu.code.analyzer.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 注意：此Dialog为JFace包的，SWT包也有一个Dialog类。在import时一定要注意选择区分
 */
public class AboutDia extends Dialog {
	/**
	 * 构造函数
	 */
	public AboutDia(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * 在这个方法里构建Dialog中的界面内容
	 */
	protected Control createDialogArea(Composite parent) {
		/*
		 * 不要直接在parent上创建组件，否则容易导致界面布局混乱。应该象本例再插入一个面板topComp，并在此面板创建及布局
		 */
		Composite topComp = new Composite(parent, SWT.NONE);
		// 应用RowLayout面局
		topComp.setLayout(new RowLayout());
		// 加入一个文本标签
		new Label(topComp, SWT.NONE).setText("请输入：");
		// 加入一个文本框
		Text text = new Text(topComp, SWT.BORDER);
		// 用RowData来设置文本框的长度
		text.setLayoutData(new RowData(100, -1));
		return topComp;
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
	protected Button createButton(Composite parent, int buttonId,
			String buttonText, boolean defaultButton) {
		return null;
	}

	/**
	 * 改写父类的initializeBounds方法，并利用父类的createButton方法建立按钮
	 */
	public static final int APPLY_ID = 101; // 自定义“应用”按钮的ID值，一个整型常量

	protected void initializeBounds() {
		/*
		 * createButton(Composite parent, int id, String label,boolean
		 * defaultButton) 参数parent：取得放置按钮的面板，参数id：定义按钮的id值 参数label：按钮上的文字
		 * 参数defaultButton：是否为Dialog的默认按钮
		 */
		super.createButton((Composite) getButtonBar(), APPLY_ID, "应用", true);
		super.createButton((Composite) getButtonBar(), IDialogConstants.OK_ID,
				"OK", false);
		super.createButton((Composite) getButtonBar(),
				IDialogConstants.CANCEL_ID, "Cancel", false);
		super.initializeBounds();
	}
}

// 这个对话框类的使用方法很简单
// AboutDia dialog = new AboutDia(shell);
// dialog.open(); 就ok了~~~