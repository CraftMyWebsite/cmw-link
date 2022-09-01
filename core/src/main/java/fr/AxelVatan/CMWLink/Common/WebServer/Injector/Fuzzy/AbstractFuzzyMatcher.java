package fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy;

import com.google.common.primitives.Ints;

public abstract class AbstractFuzzyMatcher<T> implements Comparable<AbstractFuzzyMatcher<T>> {
	
	private Integer roundNumber;
	
	public abstract boolean isMatch(T value, Object parent);
	
	protected abstract int calculateRoundNumber();
	
	public final int getRoundNumber() {
		if (roundNumber == null) {
			return roundNumber = calculateRoundNumber();
		} else {
			return roundNumber;
		}
	}
	
	protected final int combineRounds(int roundA, int roundB) {
		if (roundA == 0)
			return roundB;
		else if (roundB == 0)
			return roundA;
		else
			return Math.max(roundA, roundB);
	}
	
	protected final int combineRounds(Integer... rounds) {
		if (rounds.length < 2)
			throw new IllegalArgumentException("Must supply at least two arguments.");
		int reduced = combineRounds(rounds[0], rounds[1]);
		for (int i = 2; i < rounds.length; i++) {
			reduced = combineRounds(reduced, rounds[i]);
		}
		return reduced;
	}
		
	@Override
	public int compareTo(AbstractFuzzyMatcher<T> obj) {
		if (obj instanceof AbstractFuzzyMatcher) {
			AbstractFuzzyMatcher<?> matcher = (AbstractFuzzyMatcher<?>) obj;
			return Ints.compare(getRoundNumber(), matcher.getRoundNumber());
		}
		return -1;
	}

	public AbstractFuzzyMatcher<T> inverted() {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(T value, Object parent) {
				return !AbstractFuzzyMatcher.this.isMatch(value, parent);
			}
			
			@Override
			protected int calculateRoundNumber() {
				return -2;
			}
		};
	}
	
	public AbstractFuzzyMatcher<T> and(final AbstractFuzzyMatcher<T> other) {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(T value, Object parent) {
				return AbstractFuzzyMatcher.this.isMatch(value, parent) &&
									       other.isMatch(value, parent);
			}
			
			@Override
			protected int calculateRoundNumber() {
				return combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
			}
		};
	}
	
	public AbstractFuzzyMatcher<T> or(final AbstractFuzzyMatcher<T> other) {
		return new AbstractFuzzyMatcher<T>() {
			@Override
			public boolean isMatch(T value, Object parent) {
				return AbstractFuzzyMatcher.this.isMatch(value, parent) ||
									       other.isMatch(value, parent);
			}
			
			@Override
			protected int calculateRoundNumber() {
				return combineRounds(AbstractFuzzyMatcher.this.getRoundNumber(), other.getRoundNumber());
			}
		};
	}
}