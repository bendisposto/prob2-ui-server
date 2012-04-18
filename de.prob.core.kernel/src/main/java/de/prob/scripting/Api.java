package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.command.notImplemented.EvaluateCommand;
import de.prob.animator.command.notImplemented.GetOperationNamesCommand;
import de.prob.annotations.Home;
import de.prob.cli.ProBInstance;
import de.prob.model.StateSpace;
import de.prob.model.representation.ClassicalBMachine;
import de.prob.model.representation.Operation;

public class Api {

	Logger logger = LoggerFactory.getLogger(Api.class);

	private final FactoryProvider modelFactoryProvider;
	private final String home;

	@Inject
	public Api(final FactoryProvider modelFactoryProvider,
			@Home final String home) {
		this.modelFactoryProvider = modelFactoryProvider;
		this.home = home;
	}

	public void raise() {
		// logger.error("Fataaaaal!");
		// logger.error("Fatal!", new IllegalArgumentException("bawang"));
	}

	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public ClassicalBMachine b_def() throws ProBException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("examples/scheduler.mch");
		File f = null;
		try {
			f = new File(resource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();

		return bFactory.load(f);
	}

	public StateSpace s() throws ProBException {
		return b_def().getStatespace();
	}

	public ClassicalBMachine b_load(final String file) throws ProBException {
		File f = new File(file);
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();
		return bFactory.load(f);
	}

	public List<Operation> testX(final StateSpace s) throws ProBException {
		GetOperationNamesCommand command = new GetOperationNamesCommand();
		s.execute(command);
		return command.getOperations();
	}

	public String eval(final String text, final StateSpace s)
			throws ProBException {
		EvaluateCommand command = new EvaluateCommand(text);
		s.execute(command);
		return command.getResult();
	}

	public String getCurrentId(final StateSpace animation) throws ProBException {
		// new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		// .executeOn(animation);
		return null;
	}

	public String upgrade() {
		Downloader dl = new Downloader();
		try {
			dl.downloadCli(home);
		} catch (ProBException e) {
			logger.error(
					"Could not download files for the given operating system",
					e);
		}

		return "--UPGRADE COMPLETE--";
	}
}
