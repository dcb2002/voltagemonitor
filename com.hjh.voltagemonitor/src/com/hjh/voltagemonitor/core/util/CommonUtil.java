/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hjh.voltagemonitor.Activator;
import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.module.IPEntry;

public class CommonUtil {

	public static void closeSocket(Socket socket) {
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public static boolean testSocket(Socket socket, IPEntry ip) {
		if (null == socket) {
			return false;
		}
		if (!socket.isConnected()) {
			try {
				CommonUtil.connectSocket(socket, ip);
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	public static void connectSocket(Socket socket, IPEntry ip)
			throws IOException {
		socket.connect(new InetSocketAddress(ip.getIp(), 502), ip.getTimeout());
	}

	public static void connectSocket(Socket socket, String ip)
			throws IOException {
		socket.connect(new InetSocketAddress(ip, 502),
				(int) Config.getSocketTimeOut());
	}

	public static Socket newSocket(final String ip) {
		Socket socket = new Socket();
		long time = System.currentTimeMillis();
		try {
			connectSocket(socket, ip);
		} catch (IOException e) {
			socket = null;
		}
		LogHelper.debug("IP Test End : " + ip + ":"
				+ (socket == null ? " NOT " : " YES ") + ":cost:"
				+ (System.currentTimeMillis() - time));
		return socket;
	}

	public static Socket newSocket(final IPEntry ip) {
		Socket socket = new Socket();
		long time = System.currentTimeMillis();
		try {
			connectSocket(socket, ip);
		} catch (IOException e) {
			socket = null;
		}
		LogHelper.debug("IP Test End : " + ip.getIp() + ":"
				+ (socket == null ? " NOT " : " YES ") + ":cost:"
				+ (System.currentTimeMillis() - time));
		return socket;
	}

	public static String readBundleResource(IPath path) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = FileLocator.openStream(Activator.getDefault()
				.getBundle(), path, true);
		byte[] cache = new byte[1024];
		int len;
		while (true) {
			len = in.read(cache);
			if (len <= 0) {
				break;
			}
			out.write(cache, 0, len);
		}
		in.close();
		out.close();
		return out.toString("utf-8");
	}

	public static Shell getShell() {
		return Display.getDefault().getActiveShell();
	}

	public static Display getDisplay() {
		return Display.getDefault();
	}

}
