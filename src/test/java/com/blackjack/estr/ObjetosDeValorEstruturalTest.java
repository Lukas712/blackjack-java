package com.blackjack.estr;

import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Pontuacao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: igualdade, hash, representacao textual e acessores dos objetos de valor
 * {@link Carta}, {@link Aposta}, {@link Pontuacao} e {@link ContextoDoJogo}.
 */
public class ObjetosDeValorEstruturalTest {

    @Test
    public void carta() {
        Carta carta = new Carta(Naipe.COPAS, Valor.REI);

        assertSame(Naipe.COPAS, carta.naipe());
        assertSame(Valor.REI, carta.valor());
        assertEquals(carta, carta);
        assertEquals(new Carta(Naipe.COPAS, Valor.REI), carta);
        assertEquals(new Carta(Naipe.COPAS, Valor.REI).hashCode(), carta.hashCode());
        assertNotEquals(new Carta(Naipe.OUROS, Valor.REI), carta);
        assertNotEquals(new Carta(Naipe.COPAS, Valor.DAMA), carta);
        assertFalse(carta.equals(null));
        assertFalse(carta.equals("K"));
        assertThrows(NullPointerException.class, () -> new Carta(null, Valor.REI));
        assertThrows(NullPointerException.class, () -> new Carta(Naipe.COPAS, null));
    }

    @Test
    public void aposta() {
        Aposta aposta = new Aposta(10);

        assertEquals(10, aposta.valor());
        assertEquals(aposta, aposta);
        assertEquals(new Aposta(10), aposta);
        assertEquals(new Aposta(10).hashCode(), aposta.hashCode());
        assertNotEquals(new Aposta(20), aposta);
        assertFalse(aposta.equals(null));
        assertFalse(aposta.equals(10));
        assertEquals("Aposta[valor=10]", aposta.toString());
        assertEquals(new Aposta(20), aposta.dobrada());
    }

    @Test
    public void pontuacao() {
        Pontuacao pontuacao = new Pontuacao(17, true);

        assertEquals(17, pontuacao.valor());
        assertTrue(pontuacao.suave());
        assertTrue(pontuacao.ehSuave());
        assertEquals(pontuacao, pontuacao);
        assertEquals(new Pontuacao(17, true), pontuacao);
        assertEquals(new Pontuacao(17, true).hashCode(), pontuacao.hashCode());
        assertNotEquals(new Pontuacao(18, true), pontuacao);
        assertNotEquals(new Pontuacao(17, false), pontuacao);
        assertFalse(pontuacao.equals(null));
        assertFalse(pontuacao.equals(17));
        assertEquals("Pontuacao[valor=17, suave=true]", pontuacao.toString());
        assertFalse(pontuacao.estourou());
        assertTrue(new Pontuacao(22, false).estourou());
    }

    @Test
    public void contextoDoJogo() {
        Carta as = new Carta(Naipe.PAUS, Valor.AS);
        ContextoDoJogo contexto = new ContextoDoJogo(as);

        assertSame(as, contexto.cartaVisivelDoBanqueiro());
        assertTrue(contexto.banqueiroMostraAs());
        assertEquals(contexto, contexto);
        assertEquals(new ContextoDoJogo(new Carta(Naipe.PAUS, Valor.AS)), contexto);
        assertEquals(new ContextoDoJogo(new Carta(Naipe.PAUS, Valor.AS)).hashCode(), contexto.hashCode());
        assertNotEquals(new ContextoDoJogo(new Carta(Naipe.PAUS, Valor.DOIS)), contexto);
        assertFalse(contexto.equals(null));
        assertFalse(contexto.equals(as));
        assertEquals("ContextoDoJogo[cartaVisivelDoBanqueiro=" + as + "]", contexto.toString());
        assertThrows(NullPointerException.class, () -> new ContextoDoJogo(null));
    }
}
