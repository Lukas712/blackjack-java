package com.blackjack.domain.card;

import java.util.Objects;

/**
 * Carta imutavel do baralho, definida por naipe e valor de face.
 */
public record Carta(Naipe naipe, Valor valor) {

    public Carta {
        Objects.requireNonNull(naipe, "naipe");
        Objects.requireNonNull(valor, "valor");
    }

    public int valorBase() {
        return valor.valorBase();
    }

    public boolean ehAs() {
        return valor == Valor.AS;
    }

    public boolean valeDez() {
        return valor.valorBase() == 10;
    }

    public String exibir() {
        return valor.rotulo() + naipe.simbolo();
    }

    @Override
    public String toString() {
        return exibir();
    }
}
