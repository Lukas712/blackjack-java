package com.blackjack.result;

import com.blackjack.domain.bet.Aposta;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CalculadoraDePagamentoTest {

    private final CalculadoraDePagamento calculadora = new CalculadoraDePagamento();

    @Test
    public void vitoriaComumPagaUmParaUmMaisAApostaOriginal() {
        assertEquals(20, calculadora.pagamentoDaMao(new Aposta(10), Resultado.VITORIA));
    }

    @Test
    public void blackjackNaturalPagaTresParaDoisMaisAApostaOriginal() {
        assertEquals(25, calculadora.pagamentoDaMao(new Aposta(10), Resultado.VITORIA_BLACKJACK_NATURAL));
    }

    @Test
    public void empateDevolveExatamenteAAposta() {
        assertEquals(10, calculadora.pagamentoDaMao(new Aposta(10), Resultado.EMPATE));
    }

    @Test
    public void derrotaNaoPagaNadaPoisAApostaJaFoiRetirada() {
        assertEquals(0, calculadora.pagamentoDaMao(new Aposta(10), Resultado.DERROTA));
    }

    @Test
    public void seguroPagoPagaDoisParaUm() {
        assertEquals(30, calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.SEGURO_PAGO));
    }

    @Test
    public void seguroPerdidoNaoPagaNada() {
        assertEquals(0, calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.SEGURO_PERDIDO));
    }

    @Test
    public void pagamentoDaMaoRejeitaResultadosDeSeguro() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.pagamentoDaMao(new Aposta(10), Resultado.SEGURO_PAGO));
    }

    @Test
    public void pagamentoDoSeguroRejeitaResultadosDeMao() {
        assertThrows(IllegalArgumentException.class, () -> calculadora.pagamentoDoSeguro(new Aposta(10), Resultado.VITORIA));
    }
}
