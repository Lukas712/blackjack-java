package com.blackjack.round;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.ActionEvaluator;
import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.bet.Wallet;
import com.blackjack.domain.card.Card;
import com.blackjack.domain.card.Rank;
import com.blackjack.domain.card.Suit;
import com.blackjack.domain.deck.Deck;
import com.blackjack.domain.player.Dealer;
import com.blackjack.domain.player.HumanPlayer;
import com.blackjack.result.OutcomeResolver;
import com.blackjack.result.PayoutCalculator;
import com.blackjack.testsupport.NoOpGameView;
import com.blackjack.testsupport.ScriptedDecisionProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes de integracao de {@link Round}, usando um baralho com ordem fixa
 * ({@link Deck#orderedDeck}) e um {@link ScriptedDecisionProvider} para validar a
 * orquestracao completa de uma rodada: saque/deposito na banca, criacao de maos
 * independentes via split, double down e liquidacao do seguro.
 */
class RoundTest {

    private static Card of(Suit suit, Rank rank) {
        return new Card(suit, rank);
    }

    private Round newRound(Deck deck, HumanPlayer player, Dealer dealer, int initialBet) {
        return new Round(deck, List.of(player), dealer, Map.of(player, new Bet(initialBet)),
                new ActionEvaluator(), new OutcomeResolver(), new PayoutCalculator(), new NoOpGameView());
    }

    @Test
    void naturalBlackjackOnlyAllowsStandAndPaysThreeToTwo() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet, new ScriptedDecisionProvider(Action.STAND));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.ACE), of(Suit.COPAS, Rank.TEN),
                of(Suit.ESPADAS, Rank.KING), of(Suit.COPAS, Rank.SIX),
                of(Suit.PAUS, Rank.FIVE)));

        newRound(deck, player, dealer, 10).play();

        assertEquals(115, wallet.balance());
    }

    @Test
    void dealerBustMakesPlayerWinEvenWithALowScore() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet, new ScriptedDecisionProvider(Action.STAND));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.FOUR), of(Suit.COPAS, Rank.TEN),
                of(Suit.ESPADAS, Rank.EIGHT), of(Suit.COPAS, Rank.FIVE),
                of(Suit.PAUS, Rank.TEN)));

        newRound(deck, player, dealer, 10).play();

        assertEquals(110, wallet.balance());
    }

    @Test
    void doubleDownDoublesTheStakeDrawsOneCardAndSettlesOnTheNewStake() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet, new ScriptedDecisionProvider(Action.DOUBLE_DOWN));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.FIVE), of(Suit.COPAS, Rank.TEN),
                of(Suit.ESPADAS, Rank.SIX), of(Suit.COPAS, Rank.SEVEN),
                of(Suit.PAUS, Rank.TEN)));

        newRound(deck, player, dealer, 10).play();

        assertEquals(120, wallet.balance());
    }

    @Test
    void splitCreatesTwoIndependentHandsEachWithItsOwnBetAndSettlement() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet,
                new ScriptedDecisionProvider(Action.SPLIT, Action.STAND, Action.STAND));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.EIGHT), of(Suit.COPAS, Rank.TEN),
                of(Suit.OUROS, Rank.EIGHT), of(Suit.COPAS, Rank.SIX),
                of(Suit.ESPADAS, Rank.TEN), of(Suit.OUROS, Rank.ACE),
                of(Suit.PAUS, Rank.TWO)));

        newRound(deck, player, dealer, 10).play();

        assertEquals(110, wallet.balance());
    }

    @Test
    void insurancePaysTwoToOneWhenDealerConfirmsNaturalBlackjack() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet,
                new ScriptedDecisionProvider(Action.STAND).withInsurance(10));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.TEN), of(Suit.COPAS, Rank.ACE),
                of(Suit.ESPADAS, Rank.SEVEN), of(Suit.COPAS, Rank.KING)));

        newRound(deck, player, dealer, 20).play();

        assertEquals(100, wallet.balance());
    }

    @Test
    void insuranceIsLostWhenDealerDoesNotConfirmNaturalBlackjack() {
        Wallet wallet = new Wallet(100);
        HumanPlayer player = new HumanPlayer("Jogador", wallet,
                new ScriptedDecisionProvider(Action.STAND).withInsurance(10));
        Dealer dealer = new Dealer();
        Deck deck = Deck.orderedDeck(List.of(
                of(Suit.ESPADAS, Rank.TEN), of(Suit.COPAS, Rank.ACE),
                of(Suit.ESPADAS, Rank.SIX), of(Suit.COPAS, Rank.SEVEN)));

        newRound(deck, player, dealer, 20).play();

        assertEquals(70, wallet.balance());
    }
}
