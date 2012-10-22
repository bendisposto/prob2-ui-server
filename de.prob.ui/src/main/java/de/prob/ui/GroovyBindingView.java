package de.prob.ui;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.IGroovyExecutionListener;
import de.prob.webconsole.ServletContextListener;

public class GroovyBindingView extends ViewPart implements
		IGroovyExecutionListener {

	private TableViewer viewer;
	TableViewerColumn varname, type, value;
	

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		createViewer(parent);
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new BindingContentProvider());
		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider


		// Make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// Set the sorter for the table

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		GroovyExecution instance = ServletContextListener.INJECTOR
				.getInstance(GroovyExecution.class);
		instance.registerListener(this);
		instance.notifyListerners();
	}

	public TableViewer getViewer() {
		return viewer;
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Variable", "Type", "Value" };
		
int size = 150;
		varname = createTableViewerColumn(titles[0], size, 0);
		varname.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BindingTableEntry e = (BindingTableEntry) element;
				return e.name;
			}
		});
		
		type = createTableViewerColumn(titles[1], size, 1);
		type.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BindingTableEntry e = (BindingTableEntry) element;
				return e.type;
			}
		});
		value = createTableViewerColumn(titles[2], size, 2);
		value.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				BindingTableEntry e = (BindingTableEntry) element;
				return e.value;
			}
		});

	
		
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void notifyListner(final GroovyExecution groovyExecution) {
		this.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				viewer.setInput(groovyExecution);
			}
		});
	}

}