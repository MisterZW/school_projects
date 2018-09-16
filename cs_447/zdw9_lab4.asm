# Zachary Whitney (zdw9)
# CS 0447 MWF 11AM  ---  Recitation W 4PM
# Lab Assignment 4

.data
	opcode: .asciiz "\nopcode = "
	rs: .asciiz "\nrs = "
	rt: .asciiz "\nrt = "
	immediate: .asciiz "\nimmediate = "

.text

encode_instruction:
	push	ra

	#stitch instructions into one value
	sll	a0, a0, 26
	sll	a1, a1, 21
	sll	a2, a2, 16
	and	a3, a3, 0xFFFF
	or	a0, a0, a1
	or	a0, a0, a2
	or	a0, a0, a3
	
	#print hex value
	li	v0, 34
	syscall
	
	#print newline
	li	a0, '\n'
	li	v0, 11
	syscall

	pop	ra
	jr	ra

decode_instruction:
	push	ra
	push s0
	addi	s0, a0, 0
	
	#print opcode string
	la	a0, opcode
	li	v0, 4
	syscall
	
	#extract and print opcode
	srl	a0, s0, 26
	li 	v0, 1
	syscall
	
	#print rs string
	la	a0, rs
	li	v0, 4
	syscall
	
	#extract and print rs
	srl	t0, s0, 21
	and	a0, t0, 0x1F
	li 	v0, 1
	syscall
	
	#print rt string
	la	a0, rt
	li	v0, 4
	syscall
	
	#extract and print rt
	srl	t0, s0, 16
	and a0, t0, 0x1F
	li 	v0, 1
	syscall
	
	#print immediate string
	la	a0, immediate
	li	v0, 4
	syscall
	
	#extract and print immediate
	sll	t0, s0, 16
	sra	a0, t0, 16
	li 	v0, 1
	syscall

	pop	s0
	pop	ra
	jr	ra

.globl main
main:
	# addi t0, s1, 123
	li	a0, 8
	li	a1, 17
	li	a2, 8
	li	a3, 123
	jal	encode_instruction

	# beq t0, zero, -8
	li	a0, 4
	li	a1, 8
	li	a2, 0
	li	a3, -8
	jal	encode_instruction

	li	a0, 0x2228007B
	jal	decode_instruction

	li	a0, '\n'
	li	v0, 11
	syscall

	li	a0, 0x1100fff8
	jal	decode_instruction

	# exit the program cleanly
	li	v0, 10
	syscall
