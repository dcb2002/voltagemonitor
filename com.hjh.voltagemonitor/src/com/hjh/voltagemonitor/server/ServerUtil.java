/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.VoltageSystemFactory;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class ServerUtil {

	private static ServerUtil instance = new ServerUtil();

	public static ServerUtil getInstance() {
		return instance;
	}

	private Server server;

	public synchronized void start() {

		int port = Config.getPort();
		AbstractHandler handler = VoltageSystemFactory.getDataHandler();

		stop();
		server = new Server(port);

		ContextHandler context = new ContextHandler();
		context.setContextPath("/html");
		ResourceHandler resource = new ResourceHandler();
		resource.setDirectoriesListed(true);
		try {
			resource.setBaseResource(Resource.newResource(Config
					.getHtmlRealLocation().getAbsolutePath()));
		} catch (Exception e) {
			LogHelper.err(e);
		}
		context.setHandler(resource);

		ContextHandler contextajax = new ContextHandler();
		contextajax.setContextPath("/ajax");
		contextajax.setHandler(handler);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { context, contextajax });

		server.setHandler(contexts);
		try {
			server.start();
		} catch (Exception e) {
			LogHelper.err(e);
		}
	}

	public synchronized void stop() {
		if (null != server) {
			try {
				server.stop();
			} catch (Exception e) {
				LogHelper.err(e);
			}
		}
		server = null;
	}

}
