
// CS 0445 Spring 2018
// Assignment 2
// Zachary Whitney, zdw9, ID# 3320178
// Ramirez lecture T TH 1PM, Recitation T 10AM

public class MyStringBuilder
{
	private CNode firstC;	// reference to front of list.
	private CNode lastC; 	// reference to last node of list.
	private int length;  	// number of characters in the list

	// Create a new MyStringBuilder initialized with the chars in String s
	public MyStringBuilder(String s)
	{
		if (s == null || s.length() == 0) //for empty String or null reference
			clear();
		else
		{
			firstC = new CNode(s.charAt(0));
			length = 1;
			CNode currNode = firstC;
			for (int i = 1; i < s.length(); i++)
			{
				currNode.next = new CNode(s.charAt(i));
				currNode = currNode.next;
				length++;
			}
			lastC = currNode;
		}
	}

	// Create a new MyStringBuilder initialized with the chars in array s
	public MyStringBuilder(char[] s)
	{
		if (s == null || s.length == 0)	//for empty String or null reference
			clear();
		else
		{
			for(char c : s)
			{
				this.append(c);
			}
		}
	}

	// Create a new empty MyStringBuilder
	public MyStringBuilder()
	{
		clear();
	}

	//copy constructor
	public MyStringBuilder(MyStringBuilder other)
	{
		this();
		this.append(other);
	}

	// Append MyStringBuilder b to the end of the current MyStringBuilder, and
	// return the current MyStringBuilder.  Be careful for special cases!
	public MyStringBuilder append(MyStringBuilder b)
	{
		if(b == null || b.length() == 0) //short circuit won't dereference null
		{
			//change nothing
		}
		else
		{
			CNode currNode = b.firstC;
			int endCondition = b.length();	//required to allow self-append
			for(int i = 0; i < endCondition; i++)
			{
				this.append(currNode.data);
				currNode = currNode.next;
			}
		}
		return this;
	}

	// Append String s to the end of the current MyStringBuilder, and return
	// the current MyStringBuilder.  Be careful for special cases!
	public MyStringBuilder append(String s)
	{
		if(s != null)
		{
			for(int i = 0; i < s.length(); i++)
			{
				this.append(s.charAt(i));
			}
		}
		return this;
	}

	// Append char array c to the end of the current MyStringBuilder, and
	// return the current MyStringBuilder.  Be careful for special cases!
	public MyStringBuilder append(char [] c)
	{
		if(c != null)
		{
			for(char x : c)
			{
				this.append(x);
			}
		}
		return this;
	}

	// Append char c to the end of the current MyStringBuilder, and
	// return the current MyStringBuilder.  Be careful for special cases!
	public MyStringBuilder append(char c)
	{
		if (length == 0)
		{
			firstC = new CNode(c);
			lastC = firstC;
		}
		else
		{
			lastC.next = new CNode(c);
			lastC = lastC.next;
		}
		length++;
		return this;
	}

	// Return the character at location "index" in the current MyStringBuilder.
	// If index is invalid, throw an IndexOutOfBoundsException.
	public char charAt(int index)
	{
		if(index < 0 || index >= length)
			throw new IndexOutOfBoundsException("Index " + index + " is invalid");

		CNode currNode = firstC;
		for (int i = 0; i < index; i++)
		{
			currNode = currNode.next;
		}
		return currNode.data;
	}

	// Delete the characters from index "start" to index "end" - 1 in the
	// current MyStringBuilder, and return the current MyStringBuilder.
	// If "start" is invalid or "end" <= "start" do nothing (just return the
	// MyStringBuilder as is).  If "end" is past the end of the MyStringBuilder,
	// only remove up until the end of the MyStringBuilder. Be careful for
	// special cases!
	public MyStringBuilder delete(int start, int end)
	{
		if(end > length)	//wrap end to edge
			end = length;
		if(start < 0 || start >= end)	//reject invalid start
			return this;
		else if(start == 0)		//start at index 0 special case
		{
			if(end == length)	//only node special case
			{
				clear();
				return this;
			}
			else
				firstC = getNodeat(end);
		}
		else
		{
			CNode nodeBefore = getNodeat(start - 1);
			CNode current = nodeBefore.next;
			if(end == length)	//if deleting to the end of the list
			{
				nodeBefore.next = null;
				lastC = nodeBefore;
			}
			else
			{
				for(int i = start; i < end; i++)
				{
					current = current.next;	//traverse to end index
				}
				nodeBefore.next = current;
			}
		}
		length -= (end - start);
		return this;
	}

