package code;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

import given.AbstractHashMap;
import given.HashEntry;

/*
 * The file should contain the implementation of a hashmap with:
 * - Open addressing for collision handling
 * - Double hashing for probing. The double hash function should be of the form: q - (k mod q)
 * - Multiply-Add-Divide (MAD) for compression: (a*k+b) mod p
 * - Resizing (to double its size) and rehashing when the load factor gets above a threshold
 * 
 * Some helper functions are provided to you. We suggest that you go over them.
 * 
 * You are not allowed to use any existing java data structures other than for the keyset method
 */

public class HashMapDH<Key, Value> extends AbstractHashMap<Key, Value> {

	// The underlying array to hold hash entries (see the HashEntry class)
	private HashEntry<Key, Value>[] buckets;

	// Do not forget to call this when you need to increase the size!
	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {
		// Update the capacity
		N = nextPrime(newSize);
		buckets = (HashEntry<Key, Value>[]) Array.newInstance(HashEntry.class, N);
	}

	// The threshold of the load factor for resizing
	protected float criticalLoadFactor;

	// The prime number for the secondary hash
	int dhP;

	/*
	 * ADD MORE FIELDS IF NEEDED
	 * 
	 */

	/*
	 * ADD A NESTED CLASS IF NEEDED
	 * 
	 */

	// Default constructor
	public HashMapDH() {
		this(101);
	}

	public HashMapDH(int initSize) {
		this(initSize, 0.6f);
	}

	public HashMapDH(int initSize, float criticalAlpha) {
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

	private int getEntryIndex(Key k) {
		for (int i = 0; i < size(); i++) {
			int index = hashValue(k, i);
			if (buckets[index] != null) {
				if (k.equals(buckets[index].getKey()))
					return index;
			}
		}
		return -1;
	}

	private int findEmptyBucket(Key k) {
		int index = 0;
		int probe = 0;
		do {
			index = hashValue(k, probe);
			probe++;
		} while (buckets[index] != null && probe < capacity());
		return index;
	}

	/**
	 * Calculates the hash value by compressing the given hashcode. Note that you
	 * need to use the Multiple-Add-Divide method. The class variables "a" is the
	 * scale, "b" is the shift, "mainP" is the prime which are calculated for you.
	 * Do not include the size of the array here
	 * 
	 * Make sure to include the absolute value since there maybe integer overflow!
	 */
	protected int primaryHash(int hashCode) {
		// TODO: Implement MAD compression given the hash code, should be 1 line
		return Math.abs(((a * hashCode) + b) % P); // TODO: AND MOD N
	}

	/**
	 * The secondary hash function. Remember you need to use "dhP" here!
	 * 
	 */
	protected int secondaryHash(int hashCode) {
		return dhP - (hashCode % dhP);
	}

	@Override
	public int hashValue(Key key, int iter) {
		int k = Math.abs(key.hashCode());
		return Math.abs(primaryHash(k) + iter * secondaryHash(k)) % N;
	}

	/**
	 * checkAndResize checks whether the current load factor is greater than the
	 * specified critical load factor. If it is, the table size should be increased
	 * to 2*N and recreate the hash table for the keys (rehashing). Do not forget to
	 * re-calculate the hash parameters and do not forget to re-populate the new
	 * array!
	 */
	protected void checkAndResize() {
		if (loadFactor() > criticalLoadFactor) {
			HashEntry<Key, Value> oldBuckets[] = buckets;
			resizeBuckets(2 * N);
			for (HashEntry<Key, Value> entry : oldBuckets) {
				if (entry == null)
					continue;
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public Value get(Key k) {
		if (k == null)
			return null;
		int getIndex = getEntryIndex(k);
		if (getIndex == -1)
			return null;
		return buckets[getIndex].getValue();
	}

	@Override
	public Value put(Key k, Value v) {
		if (k == null)
			return null;
		checkAndResize();
		int putIndex = getEntryIndex(k);
		if (putIndex != -1) {
			Value temp = buckets[putIndex].getValue();
			buckets[putIndex].setValue(v);
			return temp;
		}
		buckets[findEmptyBucket(k)] = new HashEntry<>(k, v);
		n++;
		return null;
	}

	@Override
	public Value remove(Key k) {
		if (k == null)
			return null;
		int removeIndex = getEntryIndex(k);
		if (removeIndex == -1)
			return null;
		Value temp = buckets[removeIndex].getValue();
		buckets[removeIndex] = null;
		n--;
		return temp;
	}

	// This is the only function you are allowed to use an existing Java data
	// structure!
	@Override
	public Iterable<Key> keySet() {
		return Arrays.stream(buckets).filter(x -> (x != null)).map(x -> x.getKey()).collect(Collectors.toSet());
	}

	@Override
	protected void updateHashParams() {
		super.updateHashParams();
		dhP = nextPrime(N / 2);
	}

}
