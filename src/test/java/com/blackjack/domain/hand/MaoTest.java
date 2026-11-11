package com.blackjack.domain.hand;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MaoTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    private static Carta carta(Naipe naipe, Valor valor) {
        return new Carta(naipe, valor);
    }

    @Test
    public void blackjackNaturalEhDetectadoIndependentementeDaOrdemDasCartas() {
        Mao asPrimeiro = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));
        Mao valorDezPrimeiro = Mao.distribuirInicial(carta(Valor.DAMA), carta(Valor.AS), new Aposta(10));

        assertTrue(asPrimeiro.ehBlackjackNatural());
        assertTrue(valorDezPrimeiro.ehBlackjackNatural());
    }

    @Test
    public void blackjackNaturalExigeExatamenteDuasCartas() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.AS));

        assertFalse(mao.ehBlackjackNatural());
    }

    @Test
    public void maoResultanteDeDivisaoNuncaEhUmBlackjackNatural() {
        Mao original = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.AS), new Aposta(10));

        Mao irma = original.dividir();
        original.adicionarCarta(carta(Valor.REI));
        irma.adicionarCarta(carta(Valor.REI));

        assertFalse(original.ehBlackjackNatural());
        assertFalse(irma.ehBlackjackNatural());
    }

    @Test
    public void podeDividirQuandoAmbasAsCartasTemOMesmoValorDeFace() {
        Mao oitos = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.OITO), new Aposta(10));
        Mao reis = Mao.distribuirInicial(carta(Valor.REI), carta(Valor.REI), new Aposta(10));
        Mao ambosAses = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.AS), new Aposta(10));

        assertTrue(oitos.podeDividir());
        assertTrue(reis.podeDividir());
        assertTrue(ambosAses.podeDividir());
    }

    @Test
    public void naoPodeDividirQuandoAsCartasTemValoresDeFaceDiferentes() {
        Mao oitoENove = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.NOVE), new Aposta(10));
        Mao figurasDiferentes = Mao.distribuirInicial(carta(Valor.REI), carta(Valor.VALETE), new Aposta(10));
        Mao dezEDama = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.DAMA), new Aposta(10));

        assertFalse(oitoENove.podeDividir());
        assertFalse(figurasDiferentes.podeDividir());
        assertFalse(dezEDama.podeDividir());
    }

    @Test
    public void ehPrimeiraAcaoTornaSeFalsoAposUmPedirCarta() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        assertTrue(mao.ehPrimeiraAcao());

        mao.adicionarCarta(carta(Valor.DOIS));

        assertFalse(mao.ehPrimeiraAcao());
    }

    @Test
    public void dobrarDuplicaAApostaAdicionaExatamenteUmaCartaEEncerraAMao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));

        mao.dobrar(carta(Valor.TRES));

        assertEquals(20, mao.aposta().orElseThrow(NoSuchElementException::new).valor());
        assertEquals(3, mao.cartas().size());
        assertTrue(mao.estaDobrada());
        assertFalse(mao.estaAtiva());
    }

    @Test
    public void dobrarComUmaCartaQueEstouraDeixaAMaoEstourada() {
        Mao mao = Mao.distribuirInicial(carta(Valor.REI), carta(Valor.NOVE), new Aposta(10));

        mao.dobrar(carta(Valor.CINCO));

        assertTrue(mao.estaEstourada());
        assertFalse(mao.estaAtiva());
    }

    @Test
    public void dobrarSoEhPermitidoComoPrimeiraAcao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.adicionarCarta(carta(Valor.DOIS));

        assertThrows(IllegalStateException.class, () -> mao.dobrar(carta(Valor.TRES)));
    }

    @Test
    public void dividirProduzDuasMaosIndependentesComApostasCorrespondentes() {
        Mao original = Mao.distribuirInicial(carta(Naipe.COPAS, Valor.OITO), carta(Naipe.ESPADAS, Valor.OITO), new Aposta(10));

        Mao irma = original.dividir();

        assertEquals(1, original.cartas().size());
        assertEquals(1, irma.cartas().size());
        assertEquals(10, irma.aposta().orElseThrow(NoSuchElementException::new).valor());
        assertTrue(original.veioDeDivisao());
        assertTrue(irma.veioDeDivisao());
    }

    @Test
    public void dividirLancaExcecaoQuandoAsCartasNaoSaoElegiveis() {
        Mao mao = Mao.distribuirInicial(carta(Valor.OITO), carta(Valor.NOVE), new Aposta(10));

        assertThrows(IllegalStateException.class, mao::dividir);
    }

    @Test
    public void adicionarCartaAposAMaoNaoEstarMaisAtivaLancaExcecao() {
        Mao mao = Mao.distribuirInicial(carta(Valor.CINCO), carta(Valor.SEIS), new Aposta(10));
        mao.parar();

        assertThrows(IllegalStateException.class, () -> mao.adicionarCarta(carta(Valor.DOIS)));
    }

    @Test
    public void maoInicialDoBanqueiroNaoTemAposta() {
        Mao maoDoBanqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.SEIS));

        assertFalse(maoDoBanqueiro.aposta().isPresent());
    }
}
