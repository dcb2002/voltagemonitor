/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.dataReader.BlockValue;
import com.hjh.voltagemonitor.core.dataReader.ChannelValue;

/**
 * 
 * @author huangjinhong
 * 
 */
public class DataRecorder {

	private final static int CACHE_SIZE = 60;
	private final static SimpleDateFormat FILE_NAME_DF = new SimpleDateFormat(
			"yyyy_MM_dd");

	public static IDataRecorder createInstance(IVoltageHost host) {
		return new DataRecorderImpl(host);
	}

	public static interface IDataRecorder {
		public void close();

		public IVoltageHost getHost();

		public long currentTime();

		public BlockValue currentValue();

		public void write(long time, BlockValue value);

		public String toJsonData(IVoltageChannel currentChannel);

		public double currentValue(IVoltageChannel currentChannel);
	}

	private static class DataRecorderImpl implements IDataRecorder {

		private IVoltageHost host;
		private File dataFile;
		private StringBuilder cache;
		private BlockValue currentValue;
		private long currentTime;
		private int cacheCount;
		private long basetime;
		private boolean stopStroe;

		public DataRecorderImpl(IVoltageHost host) {
			this.host = host;
			dataFile = new File(Config.getDataCacheLocation() + File.separator
					+ host.getEntry().getId() + FILE_NAME_DF.format(new Date())
					+ ".log");
			dataFile.getParentFile().mkdirs();
			currentValue = null;
			currentTime = System.currentTimeMillis();
			if (dataFile.exists()) {
				basetime = readBaseTime();
			} else {
				basetime = System.currentTimeMillis();
				store(basetime + "\r\n");
			}
			stopStroe = false;
			newCache();
		}

		private String newCache() {
			StringBuilder old = cache;
			cache = new StringBuilder(200 * CACHE_SIZE);
			cacheCount = 0;
			return old == null ? "" : old.toString();
		}

		@Override
		public void write(long time, BlockValue value) {
			if (null == cache) {
				return;
			}
			this.currentTime = time;
			if (value.isSame(this.currentValue)) {
				return;
			}
			this.currentValue = value;
			String content = null;
			synchronized (cache) {
				cache.append(time - basetime);
				for (ChannelValue item : value.getChannelValues()) {
					cache.append(",");
					cache.append(item.getValue());
				}
				cache.append("\r\n");
				cacheCount++;
				if (!stopStroe && cacheCount >= CACHE_SIZE) {
					content = newCache();
				}
			}
			if (null != content) {
				// 可考虑用线程池
				store(content);
			}
		}

		private long readBaseTime() {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(dataFile));
				return Long.parseLong(reader.readLine());
			} catch (IOException e) {
				LogHelper.err(e);
			} finally {
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
						LogHelper.err(e);
					}
				}
			}
			return System.currentTimeMillis();
		}

		private void store(String content) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(dataFile, true);
				writer.write(content);
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
			}
		}

		@Override
		public void close() {
			synchronized (cache) {
				store(cache.toString());
				cache = null;
			}
		}

		@Override
		public IVoltageHost getHost() {
			return host;
		}

		@Override
		public long currentTime() {
			return this.currentTime;
		}

		@Override
		public BlockValue currentValue() {
			return currentValue;
		}

		@Override
		public String toJsonData(IVoltageChannel currentChannel) {
			int bindex = currentChannel.getBlockIndex();
			stopStroe = true;
			FileReader reader = null;
			try {
				StringBuilder builder = new StringBuilder();
				if (dataFile.exists()) {
					reader = new FileReader(dataFile);
					char[] cache = new char[512];
					int len;
					while (true) {
						len = reader.read(cache);
						if (len <= 0) {
							break;
						}
						builder.append(cache, 0, len);
					}
				}
				builder.append(this.cache);

				String lines[] = builder.toString().split("\r\n");
				builder.delete(0, builder.length());
				builder.append("[");
				boolean first = true;
				String[] temp;
				for (String item : lines) {
					if (item.trim().length() == 0 || item.indexOf(",") < 0) {
						continue;
					}
					if (first) {
						first = false;
					} else {
						builder.append(",");
					}
					builder.append("[");
					temp = item.split(",");
					builder.append(Integer.parseInt(temp[0]) + this.basetime);
					builder.append(",");
					builder.append(Double.parseDouble(temp[bindex + 1]));
					builder.append("]");
				}
				builder.append("]");
				return builder.toString();
			} catch (Exception e) {
				LogHelper.err(e);
			} finally {
				stopStroe = false;
				if (null != reader) {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			}
			return "[]";

		}

		@Override
		public double currentValue(IVoltageChannel currentChannel) {
			if (null == this.currentValue) {
				return 0.0;
			}
			return Double
					.parseDouble(this.currentValue.getChannelValues()[currentChannel
							.getBlockIndex()].getValue());
		}

	}

}