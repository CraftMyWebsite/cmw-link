package fr.AxelVatan.CMWLink.Common.WebServer.Matcher;

import java.util.Set;

import com.google.common.base.Objects;

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
	
	@Override
	public String toString() {
		return "match any: " + classes;
	}
	
	@Override
	public int hashCode() {
		return classes.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ClassSetMatcher) {
			return Objects.equal(classes, ((ClassSetMatcher) obj).classes);
		}
		return true;
	}
}