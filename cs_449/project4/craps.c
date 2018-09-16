/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #4, a craps client and linux device driver (d6 roller)
*
* This is a test driver tor the /dev/dice device driver
* It is a simple, loop-driven craps game
* NOTE: This was written to run on the QEMU linux virtual machine (ARCH = i386)
* Use the shell command "make craps" to build
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#define DIE_SIZE 6
#define INPUT_LENGTH 10
#define MAX_NAME_LENGTH 50
#define IMPOSSIBLE_RESULT 13
#define TRUE 1
#define FALSE 0

int roll2(void);

static char player_name[MAX_NAME_LENGTH];
static int fd;   //file descriptor for /dev/dice

int main(void)
{

	fd = open("/dev/dice", O_RDONLY);
	if(fd < 0)
	{
		fprintf(stderr, "ERROR! Could not load the dice device driver...\n\n");
		return 1;
	}

	printf("Welcome to Craps!\n\n");
	printf("Please enter your name --- > ");
	fgets(player_name, MAX_NAME_LENGTH, stdin);
	player_name[strlen(player_name) - 1] = '\0';

	char input[INPUT_LENGTH];

	printf("%s, would you like to play or quit? ---> ", player_name);
	fgets(input, INPUT_LENGTH, stdin);

	//make input comparison case-insensitive
	for(int i = 0; i < INPUT_LENGTH; i++)
		input[i] = tolower(input[i]);

	int result; //to store the sum of both dice

	while(TRUE) //breaks if user does not enter "yes" to play again
	{			//or it user selects quit before playing once
		if(strncmp(input, "quit", 4) == 0)
			break;

		int win, lose, point;
		win = lose = FALSE;
		point = IMPOSSIBLE_RESULT;
		
		//play the first round
		result = roll2();
		if(result == 2 || result == 3 || result == 12)
			lose = TRUE;
		else if (result == 7 || result == 11)
			win = TRUE;
		else
			point = result;

		//rounds 2+ use alternate rules -- 7 loses OR point wins
		while(!win && !lose)
		{
			result = roll2();
			if(result == 7)
				lose = TRUE;
			else if (result == point)
				win = TRUE;

		}

		if(win)
			printf("YOU WIN!\n");
		else
			printf("YOU LOSE!\n");

		printf("\nWould you like to play again? ---> ");
		fgets(input, INPUT_LENGTH, stdin);

		//make input comparison case-insensitive
		for(int i = 0; i < INPUT_LENGTH; i++)
			input[i] = tolower(input[i]);

		if(strncmp(input, "yes", 3) != 0)
			break;
	}

	close(fd);
	printf("\nGoodbye, %s. Thanks for playing craps!\n", player_name);
	return 0;
}

//get two d6 rolls, print them and their sum to console, then return their sum
int roll2()
{
	char buf[2];
	read(fd, buf, 2);
	buf[0]++; //adjust range from [0-5] to [1-6]
	buf[1]++;
	int total = buf[0] + buf[1];
	printf("You have rolled %d + %d = %d\n", buf[0], buf[1], total);
	return total;
}
