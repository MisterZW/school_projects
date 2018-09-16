/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Lab #3
* This program reads in integers from the user until he/she enters -1
* It stores the data as a linked list and calculates the average\
* The purpose of the lab is to demonstrate the usage of malloc() and free()
*/

#include <stdio.h>
#include <stdlib.h>

struct Node {
	int grade;
	struct Node *next;
};

double get_average();
void free_node(struct Node *current);

struct Node *root;
int N;

int main(void)
{
	struct Node *current;
	int input;
	double average;

	root = malloc(sizeof(struct Node));
	printf("Enter some grades to get the average, or -1 to stop: ");
	scanf("%d", &input);
	if(input != -1)
	{
		root->grade = input;
		root->next = NULL;
		current = root;
		N++;
	}

	while(input != -1)
	{
		printf("Enter another grade, or -1 to stop: ");
		scanf("%d", &input);
		if(input != -1)
		{
			current->next = malloc(sizeof(struct Node));
			current = current->next;
			current->grade = input;
			current->next = NULL;
			N++;
		}
	}
	
	if(N == 0)
		printf("\nThe list is empty, so there is no average to show\n\n");
	else
	{
		average = get_average();
		printf("\nThe average grade is %3.2f\n\n", average);
	}
	
	//cleanup
	free_node(root);  
	root = NULL;
	current = NULL;
	return 0;
}

//frees the list recursively
void free_node(struct Node *current)
{
	if(current == NULL)
		return;
	if(current->next != NULL)
		free_node(current->next);
	free(current);
}

//returns average of the list
//assert: N != 0 because condition is checked in main
double get_average()
{
	double result = 0;
	struct Node *curr = root;

	while(curr != NULL)
	{
		result += curr->grade;
		curr = curr->next;
	}
	return (result/N);
}
