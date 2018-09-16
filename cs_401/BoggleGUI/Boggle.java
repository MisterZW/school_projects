import java.util.*;
import java.io.*;

public class Boggle
{	String[] alphabet = {	"a","b","c","d","e","f","g","h","i",
								"j","k","l","m","n","o","p","qu","r",
								"s","t","u","v","w","x","y","z" };
	BoggleBoard gameBoard;
	TrieDictionary dictionary;
	final int DEFAULT_SIZE = 4;

	public Boggle(String[] cmd) throws Exception
	{
		if (cmd.length > 0)
			gameBoard = new BoggleBoard(cmd[0]);  //load from command line
		else
			gameBoard = new BoggleBoard();        //default

		dictionary = new TrieDictionary();
		BufferedReader dictFile = new BufferedReader(new FileReader("dictionary.txt"));
		while (dictFile.ready())
		{
			dictionary.load(dictFile.readLine());
		}

		gameBoard.solveBoard();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		new Boggle(args);
	}

class BoggleBoard
{
	private String[][] board;
	private int boardSize;
	private TreeSet solutions;

	public BoggleBoard()
	{
		this(DEFAULT_SIZE);
	}

	public BoggleBoard(int size)
	{
		boardSize = size;
		board = new String[boardSize][boardSize];
		Random generator = new Random();
		int next;
		for (int i = 0; i < boardSize; i++)
			for (int j = 0; j < boardSize; j++)
				board[i][j] = alphabet[generator.nextInt(26)];
	}

	public BoggleBoard(String filePath)
	{
		try
		{
			Scanner boardFile = new Scanner(new File(filePath));
			boardSize = boardFile.nextInt();
			board = new String[boardSize][boardSize];
			for (int i = 0; i < boardSize; i++)
				for (int j = 0; j < boardSize; j++)
					board[i][j] = boardFile.next();
		}
		catch (Exception ex)
		{
			System.out.println("Error: could not load file.");
			System.exit(0);
		}
	}

	@SuppressWarnings("unchecked")
	public void solveBoard()
	{
		solutions = new TreeSet<String>();
		for (int r = 0; r < boardSize; r++)
			for (int c = 0; c < boardSize; c++)
				depthFirstSearch(r, c, "", board);
		String result = "";
		Iterator<String> parser = solutions.iterator();
		while(parser.hasNext())
		{
			result += parser.next() + "\n";
		}
		System.out.print(result);
	}

	@SuppressWarnings("unchecked")
	public void depthFirstSearch(int row, int column, String path, String[][] b)
	{
		String thisLetter = b[row][column];
		path += thisLetter;
		b[row][column] = "";  //mark current as visited

		//add hits to solutions list
		if(path.length() > 2 && dictionary.contains(path))
		{
			solutions.add(path);
		}

		//abandon dead ends
		else if (!dictionary.isPrefix(path))
		{
			b[row][column] = thisLetter; //unmark
			return;
		}

		//recursive step
		for (int y = row - 1; y <= row + 1; y++)
		{
			for (int x = column - 1; x <= column + 1; x++)
			{
				try
				{
					if (!b[y][x].equals(""))
						depthFirstSearch(y, x, path, b);
				}
				catch (Exception whoops)
				{
					//do nothing; square is off the board
				}
			}
		}
		b[row][column] = thisLetter;  //unmark
	}

}

	class TrieDictionary
	{
	    private Node root;
	    TrieDictionary()
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
	                finger = finger.children[index];
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
	                return false;
	            else
	                finger = finger.children[index];
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
				   return false;
			   else
				   finger = finger.children[index];
			}
		   return finger.isParent;
		}
	}

	class Node
	{
	    Node[] children;
	    boolean isWord;    //default false
		boolean isParent;  //default false

	    Node()
		{
	        children = new Node[26];
		}
	}
}
