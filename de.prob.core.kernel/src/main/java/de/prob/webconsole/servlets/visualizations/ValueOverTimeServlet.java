package de.prob.webconsole.servlets.visualizations;

import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.EvalElementFactory;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.AnimationProperties;
import de.prob.visualization.HTMLResources;
import de.prob.visualization.VisualizationSelector;

@SuppressWarnings("serial")
@Singleton
public class ValueOverTimeServlet extends SessionBasedServlet {

	private final AnimationSelector animations;
	private final VisualizationSelector visualizations;
	private final AnimationProperties properties;
	private final EvalElementFactory deserializer;

	@Inject
	public ValueOverTimeServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations,
			final AnimationProperties properties,
			final EvalElementFactory deserializer) {
		this.animations = animations;
		this.visualizations = visualizations;
		this.properties = properties;
		this.deserializer = deserializer;
	}

	public String openSession(final String sessionId, final IEvalElement formula)
			throws AnimationNotLoadedException {
		if (animations.getCurrentHistory() == null) {
			throw new AnimationNotLoadedException("Could not visualize "
					+ formula.getCode() + " because no animation is loaded");
		}
		ValueOverTimeSession session = new ValueOverTimeSession(formula,
				animations);
		String propFile = properties.getPropFileFromModelFile(animations
				.getCurrentHistory().getModel().getModelFile()
				.getAbsolutePath());
		properties.setProperty(propFile, sessionId, formula.serialized());
		super.openSession(sessionId, session);
		visualizations.registerSession(sessionId, session);
		return sessionId;
	}

	@Override
	protected String getHTML(final String id) {
		return HTMLResources.getValueVsTimeHTML(id);
	}

	@Override
	protected String loadSession(final String id) {
		if (animations.getCurrentHistory() != null) {
			String propFile = properties.getPropFileFromModelFile(animations
					.getCurrentHistory().getModel().getModelFile()
					.getAbsolutePath());
			Properties props = properties.getProperties(propFile);
			String formula = props.getProperty(id);
			if (formula != null) {
				IEvalElement iEvalElement = deserializer.deserialize(formula);
				try {
					openSession(id, iEvalElement);
				} catch (AnimationNotLoadedException e) {
					return null;
				}
			}

		}
		return null;
	}

}
