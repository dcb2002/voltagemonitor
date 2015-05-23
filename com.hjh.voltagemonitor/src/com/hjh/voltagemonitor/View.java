/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import com.hjh.voltagemonitor.actions.ScreenshotHandler;
import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.util.BrowserUtil;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.core.util.ImageUtil;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class View extends ViewPart {

	private static boolean try_webkit = true;
	private static boolean try_mozilla = false;

	public static final String ID = "com.hjh.voltagemonitor.view";

	private static final Object[] emptyObjectArray = new Object[] {};
	private static final String emptyHTML = "<html><body></body></html>";

	private Browser page_main;
	private Browser page_block_detail;
	private Browser page_cell_detail;
	private StackLayout page_layout;
	private Composite container;
	private Composite page_detail;
	private int currentBlock;
	private int currentCell;

	private Browser newBrowser(Composite container, boolean trymoz) {
		Browser ret = null;

		if (try_webkit) {
			try {
				ret = new Browser(container, SWT.WEBKIT);
			} catch (SWTError e) {
				try_webkit = false;
				LogHelper
						.err("=============Create WEBKIT Browser Fail=============");
			}
		}

		if (try_mozilla) {
			if (null == ret && trymoz) {
				try {
					ret = new Browser(container, SWT.MOZILLA);
				} catch (SWTError e) {
					try_mozilla = false;
					LogHelper
							.err("=============Create MOZILLA Browser Fail=============");
				}
			}
		}

		if (null == ret) {
			try {
				ret = new Browser(container, SWT.NONE);
			} catch (SWTError e2) {
				LogHelper.err(e2);
			}
		}

		return ret;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		Config.setXulrunner();

		page_layout = new StackLayout();
		container = new Composite(parent, SWT.NONE);
		container.setLayout(page_layout);

		createMain(container);

		createDetail(container);

		BrowserUtil.setBrowser(page_main);
	}

	private void createDetail(Composite container) {
		page_detail = new Composite(container, SWT.NONE);

		GridData gd;

		page_detail.setLayout(new GridLayout(1, false));

		page_block_detail = newBrowser(page_detail, true);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 370;
		page_block_detail.setLayoutData(gd);

		page_cell_detail = newBrowser(page_detail, true);
		gd = new GridData(GridData.FILL_BOTH);
		page_cell_detail.setLayoutData(gd);

		Composite buttons = new Composite(page_detail, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 25;
		createDetailButtons(buttons);

	}

	private void createDetailButtons(Composite buttons) {

		buttons.setLayout(new GridLayout(25, false));

		new Label(buttons, SWT.None).setText("Show Trend Chart");
		for (int i = 1; i <= 14; i++) {
			Button cell = new Button(buttons, SWT.NONE);
			cell.setText("Cell " + i);
			final int cellId = i;
			currentCell = cellId;
			cell.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					page_cell_detail.setUrl(Config.getDetailCellFilePath(
							currentBlock, cellId));
				}
			});
		}

		new Label(buttons, SWT.None).setText("  |  ");

		Button saveblockimage = new Button(buttons, SWT.NONE);
		saveblockimage.setText("print block");
		saveblockimage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ImageUtil
						.printImage(page_block_detail, "Block_" + currentBlock);
			}
		});

		Button savecellimage = new Button(buttons, SWT.NONE);
		savecellimage.setText("print cell");
		savecellimage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ImageUtil.printImage(page_cell_detail, "Block_" + currentBlock
						+ "_Cell_" + currentCell);
			}
		});

		new Label(buttons, SWT.None).setText("  |  ");

		Button back = new Button(buttons, SWT.NONE);
		back.setText("Back");
		back.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				back();
			}
		});

	}

	private void createMain(Composite container) {
		page_main = newBrowser(container, true);
		page_layout.topControl = page_main;
		ScreenshotHandler.image_control = page_main;
		ScreenshotHandler.image_prefix = "screen_main";
		new BrowserFunction(page_main, "java_block_detail") {
			@Override
			public Object function(Object[] arguments) {
				String index = (String) arguments[0];
				if (null != index && 0 != index.length()) {
					detail(index);
				}
				return emptyObjectArray;
			}
		};
	}

	private void detail(final String index) {
		CommonUtil.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				currentBlock = Integer.parseInt(index);
				currentCell = 1;
				page_block_detail.setUrl(Config.getDetailFilePath(currentBlock));
				page_cell_detail.setUrl(Config.getDetailCellFilePath(
						currentBlock, currentCell));
				page_layout.topControl = page_detail;
				ScreenshotHandler.image_prefix = "screen_Block_" + currentBlock;
				ScreenshotHandler.image_control = page_detail;
				container.layout();
			}
		});
	}

	private void back() {
		CommonUtil.getDisplay().syncExec(new Runnable() {
			public void run() {

				page_block_detail.setText(emptyHTML);
				page_cell_detail.setText(emptyHTML);

				page_layout.topControl = page_main;
				ScreenshotHandler.image_control = page_main;
				ScreenshotHandler.image_prefix = "screen_main";
				container.layout();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		container.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
