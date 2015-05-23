/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.module;

import org.dom4j.Element;

import com.hjh.voltagemonitor.core.Config;

public class ChannelEntry extends CommonLimitEntry {

	private IPEntry parent;
	private boolean use;
	private float tare;
	private int index;

	public void toXml(Element root) {
		root.addElement("index").setText("" + index);
		root.addElement("tare").setText("" + tare);
		root.addElement("use").setText(use + "");
		super.toXml(root);
	}

	public ChannelEntry fromXml(Element root) {
		try {
			this.tare = Float.parseFloat(root.elementText("tare"));
		} catch (Exception e) {
		}
		this.index = Integer.parseInt(root.elementText("index"));
		try {
			this.use = Boolean.parseBoolean(root.elementText("use"));
		} catch (Exception e) {
		}
		super.fromXml(root);
		return this;
	}

	public ChannelEntry(IPEntry parent, boolean use) {
		this(parent, use, 1);
	}

	public ChannelEntry(IPEntry parent, boolean use, int index) {
		this.parent = parent;
		this.index = index;
		this.use = use;
		title = "Cell " + getGindex();
		this.tare = 0;
		format = "000.00";
		defaultValue = 0;
		undervoltage = -100;
		warnning = 100;
		overvoltage = 200;
	}

	public int getGindex() {
		return (getParent().getIndex() - 1) * Config.getChannelsSizeForEachIp()
				+ getIndex();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public IPEntry getParent() {
		return parent;
	}

	public void setParent(IPEntry parent) {
		this.parent = parent;
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public float getTare() {
		return tare;
	}

	public void setTare(float tare) {
		this.tare = tare;
	}

}
