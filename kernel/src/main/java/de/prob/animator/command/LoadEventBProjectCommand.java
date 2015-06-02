package de.prob.animator.command;

import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBProjectCommand extends AbstractCommand {

	private final EventBModelTranslator translator;

	public LoadEventBProjectCommand(final EventBModelTranslator translator) {
		this.translator = translator;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		translator.printProlog(pto);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
