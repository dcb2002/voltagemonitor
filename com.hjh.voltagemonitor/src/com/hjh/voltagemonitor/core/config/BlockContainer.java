/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.config;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.util.IpGetter;
import com.hjh.voltagemonitor.core.util.PropertyProxy;
import com.hjh.voltagemonitor.core.util.PropertyProxy.PropertyProxyGetAndSet;
import com.hjh.voltagemonitor.core.util.TableUtil;
import com.hjh.voltagemonitor.module.BlockEntry;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class BlockContainer {

	private CheckboxTableViewer viewer;
	private WorkspaceEntry workspace;
	private ChannelContainer channelContainer;

	public BlockContainer(ChannelContainer channelContainer) {
		this.channelContainer = channelContainer;
	}

	public void createDialogArea(final Composite parent,
			WorkspaceEntry workspace) {
		this.workspace = workspace;
		parent.setLayout(new GridLayout(1, false));

		GridData gd;
		Composite buttons = new Composite(parent, SWT.None);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 35;
		buttons.setLayoutData(gd);
		fillButtons(buttons);

		Composite content = new Composite(parent, SWT.None);
		gd = new GridData(GridData.FILL_BOTH);
		content.setLayoutData(gd);
		content.setLayout(new FillLayout());
		viewer = CheckboxTableViewer.newCheckList(content, SWT.FULL_SELECTION);

		TableUtil.layoutTable(viewer, propertyProxys);
		TableUtil.modifySetting(viewer, propertyProxys);
		TableUtil.initStruct(viewer, propertyProxys);
		TableUtil.initContent(viewer, propertyProxys);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection struct = (IStructuredSelection) event
						.getSelection();
				if (!struct.isEmpty()) {
					BlockEntry entry = (BlockEntry) struct.getFirstElement();
					channelContainer.setInput(entry);
				} else {
					channelContainer.setInput(BlockEntry.empty);
				}
			}

		});

		viewer.setInput(workspace);

	}

	@SuppressWarnings("unchecked")
	private PropertyProxy<BlockEntry>[] propertyProxys = new PropertyProxy[] {
			new PropertyProxy<BlockEntry>(false, "index", "Index", 1)
					.setGetAndSet(new PropertyProxyGetAndSet<BlockEntry>() {

						@Override
						public String get(BlockEntry obj) {
							int i = 1;
							for (BlockEntry item : workspace.getBlocks()) {
								if (item.equals(obj)) {
									return "" + i;
								}
								i++;
							}
							return "0";
						}

						@Override
						public boolean set(BlockEntry obj, String val) {
							return false;
						}

					}),
			new PropertyProxy<BlockEntry>(true, "title", "Title", 3),
			new PropertyProxy<BlockEntry>("ip1", "IP1", 2)
					.setGetAndSet(new PropertyProxyGetAndSet<BlockEntry>() {

						@Override
						public String get(BlockEntry obj) {
							return obj.getIp1().getIp();
						}

						@Override
						public boolean set(BlockEntry obj, String val) {
							if (null == val) {
								return false;
							}
							val = val.trim();
							if (0 == val.length()) {
								return false;
							}
							obj.getIp1().setIp(val);
							return true;
						}

					}),
			new PropertyProxy<BlockEntry>("ip2", "IP2", 2)
					.setGetAndSet(new PropertyProxyGetAndSet<BlockEntry>() {

						@Override
						public String get(BlockEntry obj) {
							return obj.getIp2().getIp();
						}

						@Override
						public boolean set(BlockEntry obj, String val) {
							if (null == val) {
								return false;
							}
							val = val.trim();
							if (0 == val.length()) {
								return false;
							}
							obj.getIp2().setIp(val);
							return true;
						}

					}),
			new PropertyProxy<BlockEntry>("timeout", "Timeout(ms)", 2),
			new PropertyProxy<BlockEntry>("format", "Format", 2),
			new PropertyProxy<BlockEntry>("defaultValue", "Default", 2),
			new PropertyProxy<BlockEntry>("undervoltage", "Under voltage", 2),
			new PropertyProxy<BlockEntry>("warnning", "Warnning", 2),
			new PropertyProxy<BlockEntry>("overvoltage", "Over voltage", 2), };

	private void fillButtons(final Composite buttons) {

		buttons.setLayout(new GridLayout(6, false));

		Button scan = new Button(buttons, SWT.None);
		scan.setText("Scan");

		Button add = new Button(buttons, SWT.None);
		add.setText("Add");

		Button delete = new Button(buttons, SWT.None);
		delete.setText("Delete");

		Button selectAll = new Button(buttons, SWT.None);
		selectAll.setText("Select All");

		Button selectNone = new Button(buttons, SWT.None);
		selectNone.setText("Select None");

		Button sort = new Button(buttons, SWT.None);
		sort.setText("Sort");

		selectAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
			}
		});

		selectNone.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});

		delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Object[] items = viewer.getCheckedElements();
				if (null != items && 0 != items.length) {
					if (MessageDialog.openConfirm(buttons.getShell(),
							"Confirm", "Delete " + items.length + " items?")) {
						for (Object item : items) {
							workspace.remove((BlockEntry) item);
						}
						viewer.refresh();
						viewer.getTable().layout();
					}
				}
			}
		});

		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String ip = IpGetter.getLocalIP();
				if (null == ip || 0 == ip.length() || Config.IP127.equals(ip)) {
					workspace.add(Config.IP127, Config.IP127);
				} else {
					String ip1 = null, ip2 = null;
					String prefix = ip.substring(0, ip.lastIndexOf(".") + 1);
					for (int i = 1; i < 255; i++) {
						if (!workspace.hasIp(prefix + i)) {
							if (ip1 == null) {
								ip1 = prefix + i;
							} else if (ip2 == null) {
								ip2 = prefix + i;
							} else {
								break;
							}
						}
					}
					workspace.add(ip1 == null ? Config.IP127 : ip1,
							ip2 == null ? Config.IP127 : ip2);
				}
				viewer.refresh();
				viewer.getTable().layout();
			}
		});

		scan.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IPChooser chooser = new IPChooser(new IShellProvider() {

					@Override
					public Shell getShell() {
						return buttons.getShell();
					}

				}, BlockContainer.this.workspace);
				if (IPChooser.OK == chooser.open()) {
					String ips[] = chooser.get();
					int size = ips.length;
					for (int i = 0; i < size; i++) {
						workspace.add(ips[i], i == size ? Config.IP127
								: ips[i + 1]);
						i++;
					}
					viewer.refresh();
					viewer.getTable().layout();
				}
			}
		});

		sort.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Collections.sort(workspace.getBlocks(),
						new Comparator<BlockEntry>() {

							@Override
							public int compare(BlockEntry arg0, BlockEntry arg1) {
								int index0 = arg0.getIp1().getIp()
										.lastIndexOf(".");
								int index1 = arg1.getIp1().getIp()
										.lastIndexOf(".");
								if (index0 < 0 || index1 < 0) {
									return 0;
								}
								int i0 = Integer.parseInt(arg0.getIp1().getIp()
										.substring(index0 + 1));
								int i1 = Integer.parseInt(arg1.getIp1().getIp()
										.substring(index1 + 1));
								return i0 - i1;
							}
						});
				viewer.refresh();
				viewer.getTable().layout();
			}
		});

	}

}
