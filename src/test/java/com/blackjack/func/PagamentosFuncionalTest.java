package com.blackjack.func;

import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * TestSet-Func: liquidacao das apostas e saldo da banca apos vitoria, derrota, empate,
 * Blackjack natural, seguro, double e split (P01 e P02 da especificacao dos testes).
 */
class PagamentosFuncionalTest {

    @Test
    @DisplayName("CT09 - Dealer com natural: perde a principal (40) e o seguro 20 retorna 60; saldo final 100")
    void ct09_seguroPaga2Para1QuandoODealerTemBlackjackNatural() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SETE).banqueiro(AS, DEZ).seguro(20).executar();

        assertEquals(List.of(Resultado.SEGURO_PAGO), rodada.visao().resultadosDeSeguro());
        assertEquals(List.of(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT13 - [10,7] + Hit [6] = 23: estouro, mao encerrada e aposta perdida")
    void ct13_estouroDoJogadorPerdeAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SETE).banqueiro(DEZ, OITO).proximasCartas(SEIS).acoes(PEDIR_CARTA).executar();

        assertEquals(23, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(List.of(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT14 - Natural do jogador [As,K] vs [8,9]: credito bruto 100 (40+60); saldo 160")
    void ct14_blackjackNaturalPagaTresParaDoisSobreAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(AS, REI).banqueiro(OITO, NOVE).acoes(PARAR).executar();

        assertEquals(List.of(Resultado.VITORIA_BLACKJACK_NATURAL), rodada.visao().resultadosDeMaos());
        assertEquals(160, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT15 - Dealer com natural vence o 21 de tres cartas [7,7,7]; jogador perde a principal")
    void ct15_naturalDoDealerVenceVinteEUmDeTresCartas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(SETE, SETE).banqueiro(VALETE, AS).proximasCartas(SETE)
                .acoes(PEDIR_CARTA, PARAR).executar();

        assertEquals(21, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(List.of(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT18 - Dois naturais empatam; devolve A=50 e banca final permanece 100")
    void ct18_doisBlackjacksNaturaisEmpatam() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 50)
                .jogador(AS, REI).banqueiro(AS, DAMA).executar();

        assertEquals(List.of(Resultado.EMPATE), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT32 - Natural do jogador vence o 21 comum do dealer [10,5,6]; lucro 60 (3:2)")
    void ct32_naturalTemPrioridadeSobreVinteEUmDeTresCartasDoDealer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(AS, REI).banqueiro(DEZ, CINCO).proximasCartas(SEIS).acoes(PARAR).executar();

        assertEquals(21, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().ehBlackjackNatural());
        assertEquals(List.of(Resultado.VITORIA_BLACKJACK_NATURAL), rodada.visao().resultadosDeMaos());
        assertEquals(160, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT33 - [10,10]=20 vs [10,9]=19: vitoria comum retorna 80; saldo final 140")
    void ct33_vitoriaComumRetornaODobroDaAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, DEZ).banqueiro(DEZ, NOVE).acoes(PARAR).executar();

        assertEquals(List.of(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT34 - [10,8]=18 vs [10,9]=19: derrota comum; aposta perdida, saldo final 60")
    void ct34_derrotaComumPerdeAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, NOVE).acoes(PARAR).executar();

        assertEquals(List.of(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT35 - [10,8]=18 vs [9,9]=18: empate devolve 40; banca final 100")
    void ct35_empateNaoNaturalDevolveAAposta() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(NOVE, NOVE).acoes(PARAR).executar();

        assertEquals(List.of(Resultado.EMPATE), rodada.visao().resultadosDeMaos());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT37 - Dealer [As,7] sem natural: seguro 20 perdido; vitoria comum retorna 80; saldo 120")
    void ct37_seguroPerdidoEVitoriaComumSomamNoSaldo() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertEquals(List.of(Resultado.SEGURO_PERDIDO), rodada.visao().resultadosDeSeguro());
        assertEquals(List.of(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(120, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT47 - Split [8,8] A=20: cada mao vence 1:1 e retorna 40; banca final 140")
    void ct47_cadaMaoDoSplitEhLiquidadaIndependentemente() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 20)
                .jogador(OITO, OITO).banqueiro(DEZ, SETE).proximasCartas(DEZ, TRES, OITO)
                .acoes(DIVIDIR, PARAR, PEDIR_CARTA, PARAR).executar();

        assertEquals(18, rodada.maosDoJogador().get(0).pontuacaoAtual().valor());
        assertEquals(19, rodada.maosDoJogador().get(1).pontuacaoAtual().valor());
        assertEquals(List.of(Resultado.VITORIA, Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT50 - Double [10,6]+[4]=20 vs [10,7]: aposta total 80, retorno 160, saldo final 180")
    void ct50_doubleVencedorRetornaODobroDaApostaDobrada() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SEIS).banqueiro(DEZ, SETE).proximasCartas(QUATRO).acoes(DOBRAR).executar();

        assertEquals(80, rodada.apostaDaMao(0));
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(180, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT55 - [7,7] + Hit [7] = 21 e vitoria comum (nao natural): retorna 80, saldo final 140")
    void ct55_vinteEUmComTresCartasPagaUmParaUm() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(SETE, SETE).banqueiro(DEZ, DEZ).proximasCartas(SETE)
                .acoes(PEDIR_CARTA, PARAR).executar();

        assertEquals(List.of(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }
}
