// CS 0445 Spring 2018
// Assignment 3
// Zachary Whitney, zdw9, ID# 3320178
// Ramirez lecture T TH 1PM, Recitation T 10AM

public class MyStringBuilder2
{
	private CNode firstC;
	private CNode lastC;
	private int length;

	// Create a new MyStringBuilder2 initialized with the chars in String s
	// Used "as is" from Assignment 3 specifications
	public MyStringBuilder2(String s)
	{
		if (s != null && s.length() > 0)
            makeBuilder(s, 0);
      	else  // no String so initialize empty MyStringBuilder2
            initializeEmpty();
	}

	// Create a new MyStringBuilder2 initialized with the chars in array s
	// Adapted directly from the Assignment 3 specifications
	public MyStringBuilder2(char[] s)
	{
		if (s != null && s.length > 0)
            makeBuilder(s, 0);
      	else  // no array so initialize empty MyStringBuilder2
            initializeEmpty();
	}

	// Create a new empty MyStringBuilder2
	public MyStringBuilder2()
	{
		initializeEmpty();
	}

	private final void initializeEmpty()
	{
		length = 0;
		firstC = null;
		lastC = null;
	}

	// recursive helper for constructor which takes a String
	// Used "as is" from Assignment 3 specifications
	private void makeBuilder(String s, int pos)
	{
		// Recursive case – we have not finished going through the String
		if (pos < s.length()-1)
		{
		    makeBuilder(s, pos+1);
		    firstC = new CNode(s.charAt(pos), firstC);
		    length++;
		}
		else if (pos == s.length()-1) // Special case for last char in String
		{                             // This is needed since lastC must be
		                              // set to point to this node
		    firstC = new CNode(s.charAt(pos));
		    lastC = firstC;
		    length = 1;
		}
		else  // This case should never be reached, due to the way the
		      // constructor is set up.  However, I included it as a
			  // safeguard (in case some other method calls this one)
		    initializeEmpty();
	}

	// recursive helper for constructor which takes a char[]
	// Adapted directly from Assignment 3 specifications
	private void makeBuilder(char[] c, int pos)
	{
		// Recursive case – we have not finished going through the array
		if (pos < c.length-1)
		{
			makeBuilder(c, pos+1);
			firstC = new CNode(c[pos], firstC);
			length++;
		}
		else if (pos == c.length-1) 	// Special case for last char in array
		{                           	// This is needed since lastC must be
			firstC = new CNode(c[pos]);	// set to point to this node
			lastC = firstC;
			length = 1;
		}
		else  // This case should never be reached, due to the way the
			  // constructor is set up.  However, I included it as a
		      // safeguard (in case some other method calls this one)
			initializeEmpty();
	}

	/**
	* Private helper method to get specified node. Never called when invalid.
	* @ param index 	The index of the node to return
	* @ param current	The current list position
	* @ param thisNode	Reference to the node at current's index
	* @ return 			The node at specified index
	*/
	private CNode getNodeAt(int index, int current, CNode thisNode)
	{
		assert	thisNode != null;
		if (current == index)	//base case found
			return thisNode;
		else					//recursive case
			return getNodeAt(index, current + 1, thisNode.next);
	}

	// Append MyStringBuilder2 b to the end of the current MyStringBuilder2, and
	// return the current MyStringBuilder2.
	public MyStringBuilder2 append(MyStringBuilder2 b)
	{
		if(b == null || b.length() == 0)	//invalid input
			{/*do nothing*/}
		else if (length == 0)				//empty list special case
		{
			firstC = new CNode(b.firstC.data);
			lastC = firstC;
			length++;
			appendStringBuilderRecurse(b.firstC.next);
		}
		else
			appendStringBuilderRecurse(b.firstC);
		return this;
	}

	/**Recursive implementation of append(MyStringBuilder2 b)
	* @ param current -- the next node of the other MyStringBuilder2 to append
	*/
	private void appendStringBuilderRecurse(CNode current)
	{
		if (current == null)				//base case end of mystringbuilder2
			return;
		else								//recursive case
		{
			lastC.next = new CNode(current.data);
			lastC = lastC.next;
			length++;
			appendStringBuilderRecurse(current.next);
		}
	}

