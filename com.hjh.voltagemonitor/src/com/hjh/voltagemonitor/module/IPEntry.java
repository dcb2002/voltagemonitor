/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.module;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.hjh.voltagemonitor.core.Config;

public class IPEntry {

	private BlockEntry parent;
	private String ip;
	private int index;
	private List<ChannelEntry> channels;

	public IPEntry(BlockEntry parent, int index) {
		this(parent, Config.IP127, index);
	}

	public IPEntry(BlockEntry parent, String ip, int index) {
		this.ip = ip;
		this.index = index;
		this.parent = parent;
		channels = new ArrayList<ChannelEntry>();
		for (int i = 1; i <= Config.getChannelsSizeForEachIp(); i++) {
			channels.add(new ChannelEntry(this, index != 1 && i > 6 ? false
					: true, i));
		}
	}

	public BlockEntry getParent() {
		return parent;
	}

	public void setParent(BlockEntry parent) {
		this.parent = parent;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void toXml(Element root) {
		root.addElement("ip").setText(ip);

		Element echannels = root.addElement("channels");
		for (ChannelEntry item : channels) {
			item.toXml(echannels.addElement("channel"));
		}
	}

	@SuppressWarnings("unchecked")
	public IPEntry fromXml(Element root) {

		this.ip = root.elementText("ip");

		channels = new ArrayList<ChannelEntry>();
		List<Element> list = root.element("channels").elements("channel");
		for (Element item : list) {
			channels.add(new ChannelEntry(this, true).fromXml(item));
		}

		return this;

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public List<ChannelEntry> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelEntry> channels) {
		this.channels = channels;
	}

	public int getTimeout() {
		return parent.getTimeout();
	}

}
