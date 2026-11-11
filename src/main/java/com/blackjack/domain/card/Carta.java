package com.blackjack.domain.card;

import java.util.Objects;

/**
 * Carta imutavel do baralho, definida por naipe e valor de face.
 */
public final class Carta {

    private final Naipe naipe;
    private final Valor valor;

    public Carta(Naipe naipe, Valor valor) {
        this.naipe = Objects.requireNonNull(naipe, "naipe");
        this.valor = Objects.requireNonNull(valor, "valor");
    }

    public Naipe naipe() {
        return naipe;
    }

    public Valor valor() {
        return valor;
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
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Carta)) {
            return false;
        }
        Carta carta = (Carta) outro;
        return naipe == carta.naipe && valor == carta.valor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(naipe, valor);
    }

    @Override
    public String toString() {
        return exibir();
    }
}
