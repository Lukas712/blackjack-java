package com.blackjack.estr;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.ProvedorDeDecisaoDoBanqueiro;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * TestSet-Estr: {@link Banqueiro} e {@link ProvedorDeDecisaoDoBanqueiro}.
 */
public class BanqueiroEstruturalTest {

    private static final Carta VISIVEL = new Carta(Naipe.COPAS, Valor.SEIS);
    private static final Carta OCULTA = new Carta(Naipe.OUROS, Valor.REI);
    private static final ContextoDoJogo CONTEXTO = new ContextoDoJogo(VISIVEL);

    @Test
    public void banqueiroSemCartasLancaExcecao() {
        Banqueiro banqueiro = new Banqueiro();

        assertThrows(IllegalStateException.class, banqueiro::mao);
        assertThrows(IllegalStateException.class, banqueiro::cartaVisivel);
        assertThrows(IllegalStateException.class, banqueiro::cartaOculta);
    }

    @Test
    public void banqueiroExpoeCartas() {
        Banqueiro banqueiro = new Banqueiro();
        banqueiro.iniciarNovaRodada(Mao.distribuirInicialDoBanqueiro(VISIVEL, OCULTA));

        assertEquals(VISIVEL, banqueiro.cartaVisivel());
        assertEquals(OCULTA, banqueiro.cartaOculta());
    }

    @Test
    public void decisaoDoBanqueiro() {
        ProvedorDeDecisaoDoBanqueiro provedor = new ProvedorDeDecisaoDoBanqueiro();
        Mao dezesseis = Mao.distribuirInicialDoBanqueiro(new Carta(Naipe.COPAS, Valor.DEZ),
                new Carta(Naipe.OUROS, Valor.SEIS));
        Mao dezessete = Mao.distribuirInicialDoBanqueiro(new Carta(Naipe.COPAS, Valor.DEZ),
                new Carta(Naipe.OUROS, Valor.SETE));

        assertEquals(Acao.PEDIR_CARTA, provedor.escolherAcao(dezesseis, EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR), CONTEXTO));
        assertEquals(Acao.PARAR, provedor.escolherAcao(dezesseis, EnumSet.of(Acao.PARAR), CONTEXTO));
        assertEquals(Acao.PARAR, provedor.escolherAcao(dezessete, EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR), CONTEXTO));
    }

    @Test
    public void banqueiroNuncaCompraSeguro() {
        assertEquals(0, new ProvedorDeDecisaoDoBanqueiro().escolherValorDoSeguro(50));
    }
}
