package com.blackjack.domain.bet;

/**
 * Valor de uma aposta (principal, de seguro, ou resultante de split/double down).
 */
public record Aposta(int valor) {

    public Aposta {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da aposta deve ser positivo: " + valor);
        }
    }

    public Aposta dobrada() {
        return new Aposta(valor * 2);
    }
}
