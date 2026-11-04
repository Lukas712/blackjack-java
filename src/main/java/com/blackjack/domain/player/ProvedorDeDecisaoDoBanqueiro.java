package com.blackjack.domain.player;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.hand.Mao;

import java.util.Set;

/**
 * Regra fixa de decisao do banqueiro: pede carta enquanto a pontuacao da mao for menor
 * que 17 e para a partir de 17 (inclusive). O banqueiro nunca compra seguro.
 */
public final class ProvedorDeDecisaoDoBanqueiro implements ProvedorDeDecisao {

    private static final int LIMITE_PARA_PARAR = 17;

    @Override
    public Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
        if (mao.pontuacaoAtual().valor() < LIMITE_PARA_PARAR && acoesDisponiveis.contains(Acao.PEDIR_CARTA)) {
            return Acao.PEDIR_CARTA;
        }
        return Acao.PARAR;
    }

    @Override
    public int escolherValorDoSeguro(int valorMaximo) {
        return 0;
    }
}
