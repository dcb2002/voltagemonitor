/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.dataReader;

import com.hjh.voltagemonitor.module.CommonLimitEntry;

public class ValueTypeUtil {

	public static final String PASS_VALUE = "D/P";
	public static final String PASS = "pass";
	public static final String NOT_CONNECT = "not_connect";
	public static final String ERROR = "error";

	public static String getType(CommonLimitEntry e, double value) {
		if (value <= e.getUndervoltage()) {
			return "under";
		}
		if (value >= e.getOvervoltage()) {
			return "over";
		}
		if (value >= e.getWarnning()) {
			return "warn";
		}
		return "normal";
	}

}
