import java.util.Arrays;

/*************************************************************************
 *
 *  Zachary Whitney  zdw9  ID: 3320178
 *  CS 1501 Summer 2018 (enrolled in W section)
 *  Assignment #3 -- Modify the LZW algorithm for efficiency and adaptability
 *
 *  This is a 256-way trie implementation tailored for use by LZWmod.java
 *  This trie stores the state information for the LZW compress algorithm
 *
 *************************************************************************/

public class MWTforLZW<V> {

	private static final int M = 256;  //the alphabet size

    private MWNode<V> root;
	private MWNode<V> recent;  //internal iterator optimizes trie ops

	public MWTforLZW()
	{
		root = new MWNode<V>();
		recent = root;
	}

	//call this method when dumping the dictionary to go back to all ASCII
    public final void resetToASCII()
    {
        for(int i = 0; i < M; i++)
        {
         	if (root.children[i] != null)
			{
            	for(int j = 0; j < M; j++)
                	Arrays.fill(root.children[i].children, null);
			}
        }
    }

	//returns false if the most recent node has no child @ index c
	//otherwise returns true and moves there
	public boolean checkAndAdvancePrefix(char c)
	{
		if(c >= M || recent.children[c] == null)	//mismatch
			return false;
		recent = recent.children[c];    //match, move down the trie
		return true;
	}

	//this is the the method which actually adds the newest codeword
	//after adding this, we need to reset internal iterator to the root
	public void addPrefix(char c, V codeword)
	{
		recent.children[c] = new MWNode<V>(codeword);
		recent = root;
	}

	//returns the codeword at the most recently accessed node
	public V getCode()
	{
		return recent.value;
	}

	//simple method to initialize the trie's root with single char values
    public final void addASCII(char key, V codeword)
    {
        if(root.children[key] == null)
            root.children[key] = new MWNode<V>(codeword);
		else
			root.children[key].value = codeword;
    }

	private static class MWNode<V>
	{
		private MWNode<V>[] children;
        private V value;

        @SuppressWarnings("unchecked")  //this cast is safe because the array is new
		MWNode()
		{
            value = null;
			children = (MWNode<V>[]) new MWNode<?>[M];
		}

        @SuppressWarnings("unchecked")  //this cast is safe because the array is new
        MWNode(V intialValue)
        {
            value = intialValue;
            children = (MWNode<V>[]) new MWNode<?>[M];
        }
	}
}
