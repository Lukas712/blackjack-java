package com.blackjack.domain.hand;

/**
 * Resultado do calculo de pontuacao de uma mao.
 *
 * @param valor  valor total considerando o melhor ajuste possivel para Ases
 * @param suave  {@code true} se ao menos um As ainda esta sendo contado como 11 pontos
 */
public record Pontuacao(int valor, boolean suave) {

    public boolean estourou() {
        return valor > 21;
    }

    public boolean ehSuave() {
        return suave;
    }
}
