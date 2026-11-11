package com.blackjack.ui;

import com.blackjack.domain.action.Acao;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Implementacao de {@link PortaDeEntrada} que le dados digitados pelo usuario no
 * terminal, validando e repetindo a pergunta ate obter uma resposta aceitavel.
 */
public final class EntradaConsole implements PortaDeEntrada {

    private static final Map<String, Acao> COMANDOS = criarComandos();

    private final Scanner leitor;
    private final PrintStream saida;

    public EntradaConsole(Scanner leitor, PrintStream saida) {
        this.leitor = leitor;
        this.saida = saida;
    }

    @Override
    public int lerInteiroPositivo(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor > 0) {
                    return valor;
                }
                saida.println("Informe um numero inteiro positivo.");
            } catch (NumberFormatException e) {
                saida.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public int lerApostaLimitada(String mensagem, int valorMaximo) {
        while (true) {
            int valor = lerInteiroPositivo(mensagem);
            if (valor <= valorMaximo) {
                return valor;
            }
            saida.println("O valor nao pode exceder " + valorMaximo + ".");
        }
    }

    @Override
    public int lerApostaDaRodada(String mensagem, int apostaAtual, int valorMaximo) {
        while (true) {
            saida.print(mensagem);
            String linha = leitor.nextLine().trim();
            if (linha.isEmpty()) {
                return apostaAtual;
            }
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= 1 && valor <= valorMaximo) {
                    return valor;
                }
                saida.printf("Informe um valor entre 1 e %d.%n", valorMaximo);
            } catch (NumberFormatException e) {
                saida.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public int lerNumeroDeJogadores(int minimo, int maximo) {
        while (true) {
            saida.printf("Quantos jogadores (%d a %d)? ", minimo, maximo);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                saida.printf("Informe um valor entre %d e %d.%n", minimo, maximo);
            } catch (NumberFormatException e) {
                saida.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public Acao lerAcao(Set<Acao> acoesDisponiveis) {
        while (true) {
            saida.print("Escolha uma acao: ");
            String linha = leitor.nextLine().trim().toUpperCase(Locale.ROOT);
            Acao acao = interpretarAcao(linha);
            if (acao != null && acoesDisponiveis.contains(acao)) {
                return acao;
            }
            saida.println("Acao invalida. Escolha uma das acoes listadas.");
        }
    }

    private Acao interpretarAcao(String entrada) {
        return COMANDOS.get(entrada);
    }

    private static Map<String, Acao> criarComandos() {
        Map<String, Acao> comandos = new HashMap<>();
        registrar(comandos, Acao.PEDIR_CARTA, "H", "HIT");
        registrar(comandos, Acao.PARAR, "S", "STAND");
        registrar(comandos, Acao.DOBRAR, "D", "DOUBLE", "DOUBLE DOWN", "DOUBLE_DOWN");
        registrar(comandos, Acao.DIVIDIR, "P", "SPLIT");
        registrar(comandos, Acao.SEGURO, "I", "INSURANCE");
        return comandos;
    }

    private static void registrar(Map<String, Acao> comandos, Acao acao, String... apelidos) {
        for (String apelido : apelidos) {
            comandos.put(apelido, acao);
        }
    }

    @Override
    public int lerValorDoSeguro(int valorMaximo) {
        while (true) {
            saida.printf("Valor do seguro (0 a %d, 0 para recusar): ", valorMaximo);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor >= 0 && valor <= valorMaximo) {
                    return valor;
                }
                saida.printf("Informe um valor entre 0 e %d.%n", valorMaximo);
            } catch (NumberFormatException e) {
                saida.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public boolean lerSimOuNao(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String linha = leitor.nextLine().trim().toUpperCase(Locale.ROOT);
            if (linha.equals("S") || linha.equals("SIM") || linha.equals("Y") || linha.equals("YES")) {
                return true;
            }
            if (linha.equals("N") || linha.equals("NAO") || linha.equals("NÃO") || linha.equals("NO")) {
                return false;
            }
            saida.println("Responda com S (sim) ou N (nao).");
        }
    }
}
