package com.blackjack.estr;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: guardas de {@link Banca} e {@link Aposta} (valores invalidos e saldo insuficiente).
 */
public class BancaEstruturalTest {

    @Test
    public void saldoInicialNegativoEhRecusado() {
        assertThrows(IllegalArgumentException.class, () -> new Banca(-1));
        assertEquals(0, new Banca(0).saldo());
    }

    @Test
    public void saqueInvalidoNaoAlteraSaldo() {
        Banca banca = new Banca(50);

        assertThrows(IllegalArgumentException.class, () -> banca.sacar(-1));
        assertThrows(IllegalStateException.class, () -> banca.sacar(51));
        assertEquals(50, banca.saldo());
    }

    @Test
    public void depositoNegativoNaoAlteraSaldo() {
        Banca banca = new Banca(50);

        assertThrows(IllegalArgumentException.class, () -> banca.depositar(-1));
        assertEquals(50, banca.saldo());
    }

    @Test
    public void saqueDoSaldoInteiro() {
        Banca banca = new Banca(50);

        assertTrue(banca.possuiPeloMenos(50));
        assertFalse(banca.possuiPeloMenos(51));
        banca.sacar(50);
        assertEquals(0, banca.saldo());
        banca.depositar(0);
        assertEquals(0, banca.saldo());
    }

    @Test
    public void apostaExigeValorPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Aposta(0));
        assertThrows(IllegalArgumentException.class, () -> new Aposta(-5));
        assertEquals(30, new Aposta(15).dobrada().valor());
    }
}
