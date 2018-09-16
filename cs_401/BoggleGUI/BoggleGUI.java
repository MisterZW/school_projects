import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.*;
import java.io.*;

public class BoggleGUI
{
	JFrame window;
	Container content;
	JTextArea wordsFound;
	JScrollPane scroller;
	JButton randomize, solve, load;
	JTextField dimension, inputFile;
	JPanel leftPanel, boardPanel;
	String[] alphabet = {	"a","b","c","d","e","f","g","h","i",
								"j","k","l","m","n","o","p","qu","r",
								"s","t","u","v","w","x","y","z" };
	JButton[][] boardButtons;
	BoggleBoard gameBoard;
	TrieDictionary dictionary;
	final int DEFAULT_SIZE = 4;

	@SuppressWarnings("unchecked")
	public BoggleGUI(String[] cmd) throws Exception
	{
		window = new JFrame("Boggle Solver");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		content = window.getContentPane();
		content.setLayout(new GridLayout(1,3));
		ButtonListener listener = new ButtonListener();

		wordsFound = new JTextArea();
		wordsFound.setFont(new Font("verdana", Font.PLAIN, 18));
		wordsFound.setEditable(false);
		scroller = new JScrollPane(wordsFound);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		randomize = new JButton("RANDOMIZE");
		randomize.setFont(new Font("Verdana", Font.BOLD, 32));
		randomize.setBackground(Color.BLUE.darker());
		randomize.setForeground(Color.WHITE);
		randomize.addActionListener(listener);

		load = new JButton("LOAD BOARD");
		load.setFont(new Font("verdana", Font.BOLD, 32));
		load.setBackground(Color.BLUE.darker());
		load.setForeground(Color.WHITE);
		load.addActionListener(listener);

		solve = new JButton("SOLVE BOARD");
		solve.setFont(new Font("verdana", Font.BOLD, 32));
		solve.setBackground(Color.RED.darker());
		solve.setForeground(Color.WHITE);
		solve.addActionListener(listener);

		dimension = new JTextField("type size here (default: 4x4)");
		dimension.setFont(new Font("verdana", Font.PLAIN, 18));

		inputFile = new JTextField("type boggle filename here");
		inputFile.setFont(new Font("verdana", Font.PLAIN, 18));

		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(5,1));
		leftPanel.add( randomize );
		leftPanel.add( dimension );
		leftPanel.add( load );
		leftPanel.add( inputFile );
		leftPanel.add( solve );

		if (cmd.length > 0)
			gameBoard = new BoggleBoard(cmd[0]);  //load from cmd
		else
			gameBoard = new BoggleBoard();        //default

		//load dictionary into a Trie
		dictionary = new TrieDictionary();
		BufferedReader dictFile = new BufferedReader(new FileReader("dictionary.txt"));
		while (dictFile.ready())
		{
			dictionary.load(dictFile.readLine());
		}

		window.setSize(1200, 800);
		window.setVisible( true );
	}

	class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Component whichButton = (Component) e.getSource();

			if (whichButton == randomize)
			{
				String output;
				try
				{
					int dimSize = Integer.parseInt(dimension.getText());
					gameBoard = new BoggleBoard(dimSize);
				}
				catch (Exception ex)
				{
					gameBoard = new BoggleBoard();
				}
			}
			if (whichButton == load)
			{
				try
				{
					gameBoard = new BoggleBoard(inputFile.getText());
				}
				catch (Exception problem)
				{
					//class constructor will handle the problem
				}
			}

			if (whichButton == solve)
				gameBoard.solveBoard();
		}
	}

	class BoggleBoard {

		private String[][] board;
		private int boardSize;
		private TreeSet solutions;

		public BoggleBoard()
		{
			this(DEFAULT_SIZE);
		}

		public BoggleBoard(int size)
		{
			if(!validateBoard(size))
				return;
			boardSize = size;
			board = new String[boardSize][boardSize];
			Random generator = new Random();
			int next;
			for (int i = 0; i < boardSize; i++)
				for (int j = 0; j < boardSize; j++)
					board[i][j] = alphabet[generator.nextInt(26)];
			updateBoard();
		}

		public BoggleBoard(String filePath)
		{
			try
			{
				Scanner boardFile = new Scanner(new File(filePath));
				int dim = boardFile.nextInt();
				if(!validateBoard(dim))
					return;
				boardSize = dim;
				board = new String[boardSize][boardSize];
				for (int i = 0; i < boardSize; i++)
					for (int j = 0; j < boardSize; j++)
						board[i][j] = boardFile.next();
				updateBoard();
			}
			catch (Exception ex)
			{
				wordsFound.setText("File load error.");
			}
		}

		public boolean validateBoard(int size)
		{
			if (size < 2)
			{
				wordsFound.setText("Min board size 100 x 100");
				return false;
			}
			else if(size > 100)
			{
				size = 100;
				wordsFound.setText("Max board size 100 x 100");
				return false;
			}
			else return true;
		}

		public void updateBoard()
		{
			wordsFound.setText("");
			content.removeAll();
			boardPanel = new JPanel();
			boardButtons = new JButton[boardSize][boardSize];
			boardPanel.setLayout(new GridLayout(boardSize,boardSize));

			for ( int r=0 ; r < boardButtons.length ; ++r )
				for ( int c=0 ; c < boardButtons.length ; ++c )
				{
					boardButtons[r][c] = new JButton(board[r][c]);
					boardButtons[r][c].setFont(new Font("verdana", Font.BOLD, 32));
					boardButtons[r][c].setBackground(Color.WHITE);
					boardButtons[r][c].setForeground(Color.BLUE);
					boardButtons[r][c].setBorder(new LineBorder(Color.BLUE, 2));
					boardPanel.add( boardButtons[r][c] );
				}

			content.add(leftPanel);
			content.add(boardPanel);
			content.add(scroller);
			content.validate();
			content.repaint();
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
			result += "Found " + solutions.size() + " words.";
			wordsFound.setText(result);
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

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		new BoggleGUI(args);
	}
}
