.include "led_keypad.asm"

# Zachary Whitney (zdw9)
# CS 0447 MWF 11AM  ---  Recitation W 4PM
# Lab Assignment 3

.data
	x:	.word	32
	y:	.word	32
	
.text

#	quit game if user presses "B"	
quit:
	jal	display_update_and_clear
	li	v0, 10
	syscall
	
#	draw a pixel on the screen
draw_dot:
	push		ra
	lw	a0, x
	lw	a1, y
	li	a2, COLOR_WHITE
	jal	display_set_pixel
	jal	display_update
	pop		ra
	jr	ra

#	adjusts x & y coordinates based on keyboard input
check_input:
	push		ra
	jal	input_get_keys
	beq	v0, KEY_U, move_up
	beq	v0, KEY_D, move_down
	beq	v0, KEY_L, move_left
	beq  v0, KEY_R, move_right
	beq	v0, KEY_B, quit
	b	check_input_end
move_up:
	lw	t0, y
	sub	t0, t0, 1
	sw	t0, y
	b	wrap_coordinates
move_down:
	lw	t0, y
	add	t0, t0, 1
	sw	t0, y
	b	wrap_coordinates
move_left:
	lw	t0, x
	sub	t0, t0, 1
	sw	t0, x
	b	wrap_coordinates
move_right:
	lw	t0, x
	add	t0, t0, 1
	sw	t0, x
	b	wrap_coordinates
wrap_coordinates:
	lw	t0, x
	and	t0, t0, 63
	sw	t0, x
	lw	t0, y
	and	t0, t0, 63
	sw	t0, y
check_input_end:
	pop		ra
	jr		ra
	
.globl main
main:
#	wait (roughly) 1/60th of a second
li	v0, 32
li	a0, 17
syscall

jal	check_input
jal	draw_dot
jal	display_update_and_clear
b	main		#infinite loop