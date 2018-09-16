/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #2, Part A
* This is simple utility that prints the strings found in a file
* mystrings considers ASCII values between 32 and 126 valid
* mystrings prints only strings >=4 characters long
*/

#include <stdio.h>
#include <stdlib.h>

int main(int argc, const char *argv[])
{
	if(argc < 2)
	{
		fprintf(stderr, "%s\n\n", "Usage: ./mystrings [filename]");
		return 1;
	}

	FILE *file_ref;
	file_ref = fopen(argv[1], "rb");  //try to open file in binary format for reading only
	if (file_ref == NULL)
	{
		fprintf(stderr, "%s%s\n\n", "Error: could not open ", argv[1]);
		return 2;
	}

	char *whole_file;
	long file_size;

	fseek(file_ref, 0, SEEK_END);	//find the filesize by seeking to the end
	file_size = ftell(file_ref);	//then call ftell()
	fseek(file_ref, 0, SEEK_SET);   //then seek back to the beginning to read it

	whole_file = malloc(file_size + 1); //make the buffer big enough to hold the whole file
	if(whole_file == NULL)
	{
		fprintf(stderr, "%s\n\n", "Error: malloc() failed, OUT OF MEMORY");
		return 3;
	}
	whole_file[file_size] = '\0';	//null terminate the buffer to make print loop simpler

	//read the whole file into memory and then close the file reference
	int read_result = fread(whole_file, file_size, 1, file_ref);
	fclose(file_ref);

	if(read_result < 0)
	{
		fprintf(stderr, "%s%s\n\n", "Error: could not read ", argv[1]);
		return 2;
	}
	
	//print all valid strings
	int s, e; 						//the starting and ending index of the next string to print
	for(e = 0, s = 0; e < file_size; e++)
	{
		char c = whole_file[e];
		if(c < 32 || c > 126)		//if the next character is invalid, print what we've got
		{							//will trigger on the final '\0' in the buffer at the end also
			int str_len = e - s;	//print it if it's at least 4 chars long, otherwise skip it
			if(str_len > 3)			//then, move the start index up and start fresh
				printf("%.*s\n", str_len, whole_file + s);
			s = e + 1;				
		}													
	}

	free(whole_file);
	return 0;
}