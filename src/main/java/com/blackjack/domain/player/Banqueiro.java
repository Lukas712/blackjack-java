package com.blackjack.domain.player;

import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;

/**
 * O banqueiro e um tipo de jogador cuja decisao e automatica (ver
 * {@link ProvedorDeDecisaoDoBanqueiro}) e que nao realiza apostas; a banca herdada de
 * {@link Jogador} nao e utilizada nas regras de negocio, existindo apenas para
 * respeitar o contrato da superclasse.
 */
public final class Banqueiro extends Jogador {

    public Banqueiro() {
        super("Banqueiro", new Banca(0), new ProvedorDeDecisaoDoBanqueiro());
    }

    public Mao mao() {
        if (maos().isEmpty()) {
            throw new IllegalStateException("O banqueiro ainda nao recebeu cartas nesta rodada.");
        }
        return maos().get(0);
    }

    public Carta cartaVisivel() {
        return mao().cartas().get(0);
    }

    public Carta cartaOculta() {
        return mao().cartas().get(1);
    }
}
