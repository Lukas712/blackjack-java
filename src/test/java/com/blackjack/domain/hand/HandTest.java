package com.blackjack.domain.hand;

import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandTest {

    private static Card of(Rank rank) {
        return new Card(Suit.COPAS, rank);
    }

    private static Card of(Suit suit, Rank rank) {
        return new Card(suit, rank);
    }

    @Test
    void naturalBlackjackIsDetectedRegardlessOfCardOrder() {
        Hand aceFirst = Hand.dealInitial(of(Rank.ACE), of(Rank.KING), new Bet(10));
        Hand tenValueFirst = Hand.dealInitial(of(Rank.QUEEN), of(Rank.ACE), new Bet(10));

        assertTrue(aceFirst.isNaturalBlackjack());
        assertTrue(tenValueFirst.isNaturalBlackjack());
    }

    @Test
    void naturalBlackjackRequiresExactlyTwoCards() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));
        hand.addCard(of(Rank.ACE));

        assertFalse(hand.isNaturalBlackjack());
    }

    @Test
    void handResultingFromSplitIsNeverANaturalBlackjack() {
        Hand original = Hand.dealInitial(of(Rank.ACE), of(Rank.ACE), new Bet(10));

        Hand sibling = original.split();
        original.addCard(of(Rank.KING));
        sibling.addCard(of(Rank.KING));

        assertFalse(original.isNaturalBlackjack());
        assertFalse(sibling.isNaturalBlackjack());
    }

    @Test
    void canSplitWhenBothCardsShareTheSameBaseValue() {
        Hand sameRank = Hand.dealInitial(of(Rank.EIGHT), of(Rank.EIGHT), new Bet(10));
        Hand bothTenValue = Hand.dealInitial(of(Rank.KING), of(Rank.QUEEN), new Bet(10));
        Hand bothAces = Hand.dealInitial(of(Rank.ACE), of(Rank.ACE), new Bet(10));

        assertTrue(sameRank.canSplit());
        assertTrue(bothTenValue.canSplit());
        assertTrue(bothAces.canSplit());
    }

    @Test
    void cannotSplitWhenCardsHaveDifferentBaseValues() {
        Hand hand = Hand.dealInitial(of(Rank.EIGHT), of(Rank.NINE), new Bet(10));

        assertFalse(hand.canSplit());
    }

    @Test
    void isFirstActionBecomesFalseAfterAHit() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));

        assertTrue(hand.isFirstAction());

        hand.addCard(of(Rank.TWO));

        assertFalse(hand.isFirstAction());
    }

    @Test
    void doubleDownDoublesTheBetAddsExactlyOneCardAndEndsTheHand() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));

        hand.doubleDown(of(Rank.THREE));

        assertEquals(20, hand.bet().orElseThrow().amount());
        assertEquals(3, hand.cards().size());
        assertTrue(hand.isDoubled());
        assertFalse(hand.isActive());
    }

    @Test
    void doubleDownOnABustingCardLeavesHandBusted() {
        Hand hand = Hand.dealInitial(of(Rank.KING), of(Rank.NINE), new Bet(10));

        hand.doubleDown(of(Rank.FIVE));

        assertTrue(hand.isBusted());
        assertFalse(hand.isActive());
    }

    @Test
    void doubleDownIsOnlyAllowedAsTheFirstAction() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));
        hand.addCard(of(Rank.TWO));

        assertThrows(IllegalStateException.class, () -> hand.doubleDown(of(Rank.THREE)));
    }

    @Test
    void splitProducesTwoIndependentHandsWithMatchingBets() {
        Hand original = Hand.dealInitial(of(Suit.COPAS, Rank.EIGHT), of(Suit.ESPADAS, Rank.EIGHT), new Bet(10));

        Hand sibling = original.split();

        assertEquals(1, original.cards().size());
        assertEquals(1, sibling.cards().size());
        assertEquals(10, sibling.bet().orElseThrow().amount());
        assertTrue(original.isFromSplit());
        assertTrue(sibling.isFromSplit());
    }

    @Test
    void splitThrowsWhenCardsAreNotEligible() {
        Hand hand = Hand.dealInitial(of(Rank.EIGHT), of(Rank.NINE), new Bet(10));

        assertThrows(IllegalStateException.class, hand::split);
    }

    @Test
    void addCardAfterHandIsNoLongerActiveThrows() {
        Hand hand = Hand.dealInitial(of(Rank.FIVE), of(Rank.SIX), new Bet(10));
        hand.stand();

        assertThrows(IllegalStateException.class, () -> hand.addCard(of(Rank.TWO)));
    }

    @Test
    void dealerInitialHandHasNoBet() {
        Hand dealerHand = Hand.dealerInitial(of(Rank.TEN), of(Rank.SIX));

        assertTrue(dealerHand.bet().isEmpty());
    }
}
