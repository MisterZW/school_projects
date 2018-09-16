USING THE BOGGLE SOLVER GUI:

The boggle solver loads dictionary.txt automatically. It supports loading a board text file as the first command line argument, but will load a random 4x4 board by default if one is not provided.

The "RANDOMIZE" button will try to make a new random board of an integer size provided in the dimension JTextField (right below randomize), but will make 4x4 instead if the data there is empty or cannot be treated as an integer.

I set the program to only accept board sizes between 2x2 and 100x100, party to limit graphic issues, partly as a cap to the depth of the recursion, and partly to reject negative board sizes.

The "LOAD BOARD" button will try to load the input text file with the String filename specified in the JTextField below it. The program will display an error message if the filepath is invalid.

The "SOLVE BOARD" button is pretty obvious --> give it a try!


