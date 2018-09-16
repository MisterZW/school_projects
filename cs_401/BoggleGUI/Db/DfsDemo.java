import java.util.*;
import java.io.*;

public class DfsDemo {

    TrieDictionary dictionary;
    TreeSet solutions;

    @SuppressWarnings("unchecked")
    public DfsDemo() throws Exception
    {
        String[][] b = new String[3][3];
        b[0][0] = "a";
        b[0][1] = "b";
        b[0][2] = "c";
        b[1][0] = "d";
        b[1][1] = "e";
        b[1][2] = "f";
        b[2][0] = "g";
        b[2][1] = "h";
        b[2][2] = "i";

        solveBoard(b);

        System.out.println("Found " + solutions.size() + " words.");
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        new DfsDemo();
    } //end main

    @SuppressWarnings("unchecked")
    public void solveBoard(String[][] q) throws Exception
    {
        solutions = new TreeSet<String>();
        BufferedReader dictFile = new BufferedReader(new FileReader("dictionary.txt"));
        dictionary = new TrieDictionary();
		while (dictFile.ready())
		{
			dictionary.load(dictFile.readLine());
		} //end while
        for (int r = 0; r < q.length; r++)
            for (int c = 0; c < q.length; c++)
                depthFirstSearch(r, c, "", q);
        String result = "";
        Iterator<String> parser = solutions.iterator();
        while(parser.hasNext())
        {
            result += parser.next() + System.lineSeparator();
        }
        System.out.println(result);
    }

@SuppressWarnings("unchecked")
public void depthFirstSearch(int row, int column, String path, String[][] board) throws Exception
{
    String thisLetter = board[row][column];
    path += thisLetter;  //add current letter
    board[row][column] = "";  //mark current as visited

    //add hits to solutions list
    if(dictionary.contains(path))
    {
        solutions.add(path);
    }
    //abandon dead ends
    else if (!dictionary.isPrefix(path))
    {
        board[row][column] = thisLetter; //unmark
        return;
    }

    //System.out.println(path);
    //recursive step
    for (int y = row - 1; y <= row + 1; y++)
    {
        for (int x = column - 1; x <= column + 1; x++)
        {
            try
            {
                if(!board[y][x].equals(""))
                    depthFirstSearch(y, x, path, board);
            }
            catch (ArrayIndexOutOfBoundsException whoops)
            {
                //do nothing; square is off the board
            }
        } //end inner for
    } //end outer for
    board[row][column] = thisLetter;  //unmark b4 returning
} //end dfs

}//end class
