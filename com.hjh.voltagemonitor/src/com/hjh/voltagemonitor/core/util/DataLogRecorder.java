/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.dataReader.BlockValue;
import com.hjh.voltagemonitor.core.dataReader.ChannelValue;
import com.hjh.voltagemonitor.core.util.DataRecorder.IDataRecorder;

/**
 * 
 * @author huangjinhong
 * 
 */
public class DataLogRecorder {

	private final static SimpleDateFormat FILE_NAME_DF = new SimpleDateFormat(
			"yyyy_MM_dd_HH_mm_ss");
	private final static SimpleDateFormat LINE_TIME_DF = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss S");

	private static DataLogRecorder instance;

	public static boolean isLogging() {
		return null != instance;
	}

	public interface LogStateChange {
		public void change(boolean log);
	}

	public static List<LogStateChange> listeners = new ArrayList<LogStateChange>();

	public static void addListener(LogStateChange listener) {
		listeners.add(listener);
	}

	private static void touch(boolean log) {
		for (LogStateChange item : listeners) {
			item.change(log);
		}
	}

	public static synchronized boolean start(IDataRecorder[] recorders,
			int interval) {
		if (null != instance) {
			return true;
		}
		try {
			instance = new DataLogRecorder(recorders, interval);
			touch(true);
			return true;
		} catch (IOException e) {
			LogHelper.err(e);
		}
		return false;
	}

	public static void open() {
		if (null != instance) {
			instance.doOpen();
		}
	}

	public synchronized static void stop() {
		if (null != instance) {
			instance.doStop();
			instance = null;
			touch(false);
		}
	}

	private IDataRecorder[] recorders;
	private Writer writer;
	private File storeFile;
	private int interval;
	private boolean close;

	private DataLogRecorder(IDataRecorder[] recorders, int interval)
			throws IOException {
		this.recorders = recorders;
		this.interval = interval <= 100 ? 100 : interval; // 防止错误值
		storeFile = new File(Config.getDataRecordLocation() + File.separator
				+ FILE_NAME_DF.format(new Date()) + ".csv");
		if (!storeFile.getParentFile().exists()) {
			storeFile.getParentFile().mkdirs();
		}
		close = true;
		writer = new BufferedWriter(new FileWriter(storeFile));
		StringBuilder lineBuilder = new StringBuilder();
		lineBuilder.append("TIME");
		String title;
		for (IDataRecorder item : recorders) {
			title = item.getHost().getTitle();
			lineBuilder.append(",");
			lineBuilder.append(title);
			for (IVoltageChannel channel : item.getHost().getChannels()) {
				lineBuilder.append(",");
				lineBuilder.append(channel.getTitle() + " of " + title);
			}
		}
		lineBuilder.append("\r\n");
		writer.write(lineBuilder.toString());
		writer.flush();
		doStart();
	}

	private void doStart() {
		new Thread() {
			public void run() {
				close = false;
				StringBuilder lineBuilder = new StringBuilder();
				BlockValue blockValue;
				while (true) {
					if (close) {
						break;
					}
					lineBuilder.append(LINE_TIME_DF.format(new Date()));
					for (IDataRecorder item : recorders) {
						blockValue = item.currentValue();
						lineBuilder.append(",");
						lineBuilder.append(blockValue.getValue());
						for (ChannelValue channel : blockValue
								.getChannelValues()) {
							lineBuilder.append(",");
							lineBuilder.append(channel.getValue());
						}
					}

					lineBuilder.append("\r\n");
					try {
						writer.write(lineBuilder.toString());
					} catch (IOException e) {
						LogHelper.err(e);
					}
					if (close) {
						break;
					}
					lineBuilder.delete(0, lineBuilder.length());
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						LogHelper.err(e);
					}
					if (close) {
						break;
					}
				}
				try {
					writer.close();
				} catch (IOException e) {
					LogHelper.err(e);
				}
				writer = null;
			}
		}.start();
	}

	private void doOpen() {
		try {
			Desktop.getDesktop().open(storeFile.getParentFile());
		} catch (IOException e) {
			LogHelper.err(e);
		}
	}

	private void doStop() {
		if (null != writer) {
			close = true;
			while (null != writer) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LogHelper.err(e);
				}
			}
		}
	}

}