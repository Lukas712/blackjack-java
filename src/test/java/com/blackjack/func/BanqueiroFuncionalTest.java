package com.blackjack.func;

import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.Test;

import java.util.Collections;

import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.card.Valor.AS;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.DOIS;
import static com.blackjack.domain.card.Valor.NOVE;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Func: regras do banqueiro (compra obrigatoria abaixo de 17, parada a partir de 17,
 * soft 17 e estouro) - CT10, CT11, CT30, CT31, CT36, CT51 e CT59.
 */
public class BanqueiroFuncionalTest {

    @Test
    public void ct10_dealerComDezesseisCompraUmaCartaEParaEmDezoito() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SEIS).proximasCartas(DOIS).acoes(PARAR).executar();

        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(18, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct11_dealerComDezesseteNaoCompra() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SETE).acoes(PARAR).executar();

        assertEquals(2, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct30_asCompradoPeloDealerValeUmEEncerraEmDezessete() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(DEZ, SEIS).proximasCartas(AS).acoes(PARAR).executar();

        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().pontuacaoAtual().ehSuave());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct31_dealerParaEmSoftDezessete() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(AS, SEIS).acoes(PARAR).executar();

        assertEquals(17, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().pontuacaoAtual().ehSuave());
        assertEquals(2, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct36_estouroDoDealerFazOJogadorVencer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SEIS).proximasCartas(SEIS).acoes(PARAR).executar();

        assertEquals(22, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().estaEstourada());
        assertEquals(Collections.singletonList(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct51_dealerEstouraEmVinteETresENaoCompraNovamente() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, NOVE).banqueiro(DEZ, SEIS).proximasCartas(SETE).acoes(PARAR).executar();

        assertEquals(23, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertTrue(rodada.maoDoBanqueiro().estaEstourada());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
        assertEquals(Collections.singletonList(Resultado.VITORIA), rodada.visao().resultadosDeMaos());
    }

    @Test
    public void ct59_dealerEmVinteSemNaturalVenceEOSeguroNaoContratadoNaoPaga() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, NOVE).acoes(PARAR).executar();

        assertEquals(20, rodada.maoDoBanqueiro().pontuacaoAtual().valor());
        assertFalse(rodada.maoDoBanqueiro().ehBlackjackNatural());
        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertTrue(rodada.visao().resultadosDeSeguro().isEmpty());
        assertEquals(Collections.singletonList(Resultado.DERROTA), rodada.visao().resultadosDeMaos());
        assertEquals(60, rodada.saldoFinal());
    }
}