	// Append String s to the end of the current MyStringBuilder2, and return
	// the current MyStringBuilder2.
	public MyStringBuilder2 append(String s)
	{
		if(s == null || s.length() == 0)	//invalid input
			{/*do nothing*/}
		else if (length == 0)				//empty list special case
		{
			firstC = new CNode(s.charAt(0));
			lastC = firstC;
			length++;
			appendStringRecurse(s, 1);
		}
		else
			appendStringRecurse(s, 0);
		return this;
	}

	/**Recursive implementation of append(String str)
	* @ param index -- the current index of the String to append
	* @ param s		-- the String to append
	*/
	private void appendStringRecurse(String s, int index)
	{
		if (index == s.length())			//base case end of string
			return;
		else								//recursive case
		{
			lastC.next = new CNode(s.charAt(index));
			lastC = lastC.next;
			length++;
			appendStringRecurse(s, index + 1);
		}
	}

	// Append char array c to the end of the current MyStringBuilder2, and
	// return the current MyStringBuilder2.
	public MyStringBuilder2 append(char [] c)
	{
		if(c == null || c.length == 0)	//invalid input
			{/*do nothing*/}
		else if (length == 0)				//empty list special case
		{
			firstC = new CNode(c[0]);
			lastC = firstC;
			length++;
			appendCharArrayRecurse(c, 1);
		}
		else
			appendCharArrayRecurse(c, 0);
		return this;
	}

	/**Recursive implementation of append(char[] c)
	* @ param index -- the current index of the array
	* @ param s		-- the char[] to append
	*/
	private void appendCharArrayRecurse(char[] c, int index)
	{
		if (index == c.length)			//base case end of string
			return;
		else								//recursive case
		{
			lastC.next = new CNode(c[index]);
			lastC = lastC.next;
			length++;
			appendCharArrayRecurse(c, index + 1);
		}
	}

	// Append char c to the end of the current MyStringBuilder2, and
	// return the current MyStringBuilder2.
	public MyStringBuilder2 append(char c)
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

	// Return the character at location "index" in the current MyStringBuilder2.
	// If index is invalid, throw an IndexOutOfBoundsException.
	public char charAt(int index)
	{
		if(index < 0 || index >= length)
			throw new IndexOutOfBoundsException("Index " + index + " is invalid");
		else
			return getNodeAt(index, 0, firstC).data;
	}

	// Delete the characters from index "start" to index "end" - 1 in the
	// current MyStringBuilder2, and return the current MyStringBuilder2.
	// If "start" is invalid or "end" <= "start" do nothing (just return the
	// MyStringBuilder2 as is).  If "end" is past the end of the MyStringBuilder2,
	// only remove up until the end of the MyStringBuilder2.
	public MyStringBuilder2 delete(int start, int end)
	{
		if(end > length)				//wrap end to edge
			end = length;
		if(start < 0 || start >= end)	//reject invalid start
			return this;
		else if(start == 0)				//start at index 0 special case
		{
			if(end == length)			//delete all/only node special case
			{
				initializeEmpty();
				return this;
			}
			else
				firstC = getNodeAt(end, 0, firstC);
		}
		else
		{
			CNode nodeBefore = getNodeAt(start - 1, 0, firstC);
			if(end == length)	//if deleting to the end of the list
			{
				nodeBefore.next = null;
				lastC = nodeBefore;
			}
			//not an extra traversal -- passing current position as start for getNodeAt()
			else
				nodeBefore.next = getNodeAt(end, start, nodeBefore.next);
		}
		length -= (end - start);
		return this;
	}

	// Delete the character at location "index" from the current
	// MyStringBuilder2 and return the current MyStringBuilder2.  If "index" is
	// invalid, do nothing (just return the MyStringBuilder2 as is).
	public MyStringBuilder2 deleteCharAt(int index)
	{
		{
			if (index < 0 || index >= length) 	//invalid operation
				{/*change nothing*/}
			else if (index == 0) 				//front of list special case
			{
				firstC = firstC.next;
				if(length == 1)					//only node special case
					lastC = null;
				length--;
			}
			else
			{
				CNode nodeBefore = getNodeAt(index - 1, 0, firstC);
				nodeBefore.next = nodeBefore.next.next;
				if(index == length - 1)			//last node special case
					lastC = nodeBefore;
				length--;
			}
			return this;
		}
	}

