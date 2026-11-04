package com.blackjack.domain.bet;

/**
 * Banca de fichas de um jogador: controla saldo, debitos e creditos.
 */
public final class Wallet {

    private int balance;

    public Wallet(int initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("O saldo inicial nao pode ser negativo: " + initialBalance);
        }
        this.balance = initialBalance;
    }

    public int balance() {
        return balance;
    }

    public boolean hasAtLeast(int amount) {
        return balance >= amount;
    }

    public void withdraw(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Valor de saque nao pode ser negativo: " + amount);
        }
        if (!hasAtLeast(amount)) {
            throw new IllegalStateException(
                    "Saldo insuficiente: saldo=" + balance + ", solicitado=" + amount);
        }
        balance -= amount;
    }

    public void deposit(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Valor de deposito nao pode ser negativo: " + amount);
        }
        balance += amount;
    }
}
