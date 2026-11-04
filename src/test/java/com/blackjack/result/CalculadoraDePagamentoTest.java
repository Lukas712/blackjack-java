package com.blackjack.result;

import com.blackjack.domain.bet.Aposta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraDePagamentoTest {

    private final CalculadoraDePagamento calculadora = new CalculadoraDePagamento();

    @Test
    void vitoriaComumPagaUmParaUmMaisAApostaOriginal() {
        assertEquals(20, calculadora.pagamentoDaMao(new Aposta(10), Resultado.VITORIA));
    }

    @Test
    void blackjackNaturalPagaTresParaDoisMaisAApostaOriginal() {
        assertEquals(25, calculadora.pagamentoDaMao(new Aposta(10), Resultado.VITORIA_BLACKJACK_NATURAL));
    }

    @Test
    void empateDevolveExatamenteAAposta() {
        assertEquals(10, calculadora.pagamentoDaMao(new Aposta(10), Resultado.EMPATE));
    }

    @Test
    void derrotaNaoPagaNadaPoisAApostaJaFoiRetirada() {
        assertEquals(0, calculadora.pagamentoDaMao(new Aposta(10), Resultado.DERROTA));
    }

    @Test
    void seguroPagoPagaDoisParaUm() {
        assertEquals(30, calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.SEGURO_PAGO));
    }

    @Test
    void seguroPerdidoNaoPagaNada() {
        assertEquals(0, calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.SEGURO_PERDIDO));
    }

    @Test
    void pagamentoDaMaoRejeitaResultadosDeSeguro() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.pagamentoDaMao(new Aposta(10), Resultado.SEGURO_PAGO));
    }

    @Test
    void pagamentoDoSeguroRejeitaResultadosDeMao() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.VITORIA));
    }
}
