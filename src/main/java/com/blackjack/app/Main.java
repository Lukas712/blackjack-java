package com.blackjack.app;

import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.player.HumanPlayer;
import com.blackjack.domain.player.Player;
import com.blackjack.round.BlackjackGame;
import com.blackjack.ui.ConsoleDecisionProvider;
import com.blackjack.ui.ConsoleGameView;
import com.blackjack.ui.ConsoleInputPort;
import com.blackjack.ui.GameView;
import com.blackjack.ui.InputPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicacao: configura de 1 a 3 jogadores a partir da entrada do
 * usuario (banca inicial e aposta inicial de cada um) e inicia o laco principal do jogo.
 */
public final class Main {

    private static final int MIN_PLAYERS = 1;
    private static final int MAX_PLAYERS = 3;

    private Main() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameView view = new ConsoleGameView();
        InputPort input = new ConsoleInputPort(scanner, System.out);

        view.showWelcome();
        int playerCount = input.readNumberOfPlayers(MIN_PLAYERS, MAX_PLAYERS);

        List<Player> players = new ArrayList<>();
        Map<Player, Bet> baseBets = new LinkedHashMap<>();
        for (int i = 1; i <= playerCount; i++) {
            String name = playerCount == 1 ? "Jogador" : "Jogador " + i;
            int bankroll = input.readPositiveInt("Banca inicial de " + name + ": ");
            int bet = input.readBetNotExceeding("Aposta inicial de " + name + " (<= " + bankroll + "): ", bankroll);
            HumanPlayer player = new HumanPlayer(name, new Wallet(bankroll),
                    new ConsoleDecisionProvider(view, input));
            players.add(player);
            baseBets.put(player, new Bet(bet));
        }

        BlackjackGame game = new BlackjackGame(players, baseBets, view, input, new Random());
        game.start();
    }
}
