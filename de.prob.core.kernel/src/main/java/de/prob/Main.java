package de.prob;

import static java.io.File.separator;

import java.io.File;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.google.inject.Inject;

import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.WebConsole;

public class Main {

	private static boolean shellMode;

	private final CommandLineParser parser;
	private final Options options;
	private final Shell shell;
	public final static String PROB_HOME = getProBDirectory();
	public final static String LOG_CONFIG = System
			.getProperty("PROB_LOG_CONFIG") == null ? "production.xml" : System
			.getProperty("PROB_LOG_CONFIG");

	public static WeakHashMap<Process, Boolean> processes = new WeakHashMap<Process, Boolean>();

	private static GroovyExecution executor;

	@Inject
	public Main(final CommandLineParser parser, final Options options,
			final Shell shell, GroovyExecution ex) {
		this.parser = parser;
		this.options = options;
		this.shell = shell;
		Main.executor = ex;
	}

	void run(final String[] args) {
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("shell")) {
				if ("old".equals(System.getProperty("shell"))) {
					Main.shellMode = true;
					shell.repl();
				} else {
					try {
						WebConsole.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (line.hasOption("test")) {
				String value = line.getOptionValue("test");
				shell.runScript(new File(value));
			}
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar probcli.jar", options);
		}
	}

	public static String setDebuggingLogLevel(boolean value) {
		executor.renewSideeffects();
		StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
		LoggerContext loggerFactory = (LoggerContext) singleton
				.getLoggerFactory();
		Logger root = (Logger) loggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		Level level = value ? Level.DEBUG : Level.ERROR;
		root.setLevel(level);
		return level.toString();
	}

	public static String getProBDirectory() {
		String homedir = System.getProperty("prob.home");
		if (homedir != null)
			return homedir + separator;
		String env = System.getenv("PROB_HOME");
		if (env != null)
			return env + separator;
		return System.getProperty("user.home") + separator + ".prob"
				+ separator;
	}

	public static void main(final String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Set<Process> keySet = Main.processes.keySet();
				for (Process process : keySet) {
					process.destroy();
				}
			}
		});
		System.setProperty("PROB_LOG_CONFIG", LOG_CONFIG);
		System.setProperty("PROB_LOGFILE", PROB_HOME + "logs" + separator
				+ "ProB.txt");

		Main main = ServletContextListener.INJECTOR.getInstance(Main.class);

		main.run(args);
		System.exit(0);
	}

	public static boolean isShellMode() {
		return shellMode;
	}

}