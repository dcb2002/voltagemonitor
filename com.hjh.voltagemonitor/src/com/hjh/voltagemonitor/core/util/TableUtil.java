/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.hjh.voltagemonitor.module.BlockEntry;
import com.hjh.voltagemonitor.module.ChannelEntry;
import com.hjh.voltagemonitor.module.WorkspaceEntry;

public class TableUtil {

	public static <T> void modifySetting(final TableViewer viewer,
			final PropertyProxy<T>[] propertyProxys) {
		int size = propertyProxys.length;
		String[] properties = new String[size];
		TextCellEditor txtEditor = new TextCellEditor(viewer.getTable());
		CellEditor[] editors = new CellEditor[size];
		for (int i = 0; i < size; i++) {
			properties[i] = propertyProxys[i].getName();
			editors[i] = txtEditor;
		}

		viewer.setColumnProperties(properties);
		viewer.setCellEditors(editors);
		viewer.setCellModifier(new ICellModifier() {

			@Override
			public boolean canModify(Object element, String property) {

				for (PropertyProxy<T> item : propertyProxys) {
					if (item.getName().equals(property)) {
						return item.isEdit();
					}
				}

				return false;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Object getValue(Object element, String property) {

				for (PropertyProxy<T> item : propertyProxys) {
					if (item.getName().equals(property)) {
						return item.getGetAndSet().get((T) element);
					}
				}

				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void modify(Object element, String property, Object value) {
				String strValue = ((String) value).trim();
				if (0 == strValue.length()) {
					return;
				}
				TableItem titem = (TableItem) element;
				T entry = (T) titem.getData();
				for (PropertyProxy<T> item : propertyProxys) {
					if (item.getName().equals(property)) {
						if (item.getGetAndSet().set(entry, strValue)) {
							viewer.refresh(entry);
						}
					}
				}
			}

		});

	}

	public static void addColumn(Table table, TableLayout layout, String label,
			int weight) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(label);
		layout.addColumnData(new ColumnWeightData(weight));
	}

	public static <T> void layoutTable(final TableViewer viewer,
			final PropertyProxy<T>[] propertyProxys) {

		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		TableLayout layout = new TableLayout();
		viewer.getTable().setLayout(layout);

		for (PropertyProxy<T> item : propertyProxys) {
			TableUtil.addColumn(viewer.getTable(), layout, item.getTitle(),
					item.getWeight());
		}

		viewer.getTable().layout();
	}

	public static <T> void initContent(final TableViewer viewer,
			final PropertyProxy<T>[] propertyProxys) {
		viewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener listener) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {

			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@SuppressWarnings("unchecked")
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return propertyProxys[columnIndex].getGetAndSet().get(
						(T) element);
			}

		});
	}

	public static <T> void initStruct(final TableViewer viewer,
			final PropertyProxy<T>[] propertyProxys) {
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof WorkspaceEntry) {
					WorkspaceEntry entry = (WorkspaceEntry) inputElement;
					return entry.getBlocks().toArray(
							new BlockEntry[entry.getBlocks().size()]);
				}
				if (inputElement instanceof BlockEntry) {
					BlockEntry entry = (BlockEntry) inputElement;
					if (entry.equals(BlockEntry.empty)) {
						return new ChannelEntry[0];
					}
					return entry.getChannels();
				}
				throw new RuntimeException("Un support "
						+ inputElement.getClass().getName());
			}
		});
	}

}
