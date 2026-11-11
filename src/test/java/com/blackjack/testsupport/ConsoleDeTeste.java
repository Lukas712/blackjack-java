package com.blackjack.testsupport;

import com.blackjack.ui.EntradaConsole;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Console simulado: alimenta {@link EntradaConsole} com um texto fixo e captura tudo
 * o que ela imprimiu em resposta.
 */
public final class ConsoleDeTeste {

    private final ByteArrayOutputStream saida = new ByteArrayOutputStream();
    private final EntradaConsole entrada;

    public ConsoleDeTeste(String textoDigitado) {
        this.entrada = new EntradaConsole(new Scanner(textoDigitado),
                novoPrintStream(saida));
    }

    static PrintStream novoPrintStream(ByteArrayOutputStream saida) {
        try {
            return new PrintStream(saida, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public EntradaConsole entrada() {
        return entrada;
    }

    public String saida() {
        return new String(saida.toByteArray(), StandardCharsets.UTF_8);
    }
}
