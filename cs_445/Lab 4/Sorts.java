public class Sorts{

    //////SELECTION SORT METHODS///////
    public static <T extends Comparable<? super T>> void selectionSort(T[] array)
    {
        selectionSort(array, 0, array.length - 1);
    }

    public static <T extends Comparable<? super T>> void selectionSort(
        T[] array, int first, int last)
    {
        if(array == null)
            throw new NullPointerException();
        for(int i = first; i < last; i++)
        {
            int index = findMinIndex(array, i, last);
            swap(array, i, index);
        }
    }

    //helper for selectionSort
    private static <T extends Comparable<? super T>> int findMinIndex(
        T[] array, int first, int last)
    {
        assert array.length > 0;
        T item = array[first];
        int min = first;
        for(int i = first + 1; i <= last; i++)
        {
            if(item.compareTo(array[i]) > 0)
            {
                min = i;
                item = array[i];
            }
        }
        return min;
    }

    ////////INSERTION SORT METHODS//////////

    public static <T extends Comparable<? super T>> void insertionSort(T[] array)
    {
        insertionSort(array, 0, array.length - 1);
    }

    public static <T extends Comparable<? super T>> void insertionSort(T[] array,
        int first, int last)
    {
        if(array == null)
            throw new NullPointerException();
        for(int i = first + 1; i <= last; i++)
            insertInOrder(array, first, i - 1, array[i]);
    }

    private static <T extends Comparable<? super T>> void insertInOrder(T[] array,
        int first, int last, T item)
    {
        int index = last;
        while (index >= first && item.compareTo(array[index]) < 0)
        {
            array[index + 1] = array[index];
            index--;
        }
        array[index + 1] = item;
    }

    ///////MERGESORT METHODS///////

    public static <T extends Comparable<? super T>> void mergeSort(T[] array)
    {
        @SuppressWarnings("unchecked")
        T[] temporary = (T[]) new Comparable<?>[array.length];
        mergeSort(array, 0, array.length - 1, temporary);
    }

    public static <T extends Comparable<? super T>> void mergeSort(T[] array,
        int first, int last)
    {
        @SuppressWarnings("unchecked")
        T[] temporary = (T[]) new Comparable<?>[array.length];
        mergeSort(array, first, last, temporary);
    }

    private static <T extends Comparable<? super T>> void mergeSort(T[] array,
        int first, int last, T[] temp)
    {
        if(first < last)
        {
            int mid = ((last - first) / 2) + first; //calculate midpoint w/o overflow
            mergeSort(array, first, mid, temp);       //sort left
            mergeSort(array, mid + 1, last, temp);    //sort right
            merge(array, first, mid, last, temp);     //merge both
        }
    }

    private static <T extends Comparable<? super T>> void merge(T[] array,
        int left, int mid, int right, T[] temp)
    {
        int first = left;       //need left to store for copying back from temp
        int second = mid + 1;   //to trace the right subarray
        int index = left;       //to trace where to place the next element
        while(first <= mid && second <= right)  //merge while both non-empty
        {
            if(array[first].compareTo(array[second]) <= 0)
                temp[index++] = array[first++];
            else
                temp[index++] = array[second++];
        }
        while(first <= mid)                     //consume first array
            temp[index++] = array[first++];
        while(second <= right)                  //consume second array
            temp[index++] = array[second++];
        for(int i = left; i <= right; i++)      //copy temp back to array
            array[i] = temp[i];
    }

    ///////QUICKSORT METHODS///////

    public static <T extends Comparable<? super T>> void quickSort(T[] array)
    {
        quickSort(array, 0, array.length - 1);
    }

    public static <T extends Comparable<? super T>> void quickSort(T[] array,
        int first, int last)
    {
        if(first < last)
        {
            //uses a random pivot
            int pivotIndex = (int)(Math.random() * (last - first + 1)) + first;
            pivotIndex = partition(array, first, last, pivotIndex);
            quickSort(array, first, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, last);
        }
    }

    private static <T extends Comparable<? super T>> int partition(T[] array,
        int first, int last, int pivotStartIndex)
    {
        T pivot = array[pivotStartIndex];
        swap(array, last, pivotStartIndex);
        int right = last - 1;
        int left = first;
        while(true)
        {
            while(left < last && pivot.compareTo(array[left]) > 0)
                left++;
            while(right > first && pivot.compareTo(array[right]) < 0)
                right--;
            if(left < right)
            {
                swap(array, left, right);
                left++;
                right--;
            }
            else
                break;
        }
        swap(array, left, last);    //put the pivot back in the right spot
        return left;
    }

    public static <T extends Comparable<? super T>> boolean checkIfSorted(T[] array)
    {
        if(array == null)
            throw new NullPointerException();
        for(int i = 0; i < array.length - 1; i++)
        {
            if(array[i].compareTo(array[i + 1]) > 0)
                return false;
        }
        return true;
    }

    //helper for many sorts
    private static void swap(Object[] array, int first, int second)
    {
        Object temp = array[first];
        array[first] = array[second];
        array[second] = temp;
    }
}
