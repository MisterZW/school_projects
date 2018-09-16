public final class Shoe<T> extends RandIndexQueue<T> {

    private int numDecks;
    private int originalSize;

    //makes a shoe with the specified number of decks, then shuffles it
    @SuppressWarnings("unchecked")
    public Shoe(int size)
    {
        super(size);
        numDecks = size;
        originalSize = 52 * numDecks;
        for(int i = 0; i < numDecks; i++)
        {
            for(Card.Suits suit : Card.Suits.values())
            {
                for(Card.Ranks rank: Card.Ranks.values())
                {
                    this.offer((T)new Card(suit, rank));
                } //end ranks
            } //end suits
        } //end decks
        shuffle();
    }

    public int getStartSize()
    {
        return originalSize;
    }

    public int getNumDecks()
    {
        return numDecks;
    }
}
