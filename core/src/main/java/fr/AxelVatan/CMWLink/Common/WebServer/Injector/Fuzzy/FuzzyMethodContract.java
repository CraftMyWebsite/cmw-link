package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.MethodInfo;
import fr.AxelVatan.CMWLink.Common.WebServer.Matcher.ClassExactMatcher;

public class FuzzyMethodContract extends AbstractFuzzyMember<MethodInfo> {

	private static class ParameterClassMatcher extends AbstractFuzzyMatcher<Class<?>[]> {

		private final AbstractFuzzyMatcher<Class<?>> typeMatcher;
		private final Integer indexMatch;

		public ParameterClassMatcher(@Nonnull AbstractFuzzyMatcher<Class<?>> typeMatcher) {
			this(typeMatcher, null);
		}

		public ParameterClassMatcher(@Nonnull AbstractFuzzyMatcher<Class<?>> typeMatcher, Integer indexMatch) {
			if (typeMatcher == null)
				throw new IllegalArgumentException("Type matcher cannot be NULL.");

			this.typeMatcher = typeMatcher;
			this.indexMatch = indexMatch;
		}

		public boolean isParameterMatch(Class<?> param, MethodInfo parent, int index) {
			if (indexMatch == null || indexMatch == index){
				return typeMatcher.isMatch(param, parent);
			}else{
				return false;
			}
		}

		@Override
		public boolean isMatch(Class<?>[] value, Object parent) {
			throw new UnsupportedOperationException("Use the parameter match instead.");
		}

		@Override
		protected int calculateRoundNumber() {
			return typeMatcher.getRoundNumber();
		}

		@Override
		public String toString() {
			return String.format("{Type: %s, Index: %s}", typeMatcher, indexMatch);
		}
	}

	private AbstractFuzzyMatcher<Class<?>> returnMatcher = ClassExactMatcher.MATCH_ALL;
	private List<ParameterClassMatcher> paramMatchers;
	private List<ParameterClassMatcher> exceptionMatchers;
	private Integer paramCount;

	public static class Builder extends AbstractFuzzyMember.Builder<FuzzyMethodContract> {

		public Builder requireModifier(int modifier) {
			super.requireModifier(modifier); 
			return this;
		}

		@Override
		public Builder requirePublic() {
			super.requirePublic();
			return this;
		}

		@Override
		public Builder banModifier(int modifier) {
			super.banModifier(modifier); 
			return this;
		}

		@Override
		public Builder nameRegex(String regex) {
			super.nameRegex(regex); 
			return this;
		}

		@Override
		public Builder nameRegex(Pattern pattern) {
			super.nameRegex(pattern); 
			return this;
		}

		@Override
		public Builder nameExact(String name) {
			super.nameExact(name); 
			return this;
		}

		@Override
		public Builder declaringClassExactType(Class<?> declaringClass) {
			super.declaringClassExactType(declaringClass); 
			return this;
		}

		@Override
		public Builder declaringClassSuperOf(Class<?> declaringClass) {
			super.declaringClassSuperOf(declaringClass); 
			return this;
		}

		@Override
		public Builder declaringClassDerivedOf(Class<?> declaringClass) {
			super.declaringClassDerivedOf(declaringClass); 
			return this;
		}

		@Override
		public Builder declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher) {
			super.declaringClassMatching(classMatcher); 
			return this;
		}

