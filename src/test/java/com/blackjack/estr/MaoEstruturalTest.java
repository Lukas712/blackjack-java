package com.blackjack.estr;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Mao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: caminhos de {@link Mao} nao exercitados pelo TestSet-Func (guardas e excecoes,
 * combinacoes de condicoes de Blackjack Natural e de elegibilidade a Split).
 */
class MaoEstruturalTest {

    private static final Aposta APOSTA = new Aposta(10);

    private static Carta carta(Valor valor, Naipe naipe) {
        return new Carta(naipe, valor);
    }

    private static Carta carta(Valor valor) {
        return carta(valor, Naipe.COPAS);
    }

    private static Mao mao(Valor primeira, Valor segunda) {
        return Mao.distribuirInicial(carta(primeira, Naipe.COPAS), carta(segunda, Naipe.OUROS), APOSTA);
    }

    private static Mao maoDoBanqueiro(Valor primeira, Valor segunda) {
        return Mao.distribuirInicialDoBanqueiro(carta(primeira, Naipe.COPAS), carta(segunda, Naipe.OUROS));
    }

    @Test
    @DisplayName("Dobrar fora da primeira acao e recusado")
    void dobrarAposReceberCartaEhRecusado() {
        Mao mao = mao(Valor.DOIS, Valor.TRES);
        mao.adicionarCarta(carta(Valor.QUATRO));

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.CINCO)));
    }

    @Test
    @DisplayName("Dobrar mao sem aposta (banqueiro) e recusado")
    void dobrarMaoSemApostaEhRecusado() {
        Mao mao = maoDoBanqueiro(Valor.DOIS, Valor.TRES);

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.CINCO)));
    }

    @Test
    @DisplayName("Dividir fora da primeira acao e recusado")
    void dividirAposReceberCartaEhRecusado() {
        Mao mao = mao(Valor.OITO, Valor.OITO);
        mao.adicionarCarta(carta(Valor.DOIS));

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    @DisplayName("Dividir cartas de valores diferentes e recusado")
    void dividirCartasDiferentesEhRecusado() {
        Mao mao = mao(Valor.OITO, Valor.NOVE);

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    @DisplayName("Dividir mao sem aposta (banqueiro) e recusado")
    void dividirMaoSemApostaEhRecusado() {
        Mao mao = maoDoBanqueiro(Valor.OITO, Valor.OITO);

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    @DisplayName("ehPrimeiraAcao e falso em mao ja encerrada")
    void maoEncerradaNaoEhPrimeiraAcao() {
        Mao mao = mao(Valor.DEZ, Valor.SETE);
        assertTrue(mao.ehPrimeiraAcao());

        mao.parar();

        assertFalse(mao.ehPrimeiraAcao());
    }

    @Test
    @DisplayName("Mao parada ou estourada nao aceita novas cartas nem nova parada")
    void maoEncerradaNaoAceitaNovasAcoes() {
        Mao parada = mao(Valor.DEZ, Valor.SETE);
        parada.parar();

        assertThrows(IllegalStateException.class, () -> parada.adicionarCarta(carta(Valor.DOIS)));
        assertThrows(IllegalStateException.class, parada::parar);
    }

    @Test
    @DisplayName("Blackjack Natural: Ass+10 e 10+As sao naturais; As+As, 5+6 e maos de 3 cartas nao")
    void combinacoesDeBlackjackNatural() {
        assertTrue(mao(Valor.AS, Valor.REI).ehBlackjackNatural());
        assertTrue(mao(Valor.REI, Valor.AS).ehBlackjackNatural());
        assertFalse(mao(Valor.AS, Valor.AS).ehBlackjackNatural());
        assertFalse(mao(Valor.CINCO, Valor.SEIS).ehBlackjackNatural());
        assertFalse(mao(Valor.DEZ, Valor.REI).ehBlackjackNatural());

        Mao tresCartas = mao(Valor.AS, Valor.DOIS);
        tresCartas.adicionarCarta(carta(Valor.OITO));
        assertFalse(tresCartas.ehBlackjackNatural());
    }

    @Test
    @DisplayName("21 formado em mao oriunda de Split nao e Blackjack Natural")
    void vinteEUmAposSplitNaoEhNatural() {
        Mao original = mao(Valor.REI, Valor.REI);
        original.dividir();
        original.adicionarCarta(carta(Valor.AS));

        assertTrue(original.veioDeDivisao());
        assertFalse(original.ehBlackjackNatural());
    }

    @Test
    @DisplayName("Split exige duas cartas iguais: K-K sim; J-Q, mao com 1 carta e mao com 3 cartas nao")
    void elegibilidadeAoSplit() {
        assertTrue(mao(Valor.REI, Valor.REI).podeDividir());
        assertFalse(mao(Valor.VALETE, Valor.DAMA).podeDividir());

        Mao tresCartas = mao(Valor.OITO, Valor.OITO);
        tresCartas.adicionarCarta(carta(Valor.DOIS));
        assertFalse(tresCartas.podeDividir());

        Mao umaCarta = mao(Valor.OITO, Valor.OITO);
        umaCarta.dividir();
        assertFalse(umaCarta.podeDividir());
    }
}