	// Find and return the index within the current MyStringBuilder2 where
	// String str first matches a sequence of characters within the current
	// MyStringBuilder2.  If str does not match any sequence of characters
	// within the current MyStringBuilder2, return -1.
	public int indexOf(String str)
	{
		if(str == null)					//invalid input
			return -1;
		else if (str.length() == 0)		//always find empty string @ front
			return 0;
		else							//call recursive solver
			return indexOfRecurse(str, firstC, 0);
	}

	//recursive solver for indexOf()
	private int indexOfRecurse(String key, CNode current, int index)
	{
		if(current == null)		//base case not found
			return -1;
		//recursive case local search
		else if (key.charAt(0) == current.data)
		{
			if (matchString(key, current, 0))
				return index;
		}
		//recursive case check next node
		return indexOfRecurse(key, current.next, index + 1);
	}

	//Second recursive helper for indedOf() and lastIndexOf()
	//Returns true if a string starting at this index/node matches the key, else false
	private boolean matchString(String key, CNode current, int index)
	{
		if(index == key.length())								//base case found
			return true;
		else if(current == null)								//base case not found 1
			return false;
		else if (key.charAt(index) == current.data)				//recursive case
			return matchString(key, current.next, index + 1);
		return false;											//base case not found 2
	}

	// Insert String str into the current MyStringBuilder2 starting at index
	// "offset" and return the current MyStringBuilder2.  if "offset" ==
	// length, this is the same as append.  If "offset" is invalid
	// do nothing.
	public MyStringBuilder2 insert(int offset, String str)
	{
		if(offset < 0 || offset > length || str == null || str.length() == 0)
			{/*change nothing*/}
		else if(offset == length)	//avoid list traversal for insert at end
		{							//also handles empty list
			this.append(str);
		}
		else						//case: there's at least one node already
		{
			int startpoint = 0;
			CNode nodeBefore, nodeAfter;
			if(offset == 0)			//front of list special case
			{
				firstC = new CNode(str.charAt(0), firstC);
				nodeBefore = firstC;
				startpoint++;
				length++;
			}
			else
				nodeBefore = getNodeAt(offset - 1, 0, firstC);
			nodeAfter = nodeBefore.next;
			insertString(nodeBefore, nodeAfter, str, startpoint);
		}
		return this;
	}

	/**
	* Recursive helper for insert(String str)
	* @ param	before  -- existing node to link the first new char to
	* @ param	after   -- existing node to link last new char back to
	* @ param   s		-- String to insert
	* @ param	index	-- index of the next char in the String to insert
	*/
	private void insertString(CNode before, CNode after, String s, int index)
	{
		if(index < s.length())				//recursive case
		{
			before.next = new CNode(s.charAt(index));
			insertString(before.next, after, s, index + 1);
			length++;
		}
		else
			before.next = after;			//base case done
	}

	// Insert character c into the current MyStringBuilder2 at index
	// "offset" and return the current MyStringBuilder2.  If "offset" ==
	// length, this is the same as append.  If "offset" is invalid,
	// do nothing.
	public MyStringBuilder2 insert(int offset, char c)
	{
		if(offset < 0 || offset > length)	//reject invalid index
	    	{/*change nothing*/}
	    else if(offset == length)	//avoid list traversal, also handles only node case
	        this.append(c);
	    else
	    {
	        if(offset == 0)			//front of list special case
	            firstC = new CNode(c, firstC);
	        else
	        {
	            CNode nodeBefore = getNodeAt(offset - 1, 0, firstC);
	            nodeBefore.next = new CNode(c, nodeBefore.next);
	        }
	        length++;
	    }
	    return this;
	}

	// Insert char array c into the current MyStringBuilder2 starting at index
	// index "offset" and return the current MyStringBuilder2.  If "offset" is
	// invalid, do nothing.
	public MyStringBuilder2 insert(int offset, char [] c)
	{
		if(offset < 0 || offset > length || c == null || c.length == 0)
			{/*change nothing*/}
		else if(offset == length)	//avoid list traversal for insert at end
		{							//also handles empty list
			this.append(c);
		}
		else						//case: there's at least one node already
		{
			int startpoint = 0;
			CNode nodeBefore, nodeAfter;
			if(offset == 0)			//front of list special case
			{
				firstC = new CNode(c[0], firstC);
				nodeBefore = firstC;
				startpoint++;
				length++;
			}
			else
				nodeBefore = getNodeAt(offset - 1, 0, firstC);
			nodeAfter = nodeBefore.next;
			insertCharArray(nodeBefore, nodeAfter, c, startpoint);
		}
		return this;
	}

