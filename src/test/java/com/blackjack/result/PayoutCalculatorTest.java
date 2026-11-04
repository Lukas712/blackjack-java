package com.blackjack.result;

import com.blackjack.domain.bet.Bet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayoutCalculatorTest {

    private final PayoutCalculator calculator = new PayoutCalculator();

    @Test
    void commonWinPaysOneToOnePlusTheOriginalStake() {
        assertEquals(20, calculator.handPayout(new Bet(10), Outcome.WIN));
    }

    @Test
    void naturalBlackjackPaysThreeToTwoPlusTheOriginalStake() {
        assertEquals(25, calculator.handPayout(new Bet(10), Outcome.NATURAL_BLACKJACK_WIN));
    }

    @Test
    void pushReturnsExactlyTheStake() {
        assertEquals(10, calculator.handPayout(new Bet(10), Outcome.PUSH));
    }

    @Test
    void lossPaysNothingBackSinceTheStakeWasAlreadyWithdrawn() {
        assertEquals(0, calculator.handPayout(new Bet(10), Outcome.LOSS));
    }

    @Test
    void insuranceWinPaysTwoToOne() {
        assertEquals(30, calculator.insurancePayout(new Bet(10), Outcome.INSURANCE_WIN));
    }

    @Test
    void insuranceLossPaysNothing() {
        assertEquals(0, calculator.insurancePayout(new Bet(10), Outcome.INSURANCE_LOSS));
    }

    @Test
    void handPayoutRejectsInsuranceOutcomes() {
        assertThrows(IllegalArgumentException.class, () -> calculator.handPayout(new Bet(10), Outcome.INSURANCE_WIN));
    }

    @Test
    void insurancePayoutRejectsHandOutcomes() {
        assertThrows(IllegalArgumentException.class, () -> calculator.insurancePayout(new Bet(10), Outcome.WIN));
    }
}
