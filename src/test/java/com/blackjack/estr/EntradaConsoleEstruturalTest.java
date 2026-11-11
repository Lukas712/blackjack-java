package com.blackjack.estr;

import com.blackjack.domain.action.Acao;
import com.blackjack.testsupport.ConsoleDeTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: todos os ramos de validacao e repeticao de pergunta de {@code EntradaConsole}.
 */
class EntradaConsoleEstruturalTest {

    @Test
    @DisplayName("lerInteiroPositivo repete a pergunta para texto, zero e negativo ate receber valor valido")
    void inteiroPositivo() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n0\n-5\n  7  \n");

        assertEquals(7, console.entrada().lerInteiroPositivo("Banca: "));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("inteiro positivo"));
    }

    @Test
    @DisplayName("lerApostaLimitada recusa valor acima do maximo e aceita o proprio maximo")
    void apostaLimitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("101\n100\n");

        assertEquals(100, console.entrada().lerApostaLimitada("Aposta: ", 100));
        assertTrue(console.saida().contains("O valor nao pode exceder 100."));
    }

    @Test
    @DisplayName("lerApostaDaRodada: Enter mantem a aposta atual")
    void apostaDaRodadaEnterMantemAAtual() {
        ConsoleDeTeste console = new ConsoleDeTeste("\n");

        assertEquals(20, console.entrada().lerApostaDaRodada("Aposta: ", 20, 100));
    }

    @Test
    @DisplayName("lerApostaDaRodada recusa texto, zero e valor acima do saldo; aceita fronteiras 1 e saldo")
    void apostaDaRodadaValidaFaixa() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n0\n101\n100\n");
        assertEquals(100, console.entrada().lerApostaDaRodada("Aposta: ", 20, 100));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 1 e 100."));

        assertEquals(1, new ConsoleDeTeste("1\n").entrada().lerApostaDaRodada("Aposta: ", 20, 100));
    }

    @Test
    @DisplayName("lerNumeroDeJogadores recusa texto e valores fora de 1..3 e aceita as fronteiras")
    void numeroDeJogadores() {
        ConsoleDeTeste console = new ConsoleDeTeste("x\n0\n4\n3\n");

        assertEquals(3, console.entrada().lerNumeroDeJogadores(1, 3));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 1 e 3."));
        assertEquals(1, new ConsoleDeTeste("1\n").entrada().lerNumeroDeJogadores(1, 3));
    }

    @ParameterizedTest(name = "\"{0}\" -> {1}")
    @CsvSource({"H,PEDIR_CARTA", "hit,PEDIR_CARTA", "S,PARAR", "stand,PARAR", "D,DOBRAR", "double,DOBRAR",
            "double down,DOBRAR", "DOUBLE_DOWN,DOBRAR", "P,DIVIDIR", "split,DIVIDIR", "I,SEGURO", "insurance,SEGURO"})
    @DisplayName("lerAcao interpreta abreviacoes e nomes por extenso, sem diferenciar maiusculas")
    void interpretaComandos(String digitado, Acao esperada) {
        ConsoleDeTeste console = new ConsoleDeTeste(digitado + "\n");

        assertEquals(esperada, console.entrada().lerAcao(EnumSet.allOf(Acao.class)));
    }

    @Test
    @DisplayName("lerAcao recusa comando desconhecido e acao fora das disponiveis")
    void recusaComandosInvalidos() {
        ConsoleDeTeste console = new ConsoleDeTeste("xyz\nD\nH\n");

        assertEquals(Acao.PEDIR_CARTA, console.entrada().lerAcao(EnumSet.of(Acao.PEDIR_CARTA, Acao.PARAR)));
        assertEquals(2, console.saida().split("Acao invalida", -1).length - 1);
    }

    @Test
    @DisplayName("lerValorDoSeguro recusa texto, negativo e acima do maximo; aceita 0 e o maximo")
    void valorDoSeguro() {
        ConsoleDeTeste console = new ConsoleDeTeste("abc\n-1\n26\n25\n");

        assertEquals(25, console.entrada().lerValorDoSeguro(25));
        assertTrue(console.saida().contains("Entrada invalida"));
        assertTrue(console.saida().contains("Informe um valor entre 0 e 25."));
        assertEquals(0, new ConsoleDeTeste("0\n").entrada().lerValorDoSeguro(25));
    }

    @ParameterizedTest
    @ValueSource(strings = {"S", "s", "SIM", "sim", "Y", "YES"})
    @DisplayName("lerSimOuNao aceita as respostas afirmativas")
    void respostasAfirmativas(String digitado) {
        assertTrue(new ConsoleDeTeste(digitado + "\n").entrada().lerSimOuNao("? "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"N", "n", "NAO", "n\u00e3o", "NO"})
    @DisplayName("lerSimOuNao aceita as respostas negativas")
    void respostasNegativas(String digitado) {
        assertFalse(new ConsoleDeTeste(digitado + "\n").entrada().lerSimOuNao("? "));
    }

    @Test
    @DisplayName("lerSimOuNao repete a pergunta para resposta ambigua")
    void respostaAmbiguaRepeteAPergunta() {
        ConsoleDeTeste console = new ConsoleDeTeste("talvez\nN\n");

        assertFalse(console.entrada().lerSimOuNao("? "));
        assertTrue(console.saida().contains("Responda com S (sim) ou N (nao)."));
    }
}
