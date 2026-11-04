package com.blackjack.round;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.ActionEvaluator;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.deck.Deck;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.Player;
import com.blackjack.result.Outcome;
import com.blackjack.result.OutcomeResolver;
import com.blackjack.result.PayoutCalculator;
import com.blackjack.ui.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orquestra uma rodada completa de Blackjack: distribuicao inicial, coleta de
 * seguros, turnos dos jogadores (incluindo maos geradas por split), turno do dealer
 * e liquidacao de todas as apostas.
 */
public final class Round {

    private final Deck deck;
    private final List<Player> players;
    private final Dealer dealer;
    private final Map<Player, Bet> initialBets;
    private final ActionEvaluator actionEvaluator;
    private final OutcomeResolver outcomeResolver;
    private final PayoutCalculator payoutCalculator;
    private final GameView view;

    public Round(Deck deck, List<Player> players, Dealer dealer, Map<Player, Bet> initialBets,
                 ActionEvaluator actionEvaluator, OutcomeResolver outcomeResolver,
                 PayoutCalculator payoutCalculator, GameView view) {
        this.deck = deck;
        this.players = players;
        this.dealer = dealer;
        this.initialBets = initialBets;
        this.actionEvaluator = actionEvaluator;
        this.outcomeResolver = outcomeResolver;
        this.payoutCalculator = payoutCalculator;
        this.view = view;
    }

    public void play() {
        dealInitialHands();
        view.showDealerUpCard(dealer.upCard());
        offerInsuranceToAll();
        players.forEach(this::playAllHandsOf);
        playDealerTurn();
        settleAllPlayers();
    }

    private void dealInitialHands() {
        List<List<Card>> batches = drawRoundRobin(players.size() + 1);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            Bet bet = initialBets.get(player);
            player.wallet().withdraw(bet.amount());
            List<Card> cards = batches.get(i);
            player.startNewRound(Hand.dealInitial(cards.get(0), cards.get(1), bet));
        }
        List<Card> dealerCards = batches.get(players.size());
        dealer.startNewRound(Hand.dealerInitial(dealerCards.get(0), dealerCards.get(1)));
    }

    private List<List<Card>> drawRoundRobin(int participants) {
        List<List<Card>> batches = new ArrayList<>();
        for (int i = 0; i < participants; i++) {
            batches.add(new ArrayList<>());
        }
        for (int cardNumber = 0; cardNumber < 2; cardNumber++) {
            for (int i = 0; i < participants; i++) {
                batches.get(i).add(deck.draw());
            }
        }
        return batches;
    }

    private void offerInsuranceToAll() {
        players.forEach(this::offerInsurance);
    }

    private void offerInsurance(Player player) {
        int initialBet = initialBets.get(player).amount();
        if (!actionEvaluator.isInsuranceAvailable(dealer.upCard(), player.wallet(), initialBet)) {
            return;
        }
        int maxAmount = actionEvaluator.maxInsuranceAmount(initialBet);
        view.showMessage(player.name() + ": a carta visivel do dealer e um As. "
                + "Voce pode apostar um seguro de ate " + maxAmount + ".");
        int amount = player.decideInsuranceAmount(maxAmount);
        if (amount > 0) {
            player.wallet().withdraw(amount);
            player.placeInsuranceBet(new Bet(amount));
        }
    }

    private void playAllHandsOf(Player player) {
        GameContext context = new GameContext(dealer.upCard());
        int index = 0;
        while (index < player.hands().size()) {
            playOutHand(player, player.hands().get(index), context);
            index++;
        }
    }

    private void playOutHand(Player player, Hand hand, GameContext context) {
        while (hand.isActive()) {
            Set<Action> available = actionEvaluator.handActions(hand, player.wallet());
            view.showHand(player.name(), hand, player.hands().indexOf(hand) + 1, player.hands().size());
            Action action = player.decide(hand, available, context);
            applyAction(player, hand, action);
        }
    }

    private void applyAction(Player player, Hand hand, Action action) {
        switch (action) {
            case HIT -> hand.addCard(deck.draw());
            case STAND -> hand.stand();
            case DOUBLE_DOWN -> applyDoubleDown(player, hand);
            case SPLIT -> applySplit(player, hand);
            default -> throw new IllegalStateException("Acao inesperada durante a jogada da mao: " + action);
        }
    }

    private void applyDoubleDown(Player player, Hand hand) {
        player.wallet().withdraw(hand.bet().orElseThrow().amount());
        hand.doubleDown(deck.draw());
    }

    private void applySplit(Player player, Hand hand) {
        player.wallet().withdraw(hand.bet().orElseThrow().amount());
        Hand sibling = hand.split();
        hand.addCard(deck.draw());
        sibling.addCard(deck.draw());
        player.addHand(sibling);
    }

    private void playDealerTurn() {
        GameContext context = new GameContext(dealer.upCard());
        playOutHand(dealer, dealer.hand(), context);
        view.showDealerFinalHand(dealer);
    }

    private void settleAllPlayers() {
        players.forEach(this::settlePlayer);
    }

    private void settlePlayer(Player player) {
        settleInsurance(player);
        settleHands(player);
        view.showWallet(player.name(), player.wallet().balance());
    }

    private void settleInsurance(Player player) {
        player.insuranceBet().ifPresent(insurance -> {
            Outcome outcome = outcomeResolver.resolveInsurance(dealer.hand());
            int payout = payoutCalculator.insurancePayout(insurance, outcome);
            player.wallet().deposit(payout);
            view.showInsuranceResult(player.name(), outcome, insurance.amount(), payout);
        });
    }

    private void settleHands(Player player) {
        List<Hand> hands = player.hands();
        for (int i = 0; i < hands.size(); i++) {
            settleHand(player, hands.get(i), i + 1, hands.size());
        }
    }

    private void settleHand(Player player, Hand hand, int handIndex, int totalHands) {
        Outcome outcome = outcomeResolver.resolveHand(hand, dealer.hand());
        Bet bet = hand.bet().orElseThrow();
        int payout = payoutCalculator.handPayout(bet, outcome);
        player.wallet().deposit(payout);
        view.showHandResult(player.name(), handIndex, totalHands, outcome, bet.amount(), payout);
    }
}
