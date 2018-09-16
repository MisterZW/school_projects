/**
	CS 0445 Spring 2018
	I have taken the text author's iterative and recursive versions of Insertion
	Sort and put them together in this handout.  I changed the name of the
	recursive version of insertInOrder to insertInOrderRec to distinguish it from
	the iterative version.
*/
public class Insertion
{
	// Methods for iterative Insertion Sort
   	public static <T extends Comparable<? super T>>
					void insertionSort(T[] a, int n)
	{
		insertionSort(a, 0, n - 1);
	} // end insertionSort

	public static <T extends Comparable<? super T>>
	       void insertionSort(T[] a, int first, int last)
	{
		for (int unsorted = first + 1; unsorted <= last; unsorted++)
		{
			T firstUnsorted = a[unsorted];
			insertInOrder(firstUnsorted, a, first, unsorted - 1);
		} // end for
	} // end insertionSort

	private static <T extends Comparable<? super T>>
	        void insertInOrder(T anEntry, T[] a, int begin, int end)
	{
		int index = end;
		while ((index >= begin) && (anEntry.compareTo(a[index]) < 0))
		{
			a[index + 1] = a[index]; // Make room
			index--;
		} // end for

		// Assertion: a[index + 1] is available
		a[index + 1] = anEntry;  // Insert
	} // end insertInOrder


	// Recursive InsertionSort
	public static <T extends Comparable<? super T>>
					void insertionSort2(T[] a, int n)
	{
		insertionSort2(a, 0, n - 1);
	} // end insertionSort2

	public static <T extends Comparable<? super T>>
			void insertionSort2(T[] a, int first, int last)
	{
		if (first < last)  // As long as at least two locations remain
		{
			insertionSort2(a, first, last - 1);         // Sort all but last item
			insertInOrderRec(a[last], a, first, last - 1); // Insert last item in sorted order
		} // end  if
	} // end insertionSort2

	// Inserts element into the sorted array elements a[begin] through a[end].
	private static <T extends Comparable<? super T>>
           void insertInOrderRec(T element, T[] a, int begin, int end)
	{
		if (element.compareTo(a[end]) >= 0) // base case item is greater than or
											// equal to the one to its left so
											// place it
			a[end + 1] = element;
		else if (begin < end)	// recursive case -- item is less than one to its
								// left and at least two values remain in the array
								// so shift and recurse
		{
			a[end + 1] = a[end];
			insertInOrderRec(element, a, begin, end - 1);
		}
		else	// base case -- item is less than one to its left and only one value
				// remains in the array.  Shift the last value and place the item
		{
			a[end + 1] = a[end];
			a[end] = element;
		} // end if
	} // end insertInOrderRec

} // end Insertion
