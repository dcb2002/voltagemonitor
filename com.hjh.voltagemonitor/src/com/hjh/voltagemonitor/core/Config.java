/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.hjh.voltagemonitor.core.impl.ChannelMonitor;
import com.hjh.voltagemonitor.core.util.DataLogRecorder;
import com.hjh.voltagemonitor.core.util.LogHelper;
import com.hjh.voltagemonitor.server.ServerUtil;

public class Config {

	private static int channelId = 0;

	public static String IP127 = "127.0.0.1";

	public static int getPort() {
		return 8505;
	}

	public static String getTitle() {
		return "Voltage Monitor";
	}

	public static File getRoot() {
		try {
			return new File(Platform.getInstanceLocation().getURL().toURI());
		} catch (URISyntaxException e) {
			LogHelper.err(e);
			return null;
		}
	}

	public static String getDataRecordLocation() {
		return getRoot().getAbsolutePath() + File.separator + "datarecorder";
	}

	public static String getDataCacheLocation() {
		return getRoot().getAbsolutePath() + File.separator + "datacache";
	}

	public static File getWorkspaceConfigFile() {
		return new File(getRoot(), "workspace.xml");
	}

	public static String getDetailFilePath(int blockId) {
		return "http://127.0.0.1:8505/html/detail.html?blockID=" + blockId;
	}
	
	public static String getDetailCellFilePath(int blockId,int cell) {
		return "http://127.0.0.1:8505/html/detailCell.html?blockID=" + blockId+"&cell="+cell;
	}
	
	public static String getShowFilePath() {
		return "http://127.0.0.1:8505/html/show.html";
	}

	public static File getHtmlRealLocation() {
		File html = new File(getRoot(), "html");
		if (!html.exists() && !html.mkdirs()) {
			LogHelper.err("创建文件夹失败:" + html.getAbsolutePath());
		}
		return html;
	}

	public static File getBlockInfoLocation() {
		return new File(getHtmlRealLocation(), "blocks.js");
	}

	public static IPath getHtmlResRoot() {
		return new Path("res/html");
	}

	public static int maxThreadNumber() {
		return 20;
	}

	public static int liveThreadNumber() {
		return 5;
	}

	public static long getSocketTimeOut() {
		return 3000;
	}

	public static int getChannelsSizeForEachIp() {
		return 8;
	}

	public static boolean isTest() {
		return true;
	}

	public static boolean stop = false;

	public static boolean isStop() {
		return stop;
	}

	public static void reset() {
		ChannelMonitor.reset();
		channelId = 0;
		DataLogRecorder.stop();
	}

	public static void setStop() {
		ServerUtil.getInstance().stop();
		reset();
		stop = true;
	}

	public static boolean isDebugThreadPool() {
		return false;
	}

	public static synchronized int createChannelId() {
		channelId++;
		return channelId;
	}

	public static void setXulrunner() {
		try {
			System.setProperty("org.eclipse.swt.browser.XULRunnerPath",
					new File(new File(Platform.getInstallLocation().getURL()
							.toURI()), "xulrunner").getCanonicalPath());
		} catch (Exception e) {
			LogHelper.err(e);
		}
	}
}
