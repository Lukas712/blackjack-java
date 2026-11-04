package com.blackjack.result;

import com.blackjack.domain.hand.Hand;

/**
 * Servico sem estado que compara a mao de um jogador com a do dealer para determinar
 * o resultado (vitoria, derrota, empate ou blackjack natural), alem do resultado de
 * uma eventual aposta de seguro.
 */
public final class OutcomeResolver {

    public Outcome resolveHand(Hand playerHand, Hand dealerHand) {
        if (playerHand.isBusted()) {
            return Outcome.LOSS;
        }
        boolean playerNatural = playerHand.isNaturalBlackjack();
        boolean dealerNatural = dealerHand.isNaturalBlackjack();
        if (playerNatural && dealerNatural) {
            return Outcome.PUSH;
        }
        if (playerNatural) {
            return Outcome.NATURAL_BLACKJACK_WIN;
        }
        if (dealerNatural) {
            return Outcome.LOSS;
        }
        if (dealerHand.isBusted()) {
            return Outcome.WIN;
        }
        return compareScores(playerHand, dealerHand);
    }

    private Outcome compareScores(Hand playerHand, Hand dealerHand) {
        int playerScore = playerHand.currentScore().value();
        int dealerScore = dealerHand.currentScore().value();
        if (playerScore > dealerScore) {
            return Outcome.WIN;
        }
        if (playerScore < dealerScore) {
            return Outcome.LOSS;
        }
        return Outcome.PUSH;
    }

    public Outcome resolveInsurance(Hand dealerHand) {
        return dealerHand.isNaturalBlackjack() ? Outcome.INSURANCE_WIN : Outcome.INSURANCE_LOSS;
    }
}
