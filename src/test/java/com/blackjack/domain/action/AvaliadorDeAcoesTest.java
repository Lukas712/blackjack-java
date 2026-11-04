package com.blackjack.domain.action;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.hand.Mao;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliadorDeAcoesTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    private final AvaliadorDeAcoes avaliador = new AvaliadorDeAcoes();

    @Test
    void primeiraAcaoComSaldoSuficienteECartasDiferentesOfereceHitStandEDoubleDown() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(100));

        assertEquals(Set.of(Acao.PEDIR_CARTA, Acao.PARAR, Acao.DOBRAR), acoes);
    }

    @Test
    void primeiraAcaoComCartasIguaisTambemOfereceSplit() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(100));

        assertEquals(Set.of(Acao.PEDIR_CARTA, Acao.PARAR, Acao.DOBRAR, Acao.DIVIDIR), acoes);
    }

    @Test
    void bancaInsuficienteExcluiDoubleDownESplit() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(5));

        assertEquals(Set.of(Acao.PEDIR_CARTA, Acao.PARAR), acoes);
    }

    @Test
    void blackjackNaturalOferecePenasStandMesmoComSaldoDisponivel() {
        Mao mao = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(1000));

        assertEquals(Set.of(Acao.PARAR), acoes);
    }

    @Test
    void aposPrimeiraAcaoRestamApenasHitEStand() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.DOIS));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(1000));

        assertEquals(Set.of(Acao.PEDIR_CARTA, Acao.PARAR), acoes);
    }

    @Test
    void maoInativaNaoOfereceAcoes() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.parar();

        assertTrue(avaliador.acoesDaMao(mao, new Banca(1000)).isEmpty());
    }

    @Test
    void seguroDisponivelApenasQuandoBanqueiroMostraAsEBancaTemMetadeDaAposta() {
        assertTrue(avaliador.seguroDisponivel(carta(Valor.AS), new Banca(5), 10));
        assertFalse(avaliador.seguroDisponivel(carta(Valor.AS), new Banca(4), 10));
        assertFalse(avaliador.seguroDisponivel(carta(Valor.REI), new Banca(1000), 10));
    }

    @Test
    void valorMaximoDoSeguroEhMetadeDaApostaInicial() {
        assertEquals(5, avaliador.valorMaximoDoSeguro(10));
    }
}
