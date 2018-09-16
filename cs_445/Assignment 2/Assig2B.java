import java.util.*;

// CS 0445 Spring 2018
// Assignment 2 Part B
// Zachary Whitney, zdw9, ID# 3320178
// Ramirez lecture T TH 1PM, Recitation T 10AM

public class Assig2B {

    public static void main(String[] args)
    {
        long start, end;        //to hold nanoTime()
        int n = 0;              //default # of tests
        final char c = 'A';     //test character

        try
        {
            n = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e)
        {
            System.out.println("Usage: java Assig2B [# of operations]");
            System.exit(0);
        }

        String s = new String();
        StringBuilder sb = new StringBuilder();
        MyStringBuilder msb = new MyStringBuilder();
        LinkedList<Character> ll = new LinkedList<Character>();

        //APPEND

        System.out.println("Testing append(char c) method...\n");
        System.out.println("Testing String class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            s += c;
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing StringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            sb.append(c);
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing MyStringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            msb.append(c);
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing LinkedList class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            ll.offerLast(c);
        }
        end = System.nanoTime();
        showResults(start, end, n);

        //DELETE

        System.out.println("Testing delete(int start, int stop) method...\n");
        System.out.println("Testing String class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            s = s.substring(1, s.length());
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing StringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            sb.delete(0, 1);
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing MyStringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            msb.delete(0, 1);
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Testing LinkedList class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            ll.poll();
        }
        end = System.nanoTime();
        showResults(start, end, n);

        System.out.println("Verifying operations -- all containers should be empty...");
        System.out.println("String contains " + s);
        System.out.println("StringBuilder contains " + sb);
        System.out.println("MyStringBuilder contains " + msb);
        System.out.println("LinkedList contains " + ll);

        System.out.println("\n");
        //INSERT

        System.out.println("Testing insert(int loc, char c) method...\n");
        int target = 0;
        System.out.println("Testing String class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            s = stringInsert(s, target, c);
            target = s.length() / 2;
        }
        end = System.nanoTime();
        showResults(start, end, n);

        target = 0;
        System.out.println("Testing StringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            sb.insert(target, c);
            target = sb.length() / 2;
        }
        end = System.nanoTime();
        showResults(start, end, n);

        target = 0;
        System.out.println("Testing MyStringBuilder class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            msb.insert(target, c);
            target = msb.length() / 2;
        }
        end = System.nanoTime();
        showResults(start, end, n);

        target = 0;
        System.out.println("Testing LinkedList class");
        start = System.nanoTime();
        for(int i = 0; i < n; i++)
        {
            ll.add(target, c);
            target = ll.size() / 2;
        }
        end = System.nanoTime();
        showResults(start, end, n);
    }

    private static void showResults(long start, long end, int n)
    {
        long elapsed = end - start;
        long average = elapsed / n;
        System.out.printf("\ttotal time elapsed ----> %12d  nanoseconds\n", elapsed);
        System.out.printf("\ttime per instruction --> %12d  nanoseconds\n", average);
        System.out.println("\n");
    }

    private static String stringInsert(String input, int index, char x)
    {
        if(input.length() <= 1)
            input += x;
        else
            input = (input.substring(0, index) + x + input.substring(index, input.length()));
        return input;
    }
}
