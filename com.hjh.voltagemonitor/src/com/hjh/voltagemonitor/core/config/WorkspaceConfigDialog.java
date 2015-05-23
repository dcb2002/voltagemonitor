/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.config;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.util.LogHelper;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class WorkspaceConfigDialog extends Dialog {

	private WorkspaceEntry workspace;

	private Button btn_emulation;
	private Text txt_title;
	private Text txt_logInterval;
	private Text txt_notes;

	private BlockContainer blockConatiner;
	private ChannelContainer channelContainer;

	public WorkspaceConfigDialog(IShellProvider parentShell,
			WorkspaceEntry workspace) {
		super(parentShell);
		this.workspace = workspace;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(new GridLayout(1, false));

		GridData gd;

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 30;
		Composite top = new Composite(container, SWT.NONE);
		top.setLayoutData(gd);
		createTop(top);

		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 30;
		Composite center = new Composite(container, SWT.NONE);
		center.setLayoutData(gd);
		createCenter(center);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 80;
		Composite bottom = new Composite(container, SWT.NONE);
		bottom.setLayoutData(gd);
		createBottom(bottom);

		initValues();
		return container;
	}

	private void initValues() {
		this.txt_title.setText(this.workspace.getTitle());
		this.txt_logInterval.setText("" + this.workspace.getLogInterval());
		this.txt_notes.setText(this.workspace.getNotes());
		this.btn_emulation.setSelection(this.workspace.isEmulation());
	}

	protected void okPressed() {

		this.workspace.setTitle(this.txt_title.getText());
		try {
			this.workspace.setLogInterval(Long.parseLong(this.txt_logInterval
					.getText()));
		} catch (Exception e) {
			LogHelper.err(e);
		}
		this.workspace.setEmulation(this.btn_emulation.getSelection());
		this.workspace.setNotes(this.txt_notes.getText());
		Document doc = DocumentFactory.getInstance().createDocument();
		Element root = DocumentFactory.getInstance().createElement("root");
		this.workspace.toXml(root);
		doc.add(root);

		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(new FileOutputStream(
					Config.getWorkspaceConfigFile()), format);
			writer.write(doc);
			writer.close();
			resetChannel();
		} catch (Exception e) {
			LogHelper.err(e);
			super.okPressed();
		}

	}

	private void resetChannel() {
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
				getShell());

		final IRunnableWithProgress proc = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				VoltageSystemFactory.getProvider().resetChannels(monitor);
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						WorkspaceConfigDialog.super.okPressed();
					}
				});
			}
		};

		getShell().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					dialog.run(true, false, proc);
				} catch (InvocationTargetException e) {
					LogHelper.err(e);
				} catch (InterruptedException e) {
					LogHelper.err(e);
				}
			}
		});
	}

	private void createBottom(Composite bottom) {
		bottom.setLayout(new GridLayout(1, false));
		new Label(bottom, SWT.None).setText("Notes");
		txt_notes = new Text(bottom, SWT.MULTI | SWT.BORDER);
		txt_notes.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createCenter(Composite center) {
		center.setLayout(new GridLayout(1, false));

		GridData gd;
		Group left = new Group(center, SWT.NONE);
		left.setText("IP");
		gd = new GridData(GridData.FILL_BOTH);
		left.setLayoutData(gd);
		channelContainer = new ChannelContainer();
		blockConatiner = new BlockContainer(channelContainer);
		blockConatiner.createDialogArea(left, workspace);

		Group right = new Group(center, SWT.NONE);
		right.setText("Channel");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 220;
		right.setLayoutData(gd);
		channelContainer.createDialogArea(right, workspace);

	}

	private void createTop(Composite top) {

		top.setLayout(new GridLayout(5, false));

		GridData gd;

		new Label(top, SWT.NONE).setText("Title");

		txt_title = new Text(top, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		txt_title.setLayoutData(gd);

		new Label(top, SWT.NONE).setText("   Log Interval(ms)");

		txt_logInterval = new Text(top, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 100;
		txt_logInterval.setLayoutData(gd);
		
		btn_emulation = new Button(top,SWT.CHECK);
		btn_emulation.setText("Emulation");
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configure Workspace");
	}

	protected Point getInitialSize() {
		return new Point(1000, 700);
	}

}
