# Zachary Whitney (zdw9)
# CS 0447 MWF 11AM  ---  Recitation W 4PM
# Project 1

.include "convenience.asm"
.include "display.asm"

.eqv GAME_TICK_MS			16
.eqv BULLET_MAX			6
.eqv MAX_SHOTS			50
.eqv PLAYER_LIVES_DEFAULT	3
.eqv NUM_ENEMIES			20
.eqv NUM_ENEMIES2			29
.eqv NUM_ENEMIES3			16
.eqv MAX_X				57
.eqv MAX_Y				51
.eqv MIN_X				2
.eqv MIN_Y				46
.eqv ENEMY_SPACING_X		9
.eqv ENEMY_SPACING_Y		7
.eqv MAX_ENEMY_ADVANCE  	15
.eqv GENERATOR_ID			1
.eqv INVINCIBILITY_PERIOD	90
.eqv	DEFAULT_TIMEOUT		20
.eqv	DEFAULT_SHOT_DELAY	60
.eqv	MAX_RANDOM_DELAY	45
.eqv	BOSS_HURT_PERIOD		10
.eqv	SHOT_DELAY_MIN1		30
.eqv	SHOT_DELAY_MIN2		25
.eqv	SHOT_DELAY_MIN3		20
.eqv	BOSS_LENGTH_DEFAULT	40
.eqv	BOSS_X_DEFAULT		12
.eqv	BOSS_Y_DEFAULT		5

# --------------------------------------------------------------------------------------------------

.data
# don't get rid of these, they're used by wait_for_next_frame.
last_frame_time:  	.word 0
frame_counter:		.word 0
round:			.byte  1

#bullet tracking
active_bullets:		.byte  0:BULLET_MAX
enemy_bullets:		.byte  0:BULLET_MAX
enemy_bullets_x:	.byte  0:BULLET_MAX
enemy_bullets_y:	.byte  0:BULLET_MAX
bullet_x:			.byte  0:BULLET_MAX
bullet_y:			.byte  0:BULLET_MAX

#sprites
enemy_sprite:		.byte  2 8 8 8 2   2 2 8 2 2   2 2 2 2 2   8 2 2 2 8   8 8 2 8 8
enemy_sprite2:		.byte  5 8 8 8 5   5 5 8 5 5   5 5 5 5 5   8 5 5 5 8   8 8 5 8 8
enemy_sprite3:		.byte  4 8 8 8 4   4 4 8 4 4   4 4 4 4 4   8 4 7 4 8   8 8 4 8 8
player_sprite:		.byte  8 8 1 8 8   8 1 7 1 8   8 1 1 1 8   1 1 8 1 1   3 8 8 8 3
small_x:			.byte  0 0 0 0 0   0 0 0 0 0   0 7 0 7 0   0 0 7 0 0   0 7 0 7 0
you_win:			.asciiz	"YOU WIN!"
you_lose:			.asciiz	"YOU LOSE!"
load_msg1:		.asciiz	"ROBOT ROUT"
load_msg2:		.asciiz	"PRESS B"
load_msg3:		.asciiz	"TO BEGIN"
retry_msg:		.asciiz	"RETRY"
quit_msg:			.asciiz	"QUIT"
round_msg:		.asciiz	"ROUND"
plus_40:			.asciiz	"+40"
plus_1:			.asciiz	"+1"

#player variables
player_x:			.byte  30
player_y:			.byte  49
player_shots:		.byte  MAX_SHOTS
player_lives:		.byte  PLAYER_LIVES_DEFAULT
timeout:			.byte  DEFAULT_TIMEOUT
invincibility:		.byte  0
cursor_position:	.byte  0

#enemy variables
enemies_left:		.byte  NUM_ENEMIES
enemy_x:			.byte  12
enemy_y:			.byte  2
enemies:			.byte  1:NUM_ENEMIES
enemy_direction:	.byte	  0
enemy_timeout:	.byte  DEFAULT_TIMEOUT
enemy_shot_delay:	.byte  DEFAULT_SHOT_DELAY
enemy_shot_buffer:	.byte  2
min_shot_delay:	.byte  SHOT_DELAY_MIN1

#boss variables
boss_size:		.byte  BOSS_LENGTH_DEFAULT
boss_piece:		.byte  7 7 7 7 7   7 7 7 7 7   7 7 7 7 7   7 7 7 7 7   7 7 7 7 7
boss_y:			.byte  BOSS_Y_DEFAULT
boss_x:			.byte  BOSS_X_DEFAULT
boss_hurt:		.byte  0
boss_direction:		.byte  0
boss_timeout:		.byte  DEFAULT_TIMEOUT
boss_speed:		.byte  DEFAULT_TIMEOUT

# --------------------------------------------------------------------------------------------------
.text
.globl main
main:
	jal	seed_generator
load_screen_loop:
	jal	check_input
	jal	draw_player
	jal	draw_menu_screen
	jal 	draw_player_lives
	jal	draw_player_shots
	jal	draw_boss
	jal	move_boss
	jal	display_update_and_clear
	jal	wait_for_next_frame
	jal	input_get_keys
	and	v0, v0, 0x0010
	bne	v0, KEY_B, load_screen_loop
	b 	start_game
# --------------------------------------------------------------------------------------------------

_main_loop:
	jal	check_ammo
	jal	check_for_enemy_collisions
	jal	check_for_player_collisions
	jal	decrement_timeout
	jal	check_input
	jal	enemy_fire
	jal	advance_bullets
	jal	move_enemies
