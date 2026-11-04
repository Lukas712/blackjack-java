package com.blackjack.domain.card;

/**
 * Valor de face de uma carta e sua pontuacao base associada.
 * O As e tratado com valor base 11; o ajuste para 1 ponto (suave -> dura)
 * e responsabilidade de {@link com.blackjack.domain.hand.CalculadoraDePontuacao}.
 */
public enum Valor {
    DOIS("2", 2),
    TRES("3", 3),
    QUATRO("4", 4),
    CINCO("5", 5),
    SEIS("6", 6),
    SETE("7", 7),
    OITO("8", 8),
    NOVE("9", 9),
    DEZ("10", 10),
    VALETE("J", 10),
    DAMA("Q", 10),
    REI("K", 10),
    AS("A", 11);

    private final String rotulo;
    private final int valorBase;

    Valor(String rotulo, int valorBase) {
        this.rotulo = rotulo;
        this.valorBase = valorBase;
    }

    public String rotulo() {
        return rotulo;
    }

    public int valorBase() {
        return valorBase;
    }
}
