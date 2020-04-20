package code;

import given.iPrintable;
import given.iSet;

/*
 * A set class implemented with hashing. Note that there is no "value" here 
 * 
 * You are free to implement this however you want. Two potential ideas:
 * 
 * - Use a hashmap you have implemented with a dummy value class that does not take too much space
 * OR
 * - Re-implement the methods but tailor/optimize them for set operations
 * 
 * You are not allowed to use any existing java data structures
 * 
 */

public class HashSet<Key> implements iSet<Key>, iPrintable<Key> {

	HashMapDH<Key, Integer> set;

	// A default public constructor is mandatory!
	public HashSet() {
		set = new HashMapDH<>();
	}

	/*
	 * 
	 * Add whatever you want!
	 * 
	 */

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
		// Do not touch
		return null;
	}

}
