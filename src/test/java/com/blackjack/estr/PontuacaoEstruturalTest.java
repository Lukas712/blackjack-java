package com.blackjack.estr;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.CalculadoraDePontuacao;
import com.blackjack.domain.hand.Pontuacao;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: {@link CalculadoraDePontuacao} - colecao vazia (laco sem iteracoes) e ajuste de As insuficiente.
 */
public class PontuacaoEstruturalTest {

    @Test
    public void ajusteDoAsInsuficiente() {
        List<Carta> cartas = Arrays.asList(new Carta(Naipe.COPAS, Valor.AS), new Carta(Naipe.COPAS, Valor.REI),
                new Carta(Naipe.OUROS, Valor.REI), new Carta(Naipe.PAUS, Valor.REI));

        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(cartas);

        assertEquals(31, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertTrue(pontuacao.estourou());
    }

    @Test
    public void maoVazia() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(Collections.<Carta>emptyList());

        assertEquals(0, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertFalse(pontuacao.estourou());
    }
}
