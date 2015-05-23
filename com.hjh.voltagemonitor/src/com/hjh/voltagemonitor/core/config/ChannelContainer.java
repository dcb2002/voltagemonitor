/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.config;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;

import com.hjh.voltagemonitor.core.util.PropertyProxy;
import com.hjh.voltagemonitor.core.util.PropertyProxy.PropertyProxyGetAndSet;
import com.hjh.voltagemonitor.core.util.TableUtil;
import com.hjh.voltagemonitor.module.BlockEntry;
import com.hjh.voltagemonitor.module.ChannelEntry;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class ChannelContainer {

	private TableViewer viewer;
	private Group container;

	public void createDialogArea(Group parent, WorkspaceEntry workspace) {

		this.container = parent;
		parent.setLayout(new FillLayout());
		viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		viewer.getTable().getHorizontalBar().setVisible(true);
		TableUtil.layoutTable(viewer, propertyProxys);
		TableUtil.modifySetting(viewer, propertyProxys);
		TableUtil.initStruct(viewer, propertyProxys);
		TableUtil.initContent(viewer, propertyProxys);

		if (!workspace.isEmpty()) {
			this.setInput(workspace.getBlocks().get(0));
		}
	}

	@SuppressWarnings("unchecked")
	private PropertyProxy<ChannelEntry>[] propertyProxys = new PropertyProxy[] {

			new PropertyProxy<ChannelEntry>(false, "index", "Index", 1)
					.setGetAndSet(new PropertyProxyGetAndSet<ChannelEntry>() {

						@Override
						public String get(ChannelEntry obj) {
							return "" + obj.getGindex();
						}

						@Override
						public boolean set(ChannelEntry obj, String val) {
							return false;
						}

					}),
			new PropertyProxy<ChannelEntry>(false, "title", "Title", 2),
			new PropertyProxy<ChannelEntry>("format", "Fromat", 3),
			new PropertyProxy<ChannelEntry>("defaultValue", "Default", 3),
			new PropertyProxy<ChannelEntry>("tare", "Tare value", 3),
			new PropertyProxy<ChannelEntry>("undervoltage", "Under voltage", 3),
			new PropertyProxy<ChannelEntry>("warnning", "Warnning", 3),
			new PropertyProxy<ChannelEntry>("overvoltage", "Over voltage", 3),
			new PropertyProxy<ChannelEntry>(false, "use", "Use", 1)

	};

	public void setInput(BlockEntry entry) {
		viewer.setInput(entry);
		container.setText(entry.getIp1().getIp() + "--"
				+ entry.getIp2().getIp() + " channels");
	}

}
