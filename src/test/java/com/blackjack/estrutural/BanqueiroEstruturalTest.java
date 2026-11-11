package com.blackjack.estrutural;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.ProvedorDeDecisaoDoBanqueiro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TestSet-Estr: {@link Banqueiro} e {@link ProvedorDeDecisaoDoBanqueiro}.
 */
class BanqueiroEstruturalTest {

    private static final Carta VISIVEL = new Carta(Naipe.COPAS, Valor.SEIS);
    private static final Carta OCULTA = new Carta(Naipe.OUROS, Valor.REI);
    private static final ContextoDoJogo CONTEXTO = new ContextoDoJogo(VISIVEL);

    @Test
    @DisplayName("Banqueiro sem cartas nao expoe mao nem cartas")
    void banqueiroSemCartasLancaExcecao() {
        Banqueiro banqueiro = new Banqueiro();

        assertThrows(IllegalStateException.class, banqueiro::mao);
        assertThrows(IllegalStateException.class, banqueiro::cartaVisivel);
        assertThrows(IllegalStateException.class, banqueiro::cartaOculta);
    }

    @Test
    @DisplayName("Banqueiro com a mao distribuida expoe carta visivel (1a) e oculta (2a)")
    void banqueiroExpoeCartas() {
        Banqueiro banqueiro = new Banqueiro();
        banqueiro.iniciarNovaRodada(Mao.distribuirInicialDoBanqueiro(VISIVEL, OCULTA));

        assertEquals(VISIVEL, banqueiro.cartaVisivel());
        assertEquals(OCULTA, banqueiro.cartaOculta());
    }

    @Test
    @DisplayName("Decisao do banqueiro: pede com menos de 17 se Hit estiver disponivel; senao para")
    void decisaoDoBanqueiro() {
        ProvedorDeDecisaoDoBanqueiro provedor = new ProvedorDeDecisaoDoBanqueiro();
        Mao dezesseis = Mao.distribuirInicialDoBanqueiro(new Carta(Naipe.COPAS, Valor.DEZ),
                new Carta(Naipe.OUROS, Valor.SEIS));
        Mao dezessete = Mao.distribuirInicialDoBanqueiro(new Carta(Naipe.COPAS, Valor.DEZ),
                new Carta(Naipe.OUROS, Valor.SETE));

        assertEquals(Acao.PEDIR_CARTA, provedor.escolherAcao(dezesseis, Set.of(Acao.PEDIR_CARTA, Acao.PARAR), CONTEXTO));
        assertEquals(Acao.PARAR, provedor.escolherAcao(dezesseis, Set.of(Acao.PARAR), CONTEXTO));
        assertEquals(Acao.PARAR, provedor.escolherAcao(dezessete, Set.of(Acao.PEDIR_CARTA, Acao.PARAR), CONTEXTO));
    }

    @Test
    @DisplayName("Banqueiro nunca compra seguro")
    void banqueiroNuncaCompraSeguro() {
        assertEquals(0, new ProvedorDeDecisaoDoBanqueiro().escolherValorDoSeguro(50));
    }
}
