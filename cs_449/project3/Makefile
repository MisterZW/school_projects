CC=gcc
CFLAGS=-Wall -Werror -m32 -g --std=gnu99

mallocdrv: mallocdrv.c mymalloc.c mymalloc.h
	$(CC) $(CFLAGS) -o mallocdrv mallocdrv.c mymalloc.c

malloc_driver: malloc_driver.c mymalloc.c mymalloc.h
	$(CC) $(CFLAGS) -DTEST -o malloc_driver malloc_driver.c mymalloc.c

malloc_driver2: malloc_driver.c mymalloc.c mymalloc.h
	$(CC) $(CFLAGS) -DTEST -o malloc_driver2 malloc_driver2.c mymalloc.c

.PHONY: clean

clean:
	rm -f malloc_driver malloc_driver2
