Zachary Whitney  zdw9  ID: 3320178
CS 449 Summer 2018  Prof: Johnathan Misurda
Recitation: Tues 9:30 AM
Assignment #3, a custom malloc()
This is a linked-list implementation of malloc() which uses best fit allocation

I have included two of my own test drivers, malloc_driver.c and malloc_driver2.c,
in addition to Dr Misurda's driver mallocdrv.c. They are meant to be run with the
TEST identifier #define'd in mymalloc.c for the most verbose/helpful trace. All of
the files are meant to be compiled to the gnu99 standard with the -m32 flag set.

mymalloc.h includes an additional method, printHeap(), which is called by the
drivers to better view the current state of the heap.

In addition to these drivers, I have included a Makefile which will build the 
drivers (both my own and Dr. Misurda's) with the expected flags set,
including the -DTEST option for verbose output on my drivers.