start_game:
	jal	draw_player_shots
	jal	draw_player_lives
	jal	draw_player
	lbu	t0, round
	bne	t0, 3, _skip_boss				#boss is only on level 3
	jal	boss_collisions
	jal	move_boss
	jal	draw_boss
_skip_boss:
	jal	draw_enemies
	jal	draw_bullets
	jal	display_update_and_clear
	jal	wait_for_next_frame
	b	_main_loop
	
# --------------------------------------------------------------------------------------------------
#### CONTROLLER FUNCTIONS HERE ############################################################
# --------------------------------------------------------------------------------------------------

#adjusts x & y coordinates and fires player shots based on keyboard input
# skips move if it violates player's valid zone
check_input:
	enter	s0
	jal	input_get_keys
	addi	s0, v0, 0
_fire:
	and	t1, s0, KEY_B
	bne	t1, KEY_B, _move_up
	jal	new_bullet
_move_up:
	and	t1, s0, KEY_U
	bne	t1, KEY_U, _move_down
	lbu	t0, player_y
	blt	t0, MIN_Y, _move_down		
	sub	t0, t0, 1
	sb	t0, player_y
_move_down:
	and	t1, s0, KEY_D
	bne	t1, KEY_D, _move_left	
	lbu	t0, player_y
	bgt	t0, MAX_Y, _move_left		
	add	t0, t0, 1
	sb	t0, player_y
_move_left:
	and	t1, s0, KEY_L
	bne	t1, KEY_L, _move_right
	lbu	t0, player_x
	blt	t0, MIN_X, _move_right		
	sub	t0, t0, 1
	sb	t0, player_x
_move_right:
	and	t1, s0, KEY_R
	bne  t1, KEY_R, check_input_end
	lbu	t0, player_x
	bgt	t0, MAX_X, check_input_end	
	add	t0, t0, 1
	sb	t0, player_x
check_input_end:
	leave	s0
# --------------------------------------------------------------------------------------------------
#controller funtion to give player the option to quit or replay the game on win or loss
get_user_replay_input:
	enter
	jal	input_get_keys
_check_for_b:
#---check for b key pressed---#
	and	t1, v0, KEY_B
	bne	t1, KEY_B, _check_for_direction_keys
	lbu	t0, cursor_position
	beq	t0, 1, _game_over
	beq	t0, 0, _reset_selected	
#---check for up or down keys---#
_check_for_direction_keys:
	and	t0, v0, KEY_D
	beq	t0, KEY_D, _change_selection
	and	t0, v0, KEY_U
	beq	t0, KEY_U, _change_selection
	leave
#---change cursor position appropriately---#
_change_selection:
	lbu	t0, cursor_position
	addi	t0, t0, 1
	and	t0, t0, 0x0001
	sb	t0, cursor_position
	jal	sleep_system											
	leave
_reset_selected:
	jal	reset
	b	start_game
_play_round_2:
	jal	draw_round_transition
	jal	round_2
	b	start_game
_play_round_3:
	jal	draw_round_transition
	jal	round_3
	b	start_game
_game_over:
	jal	draw_black_screen
	jal	display_update_and_clear
	exit
	
# --------------------------------------------------------------------------------------------------
#helper function to prevent rapid toggle between menu options
sleep_system:
	enter
	li	v0, 32
	li	a0, 100
	syscall
	leave

# --------------------------------------------------------------------------------------------------
# call once per main loop to keep the game running at 60FPS.
# if your code is too slow (longer than 16ms per frame), the framerate will drop.
# otherwise, this will account for different lengths of processing per frame.
wait_for_next_frame:
enter	s0
	lw	s0, last_frame_time
_wait_next_frame_loop:
	# while (sys_time() - last_frame_time) < GAME_TICK_MS {}
	li	v0, 30
	syscall # why does this return a value in a0 instead of v0????????????
	sub	t1, a0, s0
	bltu	t1, GAME_TICK_MS, _wait_next_frame_loop

	# save the time
	sw	a0, last_frame_time

	# frame_counter++
	lw	t0, frame_counter
	inc	t0
	sw	t0, frame_counter
leave	s0

# --------------------------------------------------------------------------------------------------
#seeds the random number generator at game start
seed_generator:
	enter
	li		v0, 30
	syscall
	move	t0, a0
	li		a0, GENERATOR_ID
	move 	a1, t0	
	li		v0, 40
	syscall
	leave

# --------------------------------------------------------------------------------------------------
#function psuedorandomly selects living enemies to fire on the player
#handles somewhat variable timeout between shots
enemy_fire:
	enter	s0, s1, s2
	lbu	t0, enemies_left
	beq	t0, 0, _end_enemy_fire		#only fire if enemies exist
	lbu	s2, enemy_shot_delay
	bgt	s2, 0, _cleanup_enemy_fire	#wait to fire until delay expires
	jal	choose_random_enemy		#get random # up to 20 in a0
	move	t1, a0
	li	s1, 0
_enemy_select_outer_loop:
	li	s0, 0
_enemy_select_inner_loop:
	mul	t4, s1, 5
	add	t4, t4, s0
	lbu	t0, enemies(t4)
	blt	t0, 1, _skip_dead_enemies		
	ble	t1, 0, _enemy_select_loop_end			#break both loops when target is reached
	subi t1, t1, 1
