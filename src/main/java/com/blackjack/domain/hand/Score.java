package com.blackjack.domain.hand;

/**
 * Resultado do calculo de pontuacao de uma mao.
 *
 * @param value valor total considerando o melhor ajuste possivel para Ases
 * @param soft  {@code true} se ao menos um As ainda esta sendo contado como 11 pontos
 */
public record Score(int value, boolean soft) {

    public boolean isBust() {
        return value > 21;
    }

    public boolean isSoft() {
        return soft;
    }
}
