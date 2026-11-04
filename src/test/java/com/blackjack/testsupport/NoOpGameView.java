package com.blackjack.testsupport;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.Player;
import com.blackjack.result.Outcome;
import com.blackjack.ui.GameView;

import java.util.List;
import java.util.Set;

/**
 * {@link GameView} de teste que nao produz nenhuma saida, usada para manter os testes
 * de integracao de {@code Round} silenciosos e focados no estado final do jogo.
 */
public final class NoOpGameView implements GameView {

    @Override
    public void showWelcome() {
    }

    @Override
    public void showRoundStart(int roundNumber) {
    }

    @Override
    public void showHand(String ownerLabel, Hand hand, int handIndex, int totalHands) {
    }

    @Override
    public void showDealerUpCard(Card upCard) {
    }

    @Override
    public void showDealerFinalHand(Dealer dealer) {
    }

    @Override
    public void showAvailableActions(Set<Action> actions) {
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void showHandResult(String ownerLabel, int handIndex, int totalHands, Outcome outcome, int betAmount,
                                int payout) {
    }

    @Override
    public void showInsuranceResult(String ownerLabel, Outcome outcome, int insuranceAmount, int payout) {
    }

    @Override
    public void showWallet(String ownerLabel, int balance) {
    }

    @Override
    public void showGameSummary(int roundsPlayed, List<Player> players) {
    }

    @Override
    public void showPlayerRemoved(String ownerLabel) {
    }
}
