/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.dataReader;

import com.hjh.voltagemonitor.core.IVoltageChannel;

public class ChannelValue {

	public static ChannelValue DP = new ChannelValue(ValueTypeUtil.PASS_VALUE,
			0, ValueTypeUtil.PASS);

	private static double random(ChannelValue prevalue) {
		if (null == prevalue) {
			return Math.random() * 420 - 120;
		}
		return prevalue.dvalue + (Math.random() * 5 - 2);
	}

	/**
	 * 创建随机值
	 * 
	 * @param channel
	 * @return
	 */
	public static ChannelValue createEmulationValue(IVoltageChannel channel) {
		return createValue(channel, random(channel.getPreValue()));
	}

	public static ChannelValue createNotConnectValue(IVoltageChannel channel) {
		return new ChannelValue(channel.format(channel.getEntry()
				.getDefaultValue()), channel.getEntry().getDefaultValue(),
				ValueTypeUtil.NOT_CONNECT);
	}

	public static ChannelValue createReadErrorValue(IVoltageChannel channel) {
		return new ChannelValue(channel.format(channel.getEntry()
				.getDefaultValue()), channel.getEntry().getDefaultValue(),
				ValueTypeUtil.ERROR);
	}

	public static ChannelValue createValue(IVoltageChannel channel, double value) {
		value = value - channel.getEntry().getTare();
		return new ChannelValue(channel.format(value), value, getType(channel,
				value));
	}

	private static String getType(IVoltageChannel channel, double value) {
		return ValueTypeUtil.getType(channel.getEntry(), value);
	}

	private String value;
	private String type;
	private double dvalue;

	public ChannelValue(String value, double dvalue, String type) {
		this.value = value;
		this.dvalue = dvalue;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getDvalue() {
		return dvalue;
	}

	public void setDvalue(double dvalue) {
		this.dvalue = dvalue;
	}

}