_skip_dead_enemies:
	addi	s0, s0, 1
	blt	s0, 5, _enemy_select_inner_loop
	addi	s1, s1, 1
	blt	s1, 4, _enemy_select_outer_loop
#--reset loop if overflows -- possible with multi-health enemy addition--#
	li	s1, 0
	b	_enemy_select_outer_loop
_enemy_select_loop_end:	
	#assert: s0, s1 now contains the column, row of the enemy to fire
	lbu	t0, enemy_x
	lbu	t1, enemy_y
	mul	s0, s0, ENEMY_SPACING_X
	mul	s1, s1, ENEMY_SPACING_Y
	add	t0, t0, s0
	addi	t0, t0, 2					#offset to middle of the ship's sprite
	add	t1, t1, s1
	addi t1, t1, 4					#offset to bottom of the ship's sprite
	#assert: t0 and t1 are the x and y coordinates for the new enemy shot
	li, s0, 0
_enemy_ammo_loop_top:
	lbu	t2, enemy_bullets(s0)
	bne	t2, 0, _next_available_bullet
#-- this causes enemies to fire at half speed on the first round --#
	lbu	t2, enemy_shot_buffer
	sb	t2, enemy_bullets(s0)
	sb	t0, enemy_bullets_x(s0)
	sb	t1, enemy_bullets_y(s0)		#bullet is live
	jal	generate_enemy_fire_timeout
	sb	a0, enemy_shot_delay		#set enemy shot delay	
	b	_end_enemy_fire			#fire only 1 bullet at a time
_next_available_bullet:
	addi	s0, s0, 1
	blt s0, BULLET_MAX, _enemy_ammo_loop_top
	b	_end_enemy_fire			#skip shot delay decrement if just fired	
_cleanup_enemy_fire:
	subi	s2, s2, 1
	sb	s2, enemy_shot_delay
_end_enemy_fire:
	leave	s0, s1, s2

# --------------------------------------------------------------------------------------------------	
#helper function for enemy_fire
#returns an enemy index from 0 to enemies_left in a0
choose_random_enemy:
	enter
	li	a0, GENERATOR_ID
	lbu	a1, enemies_left
	li	v0, 42
	syscall
	leave

# --------------------------------------------------------------------------------------------------
#helper function for enemy_fire
#randomize new timeout 30-89
#returns value in a0
generate_enemy_fire_timeout:
	enter
	li	a0, GENERATOR_ID
	li	a1, MAX_RANDOM_DELAY
	li	v0, 42
	syscall
	lbu	t0, min_shot_delay
	add	a0, a0, t0	
	leave

# --------------------------------------------------------------------------------------------------
#handle enemy movement as a single mass
move_enemies:
	enter
	lbu	t0, enemy_timeout
	bgt	t0, 0, _lower_enemy_timeout
	lbu	t0, enemy_direction
	beq	t0, 0, _move_enemies_right
_move_enemies_left:
	lbu	t0, enemy_x
	bgt	t0, 2, _decrement_x
	li	t1, 0
	sb	t1, enemy_direction								#change direction on edge
	lbu	t1, enemy_y
	bgt	t1, MAX_ENEMY_ADVANCE, _end_move_enemies		#limit enemy downward advancement
	addi	t1, t1, 1
	sb	t1, enemy_y
	jal	set_enemy_move_timeout
	b	_end_move_enemies
_decrement_x:											#move left if not on edge
	subi t0, t0, 1
	sb	t0, enemy_x
	jal	set_enemy_move_timeout
	b	_end_move_enemies
_move_enemies_right:
	lbu	t0, enemy_x
	blt	t0, 21, _increment_x
	li	t1, 1
	sb	t1, enemy_direction								#change direction on edge
	lbu	t1, enemy_y
	bgt	t1, MAX_ENEMY_ADVANCE, _end_move_enemies		#limit enemy downward advancement
	addi	t1, t1, 1
	sb	t1, enemy_y
	jal	set_enemy_move_timeout
	b	_end_move_enemies	
_increment_x:											#move right if not on edge
	addi t0, t0, 1
	sb	t0, enemy_x
	jal 	set_enemy_move_timeout
	b	_end_move_enemies
_lower_enemy_timeout:
	lbu	t0, enemy_timeout
	subi	t0, t0, 1
	sb	t0, enemy_timeout
_end_move_enemies:
	leave

# --------------------------------------------------------------------------------------------------		
#helper function for move_enemies
set_enemy_move_timeout:
	enter
	li	t0, DEFAULT_TIMEOUT
	sb	t0, enemy_timeout
	leave

# --------------------------------------------------------------------------------------------------
# --boss edge detection scales dynamically with its shrinking size
# --boss gains speed as its size shrinks
# --------------------------------------------------------------------------------------------------
move_boss:
	enter
	lbu	t0, boss_timeout
	bgt	t0, 0, _lower_boss_timeout
	lbu	t0, boss_direction
	beq	t0, 0, _move_boss_right
_move_boss_left:
	lbu	t0, boss_x
	subi	t0, t0, 1
	sb	t0, boss_x
	bne	t0, 2, _set_boss_timeout
	sb	zero, boss_direction
	b	_set_boss_timeout
_move_boss_right:
	lbu	t0, boss_x
	addi	t0, t0, 1
	sb	t0, boss_x
	lbu	t1, boss_size
	add	t0, t0, t1
	bne	t0, 62, _set_boss_timeout
	li	t0, 1
	sb	t0, boss_direction
