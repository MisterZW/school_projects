// CS 0445 Spring 2018
// Assignment 1 MyQ<T> interface
// Carefully read the specifications for each of the operations and
// implement them correctly in your RandIndexQueue<T> class.

// The overall logic of the MyQ<T> is the following:
//		Data is logically "added" in the same order that it is "removed".
// The interface itself does not specify in any way how the data must be
// stored / maintained.  Thus it could be implemented in many different
// ways.

// However, for this assignment, you MUST implement the interface with the
// following requirements: 
// 1) The underlying data must be a simple Java array
// 2) No Java collection classes may be used in this implementation.  All of
//    your methods must act directly on the underlying array
// 3) Your offer() and poll() methods must require only a single data assignment
//    each.  In other words, there should be no shifting to create or to fill
//    a space in your array.  To see how to implement your queue in this way,
//    go to recitation and read Section 11.7-11.16 of your text. You are not
//    required to use this exact implementation but it should be similar.
// 4) The offer() method must always succeed (barring some extreme event).  This
//    means that your implementation must have a way to dynamically resize your
//    underlying array when necessary.  Be careful about resizing -- it should not
//    affect the logical ordering of the MyQ.
// 5) Since offer() should always succeed, isFull() should always return false.
//
//    Note: Do not count the moves necessary for resizing in your totals.  We
//    will discuss the overhead of resizing the array later on in lecture.

public interface MyQ<T>
{
	// Add a new Object to the MyQ in the next available location.  If
	// all goes well, return true; otherwise return false.
	public boolean offer(T item);
	
	// Remove and return the logical front item in the MyQ.  If the MyQ
	// is empty, return null
	public T poll();
	
	// Get and return the logical front item in the MyQ without removing it.
	// If the MyQ is empty, return null
	public T peek();
	
	// Return true if the MyQ is full, and false otherwise
	public boolean isFull();
	
	// Return true if the MyQ is empty, and false otherwise
	public boolean isEmpty();
	
	// Return the number of items currently in the MyQ.  Determining the
	// length of a queue can sometimes very useful.
	public int size();

	// Reset the MyQ to empty status by reinitializing the variables
	// appropriately
	public void clear();
	
	// Methods to get and set the value for the moves variable.  The idea for
	// this is the same as shown in Recitation Exercise 1 -- but now instead
	// of a separate interface these are incorporated into the MyQ<T>
	// interface.  The value of your moves variable should be updated during
	// an offer() or poll() method call.  However, any data movement required
	// to resize the underlying array should not be counted in the moves.
	public int getMoves();
	public void setMoves(int moves);
}