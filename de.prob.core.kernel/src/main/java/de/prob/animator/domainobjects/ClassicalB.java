/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.List;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.AExpressionParseUnit;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.classicalb.PrettyPrinter;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IEntity;
import de.prob.prolog.output.IPrologTermOutput;

public class ClassicalB implements IEvalElement, IEntity {

	public FormulaUUID uuid = new FormulaUUID();

	private final String code;
	private final Start ast;

	public ClassicalB(final String code) throws BException {
		this.code = code;
		this.ast = BParser.parse(BParser.FORMULA_PREFIX + " " + code);
	}

	public ClassicalB(final Start ast) {
		this.ast = ast;
		this.code = prettyprint(ast);
	}

	@Override
	public String getKind() {
		return ast.getPParseUnit() instanceof AExpressionParseUnit ? EXPRESSION
				.toString() : PREDICATE.toString();
	}

	@Override
	public String getCode() {
		return code;
	}

	public Start getAst() {
		return ast;
	}

	@Override
	public String toString() {
		return code;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		final ASTProlog prolog = new ASTProlog(pout, null);
		getAst().apply(prolog);
	}

	@Override
	public List<IEntity> getChildren() {
		return new ArrayList<IEntity>();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	private String prettyprint(final Node predicate) {
		final PrettyPrinter prettyPrinter = new PrettyPrinter();
		predicate.apply(prettyPrinter);
		return prettyPrinter.getPrettyPrint();
	}

}
