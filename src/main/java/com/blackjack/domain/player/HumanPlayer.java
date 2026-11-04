package com.blackjack.domain.player;

import com.blackjack.domain.bet.Wallet;

/**
 * Jogador humano, cujas decisoes sao obtidas atraves de um {@link DecisionProvider}
 * ligado a interface de console (ou, em testes, a um provedor com script fixo).
 */
public final class HumanPlayer extends Player {

    public HumanPlayer(String name, Wallet wallet, DecisionProvider decisionProvider) {
        super(name, wallet, decisionProvider);
    }
}
