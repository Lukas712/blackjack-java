package com.blackjack.ui;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.Player;
import com.blackjack.result.Outcome;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementacao de {@link GameView} que imprime as informacoes do jogo no terminal.
 */
public final class ConsoleGameView implements GameView {

    @Override
    public void showWelcome() {
        System.out.println("=".repeat(50));
        System.out.println("           BLACKJACK (VINTE E UM)");
        System.out.println("=".repeat(50));
    }

    @Override
    public void showRoundStart(int roundNumber) {
        System.out.println();
        System.out.println("-".repeat(50));
        System.out.println("Rodada " + roundNumber);
        System.out.println("-".repeat(50));
    }

    @Override
    public void showHand(String ownerLabel, Hand hand, int handIndex, int totalHands) {
        String cards = formatCards(hand);
        String suffix = totalHands > 1 ? " (mao " + handIndex + "/" + totalHands + ")" : "";
        var score = hand.currentScore();
        String scoreLabel = score.isSoft() ? score.value() + " (soft)" : String.valueOf(score.value());
        System.out.printf("%s%s: %s | Pontuacao: %s%n", ownerLabel, suffix, cards, scoreLabel);
    }

    @Override
    public void showDealerUpCard(Card upCard) {
        System.out.println("Dealer mostra: " + upCard.display() + " + [carta oculta]");
    }

    @Override
    public void showDealerFinalHand(Dealer dealer) {
        Hand hand = dealer.hand();
        System.out.printf("Dealer revela: %s | Pontuacao final: %d%s%n",
                formatCards(hand), hand.currentScore().value(),
                hand.isBusted() ? " (ESTOUROU)" : "");
    }

    @Override
    public void showAvailableActions(Set<Action> actions) {
        String options = actions.stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .map(this::describeAction)
                .collect(Collectors.joining(", "));
        System.out.println("Acoes disponiveis: " + options);
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showHandResult(String ownerLabel, int handIndex, int totalHands, Outcome outcome,
                                int betAmount, int payout) {
        String suffix = totalHands > 1 ? " (mao " + handIndex + "/" + totalHands + ")" : "";
        int net = payout - betAmount;
        System.out.printf("%s%s: %s | Aposta: %d | Saldo da jogada: %+d%n",
                ownerLabel, suffix, describeOutcome(outcome), betAmount, net);
    }

    @Override
    public void showInsuranceResult(String ownerLabel, Outcome outcome, int insuranceAmount, int payout) {
        String resultado = outcome == Outcome.INSURANCE_WIN ? "Seguro PAGO (2:1)" : "Seguro PERDIDO";
        int net = payout - insuranceAmount;
        System.out.printf("%s: %s | Valor do seguro: %d | Saldo do seguro: %+d%n",
                ownerLabel, resultado, insuranceAmount, net);
    }

    @Override
    public void showWallet(String ownerLabel, int balance) {
        System.out.printf("Banca de %s: %d fichas%n", ownerLabel, balance);
    }

    @Override
    public void showGameSummary(int roundsPlayed, List<Player> players) {
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("FIM DE JOGO");
        System.out.println("Total de rodadas jogadas: " + roundsPlayed);
        for (Player player : players) {
            System.out.printf("Saldo final de %s: %d fichas%n", player.name(), player.wallet().balance());
        }
        System.out.println("=".repeat(50));
    }

    @Override
    public void showPlayerRemoved(String ownerLabel) {
        System.out.println(ownerLabel + " ficou sem banca suficiente para a aposta minima e saiu da mesa.");
    }

    private String formatCards(Hand hand) {
        return hand.cards().stream().map(Card::display).collect(Collectors.joining(" "));
    }

    private String describeOutcome(Outcome outcome) {
        return switch (outcome) {
            case NATURAL_BLACKJACK_WIN -> "BLACKJACK NATURAL! Vitoria (3:2)";
            case WIN -> "Vitoria (1:1)";
            case PUSH -> "Empate (Push)";
            case LOSS -> "Derrota";
            case INSURANCE_WIN, INSURANCE_LOSS -> outcome.name();
        };
    }

    private String describeAction(Action action) {
        return switch (action) {
            case HIT -> "[H]it";
            case STAND -> "[S]tand";
            case DOUBLE_DOWN -> "[D]ouble Down";
            case SPLIT -> "s[P]lit";
            case INSURANCE -> "[I]nsurance";
        };
    }
}
