package com.blackjack.domain.bet;

/**
 * Valor de uma aposta (principal, de seguro, ou resultante de split/double down).
 */
public final class Aposta {

    private final int valor;

    public Aposta(int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser positivo: " + valor);
        }
        this.valor = valor;
    }

    public int valor() {
        return valor;
    }

    public Aposta dobrada() {
        return new Aposta(valor * 2);
    }

    @Override
    public boolean equals(Object outro) {
        return this == outro || (outro instanceof Aposta && valor == ((Aposta) outro).valor);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(valor);
    }

    @Override
    public String toString() {
        return "Aposta[valor=" + valor + "]";
    }
}
