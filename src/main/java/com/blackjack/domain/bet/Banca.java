package com.blackjack.domain.bet;

/**
 * Banca de fichas de um jogador: controla saldo, debitos e creditos.
 */
public final class Banca {

    private int saldo;

    public Banca(int saldoInicial) {
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("O saldo inicial nao pode ser negativo: " + saldoInicial);
        }
        this.saldo = saldoInicial;
    }

    public int saldo() {
        return saldo;
    }

    public boolean possuiPeloMenos(int valor) {
        return saldo >= valor;
    }

    public void sacar(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor de saque nao pode ser negativo: " + valor);
        }
        if (!possuiPeloMenos(valor)) {
            throw new IllegalStateException(
                    "Saldo insuficiente: saldo=" + saldo + ", solicitado=" + valor);
        }
        saldo -= valor;
    }

    public void depositar(int valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor de deposito nao pode ser negativo: " + valor);
        }
        saldo = Math.addExact(saldo, valor);
    }
}
