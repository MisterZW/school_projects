CS 447 Project #1 SPACE INVADERS details

Name: Zachary Whitney
Username: zdw9
Section: 11AM MWF lecture, 4PM recitation

My program requires:
space_invaders.asm
display.asm
convenience.asm

The display and convenience files were not modified from their defaults.

HOW TO USE:
Press B to begin the game from the main menu. You may move into any legal position before starting the first round.

If you either win (clear all three rounds) or lose (lives == 0 or ammo == 0), you will be given the opportunity to try again or quit. Use the up and down arrows to move the ship to select either retry or quit.

EXTRA FEATURES IMPLEMENTED:
There are 3 rounds to play with scaling difficulties.
After clearing a round, the player gains 40 ammo and 1 extra life, which is demonstrated in a brief animation.
Round 2 introduces faster enemy fire rates and shot speeds as well as enemies with more than one HP.
Their current HP is reflected by their color -- hitting enemies with more than one health will cause them to change to the next lowest tier's color rather than being destroyed.
Round 3 includes a breakout-inspired boss who reflects shots back at the player when hit. As it sustains damage, the boss shrinks in size and increases its speed.
I have included several simple menu screens (start, game over, game won).
Player invincibility is implemented to flicker faster closer to its expiration.

Commenting out the enemy_fire function call in main makes it easy to see the other features in all 3 levels without getting shot up, if that helps.
All of the other standard features should perform as requested.
Feel free to email me if you have any questions or concerns.

