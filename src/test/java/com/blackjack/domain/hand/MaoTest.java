package com.blackjack.domain.hand;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaoTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    private static Carta carta(Naipe naipe, Valor valor) {
        return new Carta(naipe, valor);
    }

    @Test
    void blackjackNaturalEhDetectadoIndependentementeDaOrdemDasCartas() {
        Mao asPrimeiro = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));
        Mao valorDezPrimeiro = Mao.distribuirInicial(carta(Valor.DAMA), carta(Valor.AS), new Aposta(10));

        assertTrue(asPrimeiro.ehBlackjackNatural());
        assertTrue(valorDezPrimeiro.ehBlackjackNatural());
    }

    @Test
    void blackjackNaturalExigeExatamenteDuasCartas() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.AS));

        assertFalse(mao.ehBlackjackNatural());
    }

    @Test
    void maoResultanteDeDivisaoNuncaEhUmBlackjackNatural() {
        Mao original = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.AS), new Aposta(10));

        Mao irma = original.dividir();
        original.adicionarCarta(carta(Valor.REI));
        irma.adicionarCarta(carta(Valor.REI));

        assertFalse(original.ehBlackjackNatural());
        assertFalse(irma.ehBlackjackNatural());
    }

    @Test
    void podeDividirQuandoAmbasAsCartasTemOMesmoValorBase() {
        Mao mesmoValor = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));
        Mao ambasValemDez = Mao.distribuirInicial(carta(Valor.REI), carta(Valor.DAMA), new Aposta(10));
        Mao ambosAses = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.AS), new Aposta(10));

        assertTrue(mesmoValor.podeDividir());
        assertTrue(ambasValemDez.podeDividir());
        assertTrue(ambosAses.podeDividir());
    }

    @Test
    void naoPodeDividirQuandoAsCartasTemValoresBaseDiferentes() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.NOVE), new Aposta(10));

        assertFalse(mao.podeDividir());
    }

    @Test
    void ehPrimeiraAcaoTornaSeFalsoAposUmPedirCarta() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        assertTrue(mao.ehPrimeiraAcao());

        mao.adicionarCarta(carta(Valor.DOIS));

        assertFalse(mao.ehPrimeiraAcao());
    }

    @Test
    void dobrarDuplicaAApostaAdicionaExatamenteUmaCartaEEncerraAMao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        mao.dobrar(carta(Valor.TRES));

        assertEquals(20, mao.aposta().orElseThrow().valor());
        assertEquals(3, mao.cartas().size());
        assertTrue(mao.estaDobrada());
        assertFalse(mao.estaAtiva());
    }

    @Test
    void dobrarComUmaCartaQueEstouraDeixaAMaoEstourada() {
        Mao mao = Mao.distribuirInicial(carta(Valor.REI), carta(Valor.NOVE), new Aposta(10));

        mao.dobrar(carta(Valor.CINCO));

        assertTrue(mao.estaEstourada());
        assertFalse(mao.estaAtiva());
    }

    @Test
    void dobrarSoEhPermitidoComoPrimeiraAcao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.DOIS));

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.TRES)));
    }

    @Test
    void dividirProduzDuasMaosIndependentesComApostasCorrespondentes() {
        Mao original = Mao.distribuirInicial(carta(Naipe.COPAS, Valor.OITO), carta(Naipe.ESPADAS, Valor.OITO), new Aposta(10));

        Mao irma = original.dividir();

        assertEquals(1, original.cartas().size());
        assertEquals(1, irma.cartas().size());
        assertEquals(10, irma.aposta().orElseThrow().valor());
        assertTrue(original.veioDeDivisao());
        assertTrue(irma.veioDeDivisao());
    }

    @Test
    void dividirLancaExcecaoQuandoAsCartasNaoSaoElegiveis() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.NOVE), new Aposta(10));

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    void adicionarCartaAposAMaoNaoEstarMaisAtivaLancaExcecao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.parar();

        assertThrows(IllegalStateException.class, () -> mao.adicionarCarta(carta(Valor.DOIS)));
    }

    @Test
    void maoInicialDoBanqueiroNaoTemAposta() {
        Mao maoDoBanqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.SEIS));

        assertTrue(maoDoBanqueiro.aposta().isEmpty());
    }
}
