/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.threadspool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hjh.voltagemonitor.core.Config;
import com.hjh.voltagemonitor.core.util.LogHelper;

public class ThreadsPool {

	class InnerThread extends Thread {

		private Object lock = new Object();
		private InnerTask task;
		private boolean stop = false;

		public void touch() {
			synchronized (lock) {
				lock.notifyAll();
			}
		}

		public void runTask(InnerTask task) {
			this.task = task;
			touch();
		}

		public void destory() {
			stop = true;
			touch();
		}

		public void run() {
			currentSize++;
			try {
				doRun();
			} finally {
				currentSize--;
				if (Config.isDebugThreadPool()) {
					LogHelper.debug("Thread Pools live + :" + currentSize);
				}
			}
		}

		private void doRun() {
			while (true) {
				if (stop) {
					break;
				}
				if (null != task) {
					try {
						task.run();
					} catch (Throwable e) {
						LogHelper.err(e);
						task.setError();
					} finally {
						task.setFinish();
						task = null;
					}
				}
				if (Config.isStop()) {
					break;
				}
				if (threads.size() >= Config.liveThreadNumber()) {
					stop = true;
					break;
				} else {

					synchronized (lock) {
						threads.add(this);
						if (Config.isDebugThreadPool()) {
							LogHelper.debug("Thread Pools size + :"
									+ (threads.size()) + " : live :"
									+ currentSize);
						}
						try {
							lock.wait();
						} catch (InterruptedException e) {
							LogHelper.err(e);
						}
					}
				}

			}
		}

		public void setTask(InnerTask task) {
			this.task = task;
		}
	}

	static class Holder {
		public static ThreadsPool instance = new ThreadsPool();
	}

	public static ThreadsPool getInstance() {
		return Holder.instance;
	}

	private List<InnerThread> threads;
	private int currentSize = 0;

	private ThreadsPool() {
		threads = Collections.synchronizedList(new ArrayList<InnerThread>());
	}

	public void runWait(Runnable runner, long timeout) {
		InnerTask task = run(runner, timeout);
		while (!task.isFinish() && !task.isTimeout()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				LogHelper.err(e);
			}
		}
	}

	public void run(Runnable runner) {
		run(runner, 0);
	}

	private synchronized InnerTask run(Runnable runner, long timeout) {
		InnerThread thread;
		while (true) {
			if (currentSize > Config.maxThreadNumber()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LogHelper.err(e);
				}
			} else if (threads.size() > 0) {
				thread = threads.remove(0);
				if (Config.isDebugThreadPool()) {
					LogHelper.debug("Thread Pools size - :" + threads.size());
				}
				break;
			} else {
				new InnerThread().start();
				Thread.yield();
			}
		}
		InnerTask task = new InnerTask(runner, timeout);
		thread.runTask(task);
		return task;
	}
}
