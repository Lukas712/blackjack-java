package com.blackjack.result;

import com.blackjack.domain.hand.Mao;

/**
 * Servico sem estado que compara a mao de um jogador com a do banqueiro para determinar
 * o resultado (vitoria, derrota, empate ou blackjack natural), alem do resultado de
 * uma eventual aposta de seguro.
 */
public final class ResolvedorDeResultado {

    public Resultado resolverMao(Mao maoDoJogador, Mao maoDoBanqueiro) {
        if (maoDoJogador.estaEstourada()) {
            return Resultado.DERROTA;
        }
        boolean naturalDoJogador = maoDoJogador.ehBlackjackNatural();
        boolean naturalDoBanqueiro = maoDoBanqueiro.ehBlackjackNatural();
        if (naturalDoJogador && naturalDoBanqueiro) {
            return Resultado.EMPATE;
        }
        if (naturalDoJogador) {
            return Resultado.VITORIA_BLACKJACK_NATURAL;
        }
        if (naturalDoBanqueiro) {
            return Resultado.DERROTA;
        }
        if (maoDoBanqueiro.estaEstourada()) {
            return Resultado.VITORIA;
        }
        return compararPontuacoes(maoDoJogador, maoDoBanqueiro);
    }

    private Resultado compararPontuacoes(Mao maoDoJogador, Mao maoDoBanqueiro) {
        int pontuacaoDoJogador = maoDoJogador.pontuacaoAtual().valor();
        int pontuacaoDoBanqueiro = maoDoBanqueiro.pontuacaoAtual().valor();
        if (pontuacaoDoJogador > pontuacaoDoBanqueiro) {
            return Resultado.VITORIA;
        }
        if (pontuacaoDoJogador < pontuacaoDoBanqueiro) {
            return Resultado.DERROTA;
        }
        return Resultado.EMPATE;
    }

    public Resultado resolverSeguro(Mao maoDoBanqueiro) {
        return maoDoBanqueiro.ehBlackjackNatural() ? Resultado.SEGURO_PAGO : Resultado.SEGURO_PERDIDO;
    }
}