		public Builder parameterExactType(Class<?> type) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchExact(type)));
			return this;
		}

		public Builder parameterSuperOf(Class<?> type) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchSuper(type)));
			return this;
		}

		public Builder parameterDerivedOf(Class<?> type) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchDerived(type)));
			return this;
		}

		public Builder parameterMatches(AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.paramMatchers.add(new ParameterClassMatcher(classMatcher));
			return this;
		}

		public Builder parameterExactType(Class<?> type, int index) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchExact(type), index));
			return this;
		}

		public Builder parameterExactArray(Class<?>... types) {
			parameterCount(types.length);
			for (int i = 0; i < types.length; i++) {
				parameterExactType(types[i], i);
			}
			return this;
		}

		public Builder parameterSuperOf(Class<?> type, int index) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchSuper(type), index));
			return this;
		}

		public Builder parameterDerivedOf(Class<?> type, int index) {
			member.paramMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchDerived(type), index));
			return this;
		}

		public Builder parameterMatches(AbstractFuzzyMatcher<Class<?>> classMatcher, int index) {
			member.paramMatchers.add(new ParameterClassMatcher(classMatcher, index));
			return this;
		}

		public Builder parameterCount(int expectedCount) {
			member.paramCount = expectedCount;
			return this;
		}

		public Builder returnTypeVoid() {
			return returnTypeExact(Void.TYPE);
		}

		public Builder returnTypeExact(Class<?> type) {
			member.returnMatcher = FuzzyMatchers.matchExact(type);
			return this;
		}

		public Builder returnDerivedOf(Class<?> type) {
			member.returnMatcher =  FuzzyMatchers.matchDerived(type);
			return this;
		}

		public Builder returnTypeMatches(AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.returnMatcher = classMatcher;
			return this;
		}

		public Builder exceptionExactType(Class<?> type) {
			member.exceptionMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchExact(type)));
			return this;
		}

		public Builder exceptionSuperOf(Class<?> type) {
			member.exceptionMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchSuper(type)));
			return this;
		}

		public Builder exceptionMatches(AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.exceptionMatchers.add(new ParameterClassMatcher(classMatcher));
			return this;
		}

		public Builder exceptionExactType(Class<?> type, int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchExact(type), index));
			return this;
		}

		public Builder exceptionSuperOf(Class<?> type, int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(FuzzyMatchers.matchSuper(type), index));
			return this;
		}

		public Builder exceptionMatches(AbstractFuzzyMatcher<Class<?>> classMatcher, int index) {
			member.exceptionMatchers.add(new ParameterClassMatcher(classMatcher, index));
			return this;
		}

		@Override
		@Nonnull
		protected FuzzyMethodContract initialMember() {
			return new FuzzyMethodContract();
		}

		@Override
		public FuzzyMethodContract build() {
			member.prepareBuild();
			return immutableCopy(member);
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	private FuzzyMethodContract() {
		paramMatchers = Lists.newArrayList();
		exceptionMatchers = Lists.newArrayList();
	}

	private FuzzyMethodContract(FuzzyMethodContract other) {
		super(other);
		this.returnMatcher = other.returnMatcher;
		this.paramMatchers = other.paramMatchers;
		this.exceptionMatchers = other.exceptionMatchers;
		this.paramCount = other.paramCount;
	}

	private static FuzzyMethodContract immutableCopy(FuzzyMethodContract other) {
		FuzzyMethodContract copy = new FuzzyMethodContract(other);
		copy.paramMatchers = ImmutableList.copyOf(copy.paramMatchers);
		copy.exceptionMatchers = ImmutableList.copyOf(copy.exceptionMatchers);
		return copy;
	}

	public AbstractFuzzyMatcher<Class<?>> getReturnMatcher() {
		return returnMatcher;
	}

	public ImmutableList<ParameterClassMatcher> getParamMatchers() {
		if (paramMatchers instanceof ImmutableList)
			return (ImmutableList<ParameterClassMatcher>) paramMatchers;
		else
			throw new IllegalStateException("Lists haven't been sealed yet.");
	}

	public List<ParameterClassMatcher> getExceptionMatchers() {
		if (exceptionMatchers instanceof ImmutableList)
			return exceptionMatchers;
		else
			throw new IllegalStateException("Lists haven't been sealed yet.");
	}

	public Integer getParamCount() {
		return paramCount;
	}

	@Override
	protected void prepareBuild() {
		super.prepareBuild();
		Collections.sort(paramMatchers);
		Collections.sort(exceptionMatchers);
	}

	@Override
	public boolean isMatch(MethodInfo value, Object parent) {
		if (super.isMatch(value, parent)) {
			Class<?>[] params = value.getParameterTypes();
			Class<?>[] exceptions = value.getExceptionTypes();
			if (!returnMatcher.isMatch(value.getReturnType(), value))
				return false;
			if (paramCount != null && paramCount != value.getParameterTypes().length)
				return false;
			return matchParameters(params, value, paramMatchers) && 
					matchParameters(exceptions, value, exceptionMatchers);
		}
		return false;
	}

	private boolean matchParameters(Class<?>[] types, MethodInfo parent, List<ParameterClassMatcher> matchers) {
		boolean[] accepted = new boolean[matchers.size()];
		int count = accepted.length;
		for (int i = 0; i < types.length; i++) {
			int matcherIndex = processValue(types[i], parent, i, accepted, matchers);
			if (matcherIndex >= 0) {
				accepted[matcherIndex] = true;
				count--;
			}
			if (count == 0)
				return true;
		}
		return count == 0;
	}

	private int processValue(Class<?> value, MethodInfo parent, int index, boolean accepted[], List<ParameterClassMatcher> matchers) {
		for (int i = 0; i < matchers.size(); i++) {
			if (!accepted[i]) {
				if (matchers.get(i).isParameterMatch(value, parent, index)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	protected int calculateRoundNumber() {
		int current = 0;
		current = returnMatcher.getRoundNumber();
		for (ParameterClassMatcher matcher : paramMatchers) {
			current = combineRounds(current, matcher.calculateRoundNumber());
		}
		for (ParameterClassMatcher matcher : exceptionMatchers) {
			current = combineRounds(current, matcher.calculateRoundNumber());
		}
		return combineRounds(super.calculateRoundNumber(), current);
	}

	@Override
	protected Map<String, Object> getKeyValueView() {
		Map<String, Object> member = super.getKeyValueView();
		if (returnMatcher != ClassExactMatcher.MATCH_ALL) {
			member.put("return", returnMatcher);
		}
		if (paramMatchers.size() > 0) {
			member.put("params", paramMatchers);
		}
		if (exceptionMatchers.size() > 0) {
			member.put("exceptions", exceptionMatchers);
		}
		if (paramCount != null) {
			member.put("paramCount", paramCount);
		}
		return member;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(returnMatcher, paramMatchers, exceptionMatchers, paramCount, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof FuzzyMethodContract && super.equals(obj)) {
			FuzzyMethodContract other = (FuzzyMethodContract) obj;

			return Objects.equal(paramCount, other.paramCount) && 
					Objects.equal(returnMatcher, other.returnMatcher) &&
					Objects.equal(paramMatchers, other.paramMatchers) &&
					Objects.equal(exceptionMatchers, other.exceptionMatchers);
		}
		return true;
	}
}