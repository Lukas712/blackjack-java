package com.blackjack.domain.card;

/**
 * Naipe de uma carta do baralho convencional de 52 cartas.
 */
public enum Naipe {
    COPAS("\u2665"),
    OUROS("\u2666"),
    PAUS("\u2663"),
    ESPADAS("\u2660");

    private final String simbolo;

    Naipe(String simbolo) {
        this.simbolo = simbolo;
    }

    public String simbolo() {
        return simbolo;
    }
}
