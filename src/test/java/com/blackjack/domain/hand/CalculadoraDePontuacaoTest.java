package com.blackjack.domain.hand;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculadoraDePontuacaoTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    @Test
    void somaCartasNumericasEFigurasPeloValorNominal() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of(carta(Valor.NOVE), carta(Valor.REI)));

        assertEquals(19, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
    }

    @Test
    void contaUmUnicoAsComoOnzeQuandoNaoEstoura() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of(carta(Valor.AS), carta(Valor.SETE)));

        assertEquals(18, pontuacao.valor());
        assertTrue(pontuacao.ehSuave());
    }

    @Test
    void rebaixaAsDeOnzeParaUmParaEvitarEstouro() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of(carta(Valor.AS), carta(Valor.NOVE), carta(Valor.CINCO)));

        assertEquals(15, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertFalse(pontuacao.estourou());
    }

    @Test
    void lidaComDoisAsesContandoApenasUmComoOnze() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of(carta(Valor.AS), carta(Valor.AS)));

        assertEquals(12, pontuacao.valor());
        assertTrue(pontuacao.ehSuave());
    }

    @Test
    void detectaEstouroQuandoTotalUltrapassaVinteEUm() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of(carta(Valor.DEZ), carta(Valor.NOVE), carta(Valor.CINCO)));

        assertEquals(24, pontuacao.valor());
        assertTrue(pontuacao.estourou());
    }
}
