package com.blackjack.domain.card;

import java.util.Objects;

/**
 * Carta imutavel do baralho, definida por naipe e valor de face.
 */
public record Card(Suit suit, Rank rank) {

    public Card {
        Objects.requireNonNull(suit, "suit");
        Objects.requireNonNull(rank, "rank");
    }

    public int baseValue() {
        return rank.baseValue();
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    public boolean isTenValue() {
        return rank.baseValue() == 10;
    }

    public String display() {
        return rank.label() + suit.symbol();
    }

    @Override
    public String toString() {
        return display();
    }
}
