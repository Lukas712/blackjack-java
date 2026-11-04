package com.blackjack.domain.hand;

import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoreCalculatorTest {

    private static Card card(Rank rank) {
        return new Card(Suit.COPAS, rank);
    }

    @Test
    void sumsNumericAndFaceCardsAtFaceValue() {
        Score score = ScoreCalculator.calculate(List.of(card(Rank.NINE), card(Rank.KING)));

        assertEquals(19, score.value());
        assertFalse(score.isSoft());
    }

    @Test
    void countsSingleAceAsElevenWhenItDoesNotBust() {
        Score score = ScoreCalculator.calculate(List.of(card(Rank.ACE), card(Rank.SEVEN)));

        assertEquals(18, score.value());
        assertTrue(score.isSoft());
    }

    @Test
    void downgradesAceFromElevenToOneToAvoidBust() {
        Score score = ScoreCalculator.calculate(List.of(card(Rank.ACE), card(Rank.NINE), card(Rank.FIVE)));

        assertEquals(15, score.value());
        assertFalse(score.isSoft());
        assertFalse(score.isBust());
    }

    @Test
    void handlesTwoAcesByCountingOnlyOneAsEleven() {
        Score score = ScoreCalculator.calculate(List.of(card(Rank.ACE), card(Rank.ACE)));

        assertEquals(12, score.value());
        assertTrue(score.isSoft());
    }

    @Test
    void detectsBustWhenTotalExceedsTwentyOne() {
        Score score = ScoreCalculator.calculate(List.of(card(Rank.TEN), card(Rank.NINE), card(Rank.FIVE)));

        assertEquals(24, score.value());
        assertTrue(score.isBust());
    }
}
