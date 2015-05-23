/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.threadspool.ThreadsPool;
import com.hjh.voltagemonitor.core.util.BrowserUtil;
import com.hjh.voltagemonitor.core.util.DataRecorder.IDataRecorder;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class ChannelMonitor {

	private static Set<IVoltageHost> hosts = new HashSet<IVoltageHost>();

	static {
		Thread t = new Thread() {
			public void run() {
				while (true) {
					if (BrowserUtil.isStartUpdate()) {
						synchronized (hosts) {
							for (IVoltageHost item : hosts) {
								if (Config.isStop()) {
									break;
								}
								if (!item.isRunning()) {
									ThreadsPool.getInstance().run(item);
								}
							}
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						LogHelper.err(e);
					}
					if (Config.isStop()) {
						break;
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	public static void reset() {
		synchronized (hosts) {
			for (IVoltageHost item : hosts) {
				item.remove();
			}
			hosts.clear();
		}
	}

	public static void monitor(IVoltageHost host) {
		synchronized (hosts) {
			hosts.add(host);
		}
	}

	public static IDataRecorder[] getRecorders() {
		List<IDataRecorder> records = new ArrayList<IDataRecorder>();
		synchronized (hosts) {
			for (IVoltageHost item : hosts) {
				records.add(item.getRecorder());
			}
		}
		return records.toArray(new IDataRecorder[records.size()]);
	}

	public static IVoltageHost searchBlock(int id) {
		synchronized (hosts) {
			for (IVoltageHost item : hosts) {
				if (item.getId() == id) {
					return item;
				}
			}
		}
		return null;
	}

}
