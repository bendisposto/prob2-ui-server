package de.prob.animator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import de.be4.classicalb.core.parser.BParser;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.scripting.ClassicalBFactory;

public class LoadBProjectCommandTest {

	@Test
	public void testWriteCommand() throws IOException, BException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("examples/scheduler.mch");
		File f = null;
		try {
			f = new File(resource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		StructuredPrologOutput prologTermOutput = new StructuredPrologOutput();
		ClassicalBFactory factory = new ClassicalBFactory(null);
		BParser bparser = new BParser();
		Start ast = factory.parseFile(f, bparser);
		RecursiveMachineLoader rml = factory.parseAllMachines(ast, f, bparser);

		LoadBProjectCommand command = new LoadBProjectCommand(rml, f);
		command.writeCommand(prologTermOutput);
		prologTermOutput.fullstop().flush();
		Collection<PrologTerm> sentences = prologTermOutput.getSentences();
		PrologTerm next = sentences.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof CompoundPrologTerm);
		CompoundPrologTerm t = (CompoundPrologTerm) next;
		assertEquals("load_classical_b", t.getFunctor());
		assertEquals(1, t.getArity());

		PrologTerm argument = t.getArgument(1);
		assertTrue(argument.isList());

	}

}
