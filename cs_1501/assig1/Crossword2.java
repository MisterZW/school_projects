/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 1501 Summer 2018 (enrolled in W section)
* Assignment #1   Crossword Solver
* Code for part A
* This version employs a more efficient backtracking heuristic than Crossword.java
*/

import java.io.*;
import java.util.*;

public class Crossword2
{
	int n;								//board size (n x n)
	DictInterface d;					//reference to DLB or MyDictionary
	char[][] board;						//board tiles as specified in input file
	StringBuilder[] rows, columns;		//to hold solutions as they are built
	char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
	 	'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	int solutions;
	boolean containsWalls;				//track board complexity
										//use a simpler algo if no walls exist

	public Crossword2(String dictType, String boardName) throws FileNotFoundException
	{
		// Command line arg[0] should be DLB to use a de la Brandais Trie for the DictInterface
		// Command line arg[0] should be MWT to use a Multi-way Trie for the DictInterface
		// or any other string to use MyDictionary
		if (dictType.equals("DLB"))
			d = new DLB();
		else if(dictType.equals("MWT"))
			d = new MWT();
		else
			d = new MyDictionary();

		//dictionary hardcoded here
		Scanner fileScan = new Scanner(new FileInputStream("dict8.txt"));
		while (fileScan.hasNext())
			d.add(fileScan.nextLine());

		// arg[1] is the file boardName to solve
		Scanner boardScan = new Scanner(new FileInputStream(boardName));
		n = boardScan.nextInt();			//get board size (N x N grid)

		boardScan.nextLine();				//consume '\n' after board size
		board = new char[n][n];
		for(int i = 0; i < n; i++)			//build board
		{
			String s = boardScan.nextLine();
			for(int j = 0; j < n; j++)
			{
				board[i][j] = Character.toLowerCase(s.charAt(j));
				if (s.charAt(j) == '-')
					containsWalls = true;
			}
		}

		initializeBuilders();
		solutions = 0;
		solveTile(0, 0);
		if(solutions == 0)
			System.out.println("There are no solutions");
		else if(solutions == 1)
			System.out.println("There is only one solution");
		else
			System.out.println("There are " + solutions + " solutions");
	}

	public static void main(String[] args) {
		if (args.length < 2)
		{
			System.err.println("Usage: java Crossword <dictionary_type> <board_file>");
			System.exit(1);
		}
		try
		{
			new Crossword2(args[0], args[1]);
		}
		catch (FileNotFoundException e)
		{
			System.err.println(e.getMessage() +"\nPlease provide a valid filepath.");
			System.exit(2);
		}
	}

	//This is the main recursive backtracking method which fills the board
	//r is the current row and c is the current column
	//@ return the number of tiles left to backtrack
	private int solveTile(int r, int c)
	{
		if(r == n)	//solution has been found, process result
		{
			if(solutions % 10000 == 0)
				printSolution();	//print only select solutions to avoid flooding output
			solutions++;
			return 0;
		}
		boolean columnIsLegit = false;
		char currTile = board[r][c];			//get instruction
		if(currTile == '+')						//generate solutions for '+' tiles
		{
			for(int i = 0; i < 26; i++)			//try all letters in sequence
			{
				columns[c].append(alphabet[i]);
				if(verifyBuilder(columns[c]))
					columnIsLegit = true;
				else
				{
					columns[c].deleteCharAt(r);
					continue;					//try next letter
				}
				rows[r].append(alphabet[i]);
				if(!verifyBuilder(rows[r]))
				{
					columns[c].deleteCharAt(r);
					rows[r].deleteCharAt(c);
					continue;					//try next letter
				}
				int tilesToBacktrack;			//determined by recursive calls
				if(c == n - 1)
				{
					//recursive case move to next row
					tilesToBacktrack = solveTile(r + 1, 0);
				}
				else
				{
					//recursive case move down the row
					tilesToBacktrack = solveTile(r, c + 1);
				}
				rows[r].deleteCharAt(c);	//backtrack here if no letter works
				columns[c].deleteCharAt(r);
				if (tilesToBacktrack > 0)
					return (tilesToBacktrack - 1);
			}
		}
		else	//currTile is a character which must be used as is (includes '-')
		{
			columns[c].append(currTile);
			if(!verifyBuilder(columns[c]))
			{
				columns[c].deleteCharAt(r);
				return n -1;				//backtrack currTile breaks column
			}
			else
				columnIsLegit = true;
			rows[r].append(currTile);
			if(!verifyBuilder(rows[r]))
			{
				columns[c].deleteCharAt(r);
				rows[r].deleteCharAt(c);
				return 0;					//backtrack currTile breaks row
			}
			int tilesToBacktrack;			//determined by recursive calls
			if(c == n - 1)
				//recursive case move to next row
				tilesToBacktrack = solveTile(r + 1, 0);
			else
				//recursive case move down the row
				tilesToBacktrack = solveTile(r, c + 1);
			rows[r].deleteCharAt(c);	//backtrack here if recursion fails
			columns[c].deleteCharAt(r);
			if (tilesToBacktrack > 0)
				return tilesToBacktrack - 1;
		}
		return (columnIsLegit ? 0 : n - 1);
	}

	//This helper method checks that the most recently added word/substring
	//is a valid addition to the StringBuilder s. Returns true if solveTile()
	//can continue forward with this configuration, false if this configuration
	//is a dead end and solveTile() must backtrack
	// --THIS METHOD IS THE "PRUNING" PART OF THE ALGORITHM--
	private boolean verifyBuilder(StringBuilder s)
	{
		if (s.length() < 2)
			return true;
		if (!containsWalls)
			return simplifiedVerifyBuilder(s);
		int start;
		int end = s.length() - 1;
		if(s.charAt(end) == '-' && s.charAt(end - 1) != '-') //there is an
		{													 //unverified word
			do
				end--;
			while(s.charAt(end) == '-' && end > 0);			 //isolate it
			start = end;
			while(start > 0 && s.charAt(start - 1) != '-')
				start--;
			if(end > start)
				return d.searchPrefix(s, start, end) > 1; //word or word & prefix
			else
				return true;
		}
		else
		{
			start = end;
			while(start > 0 && s.charAt(start - 1) != '-')
				start--;
			if (start == end)
				return true;
			else if (end == n - 1)
				return d.searchPrefix(s, start, end) > 1; //word or word & prefix
			else
				return d.searchPrefix(s, start, end) > 0; //word, prefix, or both
		}
	}

	//this version of verifyBuilder() is called if the board contains no walls
	//this is a much simpler procedure and saves time on the all '+' boards
	private boolean simplifiedVerifyBuilder(StringBuilder s)
	{
		if (s.length() == n)
			return d.searchPrefix(s) > 1; //word or word & prefix
		else
			return d.searchPrefix(s) > 0; //word, prefix, or both;
	}

	//helper function performs the simple output
	//exits the program if we're using MyDictionary
	private void printSolution()
	{
		System.out.println("Solution Found:");
		for(StringBuilder b : rows)
			System.out.println(b);
		System.out.println();
		if(d instanceof MyDictionary)
			System.exit(0);		//MyDictionary is too slow to do all solutions
	}

	//sets up the StringBuilders solveTile() uses to keep track of the state
	private void initializeBuilders()
	{
		rows = new StringBuilder[n];
		columns = new StringBuilder[n];
		for(int i = 0; i < n; i++)			//need n builders each for rows/columns
		{
			rows[i] = new StringBuilder(n);		//each will need n space
			columns[i] = new StringBuilder(n);	//might as well allocate it now
		}
	}

}
