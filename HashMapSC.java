package code;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import given.AbstractHashMap;
import given.HashEntry;


public class HashMapSC<Key, Value> extends AbstractHashMap<Key, Value> {

	private LinkedList<HashEntry<Key, Value>>[] buckets;
	@SuppressWarnings("unchecked")
	protected void resizeBuckets(int newSize) {
		N = nextPrime(newSize);
		buckets = (LinkedList<HashEntry<Key, Value>>[]) Array.newInstance(LinkedList.class, N);
	}

	protected float criticalLoadFactor;


	public int hashValue(Key key, int iter) {
		return hashValue(key);
	}

	@Override
	public int hashValue(Key key) {
		return Math.abs(((a * Math.abs(key.hashCode())) + b) % P) % N;
	}

	public HashMapSC() {
		this(101);
	}

	public HashMapSC(int initSize) {
		this(initSize, 10f);
	}

	public HashMapSC(int initSize, float criticalAlpha) {
		N = initSize;
		criticalLoadFactor = criticalAlpha;
		resizeBuckets(N);

		updateHashParams();

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
