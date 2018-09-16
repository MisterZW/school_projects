// CS 445 Spring 2018
// Help with Assignment 2 -- one way of doing one of the
// constructor methods.  Carefully read over this code, since
// it can give you hints to other methods

// Create a new MyStringBuilder which contains the contents of the
// String argument.
public MyStringBuilder(String s)
{
	if (s == null || s.length() == 0) // Special case for empty String
	{					 			  // or null reference
		firstC = null;
		lastC = null;
		length = 0;
	}
	else
	{
		// Create front node to get started
		firstC = new CNode(s.charAt(0));
		length = 1;
		CNode currNode = firstC;
		// Create remaining nodes, copying from String.  Note
		// how each new node is simply added to the end of the
		// previous one.  Trace this to see what is going on.
		for (int i = 1; i < s.length(); i++)
		{
			CNode newNode = new CNode(s.charAt(i));
			currNode.next = newNode;
			currNode = newNode;
			length++;
		}
		lastC = currNode;
	}
}
