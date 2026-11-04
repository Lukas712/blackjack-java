package com.blackjack.domain.action;

import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.hand.Hand;

import java.util.EnumSet;
import java.util.Set;

/**
 * Servico sem estado que determina, a cada momento, o conjunto de acoes validas para
 * uma mao (Hit/Stand/Double Down/Split) e a disponibilidade da aposta de Insurance.
 * Centralizar essa regra aqui permite que novas condicoes de disponibilidade sejam
 * adicionadas sem alterar {@link Hand} ou o fluxo de {@code Round}.
 */
public final class ActionEvaluator {

    private static final int BLACKJACK = 21;

    public Set<Action> handActions(Hand hand, Wallet wallet) {
        if (!hand.isActive()) {
            return Set.of();
        }
        Set<Action> actions = EnumSet.of(Action.STAND);
        if (hand.currentScore().value() < BLACKJACK) {
            actions.add(Action.HIT);
        }
        if (canOfferFirstActionOnlyOptions(hand)) {
            addFirstActionOptions(hand, wallet, actions);
        }
        return Set.copyOf(actions);
    }

    private boolean canOfferFirstActionOnlyOptions(Hand hand) {
        return hand.isFirstAction() && hand.bet().isPresent() && !hand.isNaturalBlackjack();
    }

    private void addFirstActionOptions(Hand hand, Wallet wallet, Set<Action> actions) {
        int betAmount = hand.bet().orElseThrow().amount();
        if (!wallet.hasAtLeast(betAmount)) {
            return;
        }
        actions.add(Action.DOUBLE_DOWN);
        if (hand.canSplit()) {
            actions.add(Action.SPLIT);
        }
    }

    public boolean isInsuranceAvailable(Card dealerUpCard, Wallet wallet, int initialBet) {
        return dealerUpCard.isAce() && wallet.hasAtLeast(maxInsuranceAmount(initialBet));
    }

    public int maxInsuranceAmount(int initialBet) {
        return initialBet / 2;
    }
}
