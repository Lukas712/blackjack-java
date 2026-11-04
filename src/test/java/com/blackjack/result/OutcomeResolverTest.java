package com.blackjack.result;

import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import com.blackjack.domain.hand.Hand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutcomeResolverTest {

    private static Card of(Rank rank) {
        return new Card(Suit.COPAS, rank);
    }

    private final OutcomeResolver resolver = new OutcomeResolver();

    @Test
    void playerNaturalAgainstNonNaturalDealerWinsAsNaturalBlackjack() {
        Hand player = Hand.dealInitial(of(Rank.ACE), of(Rank.KING), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.TEN), of(Rank.EIGHT));

        assertEquals(Outcome.NATURAL_BLACKJACK_WIN, resolver.resolveHand(player, dealer));
    }

    @Test
    void bothNaturalBlackjacksResultInPush() {
        Hand player = Hand.dealInitial(of(Rank.ACE), of(Rank.KING), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.QUEEN), of(Rank.ACE));

        assertEquals(Outcome.PUSH, resolver.resolveHand(player, dealer));
    }

    @Test
    void dealerNaturalBeatsPlayerThreeCardTwentyOne() {
        Hand player = Hand.dealInitial(of(Rank.TEN), of(Rank.SIX), new Bet(10));
        player.addCard(of(Rank.FIVE));
        Hand dealer = Hand.dealerInitial(of(Rank.ACE), of(Rank.KING));

        assertEquals(Outcome.LOSS, resolver.resolveHand(player, dealer));
    }

    @Test
    void playerNaturalBeatsDealerThreeCardTwentyOne() {
        Hand player = Hand.dealInitial(of(Rank.ACE), of(Rank.KING), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.TEN), of(Rank.SIX));
        dealer.addCard(of(Rank.FIVE));

        assertEquals(Outcome.NATURAL_BLACKJACK_WIN, resolver.resolveHand(player, dealer));
    }

    @Test
    void playerBustIsAlwaysALossEvenIfDealerAlsoBusted() {
        Hand player = Hand.dealInitial(of(Rank.TEN), of(Rank.NINE), new Bet(10));
        player.addCard(of(Rank.FIVE));
        Hand dealer = Hand.dealerInitial(of(Rank.TEN), of(Rank.NINE));
        dealer.addCard(of(Rank.FIVE));

        assertEquals(Outcome.LOSS, resolver.resolveHand(player, dealer));
    }

    @Test
    void dealerBustIsAWinForAnActivePlayerHand() {
        Hand player = Hand.dealInitial(of(Rank.FOUR), of(Rank.EIGHT), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.TEN), of(Rank.NINE));
        dealer.addCard(of(Rank.FIVE));

        assertEquals(Outcome.WIN, resolver.resolveHand(player, dealer));
    }

    @Test
    void equalNonNaturalScoresResultInPush() {
        Hand player = Hand.dealInitial(of(Rank.TEN), of(Rank.EIGHT), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.NINE), of(Rank.NINE));

        assertEquals(Outcome.PUSH, resolver.resolveHand(player, dealer));
    }

    @Test
    void higherNonNaturalScoreWinsAndLowerScoreLoses() {
        Hand winner = Hand.dealInitial(of(Rank.TEN), of(Rank.NINE), new Bet(10));
        Hand loser = Hand.dealInitial(of(Rank.TEN), of(Rank.SIX), new Bet(10));
        Hand dealer = Hand.dealerInitial(of(Rank.TEN), of(Rank.SEVEN));

        assertEquals(Outcome.WIN, resolver.resolveHand(winner, dealer));
        assertEquals(Outcome.LOSS, resolver.resolveHand(loser, dealer));
    }

    @Test
    void insuranceWinsOnlyWhenDealerHasNaturalBlackjack() {
        Hand dealerWithNatural = Hand.dealerInitial(of(Rank.ACE), of(Rank.KING));
        Hand dealerWithoutNatural = Hand.dealerInitial(of(Rank.ACE), of(Rank.SEVEN));

        assertEquals(Outcome.INSURANCE_WIN, resolver.resolveInsurance(dealerWithNatural));
        assertEquals(Outcome.INSURANCE_LOSS, resolver.resolveInsurance(dealerWithoutNatural));
    }
}
