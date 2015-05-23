/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.dataReader;

import java.text.DecimalFormat;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class InputRange {

	private long upperHex;
	private long lowerHex;
	private int upperValue;
	private int lowerValue;

	private int range;

	private DecimalFormat df;

	public InputRange(String upperHex, String lowerHex, String upperValue,
			String lowerValue, int range, DecimalFormat df) {
		this.upperHex = Long.parseLong(upperHex, 16);
		this.lowerHex = Long.parseLong(lowerHex, 16);
		this.upperValue = Integer.parseInt(upperValue);
		this.lowerValue = Integer.parseInt(lowerValue);
		this.range = range;
		this.df = df;
	}

	public int getRange() {
		return range;
	}

	public DecimalFormat getDecimalFormat() {
		return df;
	}

	public double covert(long value) {

		if (range == 0) {
			if (Config.isTest()) {
				return value;
			}
			return 0;
		}

		if (value > this.upperHex || value < this.lowerHex) {
			LogHelper.err("value error:range{" + this.upperHex + ","
					+ this.lowerHex + "},but:" + value);
		}

		double ret = (value - this.lowerHex) * 1.0
				/ (this.upperHex - this.lowerHex)
				* (this.upperValue - this.lowerValue) + this.lowerValue;

		return ret;
	}

}
