package com.blackjack.estr;

import com.blackjack.app.main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: ponto de entrada {@link main}. Com banca = aposta nao ha Double, Split nem Insurance,
 * e Stand esta sempre disponivel; assim a mesma entrada conduz qualquer baralho sorteado ate o fim.
 */
public class MainEstruturalTest {

    private final InputStream entradaOriginal = System.in;
    private final PrintStream saidaOriginal = System.out;
    private final ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();

    @Before
    public void redirecionarConsole() throws Exception {
        System.setOut(new PrintStream(saidaCapturada, true, "UTF-8"));
    }

    @After
    public void restaurarConsole() {
        System.setIn(entradaOriginal);
        System.setOut(saidaOriginal);
    }

    private String executar(String entradaDoUsuario) {
        System.setIn(new ByteArrayInputStream(entradaDoUsuario.getBytes(StandardCharsets.UTF_8)));
        main.main(new String[0]);
        return new String(saidaCapturada.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void umJogador() {
        String saida = executar("1\n10\n10\nS\nN\n");

        assertTrue(saida.contains("BLACKJACK (VINTE E UM)"));
        assertTrue(saida.contains("Banca inicial de Jogador: "));
        assertTrue(saida.contains("Rodada 1"));
        assertTrue(saida.contains("FIM DE JOGO"));
        assertTrue(saida.contains("Total de rodadas jogadas: 1"));
    }

    @Test
    public void doisJogadores() {
        String saida = executar("2\n10\n10\n10\n10\nS\nS\nN\n");

        assertTrue(saida.contains("Banca inicial de Jogador 1: "));
        assertTrue(saida.contains("Aposta inicial de Jogador 2 (<= 10): "));
        assertTrue(saida.contains("Saldo final de Jogador 1: "));
        assertTrue(saida.contains("Saldo final de Jogador 2: "));
    }
}