_set_boss_timeout:
	lbu	t0, boss_speed
	sb	t0, boss_timeout
	b	_end_move_boss
_lower_boss_timeout:
	lbu	t0, boss_timeout
	subi	t0, t0, 1
	sb	t0, boss_timeout
_end_move_boss:
	leave

# --------------------------------------------------------------------------------------------------
#### VIEW/DISPLAY FUNCTIONS HERE ###########################################################
# --------------------------------------------------------------------------------------------------	
draw_menu_screen:
	enter
	li	a0, 3
	li	a1, 16
	la	a2, load_msg1
	jal	display_draw_text
	li	a0, 11
	li	a1, 30
	la	a2, load_msg2
	jal	display_draw_text
	li	a0, 8
	li	a1, 37
	la	a2, load_msg3
	jal	display_draw_text
	leave
	
# --------------------------------------------------------------------------------------------------
draw_player:
	enter
#--flashes player with increasing speed as invunerablitly expires--#
	lbu	t0, invincibility
	beq	t0, 0, _draw_player_reg
	bgt	t0, 75, _end_draw_player
	bgt	t0, 60, _draw_player_reg
	bgt	t0, 50, _end_draw_player
	bgt	t0, 40, _draw_player_reg
	bgt	t0, 32, _end_draw_player
	bgt	t0, 24, _draw_player_reg
	bgt	t0, 19, _end_draw_player
	bgt	t0, 14, _draw_player_reg
	bgt	t0, 9, _end_draw_player
	bgt	t0, 5, _draw_player_reg
	bgt	t0, 3, _end_draw_player
#--skip here if player is flashing visible or not invulnerable--#
_draw_player_reg:
	lbu	a0, player_x
	lbu	a1, player_y
	la	a2, player_sprite
	jal	display_blit_5x5_trans
_end_draw_player:
	leave
	
# --------------------------------------------------------------------------------------------------
draw_player_lives:
	enter
	li	a0, 46
	li	a1, 58
	la	a2, player_sprite
	jal	display_blit_5x5
	li	a0, 52
	li	a1, 58
	la	a2, small_x
	jal	display_blit_5x5
	li	a0, 58
	li	a1, 58
	lbu	a2, player_lives
	jal	display_draw_int
	leave

# --------------------------------------------------------------------------------------------------	
draw_player_shots:
	enter
	li	a0, 1
	li	a1, 58
	lbu	a2, player_shots
	jal	display_draw_int
	leave

# --------------------------------------------------------------------------------------------------	
draw_enemies:
	enter	s0, s1
	li	s0, 0
	li	s1, 0
_draw_next_enemy:
	mul	t0, s1, 5
	add	t0, t0, s0
	lbu	t2, enemies(t0)					#check if this enemy is alive
	beq	t2, 0, _skip_drawing_enemy
	lbu	a0, enemy_x
	lbu	a1, enemy_y
	mul	t0, s0, ENEMY_SPACING_X
	mul	t1, s1, ENEMY_SPACING_Y
	add	a0, a0, t0
	add	a1, a1, t1
	beq	t2, 1, _draw_lvl1_enemy
	beq	t2, 3, _draw_lvl3_enemy
_draw_lvl2_enemy:
	la	a2, enemy_sprite2
	b	_finish_draw_enemy
_draw_lvl3_enemy:
	la	a2, enemy_sprite3
	b	_finish_draw_enemy
_draw_lvl1_enemy:
	la	a2, enemy_sprite
_finish_draw_enemy:
	jal	display_blit_5x5_trans
_skip_drawing_enemy:
	addi	s0, s0, 1
	blt	s0, 5, _draw_next_enemy	
	li	s0, 0
	addi	s1, s1, 1
	blt	s1, 4, _draw_next_enemy
	leave	s0, s1	
	
draw_bullets:
	enter	s0
	li	s0, 0
draw_bullet_loop_start:
#--draw player bullets--#
	lbu	t0, active_bullets(s0)	
	bne	t0, 1, _draw_enemy_bullets
	lbu	a0, bullet_x(s0)
	lbu	a1, bullet_y(s0)
	li	a2, COLOR_WHITE
	jal	display_set_pixel
_draw_enemy_bullets:
	lbu	t0, enemy_bullets(s0)	
	blt	t0, 1, _draw_next_bullet
	lbu	a0, enemy_bullets_x(s0)
	lbu	a1, enemy_bullets_y(s0)
	li	a2, COLOR_RED
	jal	display_set_pixel
_draw_next_bullet:
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, draw_bullet_loop_start
	leave	s0

