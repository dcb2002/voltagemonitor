/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

public class PropertyProxy<TP> {

	public interface PropertyProxyGetAndSet<T> {
		public String get(T obj);

		public boolean set(T obj, String val);
	}

	public static class DefaultPropertyProxyGetAndSet<T> implements
			PropertyProxyGetAndSet<T> {

		private String fieldName;

		public DefaultPropertyProxyGetAndSet(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String get(T obj) {
			return ObjectUtil.get(obj, fieldName).toString();
		}

		@Override
		public boolean set(T obj, String val) {
			return ObjectUtil.set(obj, fieldName, val);
		}

	}

	private boolean edit;
	private int weight;
	private String name;
	private String title;
	private PropertyProxyGetAndSet<TP> getAndSet;

	public PropertyProxy(String name, int weight) {
		this(true, name, name, weight);
	}

	public PropertyProxy(boolean edit, String name, int weight) {
		this(edit, name, name, weight);
	}

	public PropertyProxy(String name, String title, int weight) {
		this(true, name, title, weight, new DefaultPropertyProxyGetAndSet<TP>(
				name));
	}

	public PropertyProxy(boolean edit, String name, String title, int weight) {
		this(edit, name, title, weight, new DefaultPropertyProxyGetAndSet<TP>(
				name));
	}

	public PropertyProxy(boolean edit, String name, String title, int weight,
			PropertyProxyGetAndSet<TP> getAndSet) {
		this.edit = edit;
		this.weight = weight;
		this.name = name;
		this.title = title;
		this.getAndSet = getAndSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PropertyProxyGetAndSet<TP> getGetAndSet() {
		return getAndSet;
	}

	public PropertyProxy<TP> setGetAndSet(PropertyProxyGetAndSet<TP> getAndSet) {
		this.getAndSet = getAndSet;
		return this;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

}
