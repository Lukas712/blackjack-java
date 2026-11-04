package com.blackjack.domain.player;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.hand.Hand;

import java.util.Set;

/**
 * Regra fixa de decisao do dealer: pede carta enquanto a pontuacao da mao for menor
 * que 17 e para a partir de 17 (inclusive). O dealer nunca compra seguro.
 */
public final class DealerDecisionProvider implements DecisionProvider {

    private static final int STAND_THRESHOLD = 17;

    @Override
    public Action chooseAction(Hand hand, Set<Action> availableActions, GameContext context) {
        if (hand.currentScore().value() < STAND_THRESHOLD && availableActions.contains(Action.HIT)) {
            return Action.HIT;
        }
        return Action.STAND;
    }

    @Override
    public int chooseInsuranceAmount(int maxAmount) {
        return 0;
    }
}
