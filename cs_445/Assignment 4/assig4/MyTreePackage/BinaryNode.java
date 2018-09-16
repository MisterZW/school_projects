//Zachary Whitney
//CS 445 Assignment 4
//Pitt ID: zdw9  Student ID: 3320178
//TH 1PM Lecture, 10AM Tues Recitation
//April 11, 2018

package MyTreePackage;
/**
   A class that represents nodes in a binary tree.

   @author Frank M. Carrano
   @author Timothy M. Henry
   @version 4.0
*/
public class BinaryNode<T>
{
   private T data;
   private BinaryNode<T> leftChild;  // Reference to left child
   private BinaryNode<T> rightChild; // Reference to right child

   public BinaryNode()
   {
      this(null); // Call next constructor
   } // end default constructor

   public BinaryNode(T dataPortion)
   {
      this(dataPortion, null, null); // Call next constructor
   } // end constructor

   public BinaryNode(T dataPortion, BinaryNode<T> newLeftChild,
                                    BinaryNode<T> newRightChild)
   {
      data = dataPortion;
      leftChild = newLeftChild;
      rightChild = newRightChild;
   } // end constructor

   /** Retrieves the data portion of this node.
       @return  The object in the data portion of the node. */
   public T getData()
   {
      return data;
   } // end getData

   /** Sets the data portion of this node.
       @param newData  The data object. */
   public void setData(T newData)
   {
      data = newData;
   } // end setData

   /** Retrieves the left child of this node.
       @return  The node’s left child. */
   public BinaryNode<T> getLeftChild()
   {
      return leftChild;
   } // end getLeftChild

   /** Sets this node’s left child to a given node.
       @param newLeftChild  A node that will be the left child. */
   public void setLeftChild(BinaryNode<T> newLeftChild)
   {
      leftChild = newLeftChild;
   } // end setLeftChild

   /** Detects whether this node has a left child.
       @return  True if the node has a left child. */
   public boolean hasLeftChild()
   {
      return leftChild != null;
   } // end hasLeftChild

   /** Retrieves the right child of this node.
       @return  The node’s right child. */
   public BinaryNode<T> getRightChild()
   {
      return rightChild;
   } // end getRightChild

   /** Sets this node’s right child to a given node.
       @param newRightChild  A node that will be the right child. */
   public void setRightChild(BinaryNode<T> newRightChild)
   {
      rightChild = newRightChild;
   } // end setRightChild

   /** Detects whether this node has a right child.
       @return  True if the node has a right child. */
   public boolean hasRightChild()
   {
      return rightChild != null;
   } // end hasRightChild

   /** Detects whether this node is a leaf.
       @return  True if the node is a leaf. */
   public boolean isLeaf()
   {
      return (leftChild == null) && (rightChild == null);
   } // end isLeaf

   /** Counts the nodes in the subtree rooted at this node.
       @return  The number of nodes in the subtree rooted at this node. */
   public int getNumberOfNodes()
   {
      int leftNumber = 0;
      int rightNumber = 0;

      if (leftChild != null)
         leftNumber = leftChild.getNumberOfNodes();

      if (rightChild != null)
         rightNumber = rightChild.getNumberOfNodes();

      return 1 + leftNumber + rightNumber;
   } // end getNumberOfNodes

   /** Computes the height of the subtree rooted at this node.
       @return  The height of the subtree rooted at this node. */
   public int getHeight()
   {
      return getHeight(this); // Call private getHeight
   } // end getHeight

   private int getHeight(BinaryNode<T> node)
   {
      int height = 0;

      if (node != null)
         height = 1 + Math.max(getHeight(node.getLeftChild()),
                               getHeight(node.getRightChild()));

      return height;
   } // end getHeight

   /** Copies the subtree rooted at this node.
       @return  The root of a copy of the subtree rooted at this node. */
   public BinaryNode<T> copy()
   {
      BinaryNode<T> newRoot = new BinaryNode<>(data);

      if (leftChild != null)
         newRoot.setLeftChild(leftChild.copy());

      if (rightChild != null)
         newRoot.setRightChild(rightChild.copy());

      return newRoot;
   } // end copy

   	// **********************************
	// Complete the additional methods below
	// **********************************

    //"Full" in our definition is equivalent to recursively zero-balanced
    public boolean isFull()
	{
        return isBalancedRecursive(0, this);
    }

    /* THIS VERSION WAS REMOVED TO FULFILL ASSIGNMENT REQUIREMENTS
       WHILE ASYMPTOTICALLY FASTER, IT DIDN'T REALLY STRESS THE RECURSIVE
       NATURE OF THE FULL DEFINITION
    // If the tree is a full tree, return true. Otherwise, return false.
    // Full trees contain NO nodes with only 1 child
    // AND
    // Full trees contain exactly 2^(n-1) leaves, where n is the # of nodes
	public boolean isFull()
	{
        int[] leaves = new int[1];
        leaves[0] = 0;
        boolean noNodesWithOneChild = isFullRecurse(this, leaves);
        boolean correctNumLeaves = leaves[0] == Math.pow(2, this.getHeight() - 1);
		return  noNodesWithOneChild && correctNumLeaves;
	}

    //returns true if all nodes have 0 or 2 children
    //also counts the # of leaves while recursing to help public
    //isFull() verify that all levels are filled as well
    //this avoids excessive calls to getHeight()
    private boolean isFullRecurse(BinaryNode<T> current, int[] leaves)
    {
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        if(left == null && right == null)
        {
            leaves[0]++;
            return true;
        }
        else if(left != null && right != null)
            return (isFullRecurse(left, leaves) && isFullRecurse(right, leaves));
        else
            return false;
    }
    */

    // Return true if BOTH
    // 1) the height difference between the left and right subtrees is <= k
    // 2) the left and right subtrees are recursively k-balanced
    // return false otherwise
  	public boolean isBalanced(int k)
	{
        return isBalancedRecursive(k, this);
	}

    private boolean isBalancedRecursive(int k, BinaryNode<T> current)
    {
        if (current == null)
            return true;
        BinaryNode<T> right = current.getRightChild();
        BinaryNode<T> left = current.getLeftChild();
        return Math.abs(getHeight(right) - getHeight(left)) <= k &&
            isBalancedRecursive(k, left) && isBalancedRecursive(k, right);
    }

} // end BinaryNode
