# Zachary Whitney (zdw9)
# CS 0447 MWF 11AM  ---  Recitation W 4PM
# Lab Assignment 2
# Sorry if tab alignment is wonky :-/

.data
	small: 	.byte		200
	medium: 	.half		400
	large: 	.word 	0
	
	.eqv NUM_ITEMS 5
	values:	.word 0:NUM_ITEMS
	
.text 
.globl main
main:
	lbu		t0, small
	lhu		t1, medium
	mul		t0, t0, t1
	sw		t0, large
	li		v0, 1
	move	a0, t0
	syscall	
	
	li	s0, 0						# int i = 0
	ask_loop_top: 			 		# while
	blt s0, NUM_ITEMS, ask_loop_body	# (i < 5)
	b ask_loop_exit
	
	ask_loop_body:	 	# {
	li	v0, 5				# get user input in v0
	syscall
	
	la	t0, values			# t0 = &values
	mul	t1, s0, 4			# t0 += 4i
	add	t0, t0, t1
	sw	v0, ( t0)			# values[i] = user_input_value
	
	add s0, s0, 1			# i++
	b ask_loop_top
	
	ask_loop_exit: 			# }
	
	li	v0, 10			# end main
	syscall
