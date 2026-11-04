package com.blackjack.domain.player;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.hand.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Jogador generico de Blackjack: possui nome, banca e uma ou mais maos (uma lista,
 * para suportar multiplas maos resultantes de split). A tomada de decisao e delegada
 * a um {@link DecisionProvider}, que pode representar um humano via console ou uma
 * regra fixa de IA (dealer) — ambos substituiveis onde se espera um {@code Player}.
 */
public abstract class Player {

    private final String name;
    private final Wallet wallet;
    private final List<Hand> hands = new ArrayList<>();
    private final DecisionProvider decisionProvider;
    private Bet insuranceBet;

    protected Player(String name, Wallet wallet, DecisionProvider decisionProvider) {
        this.name = Objects.requireNonNull(name, "name");
        this.wallet = Objects.requireNonNull(wallet, "wallet");
        this.decisionProvider = Objects.requireNonNull(decisionProvider, "decisionProvider");
    }

    public String name() {
        return name;
    }

    public Wallet wallet() {
        return wallet;
    }

    public List<Hand> hands() {
        return Collections.unmodifiableList(hands);
    }

    /**
     * Reinicia o estado do jogador para uma nova rodada, com uma unica mao inicial
     * e nenhuma aposta de seguro pendente.
     */
    public void startNewRound(Hand initialHand) {
        hands.clear();
        hands.add(Objects.requireNonNull(initialHand, "initialHand"));
        insuranceBet = null;
    }

    public void addHand(Hand hand) {
        hands.add(Objects.requireNonNull(hand, "hand"));
    }

    public Action decide(Hand hand, Set<Action> availableActions, GameContext context) {
        return decisionProvider.chooseAction(hand, availableActions, context);
    }

    public int decideInsuranceAmount(int maxAmount) {
        return decisionProvider.chooseInsuranceAmount(maxAmount);
    }

    public void placeInsuranceBet(Bet bet) {
        this.insuranceBet = Objects.requireNonNull(bet, "bet");
    }

    public Optional<Bet> insuranceBet() {
        return Optional.ofNullable(insuranceBet);
    }
}
