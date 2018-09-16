/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 1501 Summer 2018 (enrolled in W section)
* Assignment #2   A PHP-style array data structure implementation
*/

import java.util.*;

public class PHPArray<V> implements Iterable<V> {

	//16 is nice because the computer can use shifts for * and / on powers of 2
	private final static int DEFAULT_SIZE = 16;

	private int M; 				//the size of the underlying hash table
	private int N; 				//the # of (key, value) pairs currently in the hash table
	private Pair<V>[] table;	//this is the actual hash table
	private Pair<V> root;		//this is the first node inserted and thus the first in the linked chain
	private Pair<V> tail;		//this is the last node inserted and thus the end of the linked chain
	private Pair<V> nextPair;   //the next Pair that each() will yield

	public PHPArray()
	{
		this(DEFAULT_SIZE);
	}

	@SuppressWarnings("unchecked")  //this cast is safe because the array is new
	public PHPArray(int size)
	{
		M = size;
		N = 0;
		tail = null;
		root = null;
		nextPair = null;
		table = (Pair<V>[]) new Pair<?>[M];
	}

	public int length()
	{
		return N;
	}

	//hashes a given (key,value) pair into the table
	//if the key already exists, replace its value with the new value
	//uses the String.valueOf() method to obtain the key String
	public void put(Object k, V val)
	{
        if (N >= M/2)  // double table size if 50% full
        	resize();
        String key = String.valueOf(k);

		Pair<V> pairToInsert = new Pair<V>(key, val);
		if(root == null)
		{
			root = pairToInsert;
			tail = pairToInsert;
			nextPair = pairToInsert;
		}
		if(tail != null)
		{
			tail.next = pairToInsert;
			pairToInsert.previous = tail;
		}
		int index = hash(key);
		while (table[index] != null)
		{
			if(table[index].key.equals(key))
			{
				table[index].value = val;
				pairToInsert.previous = null;
				tail.next = null;
				return;
			}
			index = (index + 1) % M;
		}
		tail = pairToInsert;
		table[index] = pairToInsert;
		N++;
	}

	//O(1) access to table values
	public V get(Object k)
	{
		String key = String.valueOf(k);
		int index = hash(key);
		while (table[index] != null)
		{
			if(key.equals(table[index].key))
				return table[index].value;
			index = (index + 1) % M;
		}
		return null;
	}

	//delete a key from the table
	//rehashes all values in the cluster, if any
	//this method will print any rehashed values to help with grading
	//utilizes doubly-linked access to relink previous node to next node
		//or, handles special cases if these nodes are null
	//returns true if the key found and removed or false if it was never there
	public boolean unset(Object k)
	{
		String key = String.valueOf(k);
		int index = hash(key);
		while (table[index] != null && !key.equals(table[index].key))	//locate target in the cluster
            index = (index + 1) % M;
        if(table[index] == null)								//not found special case
        	return false;
		Pair<V> pairToRemove = table[index];
		table[index] = null;
		if(pairToRemove == root)								//special case root
			root = pairToRemove.next;
		if(pairToRemove == tail)								//special case tail
			tail = pairToRemove.previous;
		if(pairToRemove == nextPair)							//special case nextPair (avoid breaking it)
			nextPair = pairToRemove.next;
		if(pairToRemove.previous != null)						//link up surrounding nodes
			pairToRemove.previous.next = pairToRemove.next;		//if they exist
		if(pairToRemove.next != null)
			pairToRemove.next.previous = pairToRemove.previous;
		index = (index + 1) % M;
		while(table[index] != null)								//rehash the rest of the cluster
		{
			Pair<V> pairToRehash = table[index];
			System.out.println("\t\tKey " + pairToRehash.key + " rehashed...\n");
			table[index] = null;
			reinsert(pairToRehash);
			index = (index + 1) % M;
		}
		N--;
		return true;
	}

	//resets the internal class iterator
	public void reset()
	{
		nextPair = root;
	}

	//get the internal class iterator's next Pair
	//this could be corrupted by alterations to the list during traversal (i.e., sort)
	//the program won't crash, but the iteration might yield unexpected results
	public Pair<V> each()
	{
		if(nextPair == null)
			return null;
		Pair<V> result = nextPair;
		nextPair = nextPair.next;
		return result;
	}

	//returns an ArrayList of all the keys in the table
	public ArrayList<String> keys()
	{
		ArrayList<String> keys = new ArrayList<String>(N);
		Pair<V> current = root;
		while(current != null)
		{
			keys.add(current.key);
			current = current.next;
		}
		return keys;
	}

	//returns an ArrayList of all the values in the table
	public ArrayList<V> values()
	{
		ArrayList<V> values = new ArrayList<V>(N);
		Pair<V> current = root;
		while(current != null)
		{
			values.add(current.value);
			current = current.next;
		}
		return values;
	}

	//dump the underlying hash table structure to the console for grading
	public void showTable()
	{
		System.out.println("\tRaw Hash Table Contents");
		for (int i = 0; i < M; i++)
		{
			System.out.print(i + ": ");
			if(table[i] == null)
				System.out.println("null");
			else
				System.out.println(table[i]);
		}
	}

	//sort the hash table by values, replacing keys with consecutive int values
	//this has additional O(N) overhead to reassign keys and blank the table
	//the overall sort is still O(N) because the underlying sort is mergesort
	//and the linear overhead is performed in a one-time, independent fashion
	public void sort()
	{
		if(root == null)
			return;
		Comparable testValue = (Comparable) root.value;  //cause exception before maiming list nodes
		root = mergesort(root, N);
		Pair<V> curr = root;
		for(Pair<V> p : table)              //blank table to avoid irreproducible collisions on rehash
			p = null;
		for(int i = 0; i < N; i++)
		{
			curr.key = String.valueOf(i);	//set keys to String versions of #s 0 through N-1
			reinsert(curr);					//need to rehash so new keys don't break get()
			curr = curr.next;
		}
		tail = curr;
	}

