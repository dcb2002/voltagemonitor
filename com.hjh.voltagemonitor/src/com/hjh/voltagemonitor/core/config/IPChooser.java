/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.core.util.LogHelper;
import com.hjh.voltagemonitor.core.util.TableUtil;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class IPChooser extends Dialog {

	private CheckboxTableViewer viewer;
	private WorkspaceEntry workspace;
	private String ips[];
	private String ipsOfCheck[];

	protected IPChooser(IShellProvider parentShell, WorkspaceEntry workspace) {
		super(parentShell);
		this.workspace = workspace;
	}

	public String[] get() {
		return ipsOfCheck;
	}

	protected void okPressed() {

		Object[] checks = viewer.getCheckedElements();

		if (null == checks || 0 == checks.length) {
			MessageDialog.openInformation(getShell(), "information",
					"Please choose!");
			return;
		}

		ipsOfCheck = new String[checks.length];
		int index = 0;
		for (Object item : checks) {
			ipsOfCheck[index++] = (String) item;
		}

		super.okPressed();

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
		viewer = CheckboxTableViewer.newCheckList(container, SWT.NONE);

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		TableLayout layout = new TableLayout();
		viewer.getTable().setLayout(layout);

		TableUtil.addColumn(viewer.getTable(), layout, "ip", 1);

		viewer.getTable().layout();

		initContent();
		initStruct();

		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
				this.getShell());

		final IRunnableWithProgress proc = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				String[] sips = VoltageSystemFactory.getProvider().scanIps(
						monitor);
				List<String> list = new ArrayList<String>();
				for (String item : sips) {
					if (!workspace.hasIp(item)) {
						list.add(item);
					}
				}
				ips = list.toArray(new String[list.size()]);
				CommonUtil.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						viewer.setInput(IPChooser.this);
						viewer.setAllChecked(true);
					}

				});

			}
		};

		try {
			dialog.run(true, false, proc);
		} catch (Exception e) {
			LogHelper.err(e);
		}

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Scan IP");
	}

	protected Point getInitialSize() {
		return new Point(500, 400);
	}

	private void initContent() {
		viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener listener) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {

			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				return (String) element;
			}

		});
	}

	private void initStruct() {
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				IPChooser entry = (IPChooser) inputElement;
				return entry.ips;
			}
		});
	}
}
