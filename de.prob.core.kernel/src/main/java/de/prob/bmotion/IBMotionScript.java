package de.prob.bmotion;

import java.util.Map;

import de.prob.statespace.Trace;

public interface IBMotionScript {

	public void traceChange(Trace trace, Map<String,Object> formulas);
	
}