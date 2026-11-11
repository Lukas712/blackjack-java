package com.blackjack.funcional;

import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.card.Valor.AS;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.DOIS;
import static com.blackjack.domain.card.Valor.NOVE;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Func: regras do banqueiro (compra obrigatoria abaixo de 17, parada a partir de 17,
 * soft 17 e estouro) - CT10, CT11, CT30, CT31, CT36, CT51 e CT59.
 */
class BanqueiroFuncionalTest {

    @Test
    @DisplayName("CT10 - Dealer [10,6]=16 compra obrigatoriamente [2], fica com 18 e encerra a vez")
    void ct10_dealerComDezesseisCompraUmaCartaEParaEmDezoito() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SEIS).proximasCartas(DOIS).acoes(PARAR).executar();

        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(18, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    @DisplayName("CT11 - Dealer [10,7]=17 mantem e nao compra carta adicional")
    void ct11_dealerComDezesseteNaoCompra() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SETE).acoes(PARAR).executar();

        assertEquals(2, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    @DisplayName("CT30 - Dealer [10,6] compra [As]: As vale 1 e o dealer termina em 17")
    void ct30_asCompradoPeloDealerValeUmEEncerraEmDezessete() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SEIS).proximasCartas(AS).acoes(PARAR).executar();

        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().pontuacaoAtual().ehSuave());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    @DisplayName("CT31 - Dealer [As,6]=17 (soft 17) para mesmo com o As valendo 11")
    void ct31_dealerParaEmSoftDezessete() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(AS, SEIS).acoes(PARAR).executar();

        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().pontuacaoAtual().ehSuave());
        assertEquals(2, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    @DisplayName("CT36 - Dealer [10,6] compra [6] e estoura com 22; jogador [10,8] vence com lucro 40")
    void ct36_estouroDoDealerFazOJogadorVencer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SEIS).proximasCartas(SEIS).acoes(PARAR).executar();

        assertEquals(22, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().estaEstourada());
        assertEquals(List.of(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT51 - Dealer [10,6] compra [7] e fica com 23; nao volta a comprar e o jogador vence")
    void ct51_dealerEstouraEmVinteETresENaoCompraNovamente() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, NOVE).banqueiro(DEZ, SEIS).proximasCartas(SETE).acoes(PARAR).executar();

        assertEquals(23, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().estaEstourada());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
        assertEquals(List.of(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
    }

    @Test
    @DisplayName("CT59 - Dealer [As,9]=20 sem natural; jogador [10,9] perde 40 e nao ha credito de seguro")
    void ct59_dealerEmVinteSemNaturalVenceEOSeguroNaoContratadoNaoPaga() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, NOVE).acoes(PARAR).executar();

        assertEquals(20, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().ehBlackjackNatural());
        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertTrue(rodada.visao().resultadosDeSeguro().isEmpty());
        assertEquals(List.of(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }
}
