/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.config.WorkspaceConfigDialog;
import com.hjh.voltagemonitor.core.util.CommonUtil;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class OpenWorkspaceConfigHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		new WorkspaceConfigDialog(new IShellProvider() {

			@Override
			public Shell getShell() {
				return CommonUtil.getShell();
			}

		},WorkspaceEntry.load(Config.getWorkspaceConfigFile())).open();

		return null;
	}

}