	// Delete the character at location "index" from the current
	// MyStringBuilder and return the current MyStringBuilder.  If "index" is
	// invalid, do nothing (just return the MyStringBuilder as is).
	// Be careful for special cases!
	public MyStringBuilder deleteCharAt(int index)
	{
		if (index < 0 || index >= length) //invalid operation
		{
			//change nothing
		}
		else if (index == 0) //front of list special case
		{
			firstC = firstC.next;
			if(length == 1)		//only node special case
				lastC = null;
			length--;
		}
		else
		{
			CNode nodeBefore = getNodeat(index - 1);
			nodeBefore.next = nodeBefore.next.next;
			if(index == length - 1)	//last node special case
				lastC = nodeBefore;
			length--;
		}
		return this;
	}

	// Find and return the index within the current MyStringBuilder where
	// String str first matches a sequence of characters within the current
	// MyStringBuilder.  If str does not match any sequence of characters
	// within the current MyStringBuilder, return -1.
	public int indexOf(String str)
	{
		if(str == null)		//don't want to call .length() on a null String
			return -1;
		if(str.equals(""))	//ensure keyLength is at least 1 for the loop end condition
			return 0;
		CNode start = firstC;
		int keyLength = str.length();		//store to prevent excessive method calls
		int lastCheck = length - keyLength + 1;	//stop short if not enough chars left
		for (int i = 0; i < lastCheck; i++)
		{
			CNode curr = start;
			int k = 0;
			for (int j = 0; j < keyLength; j++)
			{
				if(curr.data != str.charAt(k))
					break;	//move on if we find a char that doesn't match
				curr = curr.next;
				k++;
			}
			if(k == keyLength)
				return i;
			start = start.next;
		}
		return -1;
	}

	// Insert String str into the current MyStringBuilder starting at index
	// "offset" and return the current MyStringBuilder.  if "offset" ==
	// length, this is the same as append.  If "offset" is invalid
	// do nothing.
	public MyStringBuilder insert(int offset, String str)
	{
		if(offset < 0 || offset > length || str == null || str.length() == 0)
			return this;		//change nothing if input is bogus
		if(offset == length)	//avoid list traversal for insert at end
		{						//also handles empty list
			this.append(str);
			return this;
		}
		int stringLength = str.length(); //avoid excess method calls
		if(offset == 0)		//front of list special case
		{
			CNode curr = new CNode(str.charAt(stringLength - 1));
			curr.next = firstC;
			for(int i = stringLength - 2; i >= 0; i--)	//work backwards
			{
				curr = new CNode(str.charAt(i), curr);
			}
			firstC = curr;
		}
		else	//normal case
		{
			CNode nodeBefore = getNodeat(offset - 1);
			CNode nodeAfter = nodeBefore.next;
			CNode current = new CNode(str.charAt(0));
			nodeBefore.next = current;
			for(int i = 1; i < stringLength; i++)
			{
				current.next = new CNode(str.charAt(i), current);
				current = current.next;
			}
			current.next = nodeAfter;
		}
		length += stringLength;
		return this;
	}

	// Insert character c into the current MyStringBuilder at index
	// "offset" and return the current MyStringBuilder.  If "offset" ==
	// length, this is the same as append.  If "offset" is invalid,
	// do nothing.
	public MyStringBuilder insert(int offset, char c)
	{
	    if(offset < 0 || offset > length)	//reject invalid index
	    {
	        //change nothing
	    }
	    else if(offset == length)	//avoid list traversal, also handles only node case
	        this.append(c);
	    else
	    {
	        if(offset == 0)		//front of list special case
	            firstC = new CNode(c, firstC);
	        else
	        {
	            CNode nodeBefore = getNodeat(offset - 1);
	            nodeBefore.next = new CNode(c, nodeBefore.next);
	        }
	        length++;
	    }
	    return this;
	}

