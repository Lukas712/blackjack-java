package com.blackjack.domain.card;

/**
 * Valor de face de uma carta e sua pontuacao base associada.
 * O As e tratado com valor base 11; o ajuste para 1 ponto (soft -> hard)
 * e responsabilidade de {@link com.blackjack.domain.hand.ScoreCalculator}.
 */
public enum Rank {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 10),
    QUEEN("Q", 10),
    KING("K", 10),
    ACE("A", 11);

    private final String label;
    private final int baseValue;

    Rank(String label, int baseValue) {
        this.label = label;
        this.baseValue = baseValue;
    }

    public String label() {
        return label;
    }

    public int baseValue() {
        return baseValue;
    }
}
