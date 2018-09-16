public class TrieDictionary
{
    private Node root;

    public TrieDictionary()
    {
        root = new Node();
    }

    public void load(String key)
    {
        Node finger = root;
        for (int i = 0; i < key.length(); i++)
        {
            char letter = key.charAt(i);
            int index = letter - 97;
            if(finger.children[index] == null)
            {
                Node next = new Node();
                finger.isParent = true;
                finger.children[index] = next;
                finger = next;
            }
            else
            {
                finger = finger.children[index];
            }
        }
        finger.isWord = true;
    }

    public boolean contains(String target)
    {
        Node finger = root;
        for (int i = 0; i < target.length(); i++)
        {
            char letter = target.charAt(i);
            int index = letter - 97;
            if(finger.children[index] == null)
            {
                return false;
            }
            else
            {
                finger = finger.children[index];
            }
        }
        return finger.isWord;
    }

    public boolean isPrefix(String target)
    {
        Node finger = root;
        for (int i = 0; i < target.length(); i++)
        {
           char letter = target.charAt(i);
           int index = letter - 97;
           if(finger.children[index] == null)
           {
               return false;  //don't keep looking if no path
           }
           else
           {
               finger = finger.children[index];
           }
       }
       return finger.isParent;
    }
}

class Node
{
    Node[] children;
    boolean isWord;
    boolean isParent;

    Node()
    {
        isWord = false;
        isParent = false;
        children = new Node[26];
    }
}
