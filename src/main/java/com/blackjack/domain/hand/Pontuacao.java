package com.blackjack.domain.hand;

import java.util.Objects;

/**
 * Resultado do calculo de pontuacao de uma mao: o valor total considerando o melhor
 * ajuste possivel para Ases e se ao menos um As ainda esta sendo contado como 11 pontos.
 */
public final class Pontuacao {

    private final int valor;
    private final boolean suave;

    public Pontuacao(int valor, boolean suave) {
        this.valor = valor;
        this.suave = suave;
    }

    public int valor() {
        return valor;
    }

    public boolean suave() {
        return suave;
    }

    public boolean estourou() {
        return valor > 21;
    }

    public boolean ehSuave() {
        return suave;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Pontuacao)) {
            return false;
        }
        Pontuacao pontuacao = (Pontuacao) outro;
        return valor == pontuacao.valor && suave == pontuacao.suave;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, suave);
    }

    @Override
    public String toString() {
        return "Pontuacao[valor=" + valor + ", suave=" + suave + "]";
    }
}
