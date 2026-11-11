package com.blackjack.domain.hand;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CalculadoraDePontuacaoTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    @Test
    public void somaCartasNumericasEFigurasPeloValorNominal() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Arrays.asList(carta(Valor.NOVE), carta(Valor.REI)));

        assertEquals(19, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
    }

    @Test
    public void contaUmUnicoAsComoOnzeQuandoNaoEstoura() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Arrays.asList(carta(Valor.AS), carta(Valor.SETE)));

        assertEquals(18, pontuacao.valor());
        assertTrue(pontuacao.ehSuave());
    }

    @Test
    public void rebaixaAsDeOnzeParaUmParaEvitarEstouro() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Arrays.asList(carta(Valor.AS), carta(Valor.NOVE), carta(Valor.CINCO)));

        assertEquals(15, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertFalse(pontuacao.estourou());
    }

    @Test
    public void lidaComDoisAsesContandoApenasUmComoOnze() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Arrays.asList(carta(Valor.AS), carta(Valor.AS)));

        assertEquals(12, pontuacao.valor());
        assertTrue(pontuacao.ehSuave());
    }

    @Test
    public void detectaEstouroQuandoTotalUltrapassaVinteEUm() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Arrays.asList(carta(Valor.DEZ), carta(Valor.NOVE), carta(Valor.CINCO)));

        assertEquals(24, pontuacao.valor());
        assertTrue(pontuacao.estourou());
    }
}
