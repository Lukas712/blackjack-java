package com.blackjack.domain.action;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.hand.Mao;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AvaliadorDeAcoesTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    private final AvaliadorDeAcoes avaliador = new AvaliadorDeAcoes();

    @Test
    public void primeiraAcaoComSaldoSuficienteECartasDiferentesOfereceHitStandEDoubleDown() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(100));

        assertEquals(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR, Acao.DOBRAR), acoes);
    }

    @Test
    public void primeiraAcaoComCartasIguaisTambemOfereceSplit() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(100));

        assertEquals(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR, Acao.DOBRAR, Acao.DIVIDIR), acoes);
    }

    @Test
    public void bancaInsuficienteExcluiDoubleDownESplit() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(5));

        assertEquals(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR), acoes);
    }


    @Test
    public void blackjackNaturalNaoOfereceAcoes() {
        Mao mao = Mao.distribuirInicial(
            carta(Valor.AS),
            carta(Valor.REI),
            new Aposta(10)
        );

        Set<Acao> acoes = avaliador.acoesDaMao(
            mao, new Banca(1000)
        );

        assertTrue(acoes.isEmpty());
    }


    @Test
    public void aposPrimeiraAcaoRestamApenasHitEStand() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.DOIS));

        Set<Acao> acoes = avaliador.acoesDaMao(mao, new Banca(1000));

        assertEquals(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR), acoes);
    }

    @Test
    public void maoInativaNaoOfereceAcoes() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.parar();

        assertTrue(avaliador.acoesDaMao(mao, new Banca(1000)).isEmpty());
    }

    @Test
    public void seguroDisponivelApenasQuandoBanqueiroMostraAsEBancaTemMetadeDaAposta() {
        assertTrue(avaliador.seguroDisponivel(carta(Valor.AS), new Banca(5), 10));
        assertFalse(avaliador.seguroDisponivel(carta(Valor.AS), new Banca(4), 10));
        assertFalse(avaliador.seguroDisponivel(carta(Valor.REI), new Banca(1000), 10));
    }

    @Test
    public void valorMaximoDoSeguroEhMetadeDaApostaInicial() {
        assertEquals(5, avaliador.valorMaximoDoSeguro(10));
    }
}
