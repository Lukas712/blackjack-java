package com.blackjack.ui;

import com.blackjack.domain.action.Action;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

/**
 * Implementacao de {@link InputPort} que le dados digitados pelo usuario no terminal,
 * validando e repetindo a pergunta ate obter uma resposta aceitavel.
 */
public final class ConsoleInputPort implements InputPort {

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInputPort(Scanner scanner, PrintStream out) {
        this.scanner = scanner;
        this.out = out;
    }

    @Override
    public int readPositiveInt(String prompt) {
        while (true) {
            out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value > 0) {
                    return value;
                }
                out.println("Informe um numero inteiro positivo.");
            } catch (NumberFormatException e) {
                out.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public int readBetNotExceeding(String prompt, int maxValue) {
        while (true) {
            int value = readPositiveInt(prompt);
            if (value <= maxValue) {
                return value;
            }
            out.println("O valor nao pode exceder " + maxValue + ".");
        }
    }

    @Override
    public int readNumberOfPlayers(int min, int max) {
        while (true) {
            out.printf("Quantos jogadores (%d a %d)? ", min, max);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                out.printf("Informe um valor entre %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                out.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public Action readAction(Set<Action> availableActions) {
        while (true) {
            out.print("Escolha uma acao: ");
            String line = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            Action action = parseAction(line);
            if (action != null && availableActions.contains(action)) {
                return action;
            }
            out.println("Acao invalida. Escolha uma das acoes listadas.");
        }
    }

    private Action parseAction(String input) {
        return switch (input) {
            case "H", "HIT" -> Action.HIT;
            case "S", "STAND" -> Action.STAND;
            case "D", "DOUBLE", "DOUBLE DOWN", "DOUBLE_DOWN" -> Action.DOUBLE_DOWN;
            case "P", "SPLIT" -> Action.SPLIT;
            case "I", "INSURANCE" -> Action.INSURANCE;
            default -> null;
        };
    }

    @Override
    public int readInsuranceAmount(int maxAmount) {
        while (true) {
            out.printf("Valor do seguro (0 a %d, 0 para recusar): ", maxAmount);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= 0 && value <= maxAmount) {
                    return value;
                }
                out.printf("Informe um valor entre 0 e %d.%n", maxAmount);
            } catch (NumberFormatException e) {
                out.println("Entrada invalida. Informe um numero inteiro.");
            }
        }
    }

    @Override
    public boolean readYesNo(String prompt) {
        while (true) {
            out.print(prompt);
            String line = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            if (line.equals("S") || line.equals("SIM") || line.equals("Y") || line.equals("YES")) {
                return true;
            }
            if (line.equals("N") || line.equals("NAO") || line.equals("NÃO") || line.equals("NO")) {
                return false;
            }
            out.println("Responda com S (sim) ou N (nao).");
        }
    }
}
