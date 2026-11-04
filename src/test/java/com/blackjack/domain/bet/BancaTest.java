package com.blackjack.domain.bet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BancaTest {

    @Test
    void sacarReduzOSaldo() {
        Banca banca = new Banca(100);

        banca.sacar(30);

        assertEquals(70, banca.saldo());
    }

    @Test
    void depositarAumentaOSaldo() {
        Banca banca = new Banca(100);

        banca.depositar(25);

        assertEquals(125, banca.saldo());
    }

    @Test
    void possuiPeloMenosRespeitaOLimiteExato() {
        Banca banca = new Banca(50);

        assertTrue(banca.possuiPeloMenos(50));
        assertFalse(banca.possuiPeloMenos(51));
    }

    @Test
    void sacarMaisDoQueOSaldoLancaExcecao() {
        Banca banca = new Banca(10);

        assertThrows(IllegalStateException.class, () -> banca.sacar(11));
    }

    @Test
    void saldoInicialNegativoEhRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> new Banca(-1));
    }
}
