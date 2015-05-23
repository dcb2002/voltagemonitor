/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.dataReader;

import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.IVoltageHost;

public class BlockValue {

	private final static double MIN_VALUE = 0.00001;

	private ChannelValue channelValues[];
	private double sum;
	private IVoltageHost host;

	public BlockValue(IVoltageHost host) {
		this.host = host;
		IVoltageChannel[] channels = host.getChannels();
		channelValues = new ChannelValue[channels.length];
		int i = 0;
		sum = 0;
		for (IVoltageChannel item : channels) {
			if (host.isRemove()) {
				break;
			}
			// LogHelper.debug("get channel value:" +
			// item.getEntry().getParent().getIp()+":"+item.getNo());
			channelValues[i] = item.getValue();
			sum += channelValues[i].getDvalue();
			i++;
		}
	}

	public String toJson() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (ChannelValue item : channelValues) {
			if (1 != builder.length()) {
				builder.append(",");
			}
			builder.append(Double.parseDouble(item.getValue()));
		}
		builder.append("]");
		return builder.toString();
	}

	public boolean isSame(BlockValue value) {
		if (null == value
				|| value.channelValues.length != this.channelValues.length) {
			return false;
		}
		int len = channelValues.length;
		for (int i = 0; i < len; i++) {
			if (Math.abs(this.channelValues[i].getDvalue()
					- value.channelValues[i].getDvalue()) > MIN_VALUE) {
				return false;
			}
		}
		return true;
	}

	public ChannelValue[] getChannelValues() {
		return channelValues;
	}

	public void setChannelValues(ChannelValue[] channelValues) {
		this.channelValues = channelValues;
	}

	public String getValue() {
		return host.format(sum);
	}

	public String getType() {
		return ValueTypeUtil.getType(this.host.getEntry(), sum);
	}

}
