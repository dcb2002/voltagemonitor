/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.threadspool;


class InnerTask {

	private Runnable runner;
	private long timeout;
	private long runtime;
	private boolean finish;
	private boolean error;

	public InnerTask(Runnable runner, long timeout) {
		this.runner = runner;
		this.timeout = timeout;
		finish = false;
		error = false;
	}

	public boolean isTimeout() {
		if (0 == timeout || 0 == runtime) {
			return false;
		}
		if (System.currentTimeMillis() - runtime > timeout) {
			return true;
		}
		return false;
	}

	public void run() {
		runtime = System.currentTimeMillis();
		runner.run();
	}

	public void setFinish() {
		this.finish = true;
	}

	public void setError() {
		this.error = true;
	}

	public boolean isFinish() {
		return this.finish;
	}

	public boolean isError() {
		return this.error;
	}

}