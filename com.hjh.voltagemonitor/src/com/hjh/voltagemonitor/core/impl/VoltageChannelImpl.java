/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.impl;

import java.net.Socket;
import java.text.DecimalFormat;

import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.dataReader.ChannelValue;
import com.hjh.voltagemonitor.core.dataReader.InputRange;
import com.hjh.voltagemonitor.core.dataReader.ValueGetter;
import com.hjh.voltagemonitor.module.ChannelEntry;

public class VoltageChannelImpl implements IVoltageChannel {

	private ChannelEntry entry;
	private IVoltageHost host;
	private InputRange range;
	private DecimalFormat df;
	private int bindex = -1;
	private ChannelValue preValue;

	public VoltageChannelImpl(IVoltageHost host, ChannelEntry entry) {
		this.host = host;
		this.entry = entry;
		this.preValue = null;
	}

	public ChannelEntry getEntry() {
		return entry;
	}

	@Override
	public IVoltageHost getHost() {
		return host;
	}

	@Override
	public InputRange getRange() {
		if (null == range) {
			range = ValueGetter.getInputRange(this);
		}
		return range;
	}

	@Override
	public ChannelValue getValue() {
		ChannelValue cur = doGetValue();
		preValue = cur;
		return cur;
	}

	private ChannelValue doGetValue() {
		if (VoltageSystemFactory.getProvider().getWorkspace().isEmulation()) {
			return ChannelValue.createEmulationValue(this);
		}
		return ValueGetter.readChannalValue(this);
	}

	@Override
	public int getNo() {
		return this.getEntry().getIndex() - 1;
	}

	@Override
	public String getTitle() {
		return entry.getTitle();
	}

	private DecimalFormat getDF() {
		if (null == df) {
			try {
				df = new DecimalFormat(entry.getFormat());
			} catch (Exception e) {
				df = this.getRange().getDecimalFormat();
			}
		}
		return df;
	}

	@Override
	public String format(double value) {
		return getDF().format(value);
	}

	public Socket getSocket() {
		return this.getHost().getSocket(this);
	}

	@Override
	public int getBlockIndex() {
		if (-1 == bindex) {
			int index = 0;
			for (IVoltageChannel item : this.getHost().getChannels()) {
				if (item.equals(this)) {
					bindex = index;
					break;
				}
				index++;
			}
		}
		return bindex;
	}

	@Override
	public ChannelValue getPreValue() {
		return preValue;
	}
}
