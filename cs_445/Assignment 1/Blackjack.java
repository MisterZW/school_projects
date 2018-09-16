/**
*   This class is a simple implementation of 2-player Blackjack
*   @ author    Zachary Whitney     <zdw9@pitt.edu>     id: 3320178
*   Date: January 22, 2018
*   Assignment 1, CS 0445, Ramirez TH 1PM, Recitation T 10AM
*/

public class Blackjack{

    private Shoe<Card> shoe;
    private RandIndexQueue<Card> discard;
    private int round, wins, losses, ties;
    Player dealer, player;

    //Firts, verifies positive values for round && decks
    //then greets user
    //next starts the appropriate Blackjack version
    //Finally posts results after the game is over
    public Blackjack(int rounds, int decks)
    {
        if(rounds < 1 || decks < 1)
        {
            System.out.println("Values for <# of rounds> and <# of decks in shoe> must" +
            " both be positive");
            System.exit(0);
        }
        greetUser(rounds, decks);
        initialize(decks);
        if(rounds <= 10)
            traceVersion(rounds);
        else
            blindVersion(rounds);
        postResults(rounds);
    }

    //verifies numerical command line arguments, then starts Blackjack
    public static void main(String[] args)
    {
        if(args.length < 2)
        {
            System.out.println("Usage: java Blackjack <# of rounds> <# of decks in shoe>");
            System.exit(0);
        }
        try
        {
            new Blackjack(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
        catch (Exception e)
        {
            System.out.println("Usage: java Blackjack <# of rounds> <# of decks in shoe>");
            System.exit(0);
        }
    }

    //Gives limited feedback during play -- executes when rounds > 10
    private void blindVersion(int rounds)
    {
        int playerScore, dealerScore;
        while(round < rounds)
        {
            deal();
            playerScore = player.scoreHand();
            dealerScore = dealer.scoreHand();
            while(playerScore < 17)
            {
                Card hit = (Card)shoe.poll();
                player.hit(hit);
                playerScore = player.scoreHand();
            }
            if(playerScore > 21)
            {
                losses++;
                cleanup();
                continue;
            }
            while(dealerScore < 17)
            {
                Card hit = (Card)shoe.poll();
                dealer.hit(hit);
                dealerScore = dealer.scoreHand();
            }
            if(dealerScore > 21)
            {
                wins++;
                cleanup();
                continue;
            }
            blindOutcome(playerScore, dealerScore);
        }//end round
    }

    //Gives detailed feedback during play -- executes when rounds <= 10
    private void traceVersion(int rounds)
    {
        int playerScore, dealerScore;
        while(round < rounds)
        {
            System.out.println("Round " + round + " Beginning");
            deal();
            playerScore = player.scoreHand();
            dealerScore = dealer.scoreHand();
            System.out.println("Player: " + player + "  :  " + playerScore);
            System.out.println("Dealer: " + dealer + "  :  " + dealerScore);
            while(playerScore < 17)
            {
                Card hit = (Card)shoe.poll();
                System.out.println("Player hits: " + hit);
                player.hit(hit);
                playerScore = player.scoreHand();
            }
            if(playerScore > 21)
            {
                playerBust(playerScore);
                continue;
            }
            System.out.println("Player STANDS: " + player + "  :  " + playerScore);
            while(dealerScore < 17)
            {
                Card hit = (Card)shoe.poll();
                System.out.println("Dealer hits: " + hit);
                dealer.hit(hit);
                dealerScore = dealer.scoreHand();
            }
            if(dealerScore > 21)
            {
                dealerBust(dealerScore);
                continue;
            }
            System.out.println("Dealer STANDS: " + dealer + "  :  " + dealerScore);
            outcome(playerScore, dealerScore);
        }//end round
    }

    //sets up all the game components when the game begins
    private void initialize(int decks)
    {
        shoe = new Shoe<Card>(decks);
        discard = new RandIndexQueue<Card>();
        player = new Player("Player");
        dealer = new Player("Dealer");
        round = 0;
        wins = 0;
        losses = 0;
        ties = 0;
    }

    //Shuffles the discard into the shoe when it gets low
    @SuppressWarnings("unchecked")
    private void checkShoe()
    {
        if(shoe.size() <= (shoe.getStartSize() / 4))
            {
                while(!discard.isEmpty())
                {
                    shoe.offer(discard.poll());
                }
                shoe.shuffle();
                System.out.println("Reshuffling the shoe in round " + round + "\n");
            }
    }

    //give each player a new hand of 2 cards
    private void deal()
    {
        for(int i = 0; i < 2; i++)
        {
            player.hit(shoe.poll());
            dealer.hit(shoe.poll());
        }
    }

    //player and dealer discard their hands
    private void discard()
    {
        while(player.handSize() > 0)
        {
            discard.offer(player.discard());
        }
        while(dealer.handSize() > 0)
        {
            discard.offer(dealer.discard());
        }
    }

    //handles player busting in trace version
    private void playerBust(int playerScore)
    {
        System.out.println("Player BUSTS: " + player + "  :  " + playerScore);
        System.out.println("Result: Dealer Wins!\n\n");
        losses++;
        cleanup();
    }

    //handles dealer busting in trace version
    private void dealerBust(int dealerScore)
    {
        System.out.println("Dealer BUSTS: " + dealer + "  :  " + dealerScore);
        System.out.println("Result: Player Wins!\n\n");
        wins++;
        cleanup();
    }

    //determines the winner in the trace version if neither player busts
    private void outcome(int playerScore, int dealerScore)
    {
        if(playerScore > dealerScore)
        {
            System.out.println("Result: Player Wins!\n\n");
            wins++;
        }
        else if(dealerScore > playerScore)
        {
            System.out.println("Result: Dealer Wins!\n\n");
            losses++;
        }
        else
        {
            System.out.println("Result: Push!\n\n");
            ties++;
        }
        cleanup();
    }

    //determines the winner in blind version if neither player busts
    private void blindOutcome(int playerScore, int dealerScore)
    {
        if(playerScore > dealerScore)
            wins++;
        else if(dealerScore > playerScore)
            losses++;
        else
            ties++;
        cleanup();
    }

    //helper method for constructor -- prints greeting on startup
    private void greetUser(int rounds, int decks)
    {
        System.out.print("Starting Blackjack with " + rounds + " round");
        if(rounds > 1)
            System.out.print("s");
        System.out.print(" and " + decks + " deck");
        if(decks > 1)
            System.out.print("s");
        System.out.println(" in the shoe\n");
    }

    //helper method for constructor -- prints results after game over
    private void postResults(int rounds)
    {
        System.out.println("After " + rounds + " rounds, here are the results: ");
        System.out.println("\tDealer wins: " + losses);
        System.out.println("\tPlayer wins: " + wins);
        System.out.println("\tPushes: " + ties);
    }

    private void cleanup()
    {
        discard();
        checkShoe();
        round++;
    }

}
