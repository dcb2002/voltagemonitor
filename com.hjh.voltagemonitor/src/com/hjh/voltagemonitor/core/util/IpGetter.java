/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.threadspool.ThreadsPool;

public class IpGetter {

	private int runCount = 0;
	private List<PingIp> runPings = new ArrayList<PingIp>();
	private List<String> ips = new ArrayList<String>();
	private IProgressMonitor monitor;
	private Object RunCountLock = new Object();
	private Object PingIPLock = new Object();

	private class Pinnger implements Runnable {

		public void run() {
			PingIp pingItem;
			while (true) {
				if (runPings.size() <= 0) {
					break;
				}
				synchronized (PingIPLock) {
					if (runPings.size() <= 0) {
						break;
					}
					pingItem = runPings.remove(0);
				}
				pingItem.run();
			}
		}
	}

	public static String getLocalIP() {
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LogHelper.err(e);
			return Config.IP127;
		}
		return host.getHostAddress();
	}

	public String[] PingAll(IProgressMonitor monitor) {
		this.monitor = monitor;

		// 首先得到本机的IP，得到网段
		InetAddress host;
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LogHelper.err(e);
			return new String[] {};
		}

		int ipSize = 255;
		monitor.beginTask("Ping ips", ipSize);
		monitor.subTask("Ping ips");

		String hostAddress = host.getHostAddress();

		if (Config.IP127.equals(hostAddress)) {
			return new String[] { Config.IP127 };
		}

		int k = 0;
		k = hostAddress.lastIndexOf(".");
		String ss = hostAddress.substring(0, k + 1);
		for (int i = 1; i < ipSize; i++) { // 对所有局域网Ip
			String iip = ss + i;
			PingIp p = new PingIp(iip);
			runPings.add(p);
		}

		runCount = runPings.size();

		for (int i = 0; i < Config.maxThreadNumber(); i++) {
			ThreadsPool.getInstance().run(new Pinnger());
		}

		// 等着所有Ping结束
		while (runCount > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LogHelper.err(e);
			}
		}

		monitor.done();
		Collections.sort(ips, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				int i0 = Integer.parseInt(arg0.substring(arg0.lastIndexOf(".") + 1));
				int i1 = Integer.parseInt(arg1.substring(arg1.lastIndexOf(".") + 1));
				return i0 - i1;
			}
		});
		return ips.toArray(new String[ips.size()]);
	}

	private class PingIp {

		private String ip; // IP

		public PingIp(String ip) {
			this.ip = ip;
		}

		public void run() {
			InputStream in = null;
			try {
				final Process p = Runtime.getRuntime().exec(
						"ping " + ip + " -w 300 -n 1");
				in = p.getInputStream();
				InputStreamReader ir = new InputStreamReader(in);
				LineNumberReader input = new LineNumberReader(ir);
				boolean start = false;
				boolean yes = false;
				String line;
				while (true) {
					line = input.readLine();
					if (null == line) {
						break;
					}
					line = line.toLowerCase();

					if (start) {
						// LogHelper.debug("--ping--:" + line);
						yes = line.indexOf(ip) >= 0;
						break;
					} else {
						if (line.indexOf("ping") >= 0 && line.indexOf(ip) >= 0) {
							start = true;
						}
					}
				}
				if (yes) {
					ips.add(ip);
				}
			} catch (Throwable e) {
			} finally {
				if (null != in) {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
				synchronized (RunCountLock) {
					runCount--;
				}
				monitor.worked(1);
			}
		}
	}

}
