package com.blackjack.domain.player;

import com.blackjack.domain.bet.Banca;

/**
 * Jogador humano, cujas decisoes sao obtidas atraves de um {@link ProvedorDeDecisao}
 * ligado a interface de console (ou, em testes, a um provedor com script fixo).
 */
public final class JogadorHumano extends Jogador {

    public JogadorHumano(String nome, Banca banca, ProvedorDeDecisao provedorDeDecisao) {
        super(nome, banca, provedorDeDecisao);
    }
}
