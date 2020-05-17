package code;

import given.AbstractHashMap;
import given.iPrintable;

public class HashCounter<Key> implements iPrintable<Key> {

	public AbstractHashMap<Key, Integer> A;

	int s;

	public HashCounter(AbstractHashMap<Key, Integer> inContainer) {
		A = inContainer;

		s = 0;
	}

	public HashCounter() {
		this(new HashMapDH<Key, Integer>());
	}

	public int size() {
		return A.size();
	}

	public boolean isEmpty() {
		return A.isEmpty();
	}

	public int total() {
		return s;
	}

	public void increment(Key key) {
		s++;
		Integer old = A.get(key);
		if (old == null) {
			A.put(key, 1);
		} else {
			A.put(key, old + 1);
		}
	}

	public int getCount(Key key) {
		Integer count = A.get(key);
		if (count == null)
			return 0;
		return count;
	}

	public void countAll(Key[] keys) {
		for (Key k : keys) {
			increment(k);
		}
	}

	public void countAll(Iterable<Key> keys) {
		for (Key k : keys) {
			increment(k);
		}
	}

	public Iterable<Key> keySet() {
		return A.keySet();
	}

	@Override
	public Object get(Key key) {
		return getCount(key);
	}

}
