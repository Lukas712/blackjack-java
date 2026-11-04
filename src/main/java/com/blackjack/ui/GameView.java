package com.blackjack.ui;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.Player;
import com.blackjack.result.Outcome;

import java.util.List;
import java.util.Set;

/**
 * Abstrai toda a exibicao de informacoes do jogo, permitindo trocar a saida (console,
 * testes) sem alterar a logica de dominio (ver RNF05 da especificacao).
 */
public interface GameView {

    void showWelcome();

    void showRoundStart(int roundNumber);

    void showHand(String ownerLabel, Hand hand, int handIndex, int totalHands);

    void showDealerUpCard(Card upCard);

    void showDealerFinalHand(Dealer dealer);

    void showAvailableActions(Set<Action> actions);

    void showMessage(String message);

    void showHandResult(String ownerLabel, int handIndex, int totalHands, Outcome outcome, int betAmount,
                         int payout);

    void showInsuranceResult(String ownerLabel, Outcome outcome, int insuranceAmount, int payout);

    void showWallet(String ownerLabel, int balance);

    void showGameSummary(int roundsPlayed, List<Player> players);

    void showPlayerRemoved(String ownerLabel);
}