# --------------------------------------------------------------------------------------------------
#draws the screen animation between rounds
draw_round_transition:
	enter
	jal	draw_player
	jal	draw_player_shots
	jal	draw_player_lives
	li	a0, 12
	li	a1, 20
	la	a2, round_msg
	jal	display_draw_text
	li	a0, 47
	li	a1, 20
	lbu	a2, round
	jal	display_draw_int
	jal	display_update_and_clear
	li	a0, 1200
	li	v0, 32
	syscall	
	li	a0, 12
	li	a1, 20
	la	a2, round_msg
	jal	display_draw_text
	li	a0, 47
	li	a1, 20
	lbu	a2, round
	jal	display_draw_int
	li	a0, 14
	li	a1, 58
	la	a2, plus_40
	jal	display_draw_text
	li	a0, 34
	li	a1, 58
	la	a2, plus_1
	jal	display_draw_text
	jal	draw_player
	jal	draw_player_shots
	jal	draw_player_lives
	jal	display_update_and_clear
	li	a0, 2000
	li	v0, 32
	syscall
	li	a0, 12
	li	a1, 20
	la	a2, round_msg
	jal	display_draw_text
	li	a0, 47
	li	a1, 20
	lbu	a2, round
	jal	display_draw_int
	lbu	t0, player_shots
	addi	t0, t0, 40
	sb	t0, player_shots
	lbu	t1, player_lives
	addi	t1, t1, 1
	sb	t1, player_lives
	jal	draw_player
	jal	draw_player_shots
	jal	draw_player_lives
	jal	display_update_and_clear
	li	a0, 1500
	li	v0, 32
	syscall
	leave

# --------------------------------------------------------------------------------------------------	
# -- boss flashes red for a few frames after being struck, otherwise is orange
# --------------------------------------------------------------------------------------------------
draw_boss:
	enter	
	lbu	a0, boss_x
	lbu	a1, boss_y
	lbu	a2, boss_size
	li	a3, 5
	lbu	t0, boss_hurt
	beq	t0, 0, _draw_unscathed
	subi t0, t0, 1
	sb	t0, boss_hurt
	li	v1, COLOR_RED
	b	_draw_boss
_draw_unscathed:
	li	v1, COLOR_ORANGE
_draw_boss:
	jal	display_fill_rect
	leave
	
# --------------------------------------------------------------------------------------------------
draw_black_screen:
	enter
	li	a0, 0
	li	a1, 0
	li	a2, 64
	li	a3, 64
	li	v1, 0
	jal	display_fill_rect_fast
	leave

# --------------------------------------------------------------------------------------------------	
#draws the game over screen
#prompts player to press b if they want to play again
#takes 3 arguments
# a0 --> x coordinate of game over message
# a1 --> y coordinate of game over message
# a2 --> the string to print on the gameover screen
# --------------------------------------------------------------------------------------------------
draw_game_over:
	enter
	jal	display_draw_text
	li	a0, 18
	li	a1, 35
	la	a2, retry_msg
	jal	display_draw_text
	li	a0, 18
	li	a1, 42
	la	a2, quit_msg
	jal	display_draw_text
	lbu	t0, cursor_position
	li	a0, 11
	la	a2, player_sprite
	bne	t0, 0, _quit_position
_retry_position:
	li	a1, 35
	b	_draw_it
_quit_position:
	li	a1, 42
_draw_it:
	jal	display_blit_5x5_trans
	jal	display_update_and_clear
	leave

# --------------------------------------------------------------------------------------------------
#### MODEL FUNCTIONS HERE#################################################################
# --------------------------------------------------------------------------------------------------

#function to move player bullets up and enemy bullets down
advance_bullets:
	enter	s0
	li	s0, 0				#loop counter
_bullet_loop_start:	
	lbu	t0, active_bullets(s0)
	bne	t0, 1, _enemy_bullet_start
	lbu	t1, bullet_y(s0)
	bne	t1, 0, _move_bullet_ahead
_delete_bullet:
	li	t0, 0
	sb	t0, active_bullets(s0)
	b _enemy_bullet_start
_move_bullet_ahead:
	subi	t1, t1, 1
	sb	t1, bullet_y(s0)
_enemy_bullet_start:
	lbu	t0, enemy_bullets(s0)
#--this conditional makes enemy fire speed slower than the player's--#
	beq	t0, 0, _next_bullet
	beq	t0, 1, _bombs_away
	subi	t0, t0, 1
	sb	t0, enemy_bullets(s0)
	b	_next_bullet
_bombs_away:
	lbu	t1, enemy_bullets_y(s0)
	bne	t1, MAX_X, _move_bullet_down
_delete_enemy_bullet:
	li	t0, 0
	sb	t0, enemy_bullets(s0)
	b _next_bullet
_move_bullet_down:
	addi	t1, t1, 1
	sb	t1, enemy_bullets_y(s0)
	lbu	t2, enemy_shot_buffer
	sb	t2, enemy_bullets(s0)
_next_bullet:
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, _bullet_loop_start
_end_bullets:
	leave	s0

# --------------------------------------------------------------------------------------------------
#ends the game if the player is out of bullets
# --------------------------------------------------------------------------------------------------
check_ammo:
	enter
	lbu	t0, player_shots
	bgt	t0, 0, _still_firing
	li	t0, 0
_check_for_live_ammo:	#make sure all player bullets clear the screen before ending the game
	lbu	t1, active_bullets(t0)
	bgt	t1, 0, _still_firing
	addi	t0, t0, 1
	blt	t0, BULLET_MAX, _check_for_live_ammo
	b	loss
_still_firing:
	leave
	
# --------------------------------------------------------------------------------------------------
#loops through each active enemy shot and resolves active shots for collisions
# --------------------------------------------------------------------------------------------------
check_for_player_collisions:
	enter	s0
	lbu	t0, invincibility
	bgt	t0, 0, _player_collisions_cleanup
	li	s0, 0		
