package com.blackjack.func;

import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.blackjack.domain.action.Acao.DIVIDIR;
import static com.blackjack.domain.action.Acao.DOBRAR;
import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.action.Acao.PEDIR_CARTA;
import static com.blackjack.domain.card.Valor.AS;
import static com.blackjack.domain.card.Valor.CINCO;
import static com.blackjack.domain.card.Valor.DAMA;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.NOVE;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.QUATRO;
import static com.blackjack.domain.card.Valor.REI;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static com.blackjack.domain.card.Valor.TRES;
import static com.blackjack.domain.card.Valor.VALETE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * TestSet-Func: liquidacao das apostas e saldo da banca apos vitoria, derrota, empate,
 * Blackjack natural, seguro, double e split (P01 e P02 da especificacao dos testes).
 */
public class PagamentosFuncionalTest {

    @Test
    public void ct09_seguroPaga2Para1QuandoODealerTemBlackjackNatural() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SETE).banqueiro(AS, DEZ).seguro(20).executar();

        assertEquals(Collections.singletonList(Resultado.SEGURO_PAGO), rodada.visao().resultadosDeSeguro());
        assertEquals(Collections.singletonList(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    public void ct13_estouroDoJogadorPerdeAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SETE).banqueiro(DEZ, OITO).proximasCartas(SEIS).acoes(PEDIR_CARTA).executar();

        assertEquals(23, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(Collections.singletonList(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct14_blackjackNaturalPagaTresParaDoisSobreAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(AS, REI).banqueiro(OITO, NOVE).acoes(PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.VITORIA_BLACKJACK_NATURAL), rodada.visao().resultadosDeMaos());
        assertEquals(160, rodada.saldoFinal());
    }

    @Test
    public void ct15_naturalDoDealerVenceVinteEUmDeTresCartas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(SETE, SETE).banqueiro(VALETE, AS).proximasCartas(SETE)
                .acoes(PEDIR_CARTA).executar();

        assertEquals(21, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(Collections.singletonList(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct18_doisBlackjacksNaturaisEmpatam() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 50)
                .jogador(AS, REI).banqueiro(AS, DAMA).executar();

        assertEquals(Collections.singletonList(Resultado.EMPATE), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    public void ct32_naturalTemPrioridadeSobreVinteEUmDeTresCartasDoDealer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(AS, REI).banqueiro(DEZ, CINCO).proximasCartas(SEIS).acoes(PARAR).executar();

        assertEquals(21, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().ehBlackjackNatural());
        assertEquals(Collections.singletonList(Resultado.VITORIA_BLACKJACK_NATURAL), rodada.visao().resultadosDeMaos());
        assertEquals(160, rodada.saldoFinal());
    }

    @Test
    public void ct33_vitoriaComumRetornaODobroDaAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, DEZ).banqueiro(DEZ, NOVE).acoes(PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct34_derrotaComumPerdeAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, NOVE).acoes(PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct35_empateNaoNaturalDevolveAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(NOVE, NOVE).acoes(PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.EMPATE), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    public void ct37_seguroPerdidoEVitoriaComumSomamNoSaldo() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.SEGURO_PERDIDO), rodada.visao().resultadosDeSeguro());
        assertEquals(Collections.singletonList(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(120, rodada.saldoFinal());
    }

    @Test
    public void ct47_cadaMaoDoSplitEhLiquidadaIndependentemente() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 20)
                .jogador(OITO, OITO).banqueiro(DEZ, SETE).proximasCartas(DEZ, TRES, OITO)
                .acoes(DIVIDIR, PARAR, PEDIR_CARTA, PARAR).executar();

        assertEquals(18, rodada.maosDoJogador().get(0).pontuacaoAtual().valor());
        assertEquals(19, rodada.maosDoJogador().get(1).pontuacaoAtual().valor());
        assertEquals(Arrays.asList(Resultado.VITORIA, Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct50_doubleVencedorRetornaODobroDaApostaDobrada() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SEIS).banqueiro(DEZ, SETE).proximasCartas(QUATRO).acoes(DOBRAR).executar();

        assertEquals(80, rodada.apostaDaMao(0));
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(180, rodada.saldoFinal());
    }

    @Test
    public void ct55_vinteEUmComTresCartasPagaUmParaUm() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(SETE, SETE).banqueiro(DEZ, DEZ).proximasCartas(SETE)
                .acoes(PEDIR_CARTA, PARAR).executar();

        assertEquals(Collections.singletonList(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }
}
