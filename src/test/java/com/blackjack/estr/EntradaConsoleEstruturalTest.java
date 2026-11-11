package com.blackjack.estr;

import com.blackjack.domain.action.Acao;
import com.blackjack.testsupport.ConsoleDeTeste;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: todos os ramos de validacao e repeticao de pergunta de {@code EntradaConsole}.
 */
public class EntradaConsoleEstruturalTest {

    @Test
    public void inteiroPositivo() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n0\n-5\n  7  \n");

        assertEquals(7, console.entrada().lerInteiroPositivo("Banca: "));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("inteiro positivo"));
    }

    @Test
    public void apostaLimitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("101\n100\n");

        assertEquals(100, console.entrada().lerApostaLimitada("Aposta: ", 100));
        assertTrue(console.saida().contains("O valor nao pode exceder 100."));
    }

    @Test
    public void apostaDaRodadaEnterMantemAAtual() {
        ConsoleDeTeste console = new ConsoleDeTeste("\n");

        assertEquals(20, console.entrada().lerApostaDaRodada("Aposta: ", 20, 100));
    }

    @Test
    public void apostaDaRodadaValidaFaixa() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n0\n101\n100\n");
        assertEquals(100, console.entrada().lerApostaDaRodada("Aposta: ", 20, 100));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 1 e 100."));

        assertEquals(1, new ConsoleDeTeste("1\n").entrada().lerApostaDaRodada("Aposta: ", 20, 100));
    }

    @Test
    public void numeroDeJogadores() {
        ConsoleDeTeste console = new ConsoleDeTeste("x\n0\n4\n3\n");

        assertEquals(3, console.entrada().lerNumeroDeJogadores(1, 3));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 1 e 3."));
        assertEquals(1, new ConsoleDeTeste("1\n").entrada().lerNumeroDeJogadores(1, 3));
    }

    @Test
    public void interpretaComandos() {
        String[][] casos = {{"H", "PEDIR_CARTA"}, {"hit", "PEDIR_CARTA"}, {"S", "PARAR"}, {"stand", "PARAR"},
                {"D", "DOBRAR"}, {"double", "DOBRAR"}, {"double down", "DOBRAR"}, {"DOUBLE_DOWN", "DOBRAR"},
                {"P", "DIVIDIR"}, {"split", "DIVIDIR"}, {"I", "SEGURO"}, {"insurance", "SEGURO"}};

        for (String[] caso : casos) {
            ConsoleDeTeste console = new ConsoleDeTeste(caso[0] + "\n");

            assertEquals(Acao.valueOf(caso[1]), console.entrada().lerAcao(EnumSet.allOf(Acao.class)));
        }
    }

    @Test
    public void recusaComandosInvalidos() {
        ConsoleDeTeste console = new ConsoleDeTeste("xyz\nD\nH\n");

        assertEquals(Acao.PEDIR_CARTA, console.entrada().lerAcao(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR)));
        assertEquals(2, console.saida().split("Acao invalida", -1).length - 1);
    }

    @Test
    public void valorDoSeguro() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n-1\n26\n25\n");

        assertEquals(25, console.entrada().lerValorDoSeguro(25));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 0 e 25."));
        assertEquals(0, new ConsoleDeTeste("0\n").entrada().lerValorDoSeguro(25));
    }

    @Test
    public void respostasAfirmativas() {
        for (String digitado : new String[]{"S", "s", "SIM", "sim", "Y", "YES"}) {
            assertTrue(new ConsoleDeTeste(digitado + "\n").entrada().lerSimOuNao("? "));
        }
    }

    @Test
    public void respostasNegativas() {
        for (String digitado : new String[]{"N", "n", "NAO", "n\u00e3o", "NO"}) {
            assertFalse(new ConsoleDeTeste(digitado + "\n").entrada().lerSimOuNao("? "));
        }
    }

    @Test
    public void respostaAmbiguaRepeteAPergunta() {
        ConsoleDeTeste console = new ConsoleDeTeste("talvez\nN\n");

        assertFalse(console.entrada().lerSimOuNao("? "));
        assertTrue(console.saida().contains("Responda com S (sim) ou N (nao)."));
    }
}
