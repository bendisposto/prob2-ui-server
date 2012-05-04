package de.prob.model.classicalb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import test.PolySuite;
import test.PolySuite.Config;
import test.PolySuite.Configuration;
import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.node.Start;

@RunWith(PolySuite.class)
public class PrettyPrinterTest {

	static final String[] tests = { "5+6", "5", "5-6", "4+5+6", "4+5-6",
			"A\\B", "5*6", "5/6", "6 mod 5", "6**5", "A<->B", "A+->B", "A-->B",
			"A>+>B", "A>->B", "A+->>B", "A-->>B", "A>+>>B", "A>->>B", "A<<->B",
			"A<->>B", "A<<->>B", "A<+B", "A><B", "A^B", "A<|B", "A<<|B",
			"A|>B", "A|>>B", "A->B", "A<-B", "A\\/B", "A/\\B", "A/|\\B",
			"A\\|/B", "(2,3)", "5..20", "-x", "A~", "A[B]", "(A||B)", "(f;g)",
			"bool(x<0)", "max({1,2,3})", "min({1,2,3})", "card({1,2,3})",
			"SIGMA(y,z).(z<5|z*z)", "PI(a,b,c).(a<0&x<5|x*a*b*c)",
			"POW(A<->B)", "POW1(A<->B)", "FIN(A\\/B)", "FIN1(B/\\A)",
			"union(z)", "inter(A)", "id(A)", "closure(A)", "dom((1,2))",
			"ran((1,2))", "%x,y.(x<y|E)", "fnc(foo)", "rel(A<->POW(B))",
			"seq(S)", "seq1(S)", "iseq(S)", "iseq1(S)", "perm(S)", "perm([])",
			"size(S)", "first(S)", "last(S)", "front(S)", "tail(S)", "rev(S)",
			"prj1(S,T)", "prj2(S,T)", "iterate(A<->B,5)", "{x,y|x<y&x<5}",
			"UNION(z).(x<y|E)", "INTER(z).(x<y|E)", "perm([a,b,c])", "conc(S)",
			"TRUE", "MAXINT", "MININT", "{}", "INTEGER", "NATURAL", "NATURAL1",
			"NAT", "NAT1", "INT", "BOOL", "STRING" };

	String theString;

	public PrettyPrinterTest(String theString) {
		this.theString = theString;
	}

	@Test
	public void testExpression() throws Exception {
		String toParse = "#EXPRESSION " + theString;
		Start parse = BParser.parse(toParse);
		PrettyPrinter prettyprinter = new PrettyPrinter();
		parse.apply(prettyprinter);
		assertEquals(theString, prettyprinter.getPrettyPrint());
	}

	/*
	 * @Test public void testCoupleSecondRepresentation() throws Exception {
	 * String toParse = "#EXPRESSION 1|->2"; Start parse =
	 * BParser.parse(toParse); PrettyPrinter prettyprinter = new
	 * PrettyPrinter(); parse.apply(prettyprinter); assertEquals(toParse,
	 * prettyprinter.getPrettyPrint()); }
	 */

	@Config
	public static Configuration getConfig() {

		return new Configuration() {

			@Override
			public int size() {
				return tests.length;
			}

			@Override
			public String getTestValue(int index) {
				return tests[index];
			}

			@Override
			public String getTestName(int index) {
				return tests[index];
			}
		};
	}

}
