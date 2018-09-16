// CS 0445 Spring 2018
//
// Testing of simple queue implementations.
// This program should run without changes with your PrimQ1 and PrimQ2 classes
import java.util.*;

public class CS445Rec1
{
	public static void main(String [] args)
	{
		// Create ArrayLists of interface types
		ArrayList<SimpleQueue<Integer>> kyews = new ArrayList<SimpleQueue<Integer>>();
		ArrayList<Moves> moo = new ArrayList<Moves>();
		
		// Add an instance of each Q type to each ArrayList.  Note that we only
		// create two objects here -- each object is being accessed (in turn) as
		// a SimpleQueue<T> and as a Moves, with each interface giving access to
		// only the methods defined therein.
		
		// Important Note: The 1 being passed into the constructor is the initial
		// size of the underlying array.  Once the array fills you should resize
		// the underlying array by doubling it.  This should be done transparently
		// so that the user does not even realize that a new array is being
		// created. If you have trouble with this see your TA and look at the 
		// dynamic Bag implementation in the CS 0445 text.
		kyews.add(new PrimQ1<Integer>(1));  moo.add((Moves) kyews.get(0));
		kyews.add(new PrimQ2<Integer>(1));	moo.add((Moves) kyews.get(1));
	
		// Set moves for each object to 0
		for (Moves m: moo)
			m.setMoves(0);
		
		System.out.print("Adding to Queues: ");	
		for (int i = 0; i < 10; i++)
		{
			Integer X = new Integer(i);
			System.out.print(X + " ");
			for (SimpleQueue<Integer> Q: kyews)
			{
				Q.offer(X);
			}
		}
		System.out.println();
		
		for (int j = 0; j < moo.size(); j++)
		{
			System.out.println("Queue " + j + " moves: " + moo.get(j).getMoves());
		}
		System.out.println();

		for (Moves m: moo)
			m.setMoves(0);
		
		// Note the syntax below.  An assignment is being made using cascading
		// method calls.  
		Integer temp;
		for (int j = 0; j < kyews.size(); j++)
		{
			System.out.print("Removing from Queue " + j + ": ");
			while ((temp = kyews.get(j).poll()) != null)
				System.out.print(temp + " ");
			System.out.println();
		}
		System.out.println();
			
		for (int j = 0; j < moo.size(); j++)
		{
			System.out.println("Queue " + j + " moves: " + moo.get(j).getMoves());
		}

	}
}
		