	/**
	* Recursive helper for insert(char[] c)
	* @ param	before  -- existing node to link the first new char to
	* @ param	after   -- existing node to link last new char back to
	* @ param   c		-- array containing the data to insert
	* @ param	index	-- index of the next char in the array to insert
	*/
	private void insertCharArray(CNode before, CNode after, char[] c, int index)
	{
		if(index < c.length)				//recursive case
		{
			before.next = new CNode(c[index]);
			insertCharArray(before.next, after, c, index + 1);
			length++;
		}
		else
			before.next = after;			//base case done
	}

	// Return the length of the current MyStringBuilder2
	public int length()
	{
		return length;
	}

	// Delete the substring from "start" to "end" - 1 in the current
	// MyStringBuilder2, then insert String "str" into the current
	// MyStringBuilder2 starting at index "start", then return the current
	// MyStringBuilder2.  If "start" is invalid or "end" <= "start", do nothing.
	// If "end" is past the end of the MyStringBuilder2, only delete until the
	// end of the MyStringBuilder2, then insert.
	public MyStringBuilder2 replace(int start, int end, String str)
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
			current = getNodeAt(start - 1, 0, firstC);
		int removeLength = end - start;
		int stringLength = str.length();
		if(end == length)		//delete to end special case
			lastC = current;
		else					//find the node to link the chain back to
			linkNode = getNodeAt(end, start - 1, current); //no extra traversal made
		insertString(current, linkNode, str, 0);
		if(dummyNode != null)
		{
			firstC = dummyNode.next;			//reset firstC to correct node
			dummyNode = null;					//trash the dummy
		}
		length = length - removeLength + stringLength;
		return this;
	}

	// Reverse the characters in the current MyStringBuilder2 and then
	// return the current MyStringBuilder2.
	public MyStringBuilder2 reverse()
	{
		if(length < 2)
			{/*change nothing*/}
		else
		{
			lastC = firstC;
			reverseRecurse(firstC);
			lastC.next = null;
		}
		return this;
	}

	//recursive reverse()
	private void reverseRecurse(CNode previous)
	{
		CNode current = previous.next;
		if(current.next == null)		//base case end of list
			firstC = current;
		else							//recursive case
			reverseRecurse(current);
		current.next = previous;		//finally
	}

	// Return as a String the substring of characters from index "start" to
	// index "end" - 1 within the current MyStringBuilder2
	public String substring(int start, int end)
	{
		if(start < 0 || end > length || start >= end)
			throw new IndexOutOfBoundsException("One or more indices invalid");
		else
		{
			CNode startNode = getNodeAt(start, 0, firstC);
			int substringLength = end - start;
			char[] result = new char[substringLength];
			buildSubstring(0, startNode, substringLength - 1, result);
			return new String(result);
		}
	}

	//recursive helper for substring() method
	private void buildSubstring(int index, CNode current, int last, char[] c)
	{
		if(index > last || current == null)	//base case finished
			return;
		else								//recursive case build rest
		{
			c[index] = current.data;
			buildSubstring(index + 1, current.next, last, c);
		}
	}

	// Return the entire contents of the current MyStringBuilder2 as a String
	// Used "as is" from the Assignment 3 specifications
	public String toString()
	{
		char [] c = new char[length];
      	getString(c, 0, firstC);
      	return (new String(c));
	}

	//recursive workhorse for toString()
	// Used "as is" from the Assignment 3 specifications
	private void getString(char [] c, int pos, CNode curr)
	{
		if (curr != null)
      	{
            c[pos] = curr.data;
            getString(c, pos+1, curr.next);
      	}
	}

	// Find and return the index within the current MyStringBuilder2 where
	// String str LAST matches a sequence of characters within the current
	// MyStringBuilder2.  If str does not match any sequence of characters
	// within the current MyStringBuilder2, return -1.
	public int lastIndexOf(String str)
	{
		if(str == null)					//invalid input
			return -1;
		else if (str.length() == 0)		//always find empty string @ front
			return 0;
		else							//call recursive solver
			return lastIndexOfRecurse(str, firstC, 0);
	}

	//recursive solver for lastIndexOf()
	private int lastIndexOfRecurse(String key, CNode current, int index)
	{
		int recursiveResult;
		if(current == null)				//base case: end of MyStringBuilder2
			return -1;
		else							//traverse to the end first
			recursiveResult = lastIndexOfRecurse(key, current.next, index + 1);
		if (recursiveResult != -1)		//pass on answer if found
			return recursiveResult;
		else							//else check local index for a match
			return (matchString(key, current, 0)) ? index : -1;
	}

	// Find and return an array of MyStringBuilder2, where each MyStringBuilder2
	// in the return array corresponds to a part of the match of the array of
	// patterns in the argument.  If the overall match does not succeed, return
	// null.
	// Helper methods: initializeMSBArray(), matchInner, matchOuter, trimBuilder
	//				   containsChar()
	// NB: This method DOES NOT verify that the strings IN the input array
	//	   are non-null before dereferencing them!
	public MyStringBuilder2[] regMatch(String [] pats)
	{
		if(pats == null || pats.length == 0)		//reject invalid inputs
			return null;
		MyStringBuilder2[] answer = new MyStringBuilder2[pats.length];
		initializeMSBArray(answer, 0);
		return matchOuter(pats, firstC, answer);
	}

	//Recursively initialize all indices in the input array of MyStringBuilder2
	//to new, empty, MyStringBuilder2 objects to use for regMatch()'s result.
	private MyStringBuilder2[] initializeMSBArray(MyStringBuilder2[] a, int i)
	{
		if(i < a.length)
		{
			a[i] = new MyStringBuilder2();
			initializeMSBArray(a, i + 1);
		}
		return a;
	}

	//Recursively determine if a string contains the given char
	//@ param which char in the string to check next
	private boolean containsChar(String s, char c, int index)
	{
		if(index == s.length())
			return false;
		else if(s.charAt(index) == c)
			return true;
		else
			return containsChar(s, c, index + 1);
	}

	//Starts regular expression matching on characters in this MSB2 which fit the
	//first pattern. Calls matchInner() to complete regMatch().
	private MyStringBuilder2[] matchOuter(String[] patterns, CNode current,
		MyStringBuilder2[] answer)
	{
		if(current == null)							//base case not found
			return null;
		if(containsChar(patterns[0], current.data, 0))	//try to match
		{							//further if first char fits the first pattern
			MyStringBuilder2[] recursiveResult = matchInner(patterns, 0, current, answer);
			if(recursiveResult != null)		//nonempty return value
				return recursiveResult;		//base case found recursively
		}
		//recursive case: try next node
		return matchOuter(patterns, current.next, answer);
	}

	//recursive method checks for regMatch() pattern matches which begin
	//with the current node
	//returns an array with the match if it exists, otherwise returns null
	private MyStringBuilder2[] matchInner(String[] patterns, int patternIndex,
		CNode current, MyStringBuilder2[] result)
	{
		if(current == null || patternIndex == patterns.length)		//end case
			return result[result.length - 1].length > 0 ? result : null;
		if(containsChar(patterns[patternIndex], current.data, 0))
		{
			result[patternIndex].append(current.data);
			MyStringBuilder2[] match1 =
				matchInner(patterns, patternIndex, current.next, result);
			if(match1 != null)							//case found recursively
				return result;
			else
				result[patternIndex].trimBuilder();		//backtrack case
		}
		if(result[patternIndex].length > 0)		//only try next pattern if this one
		{										//has a match already
			MyStringBuilder2[] match2 =
				matchInner(patterns, patternIndex + 1, current, result);
			if(match2 != null || patternIndex == patterns.length-1)	//found
				return result;
		}
		return null;							//second backtrack case
	}

	//painfully inefficient helper method for regMatch
	//Removes the last char in this MyStringBuilder2
	//Returns false if the operation fails, otherwise true
	private boolean trimBuilder()
	{
		if(length == 0)				//empty list, trim fails
			return false;
		else if (length == 1)		//only node case, clear list
			initializeEmpty();
		else
		{
			CNode nodeBefore = getNodeAt(length - 2, 0, firstC);
			nodeBefore.next = null;
			lastC = nodeBefore;
			length--;
		}
		return true;				//operation succeeds in case 2 & 3
	}

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
