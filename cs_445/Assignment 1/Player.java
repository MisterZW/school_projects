public class Player{

    private RandIndexQueue<Card> hand;
    private String name;

    public Player(String str)
    {
        name = str;
        hand = new RandIndexQueue<Card>(5);
    }

    public String toString() {
        return hand.toString();
    }

    //adds hit card to hand -- returns true if successfull, else false
    public boolean hit(Card c)
    {
        return hand.offer(c);
    }

    public Card discard()
    {
        return hand.poll();
    }

    public int handSize()
    {
        return hand.size();
    }

    //returns the score value of the Player's Blackjack hand
    //dynamically considers Aces either as 11 or 1
    public int scoreHand()
    {
        int score = 0, aces = 0;
        for(int i = 0; i < hand.size(); i++)
        {
            Card c = hand.get(i);
            if(c.value2() == 1)
                aces++;
            else
                score+= c.value();
            while(aces > 0)
            {
                if((score + aces) > 11)
                    score += 1;
                else
                    score += 11;
                aces--;
            }
        }
        return score;
    }
}
