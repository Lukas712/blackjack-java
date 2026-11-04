package com.blackjack.domain.action;

import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import com.blackjack.domain.hand.Hand;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionEvaluatorTest {

    private static Card of(Rank rank) {
        return new Card(Suit.COPAS, rank);
    }

    private final ActionEvaluator evaluator = new ActionEvaluator();

    @Test
    void firstActionWithSufficientFundsAndNonMatchingCardsOffersHitStandAndDoubleDown() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));

        Set<Action> actions = evaluator.handActions(hand, new Wallet(100));

        assertEquals(Set.of(Action.HIT, Action.STAND, Action.DOUBLE_DOWN), actions);
    }

    @Test
    void firstActionWithMatchingCardsAlsoOffersSplit() {
        Hand hand = Hand.dealInitial(of(Rank.EIGHT), of(Rank.EIGHT), new Bet(10));

        Set<Action> actions = evaluator.handActions(hand, new Wallet(100));

        assertEquals(Set.of(Action.HIT, Action.STAND, Action.DOUBLE_DOWN, Action.SPLIT), actions);
    }

    @Test
    void insufficientWalletExcludesDoubleDownAndSplit() {
        Hand hand = Hand.dealInitial(of(Rank.EIGHT), of(Rank.EIGHT), new Bet(10));

        Set<Action> actions = evaluator.handActions(hand, new Wallet(5));

        assertEquals(Set.of(Action.HIT, Action.STAND), actions);
    }

    @Test
    void naturalBlackjackOnlyOffersStandEvenWithFundsAvailable() {
        Hand hand = Hand.dealInitial(of(Rank.ACE), of(Rank.KING), new Bet(10));

        Set<Action> actions = evaluator.handActions(hand, new Wallet(1000));

        assertEquals(Set.of(Action.STAND), actions);
    }

    @Test
    void afterFirstActionOnlyHitAndStandRemain() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));
        hand.addCard(of(Rank.TWO));

        Set<Action> actions = evaluator.handActions(hand, new Wallet(1000));

        assertEquals(Set.of(Action.HIT, Action.STAND), actions);
    }

    @Test
    void inactiveHandOffersNoActions() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));
        hand.stand();

        assertTrue(evaluator.handActions(hand, new Wallet(1000)).isEmpty());
    }

    @Test
    void insuranceAvailableOnlyWhenDealerShowsAceAndWalletHasHalfTheBet() {
        assertTrue(evaluator.isInsuranceAvailable(of(Rank.ACE), new Wallet(5), 10));
        assertFalse(evaluator.isInsuranceAvailable(of(Rank.ACE), new Wallet(4), 10));
        assertFalse(evaluator.isInsuranceAvailable(of(Rank.KING), new Wallet(1000), 10));
    }

    @Test
    void maxInsuranceAmountIsHalfOfTheInitialBet() {
        assertEquals(5, evaluator.maxInsuranceAmount(10));
    }
}
