import java.util.*;
/**
 * The objects of the Solitaire class exectues a Solitaire game.
 * @author Emily Kwan
 * @version Dec 2022
 */
public class Solitaire
{
    /**
     * Executes the Solitaire game.
     * @param args arguments of the command line
     */
    public static void main(String[] args)
    {
        new Solitaire();
    }

    private Stack<Card> stock;
    private Stack<Card> waste;
    private Stack<Card>[] foundations;
    private Stack<Card>[] piles;
    private SolitaireDisplay display;

    /**
     * Constructor of the Solitaire class
     */
    public Solitaire()
    {
        foundations = new Stack[4];
        for(int i = 0; i < foundations.length; i++)
        {
            foundations[i] = new Stack<Card>();
        }

        piles = new Stack[7];
        for(int i = 0; i < piles.length; i++)
        {
            piles[i] = new Stack<Card>();
        }

        display = new SolitaireDisplay(this);
        stock = createStock();
        waste = new Stack<Card>();
        this.deal();
        display.paintComponent(display.getGraphics());
    }

    /**
     * returns the card on top of the stock, or null if the stock is empty
     * @return the card at the top of the stock
     */
    public Card getStockCard()
    {
        if(stock.isEmpty())
            return null;
        return stock.peek();
    }

    /**
     * returns the card on top of the waste, or null if the waste is empty
     * @return the card at the top of the waste
     */
    public Card getWasteCard()
    {
        if(waste.isEmpty())
            return null;
        return waste.peek();
    }

    /**
     * precondition:  0 <= index < 4
     * postcondition: returns the card on top of the given
     * foundation, or null if the foundation is empty
     * 
     * @return the card at the top of the given foundation
     * @param index the index of the foundation pile
     */
    public Card getFoundationCard(int index)
    {
        if(foundations[index].isEmpty())
            return null;
        return foundations[index].peek();
    }

    /**
     * precondition:  0 <= index < 7
     * postcondition: returns a reference to the given pile
     * @return the pile at the given index
     * @param index the index of the pile
     */
    public Stack<Card> getPile(int index)
    {
        return piles[index];
    }

    /**
     * called when the stock is clicked
     *
     */
    public void stockClicked()
    {
        if(!display.isPileSelected()&&!display.isWasteSelected())
        {
            if(stock.isEmpty())
                this.resetStock();
            else
                this.dealThreeCards();
        }

    }

    /**
     * called when the waste is clicked
     *
     */
    public void wasteClicked()
    {
        if(display.isWasteSelected())
            display.unselect();
        else if(!waste.isEmpty()&&!display.isPileSelected())
            display.selectWaste();
    }

    /**
     * called when the waste is clicked
     *@param index the index of the foundation pile
     */
    public void foundationClicked(int index)
    {
        if (display.isWasteSelected()&& canAddToFoundation(getWasteCard(),index))
        {
            foundations[index].push(waste.pop());
            display.unselect();
        }
        else if (display.isPileSelected() 
            && canAddToFoundation(getPile(display.selectedPile()).peek(), index)) 
        {
            foundations[index].push(getPile(display.selectedPile()).pop());
            display.unselect();
        }

    }



    /**
     * precondition:  0 <= index < 7
     * called when given pile is clicked
     * @param index the index of the pile
     */
    public void pileClicked(int index)
    {
        if(display.isPileSelected())
        {
            if(!piles[index].isEmpty()&&!piles[index].peek().isFaceUp())
                piles[index].peek().turnUp();
            Stack<Card> cards=this.removeFaceUpCards(display.selectedPile());
            if(this.canAddToPile(cards.peek(),index))
                addToPile(cards,index);
            else
                addToPile(cards,display.selectedPile());
        }
        if(display.isWasteSelected()&&this.canAddToPile(waste.peek(),index))
        {
            piles[index].push(waste.pop());
            this.wasteClicked();

        }
        else if(display.selectedPile()==index)
            display.unselect();
        else if(!piles[index].isEmpty()&&piles[index].peek().isFaceUp())
            display.selectPile(index);
        else if(!piles[index].isEmpty()&&!piles[index].peek().isFaceUp())
            piles[index].peek().turnUp();
    }

