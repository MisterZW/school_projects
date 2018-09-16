/**
*   This class is a simple implementation of a circular array-based queue.
*   @ author    Zachary Whitney     <zdw9@pitt.edu>     id: 3320178
*   Date: January 22, 2018
*   Assignment 1, CS 0445, Ramirez TH 1PM, Recitation T 10AM
*/

import java.util.Random;

public class RandIndexQueue<T> implements MyQ<T>, Shufflable, Indexable<T>
{
    private int front, back, move, count;
    private T[] data;

    //defer to the full constructor
    public RandIndexQueue()
    {
        this(10);
    }

    @SuppressWarnings("unchecked")  //safe cast -- new empty array
    public RandIndexQueue(int capacity)
    {
        front = 0;
        back = 0;
        move = 0;
        count = 0;
        data = (T[])(new Object[capacity]);
    }

    //prints the queue values in logical order
    public String toString()
    {
        StringBuilder value = new StringBuilder("Contents: ");
        for(int i = 0; i < this.size(); i++)
        {
            value.append(data[(front + i) % data.length] + " ");
        }
        return value.toString();
    }

    //Add a new Object to the MyQ in the next available location.  If
	// all goes well, return true; otherwise return false.
	public boolean offer(T item)
    {
        if(item == null)
        {
            return false;
        }
        else
        {
            data[back++] = item;
            back %= data.length;
            move++;
            count++;
            if(count == data.length)
                data = upsize(data);
            return true;
        }
    }

	// Remove and return the logical front item in the MyQ.  If the MyQ
	// is empty, return null
	public T poll()
    {
        if(this.isEmpty())
            return null;
        else
        {
            T item = data[front];
            data[front++] = null;    //avoids keeping live references to trash
            front %= data.length;
            move++;
            count--;
            return item;
        }
    }

	// Get and return the logical front item in the MyQ without removing it.
	// If the MyQ is empty, return null
	public T peek()
    {
        if(this.isEmpty())
            return null;
        else
            return data[front];
    }

	// Always returns false because queue dynamically resizes
	public boolean isFull()
    {
        return false;
    }

	// Return true if the MyQ is empty, and false otherwise
	public boolean isEmpty()
    {
        return count == 0;
    }

	// Return the number of items currently in the MyQ.
	public int size()
    {
        return count;
    }

	// Reset the MyQ to empty status by reinitializing the variables
	// appropriately
	public void clear()
    {
        while(!this.isEmpty())  //make sure elements are set null (avoid leak)
        {
            this.poll();
        }
    }

    // Returns a new T[] array with double the capacity of the incoming array
    public T[] upsize(T[] oldArray)
    {
        @SuppressWarnings("unchecked")  //safe cast b/c array is empty
        T[] newArray = (T[])(new Object[oldArray.length * 2]);
        for(int i = 0; i < count; i++)
        {
            newArray[i] = oldArray[(front + i) % oldArray.length];
        }
        front = 0;
        back = count;
        return newArray;
    }

	// Method to get the value for the moves variable.
	public int getMoves()
    {
        return move;
    }
    // Method to set the value for the moves variable.
	public void setMoves(int moves)
    {
        move = moves;
    }

    // Get and return the value located at logical location i in the implementing
	// collection, where location 0 is the logical beginning of the collection.
	// If the collection has fewer than (i+1) items, throw an IndexOutOfBoundsException
	public T get(int i)
    {
        if(this.size() < i + 1)
            throw new IndexOutOfBoundsException(i);
        else
            return data[(front + i) % data.length];
    }

	// Assign item to logical location i in the implementing collection, where location
	// 0 is the logical beginning of the collection.  If the collection has fewer than
	// (i+1) items, throw an IndexOutOfBoundsException
	public void set(int i, T item)
    {
        if(this.size() < i + 1)
            throw new IndexOutOfBoundsException(i);
        else if(item == null)
            return;
        else
            data[(front + i) % data.length] = item;
    }

    // Reorganize the items in the queue in a pseudo-random way.
    public void shuffle()
    {
        Random generator = new Random();
        for(int i = 0; i < count; i++)
        {
            int index = generator.nextInt(count - i);
            this.moveToFront(index + i, i);
        }
    }

    //helper method for shuffle() -- swaps values in the queue
    private void moveToFront(int start, int finish)
    {
        T itemA = this.get(start);
        T itemB = this.get(finish);
        this.set(finish, itemA);
        this.set(start, itemB);
    }
}
