/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.hjh.voltagemonitor.Activator;
import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageDataProvider;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.util.BrowserUtil;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.core.util.IpGetter;
import com.hjh.voltagemonitor.core.util.LogHelper;
import com.hjh.voltagemonitor.module.BlockEntry;
import com.hjh.voltagemonitor.module.WorkspaceEntry;
import com.hjh.voltagemonitor.server.ServerUtil;

@SuppressWarnings("restriction")
public class VoltageDataProviderImpl implements IVoltageDataProvider {

	private WorkspaceEntry workspace;

	@Override
	public boolean prepare(IProgressMonitor monitor, IWorkbenchWindow workbench) {

		try {
			doPrepare(monitor, workbench);
		} catch (Exception e) {
			LogHelper.err(e);
			return false;
		} finally {
			monitor.done();
		}

		return true;
	}

	private void doPrepare(IProgressMonitor monitor, IWorkbenchWindow workbench)
			throws IOException {
		monitor.beginTask("Init System", 6);
		monitor.subTask("Init System");
		prepareHtmlFiles(Policy.subMonitorFor(monitor, 1));
		monitor.subTask("start server");
		ServerUtil.getInstance().start();
		monitor.worked(1);
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LogHelper.err(e);
			}
			if (BrowserUtil.isReady()) {
				break;
			}
		}
		resetChannels(Policy.subMonitorFor(monitor, 4));
	}

	private void prepareHtmlFiles(IProgressMonitor monitor) throws IOException {

		String files[] = new String[] { "show.html", "detail.html",
				"detailCell.html", "jquery.min.js", "digifaw-webfont.eot",
				"digifaw-webfont.svg", "digifaw-webfont.ttf",
				"digifaw-webfont.woff", "font-stylesheet.css", "exporting.js",
				"highstock.js", "common.js", "jquery.loadmask.css",
				"jquery.loadmask.js", "loading.gif" };
		monitor.beginTask("Init Files", files.length);
		monitor.subTask("Init Files");
		File to = Config.getHtmlRealLocation();
		IPath root = Config.getHtmlResRoot();

		byte[] cache = new byte[1024];
		int len;
		for (String item : files) {

			LogHelper.debug("copy file :" + item);
			InputStream in = FileLocator.openStream(Activator.getDefault()
					.getBundle(), root.append(item), true);
			OutputStream out = new FileOutputStream(new File(to, item));
			while (true) {
				len = in.read(cache);
				if (len <= 0) {
					break;
				}
				out.write(cache, 0, len);
			}
			in.close();
			out.close();
			monitor.worked(1);
		}
		monitor.done();
	}

	public void resetChannels(IProgressMonitor monitor) {

		workspace = WorkspaceEntry.load();
		final Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0]
				.getShell();
		shell.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				shell.setText(workspace.getTitle() + "--" + Config.getTitle()
						+ (workspace.isEmulation() ? "  Emulation" : ""));
			}
		});
		Config.reset();
		monitor.beginTask("Init ChannelInfos", 15);
		monitor.subTask("Init ChannelInfos");
		IVoltageHost[] arr = loadHost(Policy.subMonitorFor(monitor, 2),
				workspace);
		for (IVoltageHost ip : arr) {
			ip.setId(Config.createChannelId());
		}
		BrowserUtil.createBlockInfoFile(arr, Policy.subMonitorFor(monitor, 8));
		monitor.subTask("Draw Panel");
		BrowserUtil.setURL();
		monitor.worked(3);
		monitor.subTask("Add Monitor");
		for (IVoltageHost ip : arr) {
			ChannelMonitor.monitor(ip);
		}
		monitor.worked(2);
		BrowserUtil.setStartUpdateItem(true);
		monitor.done();

	}

	@Override
	public String[] scanIps(IProgressMonitor monitor) {

		monitor.beginTask("Scan ips", 2);
		monitor.subTask("Scan ips");

		String ips[] = new IpGetter().PingAll(Policy.subMonitorFor(monitor, 1));

		List<String> hosts = new ArrayList<String>();
		IProgressMonitor submonitor = Policy.subMonitorFor(monitor, 1);
		submonitor.beginTask("Test ips", ips.length);
		submonitor.subTask("Test ips");
		for (String ip : ips) { // 对所有局域网Ip
			LogHelper.debug("测试IP:" + ip);
			Socket temp = CommonUtil.newSocket(ip);
			if (null != temp) {
				hosts.add(ip);
				try {
					temp.close();
				} catch (IOException e) {
					LogHelper.err(e);
				}
			}
			submonitor.worked(1);
		}
		submonitor.done();
		monitor.done();

		return hosts.toArray(new String[hosts.size()]);
	}

	private IVoltageHost[] loadHost(IProgressMonitor monitor,
			WorkspaceEntry workspace) {
		List<IVoltageHost> hosts = new ArrayList<IVoltageHost>();
		monitor.beginTask("Add blocks", workspace.getBlocks().size());
		monitor.subTask("Add blocks");
		for (BlockEntry block : workspace.getBlocks()) {
			// LogHelper.debug("Add Block:" + block.getId());
			hosts.add(new VoltageHostImpl(block));
			monitor.worked(1);
		}
		monitor.done();
		return hosts.toArray(new IVoltageHost[hosts.size()]);
	}

	@Override
	public WorkspaceEntry getWorkspace() {
		return this.workspace;
	}

}
