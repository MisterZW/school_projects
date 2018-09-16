// CS 0445 Spring 2018
// See the methods for Assignment 4 below.
package MyTreePackage;
/**
   An interface for the ADT binary tree.

   @author Frank M. Carrano
   @author Timothy M. Henry
   @version 4.0
*/
public interface BinaryTreeInterface<T> extends TreeInterface<T>,
                                                TreeIteratorInterface<T>
{
   /** Sets this binary tree to a new one-node binary tree.
       @param rootData   The object that is the data for the new tree's root.
   */
   public void setTree(T rootData);

   /** Sets this binary tree to a new binary tree.
       @param rootData   The object that is the data for the new tree's root.
       @param leftTree   The left subtree of the new tree.
       @param rightTree  The right subtree of the new tree. */
   public void setTree(T rootData, BinaryTreeInterface<T> leftTree,
                                   BinaryTreeInterface<T> rightTree);

	// ************************************************
	// Methods added for CS 0445 Assignment 4 are below
	// ************************************************
	public boolean isFull();			// Return true if the current BT
		// is a full tree and false otherwise.  See course notes for
		// definition of a full tree.
	public boolean isBalanced(int k);	// Return true for the current
		// BT if:
		// 1) The difference in height of the left subtree and the right
		// 		subtree is at most k and
		// 2) Both the left subtree and the right subtree are (recursively)
		//		k-balanced.
	public void saveInorder(String fileName); // Save the data in the BT
		// to file "fileName" using an inorder traversal.  Format
		// the file in the following way: It first contains an int
		// representing the number of nodes in the tree, followed
		// by the actual objects from the tree (inorder).  Use the
		// writeObject() method to write the objects.
	public void buildInorder(String filename); // Build a balanced BT
		// from the file "fileName".  Assume the first line of the
		// file has an integer, N, indicating the number of values
		// to follow.  The remaining N lines of the file contain N
		// values.  The order of the values in the file should be
		// preserved by the tree (i.e. an inorder traversal should
		// show the data in the order stored in the file). Also
		// if N = 2^K-1 for some K, the tree MUST be a FULL tree.
		// If N != 2^K-1 for some K, the tree should be optimally
		// balanced but it does NOT have to be a complete tree.
		// This method MUST build the tree recursively (hint: have
		// it call a recursive method).  Use the readObject()
		// method to read the objects (so the file can contain any
		// type of Serializable objects).
} // end BinaryTreeInterface
