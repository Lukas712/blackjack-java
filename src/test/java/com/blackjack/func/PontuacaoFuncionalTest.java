package com.blackjack.func;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.EstadoMao;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.hand.Pontuacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Func: pontuacao da mao, tratamento do As e fronteira de 21
 * (CT12, CT25, CT26, CT27, CT28 e CT29).
 */
class PontuacaoFuncionalTest {

    private static final Banca BANCA_FOLGADA = new Banca(1000);

    private static Carta carta(Naipe naipe, Valor valor) {
        return new Carta(naipe, valor);
    }

    private static Mao maoInicial(Carta primeira, Carta segunda) {
        return Mao.distribuirInicial(primeira, segunda, new Aposta(40));
    }

    @Test
    @DisplayName("CT12 - [As,8,5]: As ajustado de 11 para 1, pontuacao 14, mao ativa")
    void ct12_asEhAjustadoParaUmQuandoASomaUltrapassaVinteEUm() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.AS), carta(Naipe.COPAS, Valor.OITO));
        assertEquals(19, mao.pontuacaoAtual().valor());

        mao.adicionarCarta(carta(Naipe.OUROS, Valor.CINCO));

        assertEquals(14, mao.pontuacaoAtual().valor());
        assertFalse(mao.pontuacaoAtual().ehSuave());
        assertTrue(mao.estaAtiva());
    }

    @Test
    @DisplayName("CT25 - [9,10] + Hit [2]: 21 com tres cartas, nao e natural, Hit deixa de ser oferecido")
    void ct25_vinteEUmComTresCartasNaoEhBlackjackNatural() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.NOVE), carta(Naipe.COPAS, Valor.DEZ));
        assertEquals(19, mao.pontuacaoAtual().valor());

        mao.adicionarCarta(carta(Naipe.OUROS, Valor.DOIS));

        assertEquals(21, mao.pontuacaoAtual().valor());
        assertFalse(mao.ehBlackjackNatural());
        assertFalse(mao.estaEstourada());
        assertEquals(Set.of(Acao.PARAR), new AvaliadorDeAcoes().acoesDaMao(mao, BANCA_FOLGADA));
    }

    @Test
    @DisplayName("CT26 - [9,10] + Hit [3]: 22 estoura e a mao nao recebe novas acoes")
    void ct26_somaVinteEDoisEstouraAMaoESemNovasAcoes() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.NOVE), carta(Naipe.COPAS, Valor.DEZ));

        mao.adicionarCarta(carta(Naipe.OUROS, Valor.TRES));

        assertEquals(22, mao.pontuacaoAtual().valor());
        assertTrue(mao.pontuacaoAtual().estourou());
        assertEquals(EstadoMao.ESTOURADA, mao.estado());
        assertTrue(new AvaliadorDeAcoes().acoesDaMao(mao, BANCA_FOLGADA).isEmpty());
        assertThrows(IllegalStateException.class,
                () -> mao.adicionarCarta(carta(Naipe.PAUS, Valor.DOIS)));
    }

    @Test
    @DisplayName("CT27 - [As,9]: 20 com As valendo 11; Hit e Stand ativos")
    void ct27_asComoOnzeGeraVinteEMantemHitEStandAtivos() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.AS), carta(Naipe.COPAS, Valor.NOVE));

        Pontuacao pontuacao = mao.pontuacaoAtual();
        Set<Acao> acoes = new AvaliadorDeAcoes().acoesDaMao(mao, BANCA_FOLGADA);

        assertEquals(20, pontuacao.valor());
        assertTrue(pontuacao.ehSuave());
        assertTrue(acoes.contains(Acao.PEDIR_CARTA));
        assertTrue(acoes.contains(Acao.PARAR));
    }

    @Test
    @DisplayName("CT28 - [As,As,9]: 21 (11+1+9), sem estouro e sem ser natural")
    void ct28_doisAsesComNoveFecham21SemSerNatural() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.AS), carta(Naipe.OUROS, Valor.AS));
        assertEquals(12, mao.pontuacaoAtual().valor());

        mao.adicionarCarta(carta(Naipe.PAUS, Valor.NOVE));

        assertEquals(21, mao.pontuacaoAtual().valor());
        assertTrue(mao.pontuacaoAtual().ehSuave());
        assertFalse(mao.estaEstourada());
        assertFalse(mao.ehBlackjackNatural());
    }

    @Test
    @DisplayName("CT29 - [As,As,10]: 12 (1+1+10), mao permanece ativa")
    void ct29_doisAsesComDezValemDozeEMantemAMaoAtiva() {
        Mao mao = maoInicial(carta(Naipe.COPAS, Valor.AS), carta(Naipe.OUROS, Valor.AS));

        mao.adicionarCarta(carta(Naipe.PAUS, Valor.DEZ));

        assertEquals(12, mao.pontuacaoAtual().valor());
        assertFalse(mao.pontuacaoAtual().ehSuave());
        assertTrue(mao.estaAtiva());
    }
}
