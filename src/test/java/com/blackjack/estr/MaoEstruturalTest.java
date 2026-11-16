package com.blackjack.estr;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Mao;
import org.junit.Test;
import com.blackjack.domain.hand.EstadoMao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: caminhos de {@link Mao} nao exercitados pelo TestSet-Func (guardas e excecoes,
 * combinacoes de condicoes de Blackjack Natural e de elegibilidade a Split).
 */
public class MaoEstruturalTest {

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
    public void dobrarAposReceberCartaEhRecusado() {
        Mao mao = mao(Valor.DOIS, Valor.TRES);
        mao.adicionarCarta(carta(Valor.QUATRO));

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.CINCO)));
    }

    @Test
    public void dobrarMaoSemApostaEhRecusado() {
        Mao mao = maoDoBanqueiro(Valor.DOIS, Valor.TRES);

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.CINCO)));
    }

    @Test
    public void dividirAposReceberCartaEhRecusado() {
        Mao mao = mao(Valor.OITO, Valor.OITO);
        mao.adicionarCarta(carta(Valor.DOIS));

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    public void dividirCartasDiferentesEhRecusado() {
        Mao mao = mao(Valor.OITO, Valor.NOVE);

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    public void dividirMaoSemApostaEhRecusado() {
        Mao mao = maoDoBanqueiro(Valor.OITO, Valor.OITO);

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    public void maoEncerradaNaoEhPrimeiraAcao() {
        Mao mao = mao(Valor.DEZ, Valor.SETE);
        assertTrue(mao.ehPrimeiraAcao());

        mao.parar();

        assertFalse(mao.ehPrimeiraAcao());
    }

    @Test
    public void maoEncerradaNaoAceitaNovasAcoes() {
        Mao parada = mao(Valor.DEZ, Valor.SETE);
        parada.parar();

        assertThrows(IllegalStateException.class, () -> parada.adicionarCarta(carta(Valor.DOIS)));
        assertThrows(IllegalStateException.class, parada::parar);
    }

    @Test
    public void combinacoesDeBlackjackNatural() {
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
    public void vinteEUmAposSplitNaoEhNatural() {
        Mao original = mao(Valor.REI, Valor.REI);
        original.dividir();
        original.adicionarCarta(carta(Valor.AS));

        assertTrue(original.veioDeDivisao());
        assertFalse(original.ehBlackjackNatural());
    }


    @Test
    public void elegibilidadeAoSplit() {
        assertTrue(mao(Valor.REI, Valor.REI).podeDividir());
        assertTrue(mao(Valor.VALETE, Valor.DAMA).podeDividir());

        Mao tresCartas = mao(Valor.OITO, Valor.OITO);
        tresCartas.adicionarCarta(carta(Valor.DOIS));
        assertFalse(tresCartas.podeDividir());

        Mao umaCarta = mao(Valor.OITO, Valor.OITO);
        umaCarta.dividir();
        assertFalse(umaCarta.podeDividir());
    }

    @Test
    public void dobrarAteVinteEUmEncerraAMao() {
        Mao mao = mao(Valor.DEZ, Valor.SEIS);

        // Antes de dobrar
        assertFalse(mao.estaDobrada());

        // 10 + 6 + 5 = 21
        mao.dobrar(carta(Valor.CINCO));

        // Verifica a dobra
        assertTrue(mao.estaDobrada());

        // A mao ja foi encerrada automaticamente
        assertEquals(EstadoMao.PARADA, mao.estado());

        // Confirma a pontuacao
        assertEquals(21, mao.pontuacaoAtual().valor());
    }


}
