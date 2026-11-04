package com.blackjack.domain.player;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.hand.Mao;

import java.util.Set;

/**
 * Abstrai como a decisao de uma acao (ou do valor de um seguro) e obtida para um
 * jogador, permitindo substituir a origem da decisao (console, regra fixa do banqueiro,
 * scripts de teste) sem alterar {@link Jogador}. Satisfaz a inversao de dependencia:
 * {@link Jogador} depende apenas desta abstracao.
 */
public interface ProvedorDeDecisao {

    Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto);

    int escolherValorDoSeguro(int valorMaximo);
}
