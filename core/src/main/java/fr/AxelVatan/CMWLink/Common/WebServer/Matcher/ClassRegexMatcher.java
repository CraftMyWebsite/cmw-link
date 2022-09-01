package fr.AxelVatan.CMWLink.Common.WebServer.Matcher;

import java.util.regex.Pattern;

import com.google.common.base.Objects;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.AbstractFuzzyMatcher;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyMatchers;

public class ClassRegexMatcher extends AbstractFuzzyMatcher<Class<?>> {
	
	private final Pattern regex;
	private final int priority;
	
	public ClassRegexMatcher(Pattern regex, int priority) {
		if (regex == null)
			throw new IllegalArgumentException("Regular expression pattern cannot be NULL.");
		this.regex = regex;
		this.priority = priority;
	}

	@Override
	public boolean isMatch(Class<?> value, Object parent) {
		if (value != null)
			return regex.matcher(value.getCanonicalName()).matches();
		else
			return false;
	}
	
	@Override
	protected int calculateRoundNumber() {
		return -priority;
	}
	
	@Override
	public String toString() {
		return "class name of " + regex.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(regex, priority);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ClassRegexMatcher) {
			ClassRegexMatcher other = (ClassRegexMatcher) obj;
			return priority == other.priority &&
				   FuzzyMatchers.checkPattern(regex, other.regex);
		}
		return false;
	}
}