	//public-facing sort method which sorts by value and preserves the keys
	//calls the (recursive) mergesort method to actually perform the sort
	//has linear extra work here to relocate and correctly identify the list tail
	@SuppressWarnings("unchecked")  //cast might fail, but the ClassCastException is desired behavior
	public void asort()
	{
		if(root == null)
			return;
		Comparable testValue = (Comparable) root.value;  //cause exception before maiming list nodes
		root = mergesort(root, N);
		Pair<V> curr = root;
		while(curr.next != null)
			curr = curr.next;
		tail = curr;
	}

	//returns a new PHPArray with keys and values flipped
	//only works if the values in this table can be cast to String
	//if they can't, array_flip will throw a ClassCastException
	public PHPArray<String> array_flip()
	{
        PHPArray<String> result = new PHPArray<String>(M);
        Pair<V> curr = root;
        while(curr != null)
        {
        	result.put((String)curr.value, curr.key);
        	curr = curr.next;
        }
		return result;
	}

	// hash function for keys - returns value between 0 and M-1
	// this is the author's method verbatim
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }

	//simple helper to rehash values into the table without worrying about
	//duplicate values  -- useful for resize and unset
    private void reinsert(Pair<V> p)
    {
    	int index = hash(p.key);
		while (table[index] != null)
			index = (index + 1) % M;
		table[index] = p;
    }

    @SuppressWarnings("unchecked")
    // resize the hash table to the given capacity by rehashing all of the keys
	// this is an adaptation of the text author's code, without downsize
	// resize() will alert the user when the underlying array size changes
    private void resize()
    {
    	int oldM = M;
    	M *= 2;
    	System.out.println("\t\tSize -- " + N + " resizing array from " + oldM + " to " + M);
        Pair<V>[] temp = table;						//store old table in temp
        table = (Pair<V>[]) new Pair<?>[M];			//make a new array of double the size
        for (int i = 0; i < oldM; i++) {			//look for real objects
            if (temp[i] != null) {
                reinsert(temp[i]);					//rehash into the new table
            }
        }

    }

    //recursively sorts a list of doubly linked nodes
	//returns a reference to the new head of the list
    private Pair<V> mergesort(Pair<V> list, int size)
    {
    	if(size < 1)
    		return list;
    	if(size == 1)
    	{
    		list.previous = null;
    		list.next = null;
    		return list;	//list is already sorted, recursive base case
    	}
    	Pair<V> rightList = getPairAt(list, size/2);		//find list midpoint via sequential access
    	list = mergesort(list, size/2);						//sort the left half
    	rightList = mergesort(rightList, (size - size/2));	//sort the right half
    	list = merge(list, rightList);						//merge
    	return list;
    }

	//it puts the merge in mergesort
    @SuppressWarnings("unchecked")
    private Pair<V> merge(Pair<V> left, Pair<V> right)
    {
    	Pair<V> newHead, current;
    	Comparable leftValue = (Comparable)left.value;
    	Comparable rightValue = (Comparable)right.value;
    	newHead = leftValue.compareTo(rightValue) <= 0 ? left : right;	//save pointer to the new front
    	current = newHead;	//to walk through and build the list
    	if(newHead == left)
    		left = left.next;
    	else
    		right = right.next;
    	while(left != null && right != null)
    	{
    		leftValue = (Comparable)left.value;
    		rightValue = (Comparable)right.value;
    		if (leftValue.compareTo(rightValue) <= 0)
    		{
    			current.next = left;
    			left.previous = current;
    			left = left.next;
    		}
    		else
    		{
    			current.next = right;
    			right.previous = current;
    			right = right.next;
    		}
    		current = current.next;
    	}
    	while(left != null)		//consume remaining nodes on left, if any
    	{
			current.next = left;
    		left.previous = current;
    		current = current.next;
    		left = left.next;
    	}
    	while(right != null)	//consume remaining nodes on right, if any
    	{
    		current.next = right;
    		right.previous = current;
    		current = current.next;
    		right = right.next;
    	}
    	return newHead;
    }

    //helper method called by mergesort to get a Pair node at logical list[index] from curr
    private Pair<V> getPairAt(Pair<V> curr, int index)
    {
    	for(int i = 0; i < index; i++)
    		curr = curr.next;
    	return curr;
    }

    //this class acts both as a (key,value) pair and as a Node in a doubly-linked list
    //using the 2-way links makes it easier to maintain links when using unset() from the hash table
	public static class Pair<V> {

		public String key;
		public V value;
		private Pair<V> next, previous;

		public Pair()
		{
			this(null, null);
		}

		public Pair(String newKey, V initialValue)
		{
			key = newKey;
			value = initialValue;
			next = null;
			previous = null;
		}

		public String toString()
		{
			return ("Key: " + key + " Value: " + value);
		}

	}

	public Iterator<V> iterator()
	{
		return new Iterator<V>()
		{
			Pair<V> current = root;
			public boolean hasNext()
			{
				return current != null && N > 0;
			}
			public V next()
			{
				if(current == null)
					throw new NoSuchElementException();
				else
				{
					V v = current.value;
					current = current.next;
					return v;
				}
			}
		};
	}
}
