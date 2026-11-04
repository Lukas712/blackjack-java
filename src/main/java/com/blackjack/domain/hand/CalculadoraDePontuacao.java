package com.blackjack.domain.hand;

import com.blackjack.domain.card.Carta;

import java.util.List;

/**
 * Servico sem estado responsavel por calcular a pontuacao de uma mao de cartas,
 * aplicando o ajuste automatico do As (suave 11 -&gt; dura 1) quando a soma estoura 21.
 */
public final class CalculadoraDePontuacao {

    private static final int VINTE_E_UM = 21;
    private static final int AJUSTE_SUAVE_PARA_DURA = 10;

    private CalculadoraDePontuacao() {
    }

    public static Pontuacao calcular(List<Carta> cartas) {
        int total = 0;
        int asesComoOnze = 0;
        for (Carta carta : cartas) {
            total += carta.valorBase();
            if (carta.ehAs()) {
                asesComoOnze++;
            }
        }
        while (total > VINTE_E_UM && asesComoOnze > 0) {
            total -= AJUSTE_SUAVE_PARA_DURA;
            asesComoOnze--;
        }
        return new Pontuacao(total, asesComoOnze > 0);
    }
}
