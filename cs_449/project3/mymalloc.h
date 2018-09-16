#ifndef MY_MALLOC_IS_INCLUDED
#define MY_MALLOC_IS_INCLUDED

void *my_bestfit_malloc(int size);
void my_free(void *ptr);
void printHeap(void);

#endif