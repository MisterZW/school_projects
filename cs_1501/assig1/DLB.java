/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 1501 Summer 2018 (Enrolled in W section)
* Assignment #1   Crossword Solver
* Code for part B
*/

public class DLB implements DictInterface{

    private Node root;
    private static final char TERMINATOR = '%';  //signifies end of word
    private static final int NEITHER_WORD_NOR_PREFIX = 0;
    private static final int PREFIX_ONLY = 1;
    private static final int WORD_ONLY = 2;
    private static final int WORD_AND_PREFIX = 3;

    public DLB()
    {
        root = new Node();  //dummy node avoids some null tests
    }

    //hardcoded to return true since we don't care if it was a duplicate or not
    @Override //DictInterface method
    public boolean add(String s)
    {
        s += TERMINATOR;
        Node curr = root;
        outer_loop:
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(curr.child == null)              //just make a path down if
            {                                   //none exists
                curr.child = new Node(c);
                curr = curr.child;
            }
            else                                //go to nonnull node
            {
                curr = curr.child;
                if(curr.data == c)              //check if path already exists
                    continue;
                while(curr.sibling != null)     //check for path among siblings
                {
                    curr = curr.sibling;
                    if(curr.data == c)          //char exists as a sibling
                        continue outer_loop;    //set curr here and continue
                }
                //if I get here, I have to add a sibling and move there
                curr.sibling = new Node(c);
                curr = curr.sibling;
            }
        }
        return true;
    }

    /* Returns 0 if s is not a word or prefix within the DictInterface
     * Returns 1 if s is a prefix within the DictInterface but not a
     *         valid word
     * Returns 2 if s is a word within the DictInterface but not a
     *         prefix to other words
     * Returns 3 if s is both a word within the DictInterface and a
     *         prefix to other words
     */
    //note: this method does not verify that its parameters are valid
    @Override //DictInterface method
    public int searchPrefix(StringBuilder s)
    {
        Node curr = root.child;
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            while(curr != null && curr.data != c)
                curr = curr.sibling;
            if(curr == null)            //ran out of path early
                return NEITHER_WORD_NOR_PREFIX;
            curr = curr.child;
        }
        while(curr != null && curr.data != TERMINATOR)
            curr = curr.sibling;
        if(curr == null)                //no terminator
            return PREFIX_ONLY;
        else if(curr.sibling == null)   //terminator w/ no siblings
            return WORD_ONLY;
        else
            return WORD_AND_PREFIX;
     }

    //note: this method does not verify that its parameters are valid
    @Override //DictInterface method
	public int searchPrefix(StringBuilder s, int start, int end)
    {
        Node curr = root.child;
        for(int i = start; i <= end; i++)
        {
            char c = s.charAt(i);
            while(curr != null && curr.data != c)
                curr = curr.sibling;
            if(curr == null)            //ran out of path early
                return NEITHER_WORD_NOR_PREFIX;
            curr = curr.child;
        }
        while(curr != null && curr.data != TERMINATOR)
            curr = curr.sibling;
        if(curr == null)                //no terminator
            return PREFIX_ONLY;
        else if(curr.sibling == null)   //terminator w/ no siblings
            return WORD_ONLY;
        else
            return WORD_AND_PREFIX;
    }

    //no need for setters/getters because we have direct access
    private class Node {

        private char data;
        private Node sibling;
        private Node child;

        public Node()
        {
            this('\\'); //this is for the root
        }

        public Node(char c)
        {
            data = c;
            sibling = null;
            child = null;
        }
    }
}
