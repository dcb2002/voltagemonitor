/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core;

import java.net.Socket;

import com.hjh.voltagemonitor.core.dataReader.ChannelValue;
import com.hjh.voltagemonitor.core.dataReader.InputRange;
import com.hjh.voltagemonitor.module.ChannelEntry;

public interface IVoltageChannel {

	public ChannelEntry getEntry();

	public ChannelValue getValue();
	
	public ChannelValue getPreValue();

	public IVoltageHost getHost();

	public InputRange getRange();

	public int getNo();

	public String getTitle();

	public String format(double value);

	public Socket getSocket();

	public int getBlockIndex();

}
