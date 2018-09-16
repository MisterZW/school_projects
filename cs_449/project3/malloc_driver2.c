/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #3, a custom malloc()
*
* This is a (second) test driver for mymalloc.c
* It is meant to be run while mymalloc.c is in TEST mode
*/

#include "mymalloc.h"
#include <unistd.h>
#include <stdio.h>
#include <string.h>

void *stuff[10];
int sizes[] = {100, 50, 25, 100, 25, 100, 100, 25, 50, 100};

void fillArray(void);
void freeEvenItems(void);
void freeAll(void);

int main(void)
{
	printf("---making an array to test the bestfit malloc algorithm ---\n\n");
	fillArray();
	printf("\nhere is the current brk value %p\n\n", sbrk(0));

	freeEvenItems();

	printf("\n\n---malloc all the same size regions, but in reverse order---\n\n");
	printf("---Verifies that best fit is working correctly for exact matches---\n\n");
	for(int i = 9; i >= 0 ; i--)
	{
		if(i % 2 == 0)
		{
			printf("reallocating stuff[%d] with a size of %d\n", i, sizes[i]);
			stuff[i] = my_bestfit_malloc(sizes[i]);
		}
	}

	freeEvenItems();

	printf("\n\n---Verify that non-exact matches without enough space for new nodes works---\n\n");
	printf("We're allocating the same spaces as before, except all 1 byte smaller\n");
	printf("There should be no splits; in fact, this should work exactly the same as before\n\n");
	for(int i = 9; i >= 0 ; i--)
	{
		if(i % 2 == 0)
		{
			printf("reallocating stuff[%d] with a size of %d\n", i, sizes[i] - 1);
			stuff[i] = my_bestfit_malloc(sizes[i] - 1);
		}
	}

	printf("\n\nFreeing and refilling the array because allocations are out of order on the heap now---\n\n");
	freeAll();
	fillArray();

	printf("\n---verify that the splitting node size and functionality is correct ---\n\n");
	printf("freeing two nodes of contiguous memory each size 100 bytes\n\n");
	my_free(stuff[5]);
	my_free(stuff[6]);
	printf("reallocating 50 bytes should cause a split\n\n");
	stuff[5] = my_bestfit_malloc(50);
	printf("reallocating 50 bytes should cause another split\n\n");
	stuff[6] = my_bestfit_malloc(50);

	printf("Item 5 and 6 should be the same size\n\nThere should also be a free block of 100 - sizeof(node)\n\n");
	printHeap();

	return 0;
}

void fillArray(void)
{
	for(int i = 0; i < 10; i++)
	{
		printf("Allocating stuff[%d] with a size of %d\n", i, sizes[i]);
		stuff[i] = my_bestfit_malloc(sizes[i]);
	}
}

//frees every other item
//should leave gaps, but the last slot will be lost moving back brk
void freeEvenItems(void)
{
	printf("Freeing all the even numbered regions to make free space without coalescing\n\n");
	for(int i = 0; i < 10; i++)
	{
		if(i % 2 == 0)
		{
			printf("freeing stuff[%d] with a size of %d\n", i, sizes[i]);
			my_free(stuff[i]);
		}
	}
}

void freeAll(void)
{
	for(int i = 0; i < 10; i++)
		my_free(stuff[i]);
}
