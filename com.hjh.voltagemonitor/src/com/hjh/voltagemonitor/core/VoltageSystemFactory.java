/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core;

import com.hjh.voltagemonitor.core.impl.DataHandler;
import com.hjh.voltagemonitor.core.impl.VoltageDataProviderImpl;

public class VoltageSystemFactory {

	private static IVoltageDataProvider instance = new VoltageDataProviderImpl();
	private static IDataHandler datahandler = new DataHandler();

	public static IVoltageDataProvider getProvider() {
		return instance;
	}

	public static IDataHandler getDataHandler() {
		return datahandler;
	}

}
