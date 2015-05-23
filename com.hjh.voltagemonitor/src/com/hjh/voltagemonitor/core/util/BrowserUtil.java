/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.dataReader.BlockValue;

public class BrowserUtil {

	private static Browser page_main;
	private static boolean start_update_item;

	public static void setBrowser(Browser page_main) {
		BrowserUtil.page_main = page_main;
		start_update_item = false;
	}

	public static boolean isStartUpdate() {
		return start_update_item;
	}

	public static void setStartUpdateItem(boolean b) {
		start_update_item = b;
	}

	public static void setURL() {
		if (!isReady()) {
			return;
		}
		start_update_item = false;
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				LogHelper.debug("Load html");
				BrowserUtil.page_main.setUrl(Config.getShowFilePath());
			}
		});

	}

	public static boolean isReady() {
		return null != page_main;
	}

	public static void createBlockInfoFile(IVoltageHost[] arr,
			IProgressMonitor monitor) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(Config.getBlockInfoLocation());
			writer.append("hjh.datas = [\r\n");
			boolean first = true;
			monitor.beginTask("Read Channels", arr.length);
			monitor.subTask("Read Channels");
			for (IVoltageHost host : arr) {
				host.run();
				BlockValue value = host.getRecorder().currentValue();
				if (first) {
					first = false;
				} else {
					writer.append(",");
				}
				writer.append(String.format(
						"{index:%d,title:'%s',value:'%s',valuetype:'%s'}\r\n",
						host.getId(), host.getTitle(), value.getValue(),
						value.getType()));
				monitor.worked(1);
			}
			writer.append("];\r\n");
		} catch (IOException e) {
			LogHelper.err(e);
		} finally {
			if (null != writer) {
				try {
					writer.close();
				} catch (IOException e) {
					LogHelper.err(e);
				}
			}
			monitor.done();
		}

	}

}
