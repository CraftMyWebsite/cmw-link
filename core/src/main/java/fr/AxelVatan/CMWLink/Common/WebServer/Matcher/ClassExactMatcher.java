package fr.AxelVatan.CMWLink.Common.WebServer.Matcher;

import com.google.common.base.Objects;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.AbstractFuzzyMatcher;

public class ClassExactMatcher extends AbstractFuzzyMatcher<Class<?>> {

	public enum Options {

		MATCH_EXACT,
		MATCH_SUPER,
		MATCH_DERIVED
	}
	
	public static final ClassExactMatcher MATCH_ALL = new ClassExactMatcher(null, Options.MATCH_SUPER);
	
	private final Class<?> matcher;
	private final Options option;
	
	public ClassExactMatcher(Class<?> matcher, Options option) {
		this.matcher = matcher;
		this.option = option;
	}

	@Override
	public boolean isMatch(Class<?> input, Object parent) {
		if (input == null)
			throw new IllegalArgumentException("Input class cannot be NULL.");
		if (matcher == null)
			return option != Options.MATCH_EXACT;
		else if (option == Options.MATCH_SUPER)
			return input.isAssignableFrom(matcher); // matcher instanceof input
		else if (option == Options.MATCH_DERIVED)
			return matcher.isAssignableFrom(input); // input instanceof matcher
		else
			return input.equals(matcher);
	}
	
	@Override
	protected int calculateRoundNumber() {
		return -getClassNumber(matcher);
	}
	
	public static int getClassNumber(Class<?> clazz) {
		int count = 0;
		while (clazz != null) {
			count++;
			clazz = clazz.getSuperclass();
		}
		return count;
	}

	public Class<?> getMatcher() {
		return matcher;
	}

	public Options getOptions() {
		return option;
	}
	
	@Override
	public String toString() {
		if (option == Options.MATCH_SUPER)
			return matcher + " instanceof input";
		else if (option == Options.MATCH_DERIVED)
			return "input instanceof " + matcher;
		else
			return "Exact " + matcher;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(matcher, option);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ClassExactMatcher) {
			ClassExactMatcher other = (ClassExactMatcher) obj;
			
			return Objects.equal(matcher, other.matcher) && 
					Objects.equal(option, other.option);
		}
		return false;
	}
}