_check_for_player_collisions_loop:
	lbu	t0, enemy_bullets(s0)
	blt	t0, 1, _check_for_player_collisions_plus_plus			#skip inactive bulletss
	lbu	a0, enemy_bullets_x(s0)
	lbu	a1, enemy_bullets_y(s0)
	jal	is_player_collision
	bne	v0, 1, _check_for_player_collisions_plus_plus			#go to next bullet if it's a miss
_resolve_player_hit:
	li	t0, 0
	sb	t0, enemy_bullets(s0)							#deactivate bullet that hits
	li	t0, INVINCIBILITY_PERIOD
	sb	t0, invincibility
	lbu	t1, player_lives
	subi	t1, t1, 1
	beq	t1, 0, loss
	sb	t1, player_lives									#player loses 1 life
	b	_end_check_for_player_collisions					#can only resolve 1 hit per frame
_check_for_player_collisions_plus_plus:
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, _check_for_player_collisions_loop
	b	_end_check_for_player_collisions	
_player_collisions_cleanup:
	lbu	t0, invincibility
	subi	t0, t0, 1
	sb	t0, invincibility
_end_check_for_player_collisions:
	leave	s0

# --------------------------------------------------------------------------------------------------
#checks if a given x, y pair is a hit on the player
#a0 -- the x coordinate of the bullet to check
#a1 -- the y coordinate of the bullet to check
#returns 1 if it's a hit, 0 for a miss
# --------------------------------------------------------------------------------------------------
is_player_collision:
	enter
	lbu	t0, player_x
	lbu	t1, player_y
	addi	t2, t0, 4
	addi	t3, t1, 4
	blt	a0, t0, _its_a_miss		#miss left
	bgt	a0, t2, _its_a_miss		#miss right
	blt	a1, t1, _its_a_miss		#miss high
	bgt	a1, t3, _its_a_miss		#miss low
_its_a_hit:
	li	v0, 1
	b	_end_is_player_collision
_its_a_miss:
	li	v0, 0
_end_is_player_collision:
	leave

# -------------------------------------------------------------------------------------------------		
#loops through each active player shot and resolves active shots for collisions
# -------------------------------------------------------------------------------------------------
check_for_enemy_collisions:
	enter	s0
	li	s0, 0					#loop counter for active_bullets
_enemy_collisions_loop_top:
	lbu	t0, active_bullets(s0)
	beq	t0, 0, _check_next_shot	#skip to next bullet if inactive
	lbu	a0, bullet_x(s0)
	lbu	a1, bullet_y(s0)
	jal	resolve_player_bullet
	bne	v0, 1, _check_next_shot	#skip to next bullet if miss
	li	t0, 0
	sb	t0, active_bullets(s0)	#deactive bullet in the event of a hit
_check_next_shot:
	addi, s0, s0, 1
	blt	s0, BULLET_MAX, _enemy_collisions_loop_top
	leave	s0

# --------------------------------------------------------------------------------------------------
#takes two arguments
#a0  the x coordinate of the bullet
#a2  the y coordinate of the bullet
#returns 1 if a hit is registered, and a 0 for a miss
# --------------------------------------------------------------------------------------------------
resolve_player_bullet:
	enter	s0, s1
	li	s0, 0		#column loop counter
	li	s1, 0		#row loop counter
resolve_bullet_loop:
	mul	t5, s1, 5
	add	t5, t5, s0					#t5 now holds the index of the enemy to consider
	lbu	t0, enemies(t5)
	blt	t0, 1, continue_resolve_bullet_loop		#skip dead enemies
	lbu	t0, enemy_x
	mul	t1, s0, ENEMY_SPACING_X
	add	t0, t0, t1					#t0 now holds adjusted X coord for the current foe
	lbu	t1, enemy_y
	mul	t2, s1, ENEMY_SPACING_Y
	add	t1, t1, t2					#t1 now holds adjusted Y coord for the current foe
	addi	t2, t0, 4					#X boundary (no-hit)
	addi	t3, t1, 4					#Y boundary (no-hit)
	
	blt	a0, t0, continue_resolve_bullet_loop	#miss left
	bgt	a0, t2, continue_resolve_bullet_loop	#miss right
	blt	a1, t1, continue_resolve_bullet_loop	#miss high
	bgt	a1, t3, continue_resolve_bullet_loop	#miss low
	
	#assert: we've confirmed a hit if we get here
	li	v0, 1								#functions returns 1 for a hit							
	lbu	t6, enemies(t5)
	subi	t6, t6, 1
	sb	t6, enemies(t5)						#target nullified
	lbu	t0, enemies_left
	subi	t0, t0, 1
	sb	t0, enemies_left					#decrement enemies left
	jal	check_for_win
	b	end_resolve_player_bullet			#no need to search farther
continue_resolve_bullet_loop:	
	addi	s0, s0, 1
	blt	s0, 5, resolve_bullet_loop	
	li	s0, 0
	addi	s1, s1, 1
	blt	s1, 4, resolve_bullet_loop	
	li	v0, 0								#if this runs, the bullet missed all enemies	
end_resolve_player_bullet:
	leave	s0, s1

# --------------------------------------------------------------------------------------------------
#function called when player fires
# --------------------------------------------------------------------------------------------------
new_bullet:
	enter	s0
	lbu	t0, timeout
	bgt	t0, 0, _done_creating_bullet	#fail to fire if player is on timeout
	lbu	t1, player_shots
	beq	t1, 0, _done_creating_bullet	#fail to fire if player is out of ammo
	li	s0, 0
