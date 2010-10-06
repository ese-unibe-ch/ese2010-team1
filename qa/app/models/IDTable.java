package models;

import java.util.*;

/**
 * A Map of Objects with IDs.
 *
 * @param <E> Class to collect
 */
public class IDTable<E> {
	private int id;
	private HashMap<Integer,E> objects;
	
	public IDTable() {
		this.id = 1;
		this.objects = new HashMap();
	}
	
	public int nextID() {
		return this.id;
	}
	
	public int add(E o) {
		this.objects.put(this.id, o);
		return this.id++;
	}
	
	public E get(int key) {
		return this.objects.containsKey(key) ? this.objects.get(key) : null;
	}
	
	public void remove(int key) {
		this.objects.remove(key);
	}

	public Collection list() {
		return this.objects.values();
	}
	
	public Iterator<E> iterator() {
		return this.objects.values().iterator();
	}
	
	public boolean contains(E o) {
		return this.objects.containsValue(o);
	}
}
