package com.blackjack.domain.deck;

import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Baralho convencional de 52 cartas (4 naipes x 13 valores), compartilhado entre todos
 * os jogadores e o dealer de uma mesma rodada. Uma nova instancia e criada e embaralhada
 * a cada rodada.
 */
public final class Deck {

    private final Deque<Card> cards = new ArrayDeque<>();

    private Deck() {
    }

    /**
     * Cria um baralho completo de 52 cartas e o embaralha usando a fonte de
     * aleatoriedade informada (injetavel para permitir testes deterministicos).
     */
    public static Deck newShuffledDeck(Random random) {
        Deck deck = new Deck();
        List<Card> freshCards = freshCards();
        Collections.shuffle(freshCards, random);
        deck.cards.addAll(freshCards);
        return deck;
    }

    /**
     * Cria um baralho com as cartas na ordem exata informada (a primeira da lista e
     * a primeira a ser distribuida). Util para cenarios de teste deterministicos.
     */
    public static Deck orderedDeck(List<Card> cards) {
        Deck deck = new Deck();
        deck.cards.addAll(cards);
        return deck;
    }

    private static List<Card> freshCards() {
        List<Card> result = new ArrayList<>(52);
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }

    public Card draw() {
        Card card = cards.pollFirst();
        if (card == null) {
            throw new IllegalStateException("O baralho esta vazio.");
        }
        return card;
    }

    public int remainingCards() {
        return cards.size();
    }
}