	// Insert char array c into the current MyStringBuilder starting at index
	// index "offset" and return the current MyStringBuilder.  If "offset" is
	// invalid, do nothing.
	public MyStringBuilder insert(int offset, char [] c)
	{
		if(offset < 0 || offset > length || c == null || c.length == 0)
			return this;		//change nothing if input is bogus
		if(offset == length)	//avoid list traversal for insert at end
		{						//also handles empty list
			this.append(c);
			return this;
		}
		if(offset == 0)		//front of list special case
		{
			CNode curr = new CNode(c[c.length - 1], firstC);
			for(int i = c.length - 2; i >= 0; i--)	//work backwards
			{
				curr = new CNode(c[i], curr);
			}
			firstC = curr;
		}
		else	//normal case
		{
			CNode nodeBefore = getNodeat(offset - 1);
			CNode nodeAfter = nodeBefore.next;
			CNode current = new CNode(c[0]);
			nodeBefore.next = current;
			for(int i = 1; i < c.length; i++)
			{
				current.next = new CNode(c[i]);
				current = current.next;
			}
			current.next = nodeAfter;
		}
		length += c.length;
		return this;
	}

	// Return the length of the current MyStringBuilder
	public int length()
	{
		return length;
	}

	// Delete the substring from "start" to "end" - 1 in the current
	// MyStringBuilder, then insert String "str" into the current
	// MyStringBuilder starting at index "start", then return the current
	// MyStringBuilder.  If "start" is invalid or "end" <= "start", do nothing.
	// If "end" is past the end of the MyStringBuilder, only delete until the
	// end of the MyStringBuilder, then insert.
	public MyStringBuilder replace(int start, int end, String str)
	{
		if(start < 0 || str == null || start >= end || start > length)
			return this;	//change nothing if input is bogus
		if(end > length)	//end is past the list adjustment
			end = length;

		CNode dummyNode = null, linkNode = null, current;
		if(start == 0)	//to handle adding at the front, insert dummyNode
		{
			dummyNode = new CNode('$', firstC);
			current = dummyNode;
		}
		else
			current = getNodeat(start - 1);
		int removeLength = end - start;
		int stringLength = str.length();

		//find the node to link the chain back to, if it exists
		if(end <= length && start + removeLength < length)
		{
			linkNode = current;
			for(int i = 0; i <= removeLength; i++)
			{
				linkNode = linkNode.next;
			}
		}
		for(int j = 0; j < stringLength; j++)
		{
			current.next = new CNode(str.charAt(j));
			current = current.next;
		}
		current.next = linkNode;	//link back in
		if(linkNode == null)
			lastC = current;
		if(dummyNode != null)	//trash the dummy
		{
			firstC = dummyNode.next;
			dummyNode.next = null;
			dummyNode = null;
		}
		length = length - removeLength + stringLength;
		return this;
	}

	// Reverse the characters in the current MyStringBuilder and then
	// return the current MyStringBuilder.
	public MyStringBuilder reverse()
	{
		if(length < 2)
		{
			//change nothing
		}
		else	//flip direction of references during traversal
		{
			lastC = firstC;
			CNode previous = firstC;
			CNode current = firstC.next;
			firstC.next = null;
			CNode nextNode;
			for (int i = 1; i < length; i++)
			{
				nextNode = current.next;
				current.next = previous;
				previous = current;
				current = nextNode;
			}
			firstC = previous;
		}
		return this;
	}

	// Return as a String the substring of characters from index "start" to
	// index "end" - 1 within the current MyStringBuilder
	public String substring(int start, int end)
	{
		if(start < 0)
			throw new IndexOutOfBoundsException(start + " is invalid");
		else if(end > length)
			throw new IndexOutOfBoundsException(end + " is invalid");
		else if(start >= end)
			return "";
		else
		{
			int size = end - start;
			char[] result = new char[size];
			CNode current = getNodeat(start);
			for(int i = 0; i < size; i++)
			{
				result[i] = current.data;
				current = current.next;
			}
			return new String(result);
		}
	}

	// Return the entire contents of the current MyStringBuilder as a String
	public String toString()
	{
		char[] result = new char[length];
		CNode current = firstC;
		for(int i = 0; i < length; i++)
		{
			result[i] = current.data;
			current = current.next;
		}
		return new String(result);
	}

	//helper method to get node at designated index
	//assertion: there is a valid node at index to return when called
	private CNode getNodeat(int index)
	{
		CNode current = firstC;
		for (int i = 0; i < index; i++)
		{
			current = current.next;
		}
		return current;
	}

	private final void clear()
	{
		firstC = null;
		lastC = null;
		length = 0;
	}

	// You must use this inner class exactly as specified below.  Note that
	// since it is an inner class, the MyStringBuilder class MAY access the
	// data and next fields directly.
	private class CNode
	{
		private char data;
		private CNode next;

		public CNode(char c)
		{
			data = c;
			next = null;
		}

		public CNode(char c, CNode n)
		{
			data = c;
			next = n;
		}
	}
}
