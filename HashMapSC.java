package code;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import given.AbstractHashMap;
import given.HashEntry;

/*
 * The file should contain the implementation of a hashmap with:
 * - Separate Chaining for collision handling
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Java's own linked lists for the secondary containers
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 *   Note that for this type of hashmap, load factor can be higher than 1
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the buckets (which have been 
 * created for you) and the keyset method
 */

public class HashMapSC<Key, Value> extends AbstractHashMap<Key, Value> {

	// The underlying array to hold hash entry Lists
	private LinkedList<HashEntry<Key, Value>>[] buckets;

	// Note that the Linkedlists are still not initialized!
	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {
		// Update the capacity
		N = nextPrime(newSize);
		buckets = (LinkedList<HashEntry<Key, Value>>[]) Array.newInstance(LinkedList.class, N);
	}

	/*
	 * ADD MORE FIELDS IF NEEDED
	 * 
	 */

	// The threshold of the load factor for resizing
	protected float criticalLoadFactor;

	/*
	 * ADD A NESTED CLASS IF NEEDED
	 * 
	 */

	public int hashValue(Key key, int iter) {
		return hashValue(key);
	}

	@Override
	public int hashValue(Key key) {
		// TODO: Implement the hashvalue computation with the MAD method. Will be almost
		// the same as the primaryHash method of HashMapDH
		return Math.abs(((a * Math.abs(key.hashCode())) + b) % P) % N;
	}

	// Default constructor
	public HashMapSC() {
		this(101);
	}

	public HashMapSC(int initSize) {
		// High criticalAlpha for representing average items in a secondary container
		this(initSize, 10f);
	}

	public HashMapSC(int initSize, float criticalAlpha) {
		N = initSize;
		criticalLoadFactor = criticalAlpha;
		resizeBuckets(N);

		// Set up the MAD compression and secondary hash parameters
		updateHashParams();

		/*
		 * ADD MORE CODE IF NEEDED
		 * 
		 */
	}

	private Value addToBucket(LinkedList<HashEntry<Key, Value>> ls, HashEntry<Key, Value> e) {
		for (HashEntry<Key, Value> entry : ls) {
			if (entry.getKey().equals(e.getKey())) {
				Value temp = entry.getValue();
				entry.setValue(e.getValue());
				return temp;
			}
		}
		ls.add(e);
		n++;
		return null;
	}

	@Override
	public Value get(Key k) {
		if (k == null)
			return null;
		int getIndex = hashValue(k);
		if (buckets[getIndex] == null)
			return null;
		for (HashEntry<Key, Value> entry : buckets[getIndex]) {
			if (k.equals(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}

	@Override
	public Value put(Key k, Value v) {
		if (k == null)
			return null;
		checkAndResize();
		int putIndex = hashValue(k);
		if (buckets[putIndex] == null)
			buckets[putIndex] = new LinkedList<HashEntry<Key, Value>>();
		Value returnValue = addToBucket(buckets[putIndex], new HashEntry<>(k, v));
		return returnValue;
	}

	@Override
	public Value remove(Key k) {
		if (k == null)
			return null;
		int removeIndex = hashValue(k);
		if (buckets[removeIndex] == null)
			return null;
		for (HashEntry<Key, Value> entry : buckets[removeIndex]) {
			if (k.equals(entry.getKey())) {
				Value temp = entry.getValue();
				buckets[removeIndex].remove(entry);
				n--;
				return temp;
			}
		}
		return null;
	}

	@Override
	public Iterable<Key> keySet() {
		return Arrays.stream(buckets).filter(x -> x != null).map(x -> {
			return x.stream().map(y -> y.getKey()).collect(Collectors.toSet());
		}).collect(Collectors.toSet()).stream().flatMap(Set::stream).collect(Collectors.toSet());
//		Set<Key> keys = new HashSet<>();
//		for(int i = 0; i<capacity(); i++) {
//			if(buckets[i] == null)
//				continue;
//			
//		}
//		return null;
	}

//	public Iterable<Key> keySet() {
//		return Arrays.stream(buckets).map(x -> {
//			return x.stream()
//					.map(y -> y.getKey())
//					.collect(Collectors.toSet());
//		})
//				.filter(x -> x != null)
//				.collect(Collectors.toSet()).stream()
//				.flatMap(Set::stream)
//				.collect(Collectors.toSet());

	/**
	 * checkAndResize checks whether the current load factor is greater than the
	 * specified critical load factor. If it is, the table size should be increased
	 * to 2*N and recreate the hash table for the keys (rehashing). Do not forget to
	 * re-calculate the hash parameters and do not forget to re-populate the new
	 * array!
	 */
	protected void checkAndResize() {
		if (loadFactor() > criticalLoadFactor) {
			n = 0;
			LinkedList<HashEntry<Key, Value>>[] oldBuckets = buckets;
			resizeBuckets(N * 2);
			updateHashParams();
			for (LinkedList<HashEntry<Key, Value>> bucket : oldBuckets) {
				if (bucket == null)
					continue;
				for (HashEntry<Key, Value> entry : bucket) {
					put(entry.getKey(), entry.getValue());
				}
			}
		}
	}
}
