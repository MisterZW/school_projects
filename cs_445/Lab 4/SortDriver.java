import java.util.Random;

public class SortDriver{

    Integer[] randArray;
    Integer[] randArray2;
    Random r;
    int n;
    long start, finish, elapsed;
    double elapsedSeconds;
    final static double NANO_CONVERSION = 1000000000.0;

    public static void main(String[] args){
        new SortDriver(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }

    public SortDriver(int problemSize, int minSize)
    {
        n = problemSize;
        r = new Random();
        TextMergeQuick.setMin(minSize);
        initializeArrays();

        System.out.println("MERGE sorting the array...\n\n");
        start = System.nanoTime();
        TextMergeQuick.mergeSort(randArray, n);
        finish = System.nanoTime();
        elapsed = finish - start;
        elapsedSeconds = elapsed / NANO_CONVERSION;
        System.out.println("TIME ELAPSED FOR MERGESORT: " + elapsed);
        System.out.println("SECONDS ELAPSED FOR MERGESORT: " + elapsedSeconds + "\n\n");

        System.out.println("QUICKsort base is " + TextMergeQuick.MIN_SIZE);
        System.out.println("QUICK sorting the array...\n\n");
        start = System.nanoTime();
        TextMergeQuick.quickSort(randArray2, n);
        finish = System.nanoTime();
        elapsed = finish - start;
        elapsedSeconds = elapsed / NANO_CONVERSION;
        System.out.println("TIME ELAPSED FOR QUICKSORT: " + elapsed);
        System.out.println("SECONDS ELAPSED FOR QUICKSORT: " + elapsedSeconds + "\n\n");

    }

    private void displayStatus()
    {
        System.out.println();
        System.out.println("Random array " + isSorted(randArray));
        System.out.println("Random array 2 " + isSorted(randArray2));
        System.out.println();
    }

    private void printArray(Object[] array)
    {
        for(Object item : array)
        {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    private <T extends Comparable<? super T>> String isSorted(T[] array)
    {
        if(Sorts.checkIfSorted(array))
            return "is sorted.";
        else
            return "is NOT sorted";
    }

    private void initializeArrays()
    {
        randArray = new Integer[n];
        randArray2 = new Integer[n];
        for (int i = 0; i < n; i++)
        {
            Integer x = r.nextInt();
            randArray[i] = x;
            randArray2[i] = x;
        }
    }

    // Reorganize the items in the queue in a pseudo-random way.
    public void shuffle(Object[] array)
    {
        for(int i = 0; i < array.length; i++)
        {
            int index = r.nextInt(array.length - i);
            moveToFront(array, index + i, i);
        }
    }

    //helper method for shuffle() -- swaps values in the queue
    private void moveToFront(Object[] array, int start, int finish)
    {
        Object itemA = array[start];
        Object itemB = array[finish];
        array[finish] = itemA;
        array[start] = itemB;
    }
}
