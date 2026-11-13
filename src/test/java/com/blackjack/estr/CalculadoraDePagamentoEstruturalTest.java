package com.blackjack.estr;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.result.Resultado;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class CalculadoraDePagamentoEstruturalTest {

    private final CalculadoraDePagamento calculadora =
            new CalculadoraDePagamento();

    @Test
    public void pagamentoDaMaoRejeitaResultadoDeSeguro() {
        Aposta aposta = new Aposta(10);

        assertThrows(IllegalArgumentException.class, () ->
            calculadora.pagamentoDaMao(
                aposta, Resultado.SEGURO_PAGO)
        );
    }

    @Test
    public void pagamentoDoSeguroRejeitaResultadoDeMao() {
        Aposta seguro = new Aposta(10);

        assertThrows(IllegalArgumentException.class, () ->
            calculadora.pagamentoDoSeguro(
                seguro, Resultado.VITORIA)
        );
    }
}
