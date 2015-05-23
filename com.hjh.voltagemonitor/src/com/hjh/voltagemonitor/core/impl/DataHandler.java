/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

import com.hjh.voltagemonitor.core.IDataHandler;
import com.hjh.voltagemonitor.core.IVoltageChannel;
import com.hjh.voltagemonitor.core.IVoltageHost;
import com.hjh.voltagemonitor.core.dataReader.BlockValue;
import com.hjh.voltagemonitor.core.util.DataRecorder.IDataRecorder;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class DataHandler extends IDataHandler {

	public static class JSObject {

		private StringBuilder builder = new StringBuilder();

		public JSObject() {
			builder.append("{");
		}

		public void appendJsKeyVaue(String key, Object value, boolean isstring) {
			builder.append("\"");
			builder.append(key);
			builder.append("\"");
			builder.append(":");
			if (isstring) {
				builder.append("\"");
			}
			builder.append(value);
			if (isstring) {
				builder.append("\"");
			}
			builder.append(",");
		}

		public String toString() {
			if (builder.length() != 1) {
				builder.deleteCharAt(builder.length() - 1);
			}
			builder.append("}");
			return builder.toString();
		}
	}

	@Override
	public void handle(String arg0, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		IVoltageHost host = null;
		if (null != request.getParameter("blockID")
				&& !"null".equals(request.getParameter("blockID"))) {
			try {
				host = ChannelMonitor.searchBlock(Integer.parseInt(request
						.getParameter("blockID")));
			} catch (Exception e) {
				LogHelper.err(e);
			}
		}

		if ("/blockvalues".equals(arg0)) {

			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			StringBuilder builder = new StringBuilder();
			builder.append("{\"data\":[");
			boolean first = true;
			BlockValue value;
			for (IDataRecorder record : ChannelMonitor.getRecorders()) {
				value = record.currentValue();
				if (first) {
					first = false;
				} else {
					builder.append(",");
				}
				builder.append(String.format("[\"%s\",\"%s\"]", value.getValue(),
						value.getType()));
			}
			builder.append("]}");
			response.getWriter().print(builder.toString());

		} else if ("/blockInfo".equals(arg0)) {
			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			if (null != host) {
				host.getRecorder().currentValue().toJson();
				JSObject obj = new JSObject();
				obj.appendJsKeyVaue("id", host.getId(), false);
				obj.appendJsKeyVaue("title", host.getTitle(), true);
				obj.appendJsKeyVaue("time", host.getRecorder().currentTime(),
						false);
				obj.appendJsKeyVaue("data", host.getRecorder().currentValue()
						.toJson(), false);
				response.getWriter().print(obj.toString());
			} else {
				response.getWriter().println("{\"id\":0}");
			}
		} else if ("/cellvalue".equals(arg0)) {

			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			if (null != host) {
				String cell = request.getParameter("id");
				int icell = Integer.parseInt(cell.substring(cell
						.lastIndexOf(" ") + 1));
				if (icell > 0 && icell <= host.getChannels().length) {
					IVoltageChannel currentChannel = host.getChannels()[icell - 1];
					JSObject obj = new JSObject();
					obj.appendJsKeyVaue("time", host.getRecorder()
							.currentTime(), false);
					obj.appendJsKeyVaue("value", host.getRecorder()
							.currentValue(currentChannel), false);
					response.getWriter().print(obj.toString());
				}
			}

		} else if ("/showcell".equals(arg0)) {

			response.setContentType("application/json;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			boolean handle = false;
			if (null != host) {

				String cell = request.getParameter("id");
				int icell = Integer.parseInt(cell.substring(cell
						.lastIndexOf(" ") + 1));
				if (icell > 0 && icell <= host.getChannels().length) {
					IVoltageChannel currentChannel = host.getChannels()[icell - 1];
					JSObject obj = new JSObject();
					obj.appendJsKeyVaue("id", icell, false);
					obj.appendJsKeyVaue("under", currentChannel.getEntry()
							.getUndervoltage(), false);
					obj.appendJsKeyVaue("warn", currentChannel.getEntry()
							.getWarnning(), false);
					obj.appendJsKeyVaue("over", currentChannel.getEntry()
							.getOvervoltage(), false);
					obj.appendJsKeyVaue("data",
							host.getRecorder().toJsonData(currentChannel),
							false);
					response.getWriter().print(obj.toString());
					handle = true;
				}

			}
			if (!handle) {
				response.getWriter().println("{\"id\":0}");
			}

		}
	}

}