    /**
     * Creates the stock
     * @return the created stock
     */
    public Stack<Card> createStock()
    {
        ArrayList<Card> cards = new ArrayList<Card>();
        for(int i = 1; i < 14; i++)
        {
            cards.add(new Card(i, "c"));
        }
        for(int i = 1; i < 14; i++)
        {
            cards.add(new Card(i, "d"));
        }
        for(int i = 1; i < 14; i++)
        {
            cards.add(new Card(i, "h"));
        }
        for(int i = 1; i < 14; i++)
        {
            cards.add(new Card(i, "s"));
        }
        Stack<Card> newStack = new Stack<Card>();
        while(cards.size()>0)
        {
            int rand = (int)(Math.random()*cards.size());
            newStack.push(cards.remove(rand));
        }
        return newStack;
    }

    /**
     * Deals the cards
     */
    public void deal()
    {
        for(int i = piles.length-1; i >-1; i--)
        {
            for(int j = 0; j <= i; j++)
            {
                piles[i].push(stock.pop());
            }
            piles[i].peek().turnUp();
        }
    }   

    /**
     * Deals 3 cards
     */
    public void dealThreeCards()
    {
        int count=0;
        while(!stock.isEmpty()&&count<3)
        {
            waste.push(stock.pop());
            waste.peek().turnUp();
            count++;
        }
    }

    /**
     * Returns true if the given card can be added to the pile at the given index;
     *         other wise, false
     *  @return true if the given card can be added to the pile at the given index;
     *         other wise, false
     *  @param card the given card
     *  @param index the index of the pile
     */
    private boolean canAddToPile(Card card, int index)
    {
        if(!this.getPile(index).isEmpty())
        {
            Card card1 = this.getPile(index).peek();
            if(card1.isFaceUp())
            {
                if(card1.getRank()-1==card.getRank()&&(card.isRed()!=card1.isRed()))      
                    return true;
            }
        }
        else if(card.getRank()==13)
            return true;
        return false;
    }

    /**
     * Returns and removes the face up cards in the pile of the given index
     * @return the face up cards
     * @param index the index of the pile
     */
    private Stack<Card> removeFaceUpCards(int index)
    {
        Stack<Card> cards= new Stack<Card>();
        Stack<Card> pile=getPile(index);
        if(!pile.isEmpty())
        {
            while(!piles[index].isEmpty()&&piles[index].peek().isFaceUp())
            {
                cards.push(piles[index].pop());
            }
            return cards;
        }
        return null;
    }

    /**
     * Adds given cards to pile of given index
     * @param cards the cards to be added to the pile
     * @param index the index of the pile
     */
    private void addToPile(Stack<Card> cards, int index)
    {
        while(!cards.isEmpty())
        {
            piles[index].push(cards.pop());
        }
    }

    /**
     * Returns true if the given card can be added to the foundation at the given index;
     *      otherwise, false
     * @return true if the given card can be added to the foundation at the given index;
     *      otherwise, false
     * @param card the card to be added to the foundation
     * @param index the index of the foundation pile
     */
    private boolean canAddToFoundation(Card card, int index)
    {

        if(foundations[index].isEmpty())
        {
            if(card.getRank()==1)
                return true;
        }
        Card card1=getFoundationCard(index);
        if(card1.getRank()+1==card.getRank()&&card1.getSuit().equals(card.getSuit()))
            return true;
        return false;
    }

    /**
     * Resets the stock
     */
    public void resetStock()
    {
        while(!waste.isEmpty())
        {
            Card popped = waste.pop();
            popped.turnDown();
            stock.push(popped);
        }
    }
}