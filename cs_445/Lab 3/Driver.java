/**
*   This is CS 445 Lab Assignment 3
*   It compares the performance of recursive and iterative Insertion sort implementations
*   Zachary Whitney     zdw9       3320178
*/

import java.util.Random;

public class Driver{

    Integer[] array1, array2;
    Random r;
    int size;
    long before, after, elapsed;

    public Driver(int asize)
    {
        size = asize;
        r = new Random();
        initializeArrays();

        //Iterative sort
        before = System.nanoTime();
        Insertion.insertionSort(array1, size);
        after = System.nanoTime();
        elapsed = after - before;
        System.out.println("Iterative sort took " + elapsed + " nanoseconds.");
        elapsed /= 1000000000;
        System.out.println("Iterative sort took " + elapsed + " seconds.");

        System.out.println();

        //Recursive sort
        before = System.nanoTime();
        Insertion.insertionSort2(array2, size);
        after = System.nanoTime();
        elapsed = after - before;
        System.out.println("Recursive sort took " + elapsed + " nanoseconds.");
        elapsed /= 1000000000;
        System.out.println("Recursive sort took " + elapsed + " seconds.");
    }

    public static void main(String[] args)
    {
        int n = 0;
        try
        {
            n = Integer.parseInt(args[0]);
        }
        catch(Exception e)
        {
            System.out.println("Usage: java Driver [# of entries to sort]");
            System.exit(0);
        }
        new Driver(n);
    }

    //both arrays contain the same objects
    private void initializeArrays()
    {
        array1 = new Integer[size];
        array2 = new Integer[size];
        for(int i = 0; i < size; i++)
        {
            Integer item = r.nextInt();
            array1[i] = item;
            array2[i] = item;
        }
    }
}
