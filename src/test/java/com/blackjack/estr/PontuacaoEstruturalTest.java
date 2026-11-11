package com.blackjack.estr;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.CalculadoraDePontuacao;
import com.blackjack.domain.hand.Pontuacao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: {@link CalculadoraDePontuacao} - colecao vazia (laco sem iteracoes) e ajuste de As insuficiente.
 */
class PontuacaoEstruturalTest {

    @Test
    @DisplayName("As + tres figuras: o ajuste do As nao basta (31, sem As suave) e a mao estoura")
    void ajusteDoAsInsuficiente() {
        List<Carta> cartas = List.of(new Carta(Naipe.COPAS, Valor.AS), new Carta(Naipe.COPAS, Valor.REI),
                new Carta(Naipe.OUROS, Valor.REI), new Carta(Naipe.PAUS, Valor.REI));

        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(cartas);

        assertEquals(31, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertTrue(pontuacao.estourou());
    }

    @Test
    @DisplayName("Mao sem cartas vale 0, nao e suave e nao estoura")
    void maoVazia() {
        Pontuacao pontuacao = CalculadoraDePontuacao.calcular(List.of());

        assertEquals(0, pontuacao.valor());
        assertFalse(pontuacao.ehSuave());
        assertFalse(pontuacao.estourou());
    }
}
