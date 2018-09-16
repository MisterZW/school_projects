// CS 0445 Spring 2018
// Assig1A driver program.  This program must work as is with your
// RandIndexQueue<T> class.  Look carefully at all of the method calls so that
// you create your RandIndexQueue<T> methods correctly.  For example, note the
// constructor calls and the toString() method call.  The output should
// be identical to my sample output, with the exception of the result of
// the shuffle() method -- since this should be random yours should not
// match mine.
public class Assig1
{
	public static void main(String [] args)
	{
		// Testing constructor
		MyQ<Integer> theQ = new RandIndexQueue<Integer>(4);

		// Testing offer
		theQ.setMoves(0);
		for (int i = 0; i < 9; i++)
		{
			Integer newItem = Integer.valueOf(i);
			if (!(theQ.isFull()))
			{
				theQ.offer(newItem);
				System.out.println(newItem + " added to Q");
			}
			else
			{
				System.out.println("No room for " + newItem);
			}
		}

		// The moves should be tracked in the same way that you tracked them
		// in your recitation exercise.  Note, however, that in this implementation
		// the number of moves needed for both offer() and poll() should only be
		// 1 per operation.  See more on this requirement in the MyQ.java comments
		// and see the output in A1Out.txt
		int mv = theQ.getMoves();
		System.out.println("Total moves needed: " + mv);
		System.out.println();

		// Testing poll
		theQ.setMoves(0);
		while (!(theQ.isEmpty()))
		{
			Integer oldItem = theQ.poll();
			System.out.println(oldItem + " retrieved from Q");
		}
		System.out.println("Total moves needed: " + mv);
		System.out.println();

		Integer noItem = theQ.poll();
		if (noItem == null)
			System.out.println("Nothing in the Q");

		// Testing array management
		int count = 1;
		MyQ<String> theQ2 = new RandIndexQueue<String>(5);
		String theItem = new String("Item " + count);
		System.out.println("Adding " + theItem);
		theQ2.offer(theItem);
		for (int i = 0; i < 8; i++)
		{
			count++;
			theItem = new String("Item " + count);
			System.out.print("Adding " + theItem);
			theQ2.offer(theItem);
			theItem = theQ2.poll();
			System.out.println("...and removing " + theItem);
		}
		int sz = theQ2.size();
		System.out.println("There are " + sz + " items in the buffer\n");

		// This code will test the toString() method, the Shufflable interface
		// and the Indexable interface.
		System.out.println("Initializing a new RandIndexQueue...");
		RandIndexQueue<Integer> newData = new RandIndexQueue<Integer>(15);
		for (int i = 0; i < 8; i++)
		{
			newData.offer(Integer.valueOf(i));
		}

		// The toString() method for your RandIndexQueue<T> should produce
		// a String containing the contents of your collection from logical
		// front (of the Queue) to the logical end (of the Queue).  Note that
		// this will not necessarily be the beginning to the end of the
		// underlying array.
		System.out.println(newData.toString());

		System.out.println("Removing 3 items then adding 1");
		Integer bogus = newData.poll();
		bogus = newData.poll();
		bogus = newData.poll();
		newData.offer(Integer.valueOf(8));
		System.out.println(newData.toString());
		System.out.println();

		// Testing Indexable<T> interface
		iterate(newData);
		iterate(newData);

		System.out.println(newData.toString());

		System.out.println("\nAbout to test Shufflable...");
		newData.clear();
		for (int i = 0; i < 15; i++)
		{
			newData.offer(Integer.valueOf(i));
		}
		System.out.println(newData.toString());
		System.out.println("Shuffling...");
		newData.shuffle();
		System.out.println(newData.toString());
		System.out.println("Removing 2 and adding 1");
		bogus = newData.poll();
		bogus = newData.poll();
		newData.offer(Integer.valueOf(22));
		System.out.println(newData.toString());
		System.out.println("Shuffling again...");
		newData.shuffle();
		System.out.println(newData.toString());
	}

	// Generic method to test the Indexable interface
	public static <T> void iterate(Indexable<T> R)
	{
		System.out.println("Testing Indexable...");
		for (int i = 0; i < R.size()-1; i++)
		{
			T item1 = R.get(i);
			int j = i + 1;
			T item2 = R.get(j);
			System.out.println("Swapping " + item1 + " and " + item2);
			R.set(i, item2);
			R.set(j, item1);
		}
		System.out.println();

		
	}
}
