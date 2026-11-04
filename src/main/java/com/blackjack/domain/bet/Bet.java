package com.blackjack.domain.bet;

/**
 * Valor de uma aposta (principal, de seguro, ou resultante de split/double down).
 */
public record Bet(int amount) {

    public Bet {
        if (amount <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser positivo: " + amount);
        }
    }

    public Bet doubled() {
        return new Bet(amount * 2);
    }
}
