package com.blackjack.domain.bet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class BancaTest {

    @Test
    public void sacarReduzOSaldo() {
        Banca banca = new Banca(100);

        banca.sacar(30);

        assertEquals(70, banca.saldo());
    }

    @Test
    public void depositarAumentaOSaldo() {
        Banca banca = new Banca(100);

        banca.depositar(25);

        assertEquals(125, banca.saldo());
    }

    @Test
    public void possuiPeloMenosRespeitaOLimiteExato() {
        Banca banca = new Banca(50);

        assertTrue(banca.possuiPeloMenos(50));
        assertFalse(banca.possuiPeloMenos(51));
    }

    @Test
    public void sacarMaisDoQueOSaldoLancaExcecao() {
        Banca banca = new Banca(10);

        assertThrows(IllegalStateException.class, () -> banca.sacar(11));
    }

    @Test
    public void saldoInicialNegativoEhRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> new Banca(-1));
    }
}
