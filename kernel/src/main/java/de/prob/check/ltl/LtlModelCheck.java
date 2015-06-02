package de.prob.check.ltl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.be4.ltl.core.parser.LtlParseException;
import de.prob.animator.command.LtlCheckingCommand;
import de.prob.animator.domainobjects.LTL;
import de.prob.check.IModelCheckingResult;
import de.prob.check.LTLOk;
import de.prob.ltl.parser.LtlParser;
import de.prob.parser.ResultParserException;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.web.WebUtils;

@Singleton
public class LtlModelCheck extends LtlPatternManager {

	public final String FORMULAS_FILE = "D://modelcheck/formulas.ltlf";
	private final String FORMULA_ID = "%% FORMULA";
	private final Logger logger = LoggerFactory.getLogger(LtlModelCheck.class);

	private final StateSpace currentStateSpace;

	@Inject
	public LtlModelCheck(final AnimationSelector animations) {
		Trace currentTrace = animations.getCurrentTrace();
		if (currentTrace == null) {
			throw new AnimationNotLoadedException(
					"Please load model before opening Value over Time visualization");
		}
		currentStateSpace = currentTrace.getStateSpace();
	}

	@Override
	public String html(final String clientid,
			final Map<String, String[]> parameterMap) {
		return simpleRender(clientid, "ui/ltl/index.html");
	}

	public Object checkFormula(final Map<String, String[]> params) {
		logger.trace("Check formula");

		String formula = get(params, "formula");
		String mode = get(params, "startMode");
		String index = get(params, "index");
		String callback = get(params, "callbackObj");

		ParseListener listener = new ParseListener();
		LtlParser parser = parse(formula, listener);

		if (listener.getErrorMarkers().size() > 0) {
			// Parse error
			submit(checkFormulaError(1, index, callback));
		} else {
			try {
				if (checkFormula(formula, parser, mode)) {
					submit(WebUtils.wrap("cmd", callback
							+ ".checkFormulaPassed", "index", index));
				} else {
					submit(checkFormulaError(2, index, callback));
				}
			} catch (ResultParserException ex) {
				submit(checkFormulaError(3, index, callback));
			} catch (LtlParseException e) {
				submit(checkFormulaError(3, index, callback));
			}
		}

		return WebUtils.wrap("cmd", callback + ".checkFormulaFinished");
	}

	public Object checkFormulaList(final Map<String, String[]> params) {
		logger.trace("Check formula list");

		String[] formulas = getArray(params, "formulas");
		String mode = get(params, "startMode");
		String[] indizes = getArray(params, "indizes");
		String callback = get(params, "callbackObj");

		for (int i = 0; i < formulas.length; i++) {
			String formula = formulas[i];
			String index = indizes[i];
			ParseListener listener = new ParseListener();
			LtlParser parser = parse(formula, listener);

			if (listener.getErrorMarkers().size() > 0) {
				// Parse error
				submit(checkFormulaError(1, index, callback));
			} else {
				try {
					if (checkFormula(formula, parser, mode)) {
						submit(WebUtils.wrap("cmd", callback
								+ ".checkFormulaPassed", "index", index));
					} else {
						submit(checkFormulaError(2, index, callback));
					}
				} catch (ResultParserException ex) {
					submit(checkFormulaError(3, index, callback));
				} catch (LtlParseException e) {
					submit(checkFormulaError(3, index, callback));
				}
			}
		}

		return WebUtils.wrap("cmd", callback + ".checkFormulaListFinished");
	}

	private boolean checkFormula(final String formula, final LtlParser parser,
			final String mode) throws LtlParseException {

		LTL f = new LTL(formula);

		// TODO: Replace this with new model checking abstraction when it is
		// finished
		IModelCheckingResult result = LtlCheckingCommand.modelCheck(
				currentStateSpace, f, 500);
		return (result instanceof LTLOk);
	}

	private Object checkFormulaError(final int error, final String index,
			final String callback) {
		return WebUtils.wrap("cmd", callback + ".checkFormulaFailed", "index",
				index, "error", error + "");
	}

	public Object getFormulaList(final Map<String, String[]> params) {
		logger.trace("Get formula list");

		String callback = get(params, "callbackObj");

		List<String> formulas = null;
		try {
			formulas = loadFormulas(FORMULAS_FILE);
		} catch (IOException e) {
		}

		return WebUtils.wrap("cmd", callback + ".setFormulaList", "formulas",
				(formulas != null ? WebUtils.toJson(formulas) : ""));
	}

	public Object saveFormulaList(final Map<String, String[]> params) {
		logger.trace("Save formula list");

		String formulas[] = getArray(params, "formulas");
		String callback = get(params, "callbackObj");

		try {
			saveFormulas(FORMULAS_FILE, formulas);
		} catch (IOException e) {
		}

		return WebUtils.wrap("cmd", callback + ".saveFormulaListSuccess");
	}

	private List<String> loadFormulas(final String filename) throws IOException {
		List<String> formulas = new LinkedList<String>();

		BufferedReader reader = null;
		try {
			InputStream stream = getClass().getResourceAsStream(filename);
			if (stream == null) {
				stream = new FileInputStream(filename);
			}
			if (stream != null) {
				reader = new BufferedReader(new InputStreamReader(stream));

				String line = null;
				StringBuilder formulaBuilder = null;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(FORMULA_ID)) {
						if (formulaBuilder != null) {
							formulas.add(formulaBuilder.toString());
						}
						formulaBuilder = new StringBuilder();
					} else {
						if (formulaBuilder.length() > 0) {
							formulaBuilder.append('\n');
						}
						formulaBuilder.append(line);
					}
				}
				if (formulaBuilder != null && formulaBuilder.length() > 0) {
					formulas.add(formulaBuilder.toString());
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return formulas;
	}

	private void saveFormulas(final String filename, final String[] formulas)
			throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			for (String formula : formulas) {
				writer.write(FORMULA_ID);
				writer.newLine();
				writer.write(formula);
				writer.newLine();
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