_create_bullet_loop_start:
	lbu	t0, active_bullets(s0)
	bne	t0, 0, _go_to_next_bullet		#only use empty slot
	li	t0, 1
	sb	t0, active_bullets(s0)		#set the slot to active
	lbu	t1, player_x				#bullet starts at player_x + 2
	addi	t1, t1, 2
	sb	t1, bullet_x(s0)
	lbu	t2, player_y				#bullet starts at player_y - 1
	subi	t2, t2, 1
	sb	t2, bullet_y(s0)
	lbu	t0, player_shots			#subtract 1 from player ammo
	subi t0, t0, 1
	sb	t0, player_shots
	li	t0, DEFAULT_TIMEOUT		#set timeout to 20
	sb	t0, timeout
	b	_done_creating_bullet
_go_to_next_bullet:					#loop until we find an empty bullet slot
	addi	s0, s0, 1
	blt, s0, BULLET_MAX, _create_bullet_loop_start
_done_creating_bullet:
	leave	s0	

# --------------------------------------------------------------------------------------------------
#helper function for new_bullet
# --------------------------------------------------------------------------------------------------
decrement_timeout:
	enter
	lbu	t0, timeout
	ble	t0, 0, _exit_timeout
	subi	t0, t0, 1
	sb	t0, timeout
_exit_timeout:
	leave

# --------------------------------------------------------------------------------------------------	
### -- dynamically scales boss size and speed when hit
### -- checks for player win when boss is hit
### -- calls to reflect shots at player when boss is it
# --------------------------------------------------------------------------------------------------
boss_collisions:
	enter	s0
	lbu	t0, boss_hurt
	bgt	t0, 0, _decrement_boss_hurt
	lbu	t0, boss_x
	lbu	t1, boss_y
	lbu	t4, boss_size
	add	t2, t0, t4
	subi	t2, t2, 1
	add	t3, t1, 4
	
	li	s0, 0
_boss_collision_loop:
	lbu	t4, active_bullets(s0)
	bne	t4, 1, _skip_this_bullet
	lbu	a0, bullet_x(s0)
	lbu	a1, bullet_y(s0)
	
	blt	a0, t0, _skip_this_bullet	#miss left
	bgt	a0, t2, _skip_this_bullet	#miss right
	blt	a1, t1, _skip_this_bullet	#miss high
	bgt	a1, t3, _skip_this_bullet	#miss low
	
	#--it's a hit--#
	sb	zero, active_bullets(s0)
	li	t0, BOSS_HURT_PERIOD
	sb	t0, boss_hurt
	lbu	t0, boss_size
	subi	t0, t0, 2
	sb	t0, boss_size
	jal	reflect_fire
	lbu	t0, boss_speed
	beq	t0, 3, _max_speed_already
	subi	t0, t0, 1
	sb	t0, boss_speed
_max_speed_already:
	jal	check_for_win
	b	_end_boss_collisions
_skip_this_bullet:
	addi s0, s0, 1
	blt	s0, BULLET_MAX, _boss_collision_loop
	b	_end_boss_collisions
_decrement_boss_hurt:
	lbu	t0, boss_hurt
	subi	t0, t0, 1
	sb	t0, boss_hurt
_end_boss_collisions:
	leave	s0
	
# --------------------------------------------------------------------------------------------------
#reflects bullets back upon the player when the boss is hit
#arguments: a0 -- x coordinate of bullet to bounce
########## a1 -- y coordinate of bullet to bounce
# --------------------------------------------------------------------------------------------------
reflect_fire:
	enter	s0
	li	s0, 0
_reflect_fire_loop:
	lbu	t0, enemy_bullets(s0)
	bne	t0, 0, _next_reflect_bullet
	lbu	t0, enemy_shot_buffer
	sb	t0, enemy_bullets(s0)
	sb	a0, enemy_bullets_x(s0)
	sb	a1, enemy_bullets_y(s0)
	b	_end_reflect_fire
_next_reflect_bullet:
	addi s0, s0, 1
	blt, s0, BULLET_MAX, _reflect_fire_loop
_end_reflect_fire:
	leave	s0

# --------------------------------------------------------------------------------------------------
#loads win screen when player destroys all enemies
#checked only when player destroys an enemy rather than every frame
# --------------------------------------------------------------------------------------------------
check_for_win:
	enter
	lbu	t0, enemies_left
	bgt	t0, 0, _no_win
	lbu	t0, round
	bne	t0, 3, _skip_boss_check
	lbu	t1, boss_size
	bgt	t1, 2, _no_win
_skip_boss_check:
	lbu	t0, round
	addi	t0, t0, 1
	sb	t0, round
	beq	t0, 2, _play_round_2			#play next round if not game over
	beq	t0, 3, _play_round_3
	jal	draw_black_screen
	#jal	display_update_and_clear
	jal	wait_for_next_frame
	li	a0, 10
	li	a1, 18
	la	a2, you_win
	jal	draw_game_over
	li	a0, 500
	li	v0, 32
	syscall
win_loop:
	li	a0, 10
	li	a1, 18
	la	a2, you_win
	jal	draw_game_over
	jal	wait_for_next_frame
	jal	get_user_replay_input
	b	win_loop
_no_win:
	leave
	
# --------------------------------------------------------------------------------------------------
#load game over screen
#called when player runs out out of lives or ammo
# --------------------------------------------------------------------------------------------------
loss:

	jal	draw_black_screen
	jal	display_update_and_clear
	jal	wait_for_next_frame
	li	a0, 6
	li	a1, 18
	la	a2, you_lose
	jal	draw_game_over
