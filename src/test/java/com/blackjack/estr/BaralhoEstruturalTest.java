
package com.blackjack.estr;

import com.blackjack.domain.deck.Baralho;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BaralhoEstruturalTest {

    @Test
    public void comprarComBaralhoVazioLancaExcecao() {
        Baralho baralho = Baralho.baralhoOrdenado(
            Collections.emptyList()
        );

        assertEquals(0, baralho.cartasRestantes());

        assertThrows(IllegalStateException.class, () ->
            baralho.comprar()
        );
    }
}
