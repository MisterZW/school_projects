/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #3, a custom malloc()
* This is a linked-list implementation of malloc() which uses best fit allocation
* This implementation will shrink the heap size if there is free space near brk
*
* #DEFINE TEST TO INCLUDES LOTS OF CONSOLE OUTPUT FOR DEBUGGING & TESTING PURPOSES
* IT IS TOGGLED ON BY CLI ARGUMENT IN THE MAKEFILE'S TEST DRIVER BUILDS
*/

//#define TEST

#include "mymalloc.h"
#include <unistd.h>
#include <stdio.h>

#define TRUE 1
#define FALSE 0

typedef struct my_malloc_node node;
struct my_malloc_node {
	unsigned char used;	    //boolean value 0 for free, else in use
	int size;				//size of the chunk in memory
	node *next;
	node *previous;
};

static void initialize_as_tail(node *ptr, int size);
static void *get_best_fit(int request_size);
static node *coalesce(node *a, node *b);
void printHeap(void);

static node *root = NULL;
static node *tail = NULL;	

void *my_bestfit_malloc(int size)
{
	if(size <= 0)
	{
		printf("Got a bad allocation size for malloc --- returning NULL \n\n");
		return NULL;
	}

	void *allocation = get_best_fit(size);
	node *best_fit = (node *)allocation;

	if(best_fit == NULL)	//need to allocate more space w/ sbrk()
	{
		#ifdef TEST
		printf("CALLING SBRK()\n");
		#endif

		best_fit = (node *)sbrk(sizeof(node) + size);
		if(best_fit <= 0)
		{
			printf("SBRK ERROR\n\n");
			return NULL;
		}
		initialize_as_tail(best_fit, size);
		if(root == NULL)	//empty list special case
			root = tail;
	}
	else  //use the region we found
	{
		#ifdef TEST
		printf("REUSING A MEMORY REGION\n");
		#endif

		best_fit->used = TRUE;
	
		//check if there's enough unused space to split another node
		//if not enough for a new node, leave any leftover space as part of this node
		int remainder = best_fit->size - size - sizeof(node);
		if(remainder > 0) 
		{
			#ifdef TEST
			printf("Splitting node\n");
			#endif

			node *leftover = allocation + size + sizeof(node);
			leftover->previous = best_fit;
			leftover->size = remainder;

			#ifdef TEST
			printf("The new node can hold %d bytes after node overhead\n\n", leftover->size);
			#endif

			leftover->used = FALSE;
			leftover->next = best_fit->next;

			best_fit->next = leftover;
			best_fit->size = size;
		}
		#ifdef TEST
		else
			printf("%d is too small of a remainder to make a new node\n\n", remainder);
		#endif
	}
	
	return (best_fit + 1);  //return the portion AFTER the node data
}

//returns a pointer to the best fit for request_size if it exists, else return NULL
static void *get_best_fit(int request_size)
{
	node *current = root;
	node *result = NULL;
	while(current != NULL)
	{
		//consider only blocks which are unused and large enough
		if(current->used == FALSE && current->size >= request_size)			
		{ 
			if(current->size == request_size)
			{
				#ifdef TEST
				printf("FOUND A PERFECT MATCH\n");
				#endif

				return current;  //stop looking if there's an exact match
			}
			if(result == NULL || result->size > current->size) //first or better fit found
				result = current;
		}
		
		current = current->next;
	}
	return result;
}

static void initialize_as_tail(node *ptr, int size)
{
	ptr->size = size;
	ptr->previous = tail;
	ptr->used = TRUE;
	ptr->next = NULL;
	if(tail != NULL)
		tail->next = ptr;
	tail = ptr;
}

void my_free(void *ptr)
{
	if(ptr == NULL)
	{
		printf("Tried to free a null pointer -- returning \n\n");
		return;	
	}
	node *freed_node = (node *)ptr;
	freed_node--;  //back up sizeof(node) to get to the linked list data

	freed_node->used = FALSE;
	int isTail = (freed_node == tail);
	int isRoot = (freed_node == root);	

	node *predecessor = freed_node->previous;
	node *successor = freed_node->next;
	
	if(predecessor != NULL && predecessor->used == FALSE)
	{
		#ifdef TEST
		printf("COALESCING WITH PREDECESSOR NODE\n");
		#endif

		freed_node = coalesce(predecessor, freed_node);
	}
	if(successor != NULL && successor->used == FALSE)
	{
		#ifdef TEST
		printf("COALESCING WITH SUCCESSOR NODE\n");
		#endif

		freed_node = coalesce(freed_node, successor);
	}

	if(isTail) //check if was or is tail
	{
		tail = freed_node->previous;
		if(tail != NULL)
			tail->next = NULL;
		
		int backup = 0 - sizeof(node) - freed_node->size;

		#ifdef TEST
		printf("Backing up the brk pointer by %d\n", backup);
		#endif

		sbrk(backup);
		if (isRoot || freed_node == root) //check if was or is root
		{
			#ifdef TEST
			printf("THIS WAS THE ONLY NODE IN THE LIST, NULLIFYING ROOT\n");
			#endif

			root = NULL;
		}
	}
}

//assert: a and b are contiguous blocks, with a before b in memory
//returns the 1 node built from the two
static node *coalesce(node *a, node *b)
{
	int combined_size = a->size + b->size + sizeof(node);

	#ifdef TEST
	printf("The combined size of the nodes is %d\n\n", combined_size);
	#endif

	a->size = combined_size;
	a->next = b->next;
	if(b->next != NULL)
		(b->next)->previous = a;
	if(b == tail)
	{
		#ifdef TEST
		printf("Moving the tail of the list\n");
		#endif

		tail = a;
	}
	return a;
}

void printHeap(void)
{
	printf("\n---HERE IS THE STATE OF THE HEAP---\n\n");
	printf("brk is %p\n\n", sbrk(0));
	node *p = root;
	int i = 0;
	if(root == NULL)
		printf("The list is empty\n");
	while(p != NULL)
	{
		printf("Item %d is %s\n", i, ((p->used) ? "used" : "free"));
		printf("It lives at address %p\n", p);
		printf("Its size is %d\n", p->size);
		printf("Its previous pointer is %p\n", p->previous);
		printf("Its next pointer is %p\n", p->next);

		if(p == root)
			printf("This item is the root of the list\n");
		if(p == tail)
			printf("This item is the tail of the list\n");
		printf("\n\n");
		i++;
		p = p->next;
	}
}
