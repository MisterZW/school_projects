public class MWT implements DictInterface{
	
	private static final int m = 26;  //the alphabet size
	private static final int NEITHER_WORD_NOR_PREFIX = 0;
    private static final int PREFIX_ONLY = 1;
    private static final int WORD_ONLY = 2;
    private static final int WORD_AND_PREFIX = 3;

    private MWNode root;

	public MWT()
	{
		root = new MWNode();
	}

	@Override //DictInterface method
    public boolean add(String s)
    {
    	MWNode current = root;
        for (int i = 0; i < s.length(); i++)
        {
        	int index = s.charAt(i) - 'a';
            if(current.children[index] == null)
            {
            	current.hasChildren = true;
                MWNode next = new MWNode();
                current.children[index] = next;
                current = next;
            }
            else
                current = current.children[index];
        }
        if(current.isWord)
        	return false;
        current.isWord = true;
        return true;
    }


    @Override //DictInterface method
    public int searchPrefix(StringBuilder s)
    {
    	MWNode current = root;
    	for(int i = 0; i < s.length(); i++)
    	{
    		int index = s.charAt(i) - 'a';
    		if(current.children[index] == null)
    			return NEITHER_WORD_NOR_PREFIX;
    		else
    			current = current.children[index];
    	}
    	if(current.isWord)
    		return current.hasChildren ? WORD_AND_PREFIX: WORD_ONLY;
    	else
    		return PREFIX_ONLY;
    }

    @Override //DictInterface method
	public int searchPrefix(StringBuilder s, int start, int end)
	{
		MWNode current = root;
    	for(int i = start; i <= end; i++)
    	{
    		int index = s.charAt(i) - 'a';
    		if(current.children[index] == null)
    			return NEITHER_WORD_NOR_PREFIX;
    		else
    			current = current.children[index];
    	}
    	if(current.isWord)
    		return current.hasChildren ? WORD_AND_PREFIX: WORD_ONLY;
    	else
    		return PREFIX_ONLY;
	}

	private class MWNode
	{
		private boolean isWord;
		private boolean hasChildren;
		private MWNode[] children;
		MWNode()
		{
			isWord = false;
			hasChildren = false;
			children = new MWNode[m];
		}
	}
}