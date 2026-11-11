package com.blackjack.estr;

import com.blackjack.app.main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: ponto de entrada {@link main}. Com banca = aposta nao ha Double, Split nem Insurance,
 * e Stand esta sempre disponivel; assim a mesma entrada conduz qualquer baralho sorteado ate o fim.
 */
class MainEstruturalTest {

    private final InputStream entradaOriginal = System.in;
    private final PrintStream saidaOriginal = System.out;
    private final ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();

    @BeforeEach
    void redirecionarConsole() {
        System.setOut(new PrintStream(saidaCapturada, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurarConsole() {
        System.setIn(entradaOriginal);
        System.setOut(saidaOriginal);
    }

    private String executar(String entradaDoUsuario) {
        System.setIn(new ByteArrayInputStream(entradaDoUsuario.getBytes(StandardCharsets.UTF_8)));
        main.main(new String[0]);
        return saidaCapturada.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Um jogador: configura banca e aposta, joga uma rodada e exibe o resumo final")
    void umJogador() {
        String saida = executar("1\n10\n10\nS\nN\n");

        assertTrue(saida.contains("BLACKJACK (VINTE E UM)"));
        assertTrue(saida.contains("Banca inicial de Jogador: "));
        assertTrue(saida.contains("Rodada 1"));
        assertTrue(saida.contains("FIM DE JOGO"));
        assertTrue(saida.contains("Total de rodadas jogadas: 1"));
    }

    @Test
    @DisplayName("Dois jogadores: rotulos numerados e cada jogador com a propria banca")
    void doisJogadores() {
        String saida = executar("2\n10\n10\n10\n10\nS\nS\nN\n");

        assertTrue(saida.contains("Banca inicial de Jogador 1: "));
        assertTrue(saida.contains("Aposta inicial de Jogador 2 (<= 10): "));
        assertTrue(saida.contains("Saldo final de Jogador 1: "));
        assertTrue(saida.contains("Saldo final de Jogador 2: "));
    }
}
