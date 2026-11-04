package com.blackjack.domain.action;

import com.blackjack.domain.card.Card;

import java.util.Objects;

/**
 * Informacoes contextuais da rodada relevantes para a tomada de decisao de um jogador
 * (ex.: exibir/considerar a carta visivel do dealer ao decidir uma acao).
 */
public record GameContext(Card dealerUpCard) {

    public GameContext {
        Objects.requireNonNull(dealerUpCard, "dealerUpCard");
    }

    public boolean dealerShowsAce() {
        return dealerUpCard.isAce();
    }
}
