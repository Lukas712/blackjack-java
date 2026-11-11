package com.blackjack.domain.action;

import com.blackjack.domain.card.Carta;

import java.util.Objects;

/**
 * Informacoes contextuais da rodada relevantes para a tomada de decisao de um jogador
 * (ex.: exibir/considerar a carta visivel do banqueiro ao decidir uma acao).
 */
public final class ContextoDoJogo {

    private final Carta cartaVisivelDoBanqueiro;

    public ContextoDoJogo(Carta cartaVisivelDoBanqueiro) {
        this.cartaVisivelDoBanqueiro = Objects.requireNonNull(cartaVisivelDoBanqueiro, "cartaVisivelDoBanqueiro");
    }

    public Carta cartaVisivelDoBanqueiro() {
        return cartaVisivelDoBanqueiro;
    }

    public boolean banqueiroMostraAs() {
        return cartaVisivelDoBanqueiro.ehAs();
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ContextoDoJogo)) {
            return false;
        }
        return cartaVisivelDoBanqueiro.equals(((ContextoDoJogo) outro).cartaVisivelDoBanqueiro);
    }

    @Override
    public int hashCode() {
        return cartaVisivelDoBanqueiro.hashCode();
    }

    @Override
    public String toString() {
        return "ContextoDoJogo[cartaVisivelDoBanqueiro=" + cartaVisivelDoBanqueiro + "]";
    }
}
