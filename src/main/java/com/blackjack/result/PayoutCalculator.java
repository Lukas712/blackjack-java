package com.blackjack.result;

import com.blackjack.domain.bet.Bet;

/**
 * Servico sem estado que calcula o valor total a ser creditado na banca do jogador
 * (aposta devolvida + eventual ganho) a partir do {@link Outcome} de uma mao ou de
 * uma aposta de seguro. Para derrota/seguro perdido, o valor ja foi debitado da
 * banca no momento em que a aposta foi feita, portanto o credito e zero.
 */
public final class PayoutCalculator {

    public int handPayout(Bet bet, Outcome outcome) {
        return switch (outcome) {
            case NATURAL_BLACKJACK_WIN -> bet.amount() + (bet.amount() * 3) / 2;
            case WIN -> bet.amount() * 2;
            case PUSH -> bet.amount();
            case LOSS -> 0;
            case INSURANCE_WIN, INSURANCE_LOSS ->
                    throw new IllegalArgumentException("Outcome de seguro nao se aplica a uma mao: " + outcome);
        };
    }

    public int insurancePayout(Bet insuranceBet, Outcome outcome) {
        return switch (outcome) {
            case INSURANCE_WIN -> insuranceBet.amount() * 3;
            case INSURANCE_LOSS -> 0;
            default -> throw new IllegalArgumentException("Outcome de mao nao se aplica a um seguro: " + outcome);
        };
    }
}
