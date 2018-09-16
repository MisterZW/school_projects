.include "convenience.asm"

#$a0 = pitch (0-127)
#$a1 = duration in milliseconds
#$a2 = instrument (0-127)
#$a3 = volume (0-127)

.eqv NUM_NOTES	3

#	61 = C# or Db		62 = D			63 = D# or Eb
#	64 = E or Fb		65 = E# or F		66 = F# or Gb
#	67 = G			68 = G# or Ab		69 = A
#	70 = A# or Bb		71 = B or Cb		72 = B# or C
.data
lose_notes:		.byte		60, 30, 80
lose_note_lengths:	.word	200, 150, 1000

.text
.globl main

main:
	li	s0, 0
main_loop:
	lbu	a0, notes(s0)
	mul	t0, s0, 4
	lw	a1, note_lengths(t0)
	li	a2, 126
	li	a3, 60
	jal	playnote
	addi	s0, s0, 1
	blt	s0, NUM_NOTES, continue_main_loop
	b	main
continue_main_loop:
	b	 main_loop
	
	li	v0, 10	#clean exit
	syscall	
	
sleep:
	enter
	li	v0, 30
	li	a0, 100
	syscall
	leave

playnote:
	enter
	li	v0, 33
	syscall
	leave
