package com.blackjack.estrutural;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: guardas de {@link Banca} e {@link Aposta} (valores invalidos e saldo insuficiente).
 */
class BancaEstruturalTest {

    @Test
    @DisplayName("Banca nao aceita saldo inicial negativo, mas aceita zero")
    void saldoInicialNegativoEhRecusado() {
        assertThrows(IllegalArgumentException.class, () -> new Banca(-1));
        assertEquals(0, new Banca(0).saldo());
    }

    @Test
    @DisplayName("Sacar valor negativo ou maior que o saldo e recusado sem alterar o saldo")
    void saqueInvalidoNaoAlteraSaldo() {
        Banca banca = new Banca(50);

        assertThrows(IllegalArgumentException.class, () -> banca.sacar(-1));
        assertThrows(IllegalStateException.class, () -> banca.sacar(51));
        assertEquals(50, banca.saldo());
    }

    @Test
    @DisplayName("Depositar valor negativo e recusado sem alterar o saldo")
    void depositoNegativoNaoAlteraSaldo() {
        Banca banca = new Banca(50);

        assertThrows(IllegalArgumentException.class, () -> banca.depositar(-1));
        assertEquals(50, banca.saldo());
    }

    @Test
    @DisplayName("Saque do saldo inteiro e permitido e possuiPeloMenos respeita a fronteira")
    void saqueDoSaldoInteiro() {
        Banca banca = new Banca(50);

        assertTrue(banca.possuiPeloMenos(50));
        assertFalse(banca.possuiPeloMenos(51));
        banca.sacar(50);
        assertEquals(0, banca.saldo());
        banca.depositar(0);
        assertEquals(0, banca.saldo());
    }

    @Test
    @DisplayName("Aposta so aceita valores positivos e dobrada() multiplica por dois")
    void apostaExigeValorPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Aposta(0));
        assertThrows(IllegalArgumentException.class, () -> new Aposta(-5));
        assertEquals(30, new Aposta(15).dobrada().valor());
    }
}
