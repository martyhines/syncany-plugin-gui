package org.syncany.gui.history;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.syncany.config.GuiEventBus;
import org.syncany.database.PartialFileHistory.FileHistoryId;
import org.syncany.gui.Dialog;
import org.syncany.gui.Panel;
import org.syncany.gui.util.DesktopUtil;
import org.syncany.gui.util.I18n;
import org.syncany.gui.util.WidgetDecorator;

/**
 * @author Philipp C. Heckel <philipp.heckel@gmail.com>
 */
public class HistoryDialog extends Dialog {		
	private Shell windowShell;	
	private Composite stackComposite;
	private StackLayout stackLayout;
			
	private Panel currentPanel;
	private MainPanel mainPanel;
	private DetailPanel detailPanel;
			
	private HistoryModel model;	
	private GuiEventBus eventBus;	
	
	public HistoryDialog() {				
		this.windowShell = null;	
		this.stackComposite = null;
		this.stackLayout = null;

		this.currentPanel = null;
		this.mainPanel = null;
		this.detailPanel = null;
		
		this.model = new HistoryModel();
		this.eventBus = GuiEventBus.getInstance();
		this.eventBus.register(this);					
	}

	public void open() {
		// Create controls
		createContents();
		buildPanels();
		
		showMainPanel();

		// Open shell
		DesktopUtil.centerOnScreen(windowShell);

		windowShell.open();
		windowShell.layout();

		// Dispatch loop
		Display display = Display.getDefault();
	
		while (!windowShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		GridLayout shellGridLayout = new GridLayout(1, false);
		shellGridLayout.marginTop = 0;
		shellGridLayout.marginLeft = 0;
		shellGridLayout.marginHeight = 0;
		shellGridLayout.marginWidth = 0;
		shellGridLayout.horizontalSpacing = 0;
		shellGridLayout.verticalSpacing = 0;

		windowShell = new Shell(Display.getDefault(), SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		windowShell.setToolTipText("");
		windowShell.setBackground(WidgetDecorator.COLOR_WIDGET);
		windowShell.setSize(900, 560);
		windowShell.setText(I18n.getText("org.syncany.gui.history.HistoryDialog.title"));
		windowShell.setLayout(shellGridLayout);		
		windowShell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				dispose();
			}
		});
		
		createStackComposite();
	}		

	private void createStackComposite() {
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		
		GridData stackCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		stackCompositeGridData.minimumWidth = 500;

		stackComposite = new Composite(windowShell, SWT.DOUBLE_BUFFERED);
		stackComposite.setLayout(stackLayout);
		stackComposite.setLayoutData(stackCompositeGridData);
	}

	private void buildPanels() {
		mainPanel = new MainPanel(stackComposite, SWT.NONE, model, this);
		detailPanel = new DetailPanel(stackComposite, SWT.NONE, model, this);
	}

	public Shell getWindowShell() {
		return windowShell;
	}

	public void setCurrentPanel(final Panel newPanel) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				currentPanel = newPanel;
				
				stackLayout.topControl = currentPanel;
				stackComposite.layout();	
				
				currentPanel.setFocus();
			}
		});
	}
	
	public void dispose() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {	
				eventBus.unregister(HistoryDialog.this);
				
				if (!mainPanel.isDisposed()) {
					mainPanel.dispose();
				}		
				
				if (!detailPanel.isDisposed()) {
					detailPanel.dispose();
				}		
				
				if (!windowShell.isDisposed()) {
					windowShell.dispose();
				}									
			}
		});
	}
	
	public void showDetails(String root, FileHistoryId fileHistoryId) {
		detailPanel.sendLsFolderRequest(root, fileHistoryId);
		showDetailsPanel();
	}

	public void showMainPanel() {
		setCurrentPanel(mainPanel);
	}
	
	public void showDetailsPanel() {
		setCurrentPanel(detailPanel);
	}
}
