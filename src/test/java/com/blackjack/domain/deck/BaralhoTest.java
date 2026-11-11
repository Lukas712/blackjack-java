package com.blackjack.domain.deck;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BaralhoTest {

    @Test
    public void baralhoEmbaralhadoContemCinquentaEDuasCartasUnicas() {
        Baralho baralho = Baralho.novoBaralhoEmbaralhado(new Random(42));

        Set<Carta> vistas = new HashSet<>();
        assertEquals(52, baralho.cartasRestantes());
        while (baralho.cartasRestantes() > 0) {
            assertEquals(true, vistas.add(baralho.comprar()));
        }
        assertEquals(52, vistas.size());
    }

    @Test
    public void comprarTodasAsCartasEDepoisComprarNovamenteLancaExcecao() {
        Baralho baralho = Baralho.novoBaralhoEmbaralhado(new Random(1));

        for (int i = 0; i < 52; i++) {
            baralho.comprar();
        }

        assertThrows(IllegalStateException.class, baralho::comprar);
    }

    @Test
    public void baralhoOrdenadoDistribuiCartasNaOrdemExataFornecida() {
        Carta primeira = new Carta(Naipe.ESPADAS, Valor.AS);
        Carta segunda = new Carta(Naipe.COPAS, Valor.REI);
        Baralho baralho = Baralho.baralhoOrdenado(Arrays.asList(primeira, segunda));

        assertEquals(primeira, baralho.comprar());
        assertEquals(segunda, baralho.comprar());
        assertEquals(0, baralho.cartasRestantes());
    }
}
