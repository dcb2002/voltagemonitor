/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ImageUtil {

	private static SimpleDateFormat sf = new SimpleDateFormat(
			"yyyy_MM_dd_HH_mm_ss");

	private static String selectSavePath(Shell shell, String prefix) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.jpg", "*.png" });
		dialog.setFileName(prefix + "_" + sf.format(new Date()));
		return dialog.open();
	}

	public static void printImage(Control control, String prefix) {
		if (null == prefix) {
			prefix = "print";
		}
		Image img = ImageUtil.makeShotImage(control);
		if (null == img) {
			return;
		}
		try {
			ImageUtil
					.saveImage(img, selectSavePath(control.getShell(), prefix));
		} finally {
			img.dispose();
		}
	}

	public static Image makeShotImage(Control control) {
		if (null == control) {
			return null;
		}
		int width = control.getBounds().width;
		int height = control.getBounds().height;
		if (width > 0 && height > 0) {
			GC gc = new GC(control);
			final Image image = new Image(control.getDisplay(), width, height);
			gc.copyArea(image, 0, 0);
			gc.dispose();
			return image;
		}
		return null;
	}

	public static void saveImage(Image image, String savePath) {
		if (null == image || null == savePath) {
			return;
		}
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(savePath, savePath.endsWith(".jpg") ? SWT.IMAGE_JPEG
				: SWT.IMAGE_PNG);
	}

}
