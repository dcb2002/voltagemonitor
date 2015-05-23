/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Control;

import com.hjh.voltagemonitor.core.util.ImageUtil;

public class ScreenshotHandler extends AbstractHandler {

	public static Control image_control = null;

	public static String image_prefix = "screenshot";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (null != image_control) {
			ImageUtil.printImage(image_control, image_prefix);
		}
		return null;
	}

}