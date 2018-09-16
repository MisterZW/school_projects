//Zachary Whitney
//CS 445 Assignment 4
//Pitt ID: zdw9  Student ID: 3320178
//TH 1PM Lecture, 10AM Tues Recitation
//April 11, 2018

package MyTreePackage;
import StackAndQueuePackage.*;
import java.util.*;

public class ComparableBinaryTree<T extends Comparable<? super T>>
    extends BinaryTree<T> implements ComparableTreeInterface<T> {

    public ComparableBinaryTree()
    {
        super();
    }

    public ComparableBinaryTree(T rootData)
    {
        super(rootData);
    }

    public ComparableBinaryTree(T rootData, BinaryTree<T> leftTree,
                                  BinaryTree<T> rightTree)
    {
        super(rootData, leftTree, rightTree);
    }

    // If the tree is not empty, return the maximum
    // value in the tree; otherwise return null
    public T getMax()
    {
        BinaryNode<T> rootNode = this.getRootNode();
        if (rootNode == null)
            return null;
        else
            return getMaxRecurse(rootNode);
    }

    //recursively determines max element of the tree by comparing will all
    //elements in the tree
    private T getMaxRecurse(BinaryNode<T> current)
    {
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        T max = current.getData();
        T recursiveMax;
        if(left != null)
        {
            recursiveMax = getMaxRecurse(left);
            if(max.compareTo(recursiveMax) < 0)
                max = recursiveMax;
        }
        if(right != null)
        {
            recursiveMax = getMaxRecurse(right);
            if(max.compareTo(recursiveMax) < 0)
                max = recursiveMax;
        }
        return max;
    }

    // If the tree is not empty, return the minimum
    // value in the tree; otherwise return null
    public T getMin()
    {
        BinaryNode<T> rootNode = this.getRootNode();
        if (rootNode == null)
            return null;
        else
            return getMinRecurse(rootNode);
    }

    //recursively determines min element of the tree by comparing will all
    //elements in the tree
    private T getMinRecurse(BinaryNode<T> current)
    {
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        T min = current.getData();
        T recursiveMin;
        if(left != null)
        {
            recursiveMin = getMinRecurse(left);
            if(min.compareTo(recursiveMin) > 0)
                min = recursiveMin;
        }
        if(right != null)
        {
            recursiveMin = getMinRecurse(right);
            if(min.compareTo(recursiveMin) > 0)
                min = recursiveMin;
        }
        return min;
    }

    // Return true if BST, else false
    // Null parameters represent that there is no max or min
    // until one is set by a further recursive call
    public boolean isBST()
    {
        return isBSTRecurse(this.getRootNode(), null, null);
    }

    //checks if values are nondecreasing recursively
    //when recursing left, disallow values larger than the current by passing data as max
    //when recursing right, disallow smaller values than data by passing data as min
    //returns true iff no violations are found
    private boolean isBSTRecurse(BinaryNode<T> current, T min, T max)
    {
        if (current == null)    //empty tree is a BST
            return true;
        T data = current.getData();
        if((min != null && data.compareTo(min) < 0) ||
            (max != null && data.compareTo(max) > 0))
            return false;
        return isBSTRecurse(current.getLeftChild(), min, data) &&
            isBSTRecurse(current.getRightChild(), data, max);
    }

     // Return the rank of data in the tree with
    // 0 being the smallest answer and N being the largest answer
    // (if data is greater than all of the items in the tree).
    // If there are duplicates, the rank of data should be minimized.
    // data does not actually have to be present in the tree.
     public int rank(T data)
     {
        if(this.getRootNode() == null)
            return 0;
        else
            return rankRecursive(data, this.getRootNode());
     }

    //Recursively determines the rank of key in the tree by comparing it to each
    //element in the tree -- must check each element because data is not guaranteed
    //to be sorted
    private int rankRecursive(T key, BinaryNode<T> current)
    {
        int value = 0;
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        if(key.compareTo(current.getData()) > 0)
            value++;
        if(right != null)
            value += rankRecursive(key, right);
        if(left != null)
            value += rankRecursive(key, left);
        return value;
    }

    // Return the value in the tree with rank equal
    // to i. i should be in range 0 to N-1 (where the N is the number
    // of nodes in the tree). If i is out of range, throw an
    // IndexOutofBoundsException.
    public T get(int i)
    {
        BinaryNode<T> rootNode = this.getRootNode();
        if(i < 0 || rootNode == null)
            throw new IndexOutOfBoundsException();
        List<T> values = new ArrayList<T>();
        buildDataArray(rootNode, values);
        if(i >= values.size())
            throw new IndexOutOfBoundsException();
        Collections.sort(values);
        return values.get(i);
    }

    //InOrder recursive traversal builds an ArrayList of T from the Binary Tree
    private void buildDataArray(BinaryNode<T> current,
        List<T> result)
    {
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        if(left != null)
            buildDataArray(left, result);
        result.add(current.getData());
        if(right != null)
            buildDataArray(right, result);
    }
 }
