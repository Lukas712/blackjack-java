package com.blackjack.domain.card;

/**
 * Naipe de uma carta do baralho convencional de 52 cartas.
 */
public enum Suit {
    COPAS("\u2665"),
    OUROS("\u2666"),
    PAUS("\u2663"),
    ESPADAS("\u2660");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
