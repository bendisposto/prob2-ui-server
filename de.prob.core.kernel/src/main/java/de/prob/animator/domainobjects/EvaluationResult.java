package de.prob.animator.domainobjects;

import java.util.Arrays;
import java.util.List;

public class EvaluationResult {

	public final String value;
	public final String solution;
	public final String errors;
	public final String code;
	public final String explanation;
	private final String resultType;
	private final List<String> quantifiedVars;
	private final boolean enumerationWarnings;

	public EvaluationResult(final String code, final String value,
			final String solution, final String errors, String resultType,
			List<String> quantifiedVars, boolean enumerationWarnings) {
		this.code = code;
		this.value = value;
		this.solution = solution;
		this.errors = errors;
		this.resultType = resultType;
		this.quantifiedVars = quantifiedVars;
		this.enumerationWarnings = enumerationWarnings;
		if (!solutionMode(resultType) && "TRUE".equals(value))
			this.explanation = "Solution";
		else
			this.explanation = solutionMode(resultType) ? " Solution: "
					: " Counterexample: ";
	}

	public EvaluationResult(String code, String value, String solution,
			String errors, String resultType, String[] strings,
			boolean enumerationWarnings) {
		this(code, value, solution, errors, resultType, Arrays.asList(strings),
				enumerationWarnings);
	}

	private boolean solutionMode(String arg0) {
		return "exists".equals(arg0);
	}

	public String getResultType() {
		return resultType;
	}

	public List<String> getQuantifiedVars() {
		return quantifiedVars;
	}

	public boolean hasEnumerationWarnings() {
		return enumerationWarnings;
	}

	public boolean hasError() {
		return errors != null && !errors.isEmpty(); // You're kidding, aren't
													// you? ;-)
	}

	public String getErrors() {
		return errors;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		final String result;
		if (hasError())
			result = "'Errors: " + errors + "'";
		else {
			result = (solution == null || solution.equals("")) ? value : value
					+ explanation + solution;
		}
		return result;
	}

}
