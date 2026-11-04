package com.blackjack.domain.bet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletTest {

    @Test
    void withdrawReducesBalance() {
        Wallet wallet = new Wallet(100);

        wallet.withdraw(30);

        assertEquals(70, wallet.balance());
    }

    @Test
    void depositIncreasesBalance() {
        Wallet wallet = new Wallet(100);

        wallet.deposit(25);

        assertEquals(125, wallet.balance());
    }

    @Test
    void hasAtLeastRespectsExactBoundary() {
        Wallet wallet = new Wallet(50);

        assertTrue(wallet.hasAtLeast(50));
        assertFalse(wallet.hasAtLeast(51));
    }

    @Test
    void withdrawMoreThanBalanceThrows() {
        Wallet wallet = new Wallet(10);

        assertThrows(IllegalStateException.class, () -> wallet.withdraw(11));
    }

    @Test
    void negativeInitialBalanceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Wallet(-1));
    }
}
