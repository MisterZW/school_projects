//Zachary Whitney
//CS 445 Assignment 4
//Pitt ID: zdw9  Student ID: 3320178
//TH 1PM Lecture, 10AM Tues Recitation
//April 11, 2018

package MyTreePackage;
import java.util.Iterator;
/**
   A class that implements the ADT binary search tree by extending BinaryTree.
   Recursive version.

   @author Frank M. Carrano
   @author Timothy M. Henry
   @version 4.0
*/
public class BinarySearchTree<T extends Comparable<? super T>>
             extends ComparableBinaryTree<T>
             implements SearchTreeInterface<T>
{
   public BinarySearchTree()
   {
      super();
   } // end default constructor

   public BinarySearchTree(T rootEntry)
   {
      super();
      setRootNode(new BinaryNode<>(rootEntry));
   } // end constructor

   public void setTree(T rootData) // Disable setTree (see Segment 25.6)
   {
      throw new UnsupportedOperationException();
   } // end setTree

   public void setTree(T rootData, BinaryTreeInterface<T> leftTree,
                                   BinaryTreeInterface<T> rightTree)
   {
      throw new UnsupportedOperationException();
   } // end setTree

	public T getEntry(T entry)
	{
	   return findEntry(getRootNode(), entry);
	} // end getEntry

   // Recursively finds the given entry in the binary tree rooted at the given node.
	private T findEntry(BinaryNode<T> rootNode, T entry)
	{
      T result = null;

      if (rootNode != null)
      {
         T rootEntry = rootNode.getData();

         if (entry.equals(rootEntry))
            result = rootEntry;
         else if (entry.compareTo(rootEntry) < 0)
            result = findEntry(rootNode.getLeftChild(), entry);
         else
            result = findEntry(rootNode.getRightChild(), entry);
      } // end if

      return result;
	} // end findEntry

	public boolean contains(T entry)
	{
		return getEntry(entry) != null;
	} // end contains

   public T add(T newEntry)
   {
      T result = null;

      if (isEmpty())
         setRootNode(new BinaryNode<>(newEntry));
      else
         result = addEntry(getRootNode(), newEntry);

      return result;
   } // end add

   // Adds newEntry to the nonempty subtree rooted at rootNode.
   private T addEntry(BinaryNode<T> rootNode, T newEntry)
   {
      assert rootNode != null;
      T result = null;
      int comparison = newEntry.compareTo(rootNode.getData());

      if (comparison == 0)
      {
         result = rootNode.getData();
         rootNode.setData(newEntry);
      }
      else if (comparison < 0)
      {
         if (rootNode.hasLeftChild())
            result = addEntry(rootNode.getLeftChild(), newEntry);
         else
            rootNode.setLeftChild(new BinaryNode<>(newEntry));
      }
      else
      {
         assert comparison > 0;

         if (rootNode.hasRightChild())
            result = addEntry(rootNode.getRightChild(), newEntry);
         else
            rootNode.setRightChild(new BinaryNode<>(newEntry));
      } // end if

      return result;
   } // end addEntry

	public T remove(T entry)
   {
      ReturnObject oldEntry = new ReturnObject(null);
      BinaryNode<T> newRoot = removeEntry(getRootNode(), entry, oldEntry);
      setRootNode(newRoot);

      return oldEntry.get();
   } // end remove

	// Removes an entry from the tree rooted at a given node.
   // Parameters:
   //    rootNode  A reference to the root of a tree.
   //    entry  The object to be removed.
   //    oldEntry  An object whose data field is null.
   // Returns:  The root node of the resulting tree; if entry matches
   //           an entry in the tree, oldEntry's data field is the entry
   //           that was removed from the tree; otherwise it is null.
   private BinaryNode<T> removeEntry(BinaryNode<T> rootNode, T entry,
                                     ReturnObject oldEntry)
   {
      if (rootNode != null)
      {
         T rootData = rootNode.getData();
         int comparison = entry.compareTo(rootData);

         if (comparison == 0)       // entry == root entry
         {
            oldEntry.set(rootData);
            rootNode = removeFromRoot(rootNode);
         }
         else if (comparison < 0)   // entry < root entry
         {
            BinaryNode<T> leftChild = rootNode.getLeftChild();
            BinaryNode<T> subtreeRoot = removeEntry(leftChild, entry, oldEntry);
            rootNode.setLeftChild(subtreeRoot);
         }
         else                       // entry > root entry
         {
            BinaryNode<T> rightChild = rootNode.getRightChild();
            rootNode.setRightChild(removeEntry(rightChild, entry, oldEntry));
         } // end if
      } // end if

      return rootNode;
   } // end removeEntry

	// Removes the entry in a given root node of a subtree.
   // Parameter:
   //    rootNode  A reference to the root of the subtree.
   // Returns:  The root node of the revised subtree.
   private BinaryNode<T> removeFromRoot(BinaryNode<T> rootNode)
   {
      // Case 1: rootNode has two children
      if (rootNode.hasLeftChild() && rootNode.hasRightChild())
      {
         // Find node with largest entry in left subtree
         BinaryNode<T> leftSubtreeRoot = rootNode.getLeftChild();
         BinaryNode<T> largestNode = findLargest(leftSubtreeRoot);

         // Replace entry in root
         rootNode.setData(largestNode.getData());

         // Remove node with largest entry in left subtree
         rootNode.setLeftChild(removeLargest(leftSubtreeRoot));
      } // end if

      // Case 2: rootNode has at most one child
      else if (rootNode.hasRightChild())
         rootNode = rootNode.getRightChild();
      else
         rootNode = rootNode.getLeftChild();

      // Assertion: If rootNode was a leaf, it is now null

      return rootNode;
   } // end removeEntry

   // Finds the node containing the largest entry in a given tree.
   // Parameter:
   //    rootNode  A reference to the root node of the tree.
   // Returns:  The node containing the largest entry in the tree.
   private BinaryNode<T> findLargest(BinaryNode<T> rootNode)
   {
      if (rootNode.hasRightChild())
         rootNode = findLargest(rootNode.getRightChild());

      return rootNode;
   } // end findLargest

	// Removes the node containing the largest entry in a given tree.
   // Parameter:
   //    rootNode  A reference to the root node of the tree.
   // Returns:  The root node of the revised tree.
   private BinaryNode<T> removeLargest(BinaryNode<T> rootNode)
   {
      if (rootNode.hasRightChild())
      {
         BinaryNode<T> rightChild = rootNode.getRightChild();
         rightChild = removeLargest(rightChild);
         rootNode.setRightChild(rightChild);
      }
      else
         rootNode = rootNode.getLeftChild();

      return rootNode;
   } // end removeLargest

	private class ReturnObject
	{
		private T item;

		private ReturnObject(T entry)
		{
			item = entry;
		} // end constructor

		public T get()
		{
			return item;
		} // end get

		public void set(T entry)
		{
			item = entry;
		} // end set
	} // end ReturnObject

	// **************************************************
	// Override the methods specified in Assignment 4 below
	// **************************************************

    // max is the farthest right element
    public T getMax()
    {
        BinaryNode<T> current = getRootNode();
        if (current == null)
            return null;
        while(current.getRightChild() != null)
            current = current.getRightChild();
        return current.getData();
    }

    //min is the farthest left element
    public T getMin()
    {
        BinaryNode<T> current = getRootNode();
        if (current == null)
            return null;
        while(current.getLeftChild() != null)
            current = current.getLeftChild();
        return current.getData();
    }

    public boolean isBST() // override to always return true
    {
        return true;
    }

    //iterative implementation
    public int rank(T data)
    {
        int result = 0;
        BinaryNode<T> current = getRootNode();
        T thisValue = null;
        while(current != null)
        {
            thisValue = current.getData();
            if(data.compareTo(thisValue) <= 0)      //case 1 progress left
                current = current.getLeftChild();
            else if(data.compareTo(thisValue) > 0)  //case 2 progress right
            {                                       //add size of left subtree
                result++;                           //and 1 (for parent) to result
                BinaryNode<T> left = current.getLeftChild();
                if(left != null)
                    result += left.getNumberOfNodes();
                current = current.getRightChild();
            }
        }
        return result;
    }

    //very similar logic to rank
    //this time I used a recursive structure, but the idea is the same
    public T get(int i)
    {
        if(i < 0)
            throw new IndexOutOfBoundsException();
        else
            return getRecurse(i, getRootNode());
    }

    //to locate rank k, recurse to the part of the tree with only k
    //nodes which are smaller. When moving right, lower k by the size of the left
    //subtree + 1 (for the parent).
    private T getRecurse(int key, BinaryNode<T> current)
    {
        if(current == null)
            throw new IndexOutOfBoundsException();
        BinaryNode<T> left = current.getLeftChild();
        int rank = (left == null) ? 0 : left.getNumberOfNodes();
        if(key < rank)
            return getRecurse(key, left);
        else if(key > rank)
            return getRecurse(key - rank - 1, current.getRightChild());
        else
            return current.getData();
    }

} // end BinarySearchTree
