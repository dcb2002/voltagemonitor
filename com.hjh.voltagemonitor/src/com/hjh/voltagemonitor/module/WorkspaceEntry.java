/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class WorkspaceEntry {

	private List<BlockEntry> blocks;
	private String title;
	private long logInterval;
	private boolean emulation;
	private String notes;

	private WorkspaceEntry() {
		blocks = new ArrayList<BlockEntry>();
		logInterval = 1000;
		notes = "workspace note";
		title = "workspace title";
		emulation = false;
	}

	public void toXml(Element root) {
		root.addElement("title").setText(title);
		root.addElement("logInterval").setText("" + logInterval);
		root.addElement("notes").setText(notes);
		root.addElement("emulation").setText(emulation + "");

		Element eips = root.addElement("blocks");
		for (BlockEntry item : blocks) {
			item.toXml(eips.addElement("block"));
		}
	}

	@SuppressWarnings("unchecked")
	public WorkspaceEntry fromXml(Element root) {

		this.title = root.elementText("title");
		this.notes = root.elementText("notes");
		this.logInterval = Integer.parseInt(root.elementText("logInterval"));
		try {
			this.emulation = Boolean
					.parseBoolean(root.elementText("emulation"));
		} catch (Exception e) {
		}
		blocks = new ArrayList<BlockEntry>();
		List<Element> list = root.element("blocks").elements("block");
		for (Element item : list) {
			blocks.add(new BlockEntry().fromXml(item));
		}

		return this;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEmulation() {
		return emulation;
	}

	public void setEmulation(boolean emulation) {
		this.emulation = emulation;
	}

	public List<BlockEntry> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<BlockEntry> blocks) {
		this.blocks = blocks;
	}

	public long getLogInterval() {
		return logInterval;
	}

	public void setLogInterval(long logInterval) {
		this.logInterval = logInterval;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void add(String ip1, String ip2) {
		getBlocks().add(new BlockEntry(ip1, ip2));
	}

	public boolean hasIp(String ip) {
		for (BlockEntry item : getBlocks()) {
			if (item.getIp1().getIp().equals(ip)
					|| item.getIp2().getIp().equals(ip)) {
				return true;
			}
		}
		return false;
	}

	public void remove(BlockEntry item) {
		getBlocks().remove(item);
	}

	public static WorkspaceEntry load() {
		return load(Config.getWorkspaceConfigFile());
	}

	public boolean isEmpty() {
		return this.getBlocks().size() == 0;
	}

	public static WorkspaceEntry load(File workspaceConfigFile) {
		WorkspaceEntry ret = new WorkspaceEntry();
		if(workspaceConfigFile.exists()){
			SAXReader reader = new SAXReader();
			try {
				ret.fromXml(reader.read(workspaceConfigFile).getRootElement());
			} catch (DocumentException e) {
				LogHelper.err(e);
			}
		}
		return ret;
	}

}
