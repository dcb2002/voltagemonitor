/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.module;

import org.dom4j.Element;

public class CommonLimitEntry {

	protected String title;

	protected String format;
	protected float defaultValue;
	protected float undervoltage;
	protected float warnning;
	protected float overvoltage;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(float defaultValue) {
		this.defaultValue = defaultValue;
	}

	public float getWarnning() {
		return warnning;
	}

	public void setWarnning(float warnning) {
		this.warnning = warnning;
	}

	public float getUndervoltage() {
		return undervoltage;
	}

	public void setUndervoltage(float undervoltage) {
		this.undervoltage = undervoltage;
	}

	public float getOvervoltage() {
		return overvoltage;
	}

	public void setOvervoltage(float overvoltage) {
		this.overvoltage = overvoltage;
	}

	public void toXml(Element root) {
		root.addElement("title").setText(title);
		root.addElement("format").setText(format);
		root.addElement("defaultValue").setText("" + defaultValue);
		root.addElement("undervoltage").setText("" + undervoltage);
		root.addElement("warnning").setText("" + warnning);
		root.addElement("overvoltage").setText("" + overvoltage);

	}

	public CommonLimitEntry fromXml(Element root) {
		this.title = root.elementText("title");
		this.format = root.elementText("format");
		this.defaultValue = Float.parseFloat(root.elementText("defaultValue"));
		this.undervoltage = Float.parseFloat(root.elementText("undervoltage"));
		this.warnning = Float.parseFloat(root.elementText("warnning"));
		this.overvoltage = Float.parseFloat(root.elementText("overvoltage"));
		return this;
	}

}
