package code;

import given.iPrintable;
import given.iSet;


public class HashSet<Key> implements iSet<Key>, iPrintable<Key> {

	HashMapDH<Key, Integer> set;

	public HashSet() {
		set = new HashMapDH<>();
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(Key k) {
		for (Key key : set.keySet()) {
			if (key.equals(k))
				return true;
		}
		return false;
	}

	@Override
	public boolean put(Key k) {
		if (set.get(k) != null)
			return true;
		set.put(k, 0);
		return false;
	}

	@Override
	public boolean remove(Key k) {
		return set.remove(k) == null ? false : true;
	}

	@Override
	public Iterable<Key> keySet() {
		return set.keySet();
	}

	@Override
	public Object get(Key key) {
		return null;
	}

}
