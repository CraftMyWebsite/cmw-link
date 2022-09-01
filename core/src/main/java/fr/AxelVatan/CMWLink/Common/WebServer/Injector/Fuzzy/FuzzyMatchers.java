package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy;

import java.lang.reflect.Member;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import fr.AxelVatan.CMWLink.Common.WebServer.Matcher.ClassExactMatcher;
import fr.AxelVatan.CMWLink.Common.WebServer.Matcher.ClassRegexMatcher;
import fr.AxelVatan.CMWLink.Common.WebServer.Matcher.ClassSetMatcher;

public class FuzzyMatchers {

	private static AbstractFuzzyMatcher<Class<?>> MATCH_ALL = new AbstractFuzzyMatcher<Class<?>>() {
		@Override
		public boolean isMatch(Class<?> value, Object parent) {
			return true;
		}

		@Override
		protected int calculateRoundNumber() {
			return 0;
		}
	};

	private FuzzyMatchers() {
	}

	public static AbstractFuzzyMatcher<Class<?>> matchArray(@Nonnull final AbstractFuzzyMatcher<Class<?>> componentMatcher) {
		Preconditions.checkNotNull(componentMatcher, "componentMatcher cannot be NULL.");
		return new AbstractFuzzyMatcher<Class<?>>() {
			@Override
			public boolean isMatch(Class<?> value, Object parent) {
				return value.isArray() && componentMatcher.isMatch(value.getComponentType(), parent);
			}

			@Override
			protected int calculateRoundNumber() {
				return -1;
			}
		};
	}

	public static AbstractFuzzyMatcher<Class<?>> matchAll() {
		return MATCH_ALL;
	}

	public static AbstractFuzzyMatcher<Class<?>> matchExact(Class<?> matcher) {
		return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_EXACT);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchAnyOf(Class<?>... classes) {
		return matchAnyOf(Sets.newHashSet(classes));
	}

	public static AbstractFuzzyMatcher<Class<?>> matchAnyOf(Set<Class<?>> classes) {
		return new ClassSetMatcher(classes);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchSuper(Class<?> matcher) {
		return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_SUPER);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchDerived(Class<?> matcher) {
		return new ClassExactMatcher(matcher, ClassExactMatcher.Options.MATCH_DERIVED);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchRegex(final Pattern regex, final int priority) {
		return new ClassRegexMatcher(regex, priority);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchRegex(String regex, final int priority) {
		return FuzzyMatchers.matchRegex(Pattern.compile(regex), priority);
	}

	public static AbstractFuzzyMatcher<Class<?>> matchParent() {
		return new AbstractFuzzyMatcher<Class<?>>() {
			@Override
			public boolean isMatch(Class<?> value, Object parent) {
				if (parent instanceof Member) {
					return ((Member) parent).getDeclaringClass().equals(value);
				} else if (parent instanceof Class) {
					return parent.equals(value);
				} else {
					return false;
				}
			}

			@Override
			protected int calculateRoundNumber() {
				return -100;
			}

			@Override
			public String toString() {
				return "match parent class";
			}

			@Override
			public int hashCode() {
				return 0;
			}

			@Override
			public boolean equals(Object obj) {
				return obj != null && obj.getClass() == this.getClass();
			}
		};
	}

	public static boolean checkPattern(Pattern a, Pattern b) {
		if (a == null)
			return b == null;
		else if (b == null)
			return false;
		else
			return a.pattern().equals(b.pattern());
	}
}