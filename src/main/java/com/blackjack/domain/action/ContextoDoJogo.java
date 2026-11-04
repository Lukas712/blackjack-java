package com.blackjack.domain.action;

import com.blackjack.domain.card.Carta;

import java.util.Objects;

/**
 * Informacoes contextuais da rodada relevantes para a tomada de decisao de um jogador
 * (ex.: exibir/considerar a carta visivel do banqueiro ao decidir uma acao).
 */
public record ContextoDoJogo(Carta cartaVisivelDoBanqueiro) {

    public ContextoDoJogo {
        Objects.requireNonNull(cartaVisivelDoBanqueiro, "cartaVisivelDoBanqueiro");
    }

    public boolean banqueiroMostraAs() {
        return cartaVisivelDoBanqueiro.ehAs();
    }
}
