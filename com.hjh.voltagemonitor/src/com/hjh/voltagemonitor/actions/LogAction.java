/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.impl.ChannelMonitor;
import com.hjh.voltagemonitor.core.util.DataLogRecorder;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.core.util.DataLogRecorder.LogStateChange;

public class LogAction extends ContributionItem {

	private SelectionAdapter startlog = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			DataLogRecorder.start(ChannelMonitor.getRecorders(),
					(int) VoltageSystemFactory.getProvider().getWorkspace()
							.getLogInterval());
		}
	};

	private SelectionAdapter stoplog = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			DataLogRecorder.stop();
		}
	};

	private SelectionAdapter viewlog = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			DataLogRecorder.open();
		}
	};

	private MenuItem start;
	private MenuItem stop;
	private MenuItem viewLog;

	private void updateState() {
		CommonUtil.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (null == start || start.isDisposed()) {
					return;
				}
				if (VoltageSystemFactory.getProvider().getWorkspace().isEmpty()) {
					start.setEnabled(false);
					stop.setEnabled(false);
					viewLog.setEnabled(false);
				} else if (DataLogRecorder.isLogging()) {
					start.setEnabled(false);
					stop.setEnabled(true);
					viewLog.setEnabled(true);
				} else {
					start.setEnabled(true);
					stop.setEnabled(false);
					viewLog.setEnabled(false);
				}
			}
		});

	}

	public LogAction() {
		DataLogRecorder.addListener(new LogStateChange() {

			@Override
			public void change(boolean log) {
				updateState();
			}
		});
	}

	public void fill(Menu menu, int index) {

		start = new MenuItem(menu, SWT.NONE);
		start.setText("Start Logging");
		start.addSelectionListener(startlog);

		stop = new MenuItem(menu, SWT.NONE);
		stop.setText("Stop Logging");
		stop.addSelectionListener(stoplog);

		viewLog = new MenuItem(menu, SWT.NONE);
		viewLog.setText("View Last Log");
		viewLog.addSelectionListener(viewlog);

		updateState();
	}

}
