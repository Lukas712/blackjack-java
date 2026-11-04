package com.blackjack.round;

import com.blackjack.domain.action.ActionEvaluator;
import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.deck.Deck;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.Player;
import com.blackjack.result.OutcomeResolver;
import com.blackjack.result.PayoutCalculator;
import com.blackjack.ui.GameView;
import com.blackjack.ui.InputPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controla o laco principal do jogo: repete rodadas enquanto os jogadores tiverem
 * banca suficiente para a aposta minima e desejarem continuar, encerrando com a
 * exibicao do saldo final e do total de rodadas jogadas.
 */
public final class BlackjackGame {

    private final List<Player> allPlayers;
    private final List<Player> activePlayers;
    private final Map<Player, Bet> baseBets;
    private final Dealer dealer = new Dealer();
    private final ActionEvaluator actionEvaluator = new ActionEvaluator();
    private final OutcomeResolver outcomeResolver = new OutcomeResolver();
    private final PayoutCalculator payoutCalculator = new PayoutCalculator();
    private final GameView view;
    private final InputPort input;
    private final Random random;
    private int roundsPlayed;

    public BlackjackGame(List<Player> players, Map<Player, Bet> baseBets, GameView view,
                          InputPort input, Random random) {
        this.allPlayers = List.copyOf(players);
        this.activePlayers = new ArrayList<>(players);
        this.baseBets = new HashMap<>(baseBets);
        this.view = view;
        this.input = input;
        this.random = random;
    }

    public void start() {
        while (!activePlayers.isEmpty()) {
            removePlayersWithInsufficientBalance();
            if (activePlayers.isEmpty()) {
                break;
            }
            playOneRound();
            if (!input.readYesNo("Deseja jogar outra rodada? (S/N) ")) {
                break;
            }
        }
        view.showGameSummary(roundsPlayed, allPlayers);
    }

    private void removePlayersWithInsufficientBalance() {
        List<Player> toRemove = new ArrayList<>();
        for (Player player : activePlayers) {
            int minimumBet = baseBets.get(player).amount();
            if (!player.wallet().hasAtLeast(minimumBet)) {
                toRemove.add(player);
                view.showPlayerRemoved(player.name());
            }
        }
        activePlayers.removeAll(toRemove);
    }

    private void playOneRound() {
        roundsPlayed++;
        view.showRoundStart(roundsPlayed);
        Deck deck = Deck.newShuffledDeck(random);
        Round round = new Round(deck, activePlayers, dealer, baseBets, actionEvaluator, outcomeResolver,
                payoutCalculator, view);
        round.play();
    }

    public int roundsPlayed() {
        return roundsPlayed;
    }
}
