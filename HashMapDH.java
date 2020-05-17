package code;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Collectors;

import given.AbstractHashMap;
import given.HashEntry;

public class HashMapDH<Key, Value> extends AbstractHashMap<Key, Value> {

	private HashEntry<Key, Value>[] buckets;

	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {

		N = nextPrime(newSize);
		buckets = (HashEntry<Key, Value>[]) Array.newInstance(HashEntry.class, N);
	}

	protected float criticalLoadFactor;

	int dhP;

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

		updateHashParams();

	}

	private int getEntryIndex(Key k) {
		for (int i = 0; i < N; i++) {
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

	protected int primaryHash(int hashCode) {
		return Math.abs(((a * hashCode) + b) % P) % N;
	}

	protected int secondaryHash(int hashCode) {
		return dhP - (hashCode % dhP);
	}

	@Override
	public int hashValue(Key key, int iter) {
		int k = Math.abs(key.hashCode());
		return Math.abs(primaryHash(k) + iter * secondaryHash(k)) % N;
	}

	protected void checkAndResize() {
		if (loadFactor() > criticalLoadFactor) {
			n = 0;
			HashEntry<Key, Value> oldBuckets[] = buckets;
			resizeBuckets(2 * N);
			updateHashParams();
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
		if (removeIndex == -1) {
			return null;
		}
		Value temp = buckets[removeIndex].getValue();
		buckets[removeIndex] = null;
		n--;
		return temp;
	}

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
