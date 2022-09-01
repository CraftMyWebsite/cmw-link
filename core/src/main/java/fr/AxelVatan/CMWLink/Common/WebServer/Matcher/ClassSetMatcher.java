package fr.AxelVatan.CMWLink.Common.WebServer.Matcher;

import java.util.Set;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.AbstractFuzzyMatcher;

public class ClassSetMatcher extends AbstractFuzzyMatcher<Class<?>> {
	
	private final Set<Class<?>> classes;
	
	public ClassSetMatcher(Set<Class<?>> classes) {
		if (classes == null)
			throw new IllegalArgumentException("Set of classes cannot be NULL.");
		this.classes = classes;
	}

	@Override
	public boolean isMatch(Class<?> value, Object parent) {
		return classes.contains(value);
	}
	
	@Override
	protected int calculateRoundNumber() {
		int roundNumber = 0;
		for (Class<?> clazz : classes) {
			roundNumber = combineRounds(roundNumber, -ClassExactMatcher.getClassNumber(clazz));
		}
		return roundNumber;
	}
	
}