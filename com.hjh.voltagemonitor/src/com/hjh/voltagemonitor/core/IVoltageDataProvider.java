/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;

import com.hjh.voltagemonitor.module.WorkspaceEntry;

public interface IVoltageDataProvider {

	public boolean prepare(IProgressMonitor monitor,
			IWorkbenchWindow iWorkbenchWindow);

	public void resetChannels(IProgressMonitor monitor);

	public String[] scanIps(IProgressMonitor monitor);

	public WorkspaceEntry getWorkspace();

}
