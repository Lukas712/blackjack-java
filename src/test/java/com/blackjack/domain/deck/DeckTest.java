package com.blackjack.domain.deck;

import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeckTest {

    @Test
    void shuffledDeckContainsFiftyTwoUniqueCards() {
        Deck deck = Deck.newShuffledDeck(new Random(42));

        Set<Card> seen = new HashSet<>();
        assertEquals(52, deck.remainingCards());
        while (deck.remainingCards() > 0) {
            assertEquals(true, seen.add(deck.draw()));
        }
        assertEquals(52, seen.size());
    }

    @Test
    void drawingAllCardsThenDrawingAgainThrows() {
        Deck deck = Deck.newShuffledDeck(new Random(1));

        for (int i = 0; i < 52; i++) {
            deck.draw();
        }

        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    void orderedDeckDealsCardsInExactGivenOrder() {
        Card first = new Card(Suit.ESPADAS, Rank.ACE);
        Card second = new Card(Suit.COPAS, Rank.KING);
        Deck deck = Deck.orderedDeck(List.of(first, second));

        assertEquals(first, deck.draw());
        assertEquals(second, deck.draw());
        assertEquals(0, deck.remainingCards());
    }
}
