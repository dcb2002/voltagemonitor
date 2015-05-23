/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core;

import java.net.Socket;

import com.hjh.voltagemonitor.core.util.DataRecorder.IDataRecorder;
import com.hjh.voltagemonitor.module.BlockEntry;

public interface IVoltageHost extends Runnable {

	public IDataRecorder getRecorder();

	public IVoltageChannel search(String ip, int index);

	public String format(double value);

	public BlockEntry getEntry();

	public IVoltageChannel[] getChannels();

	public boolean isRunning();

	public boolean testSocket();

	public void resetSocket();

	public void remove();

	public boolean isRemove();

	public int getId();

	public void setId(int id);

	public String getTitle();

	public Socket getSocket(IVoltageChannel channel);

}
