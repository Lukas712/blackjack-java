package com.blackjack.domain.hand;

import com.blackjack.domain.card.Card;

import java.util.List;

/**
 * Servico sem estado responsavel por calcular a pontuacao de uma mao de cartas,
 * aplicando o ajuste automatico do As (soft 11 -&gt; hard 1) quando a soma estoura 21.
 */
public final class ScoreCalculator {

    private static final int BLACKJACK = 21;
    private static final int SOFT_TO_HARD_ADJUSTMENT = 10;

    private ScoreCalculator() {
    }

    public static Score calculate(List<Card> cards) {
        int total = 0;
        int acesAsEleven = 0;
        for (Card card : cards) {
            total += card.baseValue();
            if (card.isAce()) {
                acesAsEleven++;
            }
        }
        while (total > BLACKJACK && acesAsEleven > 0) {
            total -= SOFT_TO_HARD_ADJUSTMENT;
            acesAsEleven--;
        }
        return new Score(total, acesAsEleven > 0);
    }
}
