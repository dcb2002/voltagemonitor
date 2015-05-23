/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.dataReader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class ValueGetter {

	private static boolean DEBUG = false;

	private static InputRange[] RANGES = new InputRange[] {
			new InputRange("D8F0", "2710", "-10000", "10000", Integer.parseInt(
					"08", 16), new DecimalFormat("00000")),
			new InputRange("EC78", "1388", "-5000", "5000", Integer.parseInt(
					"09", 16), new DecimalFormat("0000")),
			new InputRange("FC18", "03E8", "-100", "100", Integer.parseInt(
					"0A", 16), new DecimalFormat("000.0")),
			new InputRange("EC78", "1388", "-500", "500", Integer.parseInt(
					"0B", 16), new DecimalFormat("000.0")),
			new InputRange("FA24", "05DC", "-150", "150", Integer.parseInt(
					"0C", 16), new DecimalFormat("000.0")),
			new InputRange("B1E0", "4E20", "-20", "20", Integer.parseInt("0D",
					16), new DecimalFormat("00.000")),
			new InputRange("00", "00", "00", "00", 0, new DecimalFormat(
					"000.000")) };

	private static int[][] commands;

	static {
		if (Config.isTest()) {
			commands = new int[][] {
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x03,
							0x00, 0x00, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x01, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x02, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x03, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x04, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x05, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x06, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x07, 0x00, 0x01 }, };
		} else {
			commands = new int[][] {
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x00, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x01, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x02, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x03, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x04, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x05, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x06, 0x00, 0x01 },
					new int[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x01, 0x04,
							0x00, 0x07, 0x00, 0x01 }, };
		}
	}

	private static int[] inputRangeCommand = new int[] { 0x00, 0x00, 0x00,
			0x00, 0x00, 0x06, 0x01, 0x03, 0x00, 0x0A, 0x00, 0x01 };

	private static InputRange RANGE_EMPTY = RANGES[RANGES.length - 1];

	private static void sendCommon(Socket socket, int channelNo)
			throws IOException {
		OutputStream in = socket.getOutputStream();
		for (int item : commands[channelNo]) {
			in.write(item);
		}
	}

	private static void sendCommonOfInputRange(Socket socket)
			throws IOException {
		OutputStream in = socket.getOutputStream();
		for (int item : inputRangeCommand) {
			in.write(item);
		}
	}

	private static String toString(int[] datas) {
		StringBuffer buf = new StringBuffer();
		for (int i : datas) {
			buf.append(Integer.toHexString(i));
			buf.append(" ");
		}
		return buf.toString();
	}

	private static int[] read(Socket socket) throws IOException {
		byte[] data = new byte[1024];
		int len;
		len = socket.getInputStream().read(data);
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			ret[i] = data[i] & 0xff;
		}
		return ret;
	}

	// 00 00 00 00 00 05 01 03 02 33 DD
	private static Integer getUpper(int[] data) {
		if (null == data || data.length < 11) {
			return null;
		}
		return data[9];
	}

	private static Integer getLower(int[] data) {
		if (null == data || data.length < 11) {
			return null;
		}
		return data[10];
	}

	public static InputRange getInputRange(IVoltageChannel channel) {
		if (!channel.getHost().testSocket()) {
			return RANGE_EMPTY;
		}
		try {
			sendCommonOfInputRange(channel.getSocket());
			Integer data = getLower(read(channel.getSocket()));
			if (null != data) {
				for (int i = 0; i < RANGES.length; i++) {
					if (data.intValue() == RANGES[i].getRange()) {
						return RANGES[i];
					}
				}
			}
		} catch (Exception e) {
			LogHelper.err(e);
		}
		return RANGE_EMPTY;
	}

	public static ChannelValue readChannalValue(IVoltageChannel channel) {
		if (!channel.getHost().testSocket()) {
			return ChannelValue.createNotConnectValue(channel);
		}
		try {
			sendCommon(channel.getSocket(), channel.getNo());
			int[] data = read(channel.getSocket());
			if (DEBUG && Config.isTest()) {
				LogHelper.debug(channel.getEntry().getParent().getIp() + ":"
						+ channel.getEntry().getIndex() + ":read:"
						+ toString(data));
			}
			Integer upper = getUpper(data);
			Integer lower = getLower(data);
			if (null != upper && null != lower) {
				long value = Long.parseLong(
						Integer.toHexString(upper.intValue())
								+ Integer.toHexString(lower.intValue()), 16);
				return ChannelValue.createValue(channel, channel.getRange()
						.covert(value));
			}
		} catch (SocketException se) {
			LogHelper.err(se);
			channel.getHost().resetSocket();
		} catch (Exception e) {
			LogHelper.err(e);
		}
		return ChannelValue.createReadErrorValue(channel);
	}

}
