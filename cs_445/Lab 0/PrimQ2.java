public class PrimQ2<T> implements SimpleQueue<T>, Moves {

    private T[] array;
    private int moves;
    private int count;

    public PrimQ2 (int size)
    {
        array = (T[])(new Object[size]);
        count = 0;
    }

    public boolean offer(T element)  // or add() or enqueue()
    {
        if(element == null)
            return false;

        if (count == array.length)
            array = upsize(array);

        array[count++] = element;
        moves++;
        return true;
    }

    // remove the element at the logical front of the Queue
    // return the element or null if the Queue is empty
    public T poll()  // or remove() or dequeue()
    {
        if (count == 0)
            return null;
        else
        {
            T ele = array[0];
            for(int i = 1; i < count; i++)
            {
                array[i - 1] = array[i];
                moves++;
            }
            count--;
            moves++;
            return ele;
        }
    }

    // get and return element at logical front of the Queue
    // do not remove the element
    // return null if Queue is empty
    public T peek() // or getFront()
    {
        if (count == 0)
            return null;
        else
            return array[0];
    }

    // return true if Queue is empty; false otherwise
    public boolean isEmpty()
    {
        return (count == 0);
    }

    //helper method to enlarge array when full
    private T[] upsize(T[] small)
    {
        T[] big = (T[])(new Object[small.length * 2]);
        for(int i = 0; i < small.length; i++)
            big[i] = small[i];
        return big;
    }

    // clear all contents from Queue and set to empty
    public void clear()
    {
        count = 0;
    }

    // return the value of the moves variable
    public int getMoves()
    {
        return moves;
    }

    // initialize the moves variable to val
	public void setMoves(int val)
    {
        moves = val;
    }
}
