package com.blackjack.domain.player;

import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.hand.Hand;

/**
 * O dealer e um tipo de jogador cuja decisao e automatica (ver
 * {@link DealerDecisionProvider}) e que nao realiza apostas; a banca herdada de
 * {@link Player} nao e utilizada nas regras de negocio, existindo apenas para
 * respeitar o contrato da superclasse.
 */
public final class Dealer extends Player {

    public Dealer() {
        super("Dealer", new Wallet(0), new DealerDecisionProvider());
    }

    public Hand hand() {
        if (hands().isEmpty()) {
            throw new IllegalStateException("O dealer ainda nao recebeu cartas nesta rodada.");
        }
        return hands().get(0);
    }

    public Card upCard() {
        return hand().cards().get(0);
    }

    public Card holeCard() {
        return hand().cards().get(1);
    }
}
