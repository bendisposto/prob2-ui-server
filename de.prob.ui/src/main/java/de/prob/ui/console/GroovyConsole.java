package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.Main;
import de.prob.webconsole.WebConsole;

public class GroovyConsole extends ViewPart {

	public static final String ID = "de.prob.ui.console.GroovyConsole";

	private final int port;
	private Browser consoleBrowser;
	private Browser outputBrowser;

	private static GroovyConsole instance;

	public GroovyConsole() {
		port = WebConsole.getPort();
		instance = this;
	}

	@Override
	public void createPartControl(Composite shell) {
		GridLayout gl_shell = new GridLayout(1, true);
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		consoleBrowser = new Browser(sashForm, SWT.NONE);
		consoleBrowser.setUrl("http://localhost:" + port + "/console.jsp");
		outputBrowser = new Browser(sashForm, SWT.NONE);
		outputBrowser.setUrl("http://localhost:" + port + "/sysout.jsp");

		consoleBrowser.setLayoutData(gridData);
		outputBrowser.setLayoutData(gridData);
		sashForm.setLayoutData(gridData);

		outputBrowser.addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent event) {
				final String level = Main.setDebuggingLogLevel(false);
				outputBrowser.execute("setLogLevel('x" + level
						+ "'); initialize();");
			}

			@Override
			public void changed(ProgressEvent event) {
			}
		});
	}

	public Browser getConsoleBrowser() {
		return consoleBrowser;
	}

	public Browser getOutputBrowser() {
		return outputBrowser;
	}

	@Override
	public void setFocus() {
	}

	public static GroovyConsole getInstance() {
		if (instance == null)
			instance = new GroovyConsole();
		return instance;
	}

}
