/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.impl;

import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.dataReader.BlockValue;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.core.util.DataRecorder;
import com.hjh.voltagemonitor.core.util.DataRecorder.IDataRecorder;
import com.hjh.voltagemonitor.module.BlockEntry;
import com.hjh.voltagemonitor.module.ChannelEntry;

public class VoltageHostImpl implements IVoltageHost {

	private int id;
	private BlockEntry block;
	private Socket socket1;
	private Socket socket2;
	private boolean running = false;
	private List<IVoltageChannel> channels;
	private boolean remove = false;
	private IDataRecorder recorder;
	private DecimalFormat df;

	public VoltageHostImpl(BlockEntry block) {
		this.block = block;
		resetSocket();
		this.channels = new ArrayList<IVoltageChannel>();
		for (ChannelEntry item : block.getChannels()) {
			if (item.isUse()) {
				channels.add(new VoltageChannelImpl(this, item));
			}
		}
		this.recorder = DataRecorder.createInstance(this);
	}

	@Override
	public IDataRecorder getRecorder() {
		return recorder;
	}

	public IVoltageChannel[] getChannels() {
		return channels.toArray(new IVoltageChannel[channels.size()]);
	}

	@Override
	public void run() {
		running = true;
		try {
			long time = System.currentTimeMillis();
			BlockValue value = new BlockValue(this);
			if (this.isRemove()) {
				return;
			}
			this.getRecorder().write(time, value);
		} finally {
			running = false;
		}
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean testSocket() {

		if (null == this.block) {
			return false;
		}

		if (!CommonUtil.testSocket(socket1, this.block.getIp1())) {
			return false;
		}

		if (!CommonUtil.testSocket(socket2, this.block.getIp2())) {
			return false;
		}

		return true;
	}

	@Override
	public void resetSocket() {
		CommonUtil.closeSocket(socket1);
		CommonUtil.closeSocket(socket2);
		socket1 = VoltageSystemFactory.getProvider().getWorkspace()
				.isEmulation() ? null : CommonUtil.newSocket(this.block
				.getIp1());
		socket2 = VoltageSystemFactory.getProvider().getWorkspace()
				.isEmulation() ? null : CommonUtil.newSocket(this.block
				.getIp2());
	}

	public boolean isRemove() {
		return remove;
	}

	@Override
	public void remove() {
		this.remove = true;
		getRecorder().close();
	}

	public IVoltageChannel search(String ip, int index) {
		if (block.getIp1().getIp().equals(ip)
				|| block.getIp2().getIp().equals(ip)) {
			for (IVoltageChannel subitem : getChannels()) {
				if (subitem.getEntry().getParent().getIp().equals(ip)
						&& subitem.getEntry().getIndex() == index) {
					return subitem;
				}
			}
		}
		return null;
	}

	private DecimalFormat getDF() {
		if (null == df) {
			try {
				df = new DecimalFormat(block.getFormat());
			} catch (Exception e) {
				df = new DecimalFormat();
			}
		}
		return df;
	}

	@Override
	public String format(double value) {
		return getDF().format(value);
	}

	@Override
	public BlockEntry getEntry() {
		return this.block;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getTitle() {
		return block.getTitle().replace("{index}", "" + this.getId());
	}

	@Override
	public Socket getSocket(IVoltageChannel channel) {
		if (1 == channel.getEntry().getParent().getIndex()) {
			return this.socket1;
		}
		return this.socket2;
	}
}
