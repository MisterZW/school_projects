/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #1, Part A
* This is a simplistic version of interactive Blackjack.
*/

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define CARD_RANGE 13       //# of ranks in the standard deck
#define PLAYER 0
#define DEALER 1
#define TRUE 1
#define INSIZE 25           //buffer size for input
#define ACE 14

//prototypes
int draw();                 //gets a random value (card) from 2 to 14(Ace)
char * card_name(int);      //formats number values as cards for user interaction
int value_of(int);          //returns # of points a card rank is (normally) worth

//externs
char buffer[3];             //used by card_name to convert integer values to strings
int player_aces, dealer_aces; //track aces dealt to assist in scoring the hand

int main(void)
{
    srand((unsigned int)time(NULL));    //seed the generator

    int dealer_score, player_score;
    player_score = dealer_score = player_aces = dealer_aces = 0;
    int dealer_card, player_card;
    int player_bust, dealer_bust;                      //boolean flags
    player_bust = dealer_bust = 0;

    /* Initial Cards are Dealt */

    dealer_score = value_of(draw(DEALER));             //hidden card value
    dealer_card = draw(DEALER);                        //dealer visible card
    dealer_score += value_of(dealer_card);

    printf("The Dealer:\n? + %s\n\n", card_name(dealer_card));
    printf("You:\n");

    player_card = draw(PLAYER);
    printf("%s + ", card_name(player_card));
    player_score += value_of(player_card);
    player_card = draw(PLAYER);
    player_score += value_of(player_card);
    printf("%s = %d\n\n", card_name(player_card), player_score);

    /* Player Turn */

    char input[INSIZE];                      //vulnerable to overflowing
    while(!player_bust)
    {
        printf("Would you like to hit or stand? --> " );
        scanf("%s", input);
        printf("\n");
        if(input[0] == 'h' || input[0] == 'H')      //hit
        {
            player_card = draw(PLAYER);
            printf("You HIT\n%d + %s ", player_score, card_name(player_card));
            player_score += value_of(player_card);
            while(player_score > 21 && player_aces > 0)
            {
                printf(" A(11 --> 1)  ");   //clarify when ace value changes
                player_aces--;
                player_score -= 10; //adjust 11 to 1
            }
            printf("= %d ", player_score);
            if(player_score > 21)   //player busts
            {
                player_bust = TRUE;
                printf("BUSTED");
            }
            printf("\n\n");
        }
        else if(input[0] == 's' || input[0] == 'S') //stand
        {
            printf("Player STANDS with %d\n\n", player_score);
            break;
        }
        else
        {
            //use more of the buffer, or else prompt user again
        }
    }

    //dealer plays only if player doesn't bust
    while(!player_bust && !dealer_bust)
    {
        if(dealer_score > 16)   //dealer stands @ 17 or higher
        {
            printf("Dealer STANDS with %d\n\n", dealer_score);
            break;
        }
        else                    //dealer hits
        {
            dealer_card = draw(DEALER);
            printf("Dealer HITS\n%d + %s ", dealer_score, card_name(dealer_card));
            dealer_score += value_of(dealer_card);
            while(dealer_score > 21 && dealer_aces > 0)
            {
                printf(" A(11 --> 1)  ");   //clarify when ace value changes
                dealer_aces--;
                dealer_score -= 10; //adjust 11 to 1
            }
            printf("= %d ", dealer_score);
            if(dealer_score > 21)   //dealer busts
            {
                dealer_bust = TRUE;
                printf("BUSTED");
            }
            printf("\n\n");
        }
    }

    //assess the outcome of the round
    if(player_bust)
        printf("You busted. Dealer wins.\n");
    else if(dealer_bust)
        printf("Dealer busted. You win!\n");
    else
    {
        printf("Dealer: %d  You: %d\n", dealer_score, player_score);
        if(dealer_score > player_score)
            printf("DEALER WINS\n");
        else if(player_score > dealer_score)
            printf("YOU WIN!\n");
        else
            printf("PUSH\n");
    }

}
//draws a new random card, updating # aces drawn for each player
int draw(int which_player)
{
    int card = (rand() % CARD_RANGE) + 2;
    if(card == ACE && which_player == PLAYER)
        player_aces++;
    else if(card == ACE && which_player == DEALER)
        dealer_aces++;
    return card;
}

//Returns the string version of a card's rank
//Helps aesthetically distinguish face cards and aces
char * card_name(int value)
{
    if(value < 11)
    {
        sprintf(buffer, "%d", value);
        return buffer;
    }
    else if(value == 11)
        return "J";
    else if(value == 12)
        return "Q";
    else if (value == 13)
        return "K";
    else
        return "A";
}

//returns the point value of the input card
//assumes no cards are generated with a rank of 1
int value_of(int rank)
{
    if(rank < 11)
        return rank;
    else if (rank < 14)
        return 10;
    else
        return 11;
}
