/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1000, 700));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle(Config.getTitle());
	}

	public void postWindowCreate() {

		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(this
				.getWindowConfigurer().getWindow().getShell());

		final IRunnableWithProgress proc = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				VoltageSystemFactory.getProvider().prepare(monitor,
						getWindowConfigurer().getWindow());
			}
		};

		this.getWindowConfigurer().getWindow().getShell().getDisplay()
				.asyncExec(new Runnable() {

					@Override
					public void run() {
						try {
							dialog.run(true, false, proc);
						} catch (InvocationTargetException e) {
							LogHelper.err(e);
						} catch (InterruptedException e) {
							LogHelper.err(e);
						}
					}
				});

	}

}