#--pause keeps user from accidentally restarting without seeing the game over screen--#	
	li	a0, 500
	li	v0, 32
	syscall
_lose_loop:
	li	a0, 6
	li	a1, 18
	la	a2, you_lose
	jal	draw_game_over
	jal	wait_for_next_frame
	jal	get_user_replay_input
	b	_lose_loop

# --------------------------------------------------------------------------------------------------
#resets all parameters for a new game
# --------------------------------------------------------------------------------------------------
reset:
	enter	s0
	jal	set_common_variables
	li	t0, 1
	sb	t0, round
	li	t0, 2
	sb	t0, enemy_shot_buffer
	li	t0, SHOT_DELAY_MIN1
	sb	t0, min_shot_delay
	li	t0, PLAYER_LIVES_DEFAULT
	sb	t0, player_lives
	li	t0, MAX_SHOTS
	sb	t0, player_shots
	li	t0, NUM_ENEMIES
	sb	t0, enemies_left
	li	t0, 30
	sb	t0, player_x
	li	t0, 49
	sb	t0, player_y
	
	li	t0, 0
	li	s0, 0
_reset_bullets_loop:
	sb	t0, active_bullets(s0)
	sb	t0, enemy_bullets(s0)
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, _reset_bullets_loop	
	li	t0, 1
	li	s0, 0
_reset_enemies_loop:
	sb	t0, enemies(s0)
	addi	s0, s0, 1
	blt	s0, NUM_ENEMIES, _reset_enemies_loop
	leave	s0
	
# --------------------------------------------------------------------------------------------------
# --initializes variables appropriately for the boss stage
# --------------------------------------------------------------------------------------------------	
round_2:
	enter	s0
	jal	set_common_variables
	li	t0, NUM_ENEMIES2
	sb	t0, enemies_left
	li	t0, SHOT_DELAY_MIN2
	sb	t0, min_shot_delay
	li	t0, 1
	sb	t0, enemy_shot_buffer
	li	t0, 0
	li	s0, 0
_reset_bullets_loop2:
	sb	t0, active_bullets(s0)
	sb	t0, enemy_bullets(s0)
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, _reset_bullets_loop2
	li	t0, 1
	li	s0, 0
_reset_enemies_loop2:
	sb	t0, enemies(s0)
	addi	s0, s0, 1
	blt	s0, NUM_ENEMIES, _reset_enemies_loop2
#set level 2 & 3 enemies manually
	li	t0, 2
	li	t1, 1
	sb	t0, enemies(t1)
	li	t1, 3
	sb	t0, enemies(t1)
	li	t1, 7
	sb	t0, enemies(t1)
	li	t0, 3
	li	t1, 2
	sb	t0, enemies(t1)
	li	t1, 15
	sb	t0, enemies(t1)
	li	t1, 19
	sb	t0, enemies(t1)
	leave	s0

# --------------------------------------------------------------------------------------------------
# --initializes variables appropriately for the boss stage
# --------------------------------------------------------------------------------------------------
round_3:
	enter	s0
	jal	set_common_variables
	li	t0, NUM_ENEMIES3
	sb	t0, enemies_left
	li	t0, BOSS_LENGTH_DEFAULT
	sb	t0, boss_size
	li	t0, DEFAULT_TIMEOUT
	sb	t0, boss_speed
	li	t0, BOSS_X_DEFAULT
	sb	t0, boss_x
	li	t0, BOSS_Y_DEFAULT
	sb	t0, boss_y
	li	t0, SHOT_DELAY_MIN3
	sb	t0, min_shot_delay
	li	t0, 1
	sb	t0, enemy_shot_buffer
	sb	zero, boss_direction
	sb	zero, boss_hurt
	li	t0, 0
	li	s0, 0
_reset_bullets_loop3:
	sb	t0, active_bullets(s0)
	sb	t0, enemy_bullets(s0)
	addi	s0, s0, 1
	blt	s0, BULLET_MAX, _reset_bullets_loop3
	li	t0, 2
	li	s0, 0
_reset_enemies_loop3:
	sb	zero, enemies(s0)
	addi	s0, s0, 1
	blt	s0, 10, _reset_enemies_loop3
_reset_enemies_loop3_part2:
	sb	t0, enemies(s0)
	addi	s0, s0, 1
	blt	s0, 15, _reset_enemies_loop3_part2
	li	t0, 1
_reset_enemies_loop3_part3:
	sb	t0, enemies(s0)
	addi	s0, s0, 1
	blt	s0, NUM_ENEMIES, _reset_enemies_loop3_part3
	li	t0, 3
	li	t1, 12
	sb	t0, enemies(t1)
	leave	s0

# --------------------------------------------------------------------------------------------------
# --these variables are set the same for all 3 stages
# --------------------------------------------------------------------------------------------------
set_common_variables:
	enter
	sb	zero, invincibility
	sb	zero, enemy_direction
	li	t0, 12
	sb	t0, enemy_x
	li	t0, 2
	sb	t0, enemy_y
	li	t0, DEFAULT_TIMEOUT
	sb	t0, timeout
	sb	t0, enemy_timeout
	li	t0, DEFAULT_SHOT_DELAY
	sb	t0, enemy_shot_delay
	leave
	
