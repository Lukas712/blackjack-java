package com.blackjack.estr;

import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: {@link Carta}, {@link Naipe}, {@link Valor} e {@link ContextoDoJogo}.
 */
class CartaEstruturalTest {

    @Test
    @DisplayName("exibir() e toString() concatenam rotulo do valor e simbolo do naipe")
    void exibicaoDaCarta() {
        Carta carta = new Carta(Naipe.ESPADAS, Valor.AS);

        assertEquals("A\u2660", carta.exibir());
        assertEquals(carta.exibir(), carta.toString());
        assertEquals("10\u2665", new Carta(Naipe.COPAS, Valor.DEZ).exibir());
    }

    @Test
    @DisplayName("Simbolos dos quatro naipes e valores base de As, figuras e numericas")
    void simbolosEValoresBase() {
        assertEquals("\u2665", Naipe.COPAS.simbolo());
        assertEquals("\u2666", Naipe.OUROS.simbolo());
        assertEquals("\u2663", Naipe.PAUS.simbolo());
        assertEquals("\u2660", Naipe.ESPADAS.simbolo());
        assertEquals(11, new Carta(Naipe.COPAS, Valor.AS).valorBase());
        assertEquals(10, new Carta(Naipe.COPAS, Valor.DAMA).valorBase());
        assertEquals("Q", Valor.DAMA.rotulo());
    }

    @Test
    @DisplayName("ehAs() e valeDez() distinguem As, figuras, 10 e demais cartas")
    void classificacaoDaCarta() {
        assertTrue(new Carta(Naipe.COPAS, Valor.AS).ehAs());
        assertFalse(new Carta(Naipe.COPAS, Valor.REI).ehAs());
        assertTrue(new Carta(Naipe.COPAS, Valor.REI).valeDez());
        assertTrue(new Carta(Naipe.COPAS, Valor.DEZ).valeDez());
        assertFalse(new Carta(Naipe.COPAS, Valor.NOVE).valeDez());
        assertFalse(new Carta(Naipe.COPAS, Valor.AS).valeDez());
    }

    @Test
    @DisplayName("Carta nao aceita naipe ou valor nulo")
    void cartaNaoAceitaNulos() {
        assertThrows(NullPointerException.class, () -> new Carta(null, Valor.AS));
        assertThrows(NullPointerException.class, () -> new Carta(Naipe.COPAS, null));
    }

    @Test
    @DisplayName("ContextoDoJogo informa se o banqueiro mostra As e exige a carta visivel")
    void contextoDoJogo() {
        assertTrue(new ContextoDoJogo(new Carta(Naipe.COPAS, Valor.AS)).banqueiroMostraAs());
        assertFalse(new ContextoDoJogo(new Carta(Naipe.COPAS, Valor.REI)).banqueiroMostraAs());
        assertThrows(NullPointerException.class, () -> new ContextoDoJogo(null));
    }
}
