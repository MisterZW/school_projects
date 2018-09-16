/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #3, a custom malloc()
*
* This is a test driver for mymalloc.c
* It is meant to be run while mymalloc.c is in TEST mode
*/

#include "mymalloc.h"
#include <unistd.h>
#include <stdio.h>
#include <string.h>

struct ll_node {	
	int number;
	struct ll_node *nex;
}; 

void free_node(struct ll_node *q);
void print_list(void);

struct ll_node *head;
struct ll_node *cur;

int main()
{
	printf("---testing my_bestfit_malloc on 0 parameter ---\n\n");
	my_bestfit_malloc(0);
	printf("---testing my_bestfit_malloc on NEGATIVE parameter ---\n\n");
	my_bestfit_malloc(-42);
	printf("---testing my_free on NULL parameter ---\n\n");
	my_free(NULL);

	printf("---testing moving brk up by mallocing a new linked list on empty heap ---\n\n");
	printf("Creating head\n");
	head = my_bestfit_malloc(sizeof(struct ll_node));
	head->number = 0;

	cur = head;
	struct ll_node *new_node;

	for(int i = 1; i < 10; i++)
	{
		printf("sbrk is %p\n", sbrk(0));
		printf("Creating node %d\n", i);
		new_node = my_bestfit_malloc(sizeof(struct ll_node));
		new_node->number = i;

		cur->nex = new_node;
		cur = new_node;
	}
	cur->nex = NULL;
	printHeap();

	//print_list();

	printf("\n\n---testing that recursively freeing the list works & incrementally resets the heap ---\n\n");
	free_node(head);
	head = NULL;
	printf("\n\n");
	printHeap();

	printf("\n\n---testing variable length sequences, out of order freeing, and coalesce ---\n\n");

	printf("Making a length 3 array\n");
	char *array[3];
	char *a = "A";      //2 chars long
	char *b = "ABC";	//4 chars long
	char *c = "ABCDE";  //6 chars long

	array[0] = my_bestfit_malloc(2);
	array[1] = my_bestfit_malloc(4);
	array[2] = my_bestfit_malloc(6);
	strcpy(array[0], a);
	strcpy(array[1], b);
	strcpy(array[2], c);

	printf("\nHERE IS THE LIST:\n");
	for(int i = 0; i < 3; i++)
		printf("%s\n", array[i]);
	
	printf("\nFreeing the middle element -- should not trigger coalesce\n"); 
	my_free(array[1]);
	printf("\nFreeing the first element -- should trigger coalesce with successor node\n");
	my_free(array[0]);
	printf("\nFreeing the final element -- should cause the heap to fully collapse\n");
	my_free(array[2]);

	printHeap();

	return 0;
}

void free_node(struct ll_node *q)
{
	if(q->nex != NULL)
		free_node(q->nex);
	printf("FREEING NODE %d\n", q->number);
	my_free(q);
}

void print_list(void)
{
	cur = head;
	printf("\nPRINTING THE TEST LIST'S DATA\n\n");
	while(cur != NULL)
	{
		printf("THE NODE HAS THE VALUE %d\n", cur->number);
		cur = cur->nex;
	}
}

