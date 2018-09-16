/*************************************************************************
 *
 *  Zachary Whitney  zdw9  ID: 3320178
 *  CS 1501 Summer 2018 (enrolled in W section)
 *  Assignment #3 -- Modify the LZW algorithm for efficiency and adaptability
 *
 *  Compress or expand binary input from standard input using LZW.
 *  Execution:    java LZWmod - r < input.txt   (compress, throw out dictionary)
 *  Execution:    java LZWmod - n < input.txt   (compress, preserve dictionary)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryStdIn.java, BinaryStdOut.java, MWTforLZW.java
 *
 *************************************************************************/

public class LZWmod {
    private static final int R = 256;           // number of input chars
    private static final int MAX_CODES = 65536; // cap @ 2^16 codewords
    private static int L = 512;                 // number of codewords = 2^W
    private static int W = 9;                   // initial codeword width

    public static void compress(boolean resetDict) 
    {
        BinaryStdOut.write(resetDict);        //add the reset flag as file's first bit
        MWTforLZW<Integer> table = new MWTforLZW<Integer>();   //a 256-way trie
        for (int i = 0; i < R; i++)
            table.addASCII((char)i, i);       //initialize with ASCII values
        int code = R + 1;                     //R is codeword for EOF

        char c = BinaryStdIn.readChar();      //get first char from file
        while(true)
        {
            while(table.checkAndAdvancePrefix(c))   //move down trie until mismatch
                c = BinaryStdIn.readChar();         //get next char from the file

            BinaryStdOut.write(table.getCode(), W); //write longest existing codeword

            if(c == R)              //EOF break here because still need last write
                break;
            if(code == L)           //if out of codewords
            {
                if(W < 16)          //expand codeword size if not yet using 16 bit words
                {
                    W++;
                    L *= 2;
                }
                else if(resetDict)   //reset dictionary only if option is selected
                {
                    resetCodes();
                    table.resetToASCII();
                    code = R + 1;
                }
            }
            if(code < L)
                table.addPrefix(c, code++);  //add codeword to dictionary
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }


    public static void expand() 
    {
        boolean reset = BinaryStdIn.readBoolean(); //was this file compressed with dict reset
        String[] st = new String[MAX_CODES];       //can handle max size at the outset
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while(true) 
        {
            BinaryStdOut.write(val);
            if(i == L)
            {
                if(W < 16)            //expand codeword bits if out of words
                {                               //cap @ 16 bit codewords
                    W++;
                    L *= 2;
                }
                else
                {
                    if(reset)
                    {
                        resetCodes();
                        for(int j = R + 1; j < MAX_CODES; j++)
                            st[j] = null;
                        i = R + 1;
                    }
                }
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;

            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }

    //sets the codeword size and max codeword number back to defaults
    private static void resetCodes()
    {
        W = 9;
        L = 512;
    }

    public static void main(String[] args) {
        if(args.length < 1)
            throw new RuntimeException("Usage: java LZWmod [+/-] [r/n] < file_in > file_out");
        if(args[0].equals("-"))
        {
            if (args.length > 1 && (args[1].equalsIgnoreCase("r") || args[1].equalsIgnoreCase("-r")))
                compress(true);
            else
                compress(false);
        }
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
