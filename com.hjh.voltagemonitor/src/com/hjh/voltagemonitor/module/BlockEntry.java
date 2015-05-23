/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.module;

import org.dom4j.Element;

import com.hjh.voltagemonitor.core.Config;

public class BlockEntry extends CommonLimitEntry {

	public static BlockEntry empty = new BlockEntry();

	private IPEntry ip1;
	private IPEntry ip2;
	private int timeout;

	public BlockEntry() {
		this(Config.IP127, Config.IP127);
	}

	public BlockEntry(String ip1, String ip2) {
		this.ip1 = new IPEntry(this, ip1, 1);
		this.ip2 = new IPEntry(this, ip2, 2);
		timeout = 1000;
		title = "Block {index}";
		format = "000.00";
		defaultValue = 0;
		undervoltage = -1000;
		warnning = 1000;
		overvoltage = 2000;
	}

	public void toXml(Element root) {
		ip1.toXml(root.addElement("ip1"));
		ip2.toXml(root.addElement("ip2"));
		super.toXml(root);
		root.addElement("timeout").setText("" + timeout);
	}

	public BlockEntry fromXml(Element root) {

		this.ip1 = new IPEntry(this, 1).fromXml(root.element("ip1"));
		this.ip2 = new IPEntry(this, 2).fromXml(root.element("ip2"));
		this.timeout = Integer.parseInt(root.elementText("timeout"));
		super.fromXml(root);
		return this;

	}

	public IPEntry getIp1() {
		return ip1;
	}

	public void setIp1(IPEntry ip1) {
		this.ip1 = ip1;
	}

	public IPEntry getIp2() {
		return ip2;
	}

	public void setIp2(IPEntry ip2) {
		this.ip2 = ip2;
	}

	public ChannelEntry[] getChannels() {
		ChannelEntry[] entrys = new ChannelEntry[Config
				.getChannelsSizeForEachIp() * 2];
		for (int i = 0; i < Config.getChannelsSizeForEachIp(); i++) {
			entrys[i] = ip1.getChannels().get(i);
			entrys[i + Config.getChannelsSizeForEachIp()] = ip2.getChannels()
					.get(i);
		}
		return entrys;
	}

	public int getTimeout() {
		if (timeout <= 0) {
			return (int) Config.getSocketTimeOut();
		}
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getId() {
		return this.getIp1().getIp() + "_" + this.getIp2().getIp();
	}

}
