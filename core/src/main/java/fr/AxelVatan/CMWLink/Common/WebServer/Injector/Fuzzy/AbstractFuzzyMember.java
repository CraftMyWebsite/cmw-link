package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import fr.AxelVatan.CMWLink.Common.WebServer.Matcher.ClassExactMatcher;

public abstract class AbstractFuzzyMember<T extends Member> extends AbstractFuzzyMatcher<T> {

	protected int modifiersRequired;
	protected int modifiersBanned;
	protected Pattern nameRegex;
	protected AbstractFuzzyMatcher<Class<?>> declaringMatcher = ClassExactMatcher.MATCH_ALL;
	protected transient boolean sealed;
	
	public static abstract class Builder<T extends AbstractFuzzyMember<?>> {
		protected T member = initialMember();

		public Builder<T> requireModifier(int modifier) {
			member.modifiersRequired |= modifier;
			return this;
		}
		
		public Builder<T> requirePublic() {
			return requireModifier(Modifier.PUBLIC);
		}
		
		public Builder<T> banModifier(int modifier) {
			member.modifiersBanned |= modifier;
			return this;
		}
		
		public Builder<T> nameRegex(String regex) {
			member.nameRegex = Pattern.compile(regex);
			return this;
		}
		
		public Builder<T> nameRegex(Pattern pattern) {
			member.nameRegex = pattern;
			return this;
		}
		
		public Builder<T> nameExact(String name) {
			return nameRegex(Pattern.quote(name));
		}
		
		public Builder<T> declaringClassExactType(Class<?> declaringClass) {
			member.declaringMatcher = FuzzyMatchers.matchExact(declaringClass);
			return this;
		}
		
		public Builder<T> declaringClassSuperOf(Class<?> declaringClass) {
			member.declaringMatcher = FuzzyMatchers.matchSuper(declaringClass);
			return this;
		}
		
		public Builder<T> declaringClassDerivedOf(Class<?> declaringClass) {
			member.declaringMatcher = FuzzyMatchers.matchDerived(declaringClass);
			return this;
		}
		
		public Builder<T> declaringClassMatching(AbstractFuzzyMatcher<Class<?>> classMatcher) {
			member.declaringMatcher = classMatcher;
			return this;
		}
		
		@Nonnull
		protected abstract T initialMember();
		
		public abstract T build();
	}
	
	protected AbstractFuzzyMember() {
	}
	
	protected void prepareBuild() {
	}
	
	protected AbstractFuzzyMember(AbstractFuzzyMember<T> other) {
		this.modifiersRequired = other.modifiersRequired;
		this.modifiersBanned = other.modifiersBanned;
		this.nameRegex = other.nameRegex;
		this.declaringMatcher = other.declaringMatcher;
		this.sealed = true;
	}

	public int getModifiersRequired() {
		return modifiersRequired;
	}

	public int getModifiersBanned() {
		return modifiersBanned;
	}

	public Pattern getNameRegex() {
		return nameRegex;
	}

	public AbstractFuzzyMatcher<Class<?>> getDeclaringMatcher() {
		return declaringMatcher;
	}

	@Override
	public boolean isMatch(T value, Object parent) {
		int mods = value.getModifiers();
		return (mods & modifiersRequired) == modifiersRequired &&
			   (mods & modifiersBanned) == 0 &&
			   declaringMatcher.isMatch(value.getDeclaringClass(), value) &&
			   isNameMatch(value.getName());
	}
	
	private boolean isNameMatch(String name) {
		if (nameRegex == null)
			return true;
		else
			return nameRegex.matcher(name).matches();
	}

	@Override
	protected int calculateRoundNumber() {
		if (!sealed) 
			throw new IllegalStateException("Cannot calculate round number during construction.");
		return declaringMatcher.getRoundNumber();
	}
	
	@Override
	public String toString() {
		return getKeyValueView().toString();
	}
	
	protected Map<String, Object> getKeyValueView() {
		Map<String, Object> map = Maps.newLinkedHashMap();
		if (modifiersRequired != Integer.MAX_VALUE || modifiersBanned != 0) {
			map.put("modifiers", String.format("[required: %s, banned: %s]", 
				   getBitView(modifiersRequired, 16),
				   getBitView(modifiersBanned, 16))
			);
		}
		if (nameRegex != null) {
			map.put("name", nameRegex.pattern());
		}
		if (declaringMatcher != ClassExactMatcher.MATCH_ALL) {
			map.put("declaring", declaringMatcher);
		}
		
		return map;
	}
	
	private static String getBitView(int value, int bits) {
		if (bits < 0 || bits > 31)
			throw new IllegalArgumentException("Bits must be a value between 0 and 32");
		int snipped = value & ((1 << bits) - 1);
		return Integer.toBinaryString(snipped);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof AbstractFuzzyMember) {
			@SuppressWarnings("unchecked")
			AbstractFuzzyMember<T> other = (AbstractFuzzyMember<T>) obj;
			return modifiersBanned == other.modifiersBanned &&
				   modifiersRequired == other.modifiersRequired &&
				   FuzzyMatchers.checkPattern(nameRegex, other.nameRegex) &&
				   Objects.equal(declaringMatcher, other.declaringMatcher);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(modifiersBanned, modifiersRequired, 
					nameRegex != null ? nameRegex.pattern() : null, declaringMatcher);
	}
}