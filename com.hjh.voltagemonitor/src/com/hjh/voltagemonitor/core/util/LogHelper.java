/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import com.hjh.voltagemonitor.Activator;
import com.hjh.voltagemonitor.core.Config;

public class LogHelper {

	private static Status newStatus(int level, String msg, Throwable e) {
		return new Status(level, Activator.PLUGIN_ID, IStatus.OK, msg, e);
	}

	private static void logToPlatform(Status s) {
		if (Config.isStop()) {
			return;
		}
		ILog log = Platform.getLog(Activator.getDefault().getBundle());
		if (null != log) {
			log.log(s);
		}
	}

	private static void logToPlatform(int level, String msg, Throwable e) {
		if (!Platform.isRunning() || Config.isStop()) {
			return;
		}
		logToPlatform(newStatus(level, msg, e));
	}

	public static void err(Throwable e) {
		logToPlatform(IStatus.ERROR, "", e);
	}

	public static void err(String msg) {
		logToPlatform(IStatus.ERROR, msg, null);
	}

	public static void debug(String msg) {
		logToPlatform(IStatus.INFO, msg, null);
	}

	public static void info(String msg) {
		logToPlatform(IStatus.INFO, msg, null);
	}

}
