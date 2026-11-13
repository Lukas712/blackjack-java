package com.blackjack.domain.action;

import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;

import java.util.Collections;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Servico sem estado que determina, a cada momento, o conjunto de acoes validas para
 * uma mao (Hit/Stand/Double Down/Split) e a disponibilidade da aposta de Insurance.
 * Centralizar essa regra aqui permite que novas condicoes de disponibilidade sejam
 * adicionadas sem alterar {@link Mao} ou o fluxo de {@code Rodada}.
 */
public final class AvaliadorDeAcoes {

    private static final int VINTE_E_UM = 21;


    public Set<Acao> acoesDaMao(Mao mao, Banca banca) {
        if (!mao.estaAtiva()) {
            return Collections.emptySet();
        }

        Set<Acao> acoes = EnumSet.of(
            Acao.PARAR,
            Acao.PEDIR_CARTA
        );

        if (podeOferecerOpcoesDePrimeiraAcao(mao)) {
            adicionarOpcoesDePrimeiraAcao(mao, banca, acoes);
        }

        return Collections.unmodifiableSet(acoes);
    }



    private boolean podeOferecerOpcoesDePrimeiraAcao(Mao mao) {
        return mao.ehPrimeiraAcao()
            && mao.aposta().isPresent();
    }


    private void adicionarOpcoesDePrimeiraAcao(Mao mao, Banca banca, Set<Acao> acoes) {
        int valorDaAposta = mao.aposta().orElseThrow(NoSuchElementException::new).valor();
        if (!banca.possuiPeloMenos(valorDaAposta)) {
            return;
        }
        acoes.add(Acao.DOBRAR);
        if (mao.podeDividir()) {
            acoes.add(Acao.DIVIDIR);
        }
    }

    public boolean seguroDisponivel(Carta cartaVisivelDoBanqueiro, Banca banca, int apostaInicial) {
        return cartaVisivelDoBanqueiro.ehAs() && banca.possuiPeloMenos(valorMaximoDoSeguro(apostaInicial));
    }

    public int valorMaximoDoSeguro(int apostaInicial) {
        return apostaInicial / 2;
    }
}
