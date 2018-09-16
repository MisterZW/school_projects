# Zachary Whitney (zdw9)
# CS 0447 MWF 11AM  ---  Recitation W 4PM
# Lab Assignment 1

print_int:
	li	v0, 1
	syscall
	jr	ra

newline:
	li	a0, '\n'
	li	v0, 11
	syscall
	jr	ra

.globl main
main:
	li	a0, 1234
	li	v0, 1
	syscall

	jal	newline
	li	a0, 5678
	jal	print_int

# HOW TO PRINT 1234 IN C
#
# #include <stdio.h>
# 
# int main(void) {
# printf("%d", 1234);
# return 0;
# }
#
#
# HOW TO PRINT 1234 IN JAVA
#
# public class Lab1 {
#
#	public static void main(String[] args) {
#		System.out.print(1234);
#	}	
# }
