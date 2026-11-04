package com.blackjack.domain.hand;

import com.blackjack.domain.bet.Bet;
import com.blackjack.domain.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Mao genérica de cartas, reutilizada tanto pelos jogadores humanos quanto pelo dealer
 * (cuja mao nao possui aposta associada). Concentra o estado das cartas e do status da
 * mao, delegando o calculo de pontuacao ao {@link ScoreCalculator}. Essa generalizacao
 * permite que qualquer tipo de jogador (humano ou dealer) seja integrado a sua propria
 * mao atraves da mesma classe.
 */
public final class Hand {

    private final List<Card> cards = new ArrayList<>();
    private Bet bet;
    private HandStatus status = HandStatus.ACTIVE;
    private boolean doubled;
    private boolean fromSplit;

    private Hand(Bet bet, boolean fromSplit) {
        this.bet = bet;
        this.fromSplit = fromSplit;
    }

    /**
     * Cria a mao inicial de 2 cartas de um jogador apostador.
     */
    public static Hand dealInitial(Card first, Card second, Bet bet) {
        Hand hand = new Hand(Objects.requireNonNull(bet, "bet"), false);
        hand.addCardInternal(first);
        hand.addCardInternal(second);
        return hand;
    }

    /**
     * Cria a mao inicial de 2 cartas do dealer, que nao possui aposta associada.
     */
    public static Hand dealerInitial(Card first, Card second) {
        Hand hand = new Hand(null, false);
        hand.addCardInternal(first);
        hand.addCardInternal(second);
        return hand;
    }

    private static Hand fromSplitCard(Card card, Bet bet) {
        Hand hand = new Hand(bet, true);
        hand.addCardInternal(card);
        return hand;
    }

    public void addCard(Card card) {
        ensureActive();
        addCardInternal(card);
    }

    private void addCardInternal(Card card) {
        cards.add(Objects.requireNonNull(card, "card"));
        if (currentScore().isBust()) {
            status = HandStatus.BUSTED;
        }
    }

    public void stand() {
        ensureActive();
        status = HandStatus.STANDING;
    }

    /**
     * Dobra a aposta da mao, recebe exatamente mais uma carta e encerra a jogada,
     * esteja ela estourada ou nao.
     */
    public void doubleDown(Card card) {
        if (!isFirstAction()) {
            throw new IllegalStateException("Double Down so e permitido como primeira acao da mao.");
        }
        bet = requireBet().doubled();
        doubled = true;
        addCardInternal(card);
        if (status == HandStatus.ACTIVE) {
            status = HandStatus.STANDING;
        }
    }

    /**
     * Divide a mao em duas: esta mao mantem a primeira carta (e passa a ser marcada
     * como oriunda de split, perdendo a elegibilidade a Blackjack Natural), e uma nova
     * mao e retornada contendo a segunda carta e uma aposta de igual valor.
     */
    public Hand split() {
        if (!isFirstAction()) {
            throw new IllegalStateException("Split so e permitido como primeira acao da mao.");
        }
        if (!canSplit()) {
            throw new IllegalStateException("As cartas desta mao nao permitem split.");
        }
        Bet currentBet = requireBet();
        Card movedCard = cards.remove(1);
        fromSplit = true;
        return fromSplitCard(movedCard, new Bet(currentBet.amount()));
    }

    public Score currentScore() {
        return ScoreCalculator.calculate(cards);
    }

    /**
     * Blackjack Natural: exatamente as duas primeiras cartas da mao, sendo um As e uma
     * carta de valor 10, desde que a mao nao seja resultante de um split.
     */
    public boolean isNaturalBlackjack() {
        if (fromSplit || cards.size() != 2) {
            return false;
        }
        Card first = cards.get(0);
        Card second = cards.get(1);
        return (first.isAce() && second.isTenValue()) || (second.isAce() && first.isTenValue());
    }

    public boolean isFirstAction() {
        return status == HandStatus.ACTIVE && cards.size() == 2 && !doubled;
    }

    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).baseValue() == cards.get(1).baseValue();
    }

    public boolean isActive() {
        return status == HandStatus.ACTIVE;
    }

    public boolean isBusted() {
        return status == HandStatus.BUSTED;
    }

    public boolean isDoubled() {
        return doubled;
    }

    public boolean isFromSplit() {
        return fromSplit;
    }

    public HandStatus status() {
        return status;
    }

    public Optional<Bet> bet() {
        return Optional.ofNullable(bet);
    }

    public List<Card> cards() {
        return List.copyOf(cards);
    }

    private Bet requireBet() {
        if (bet == null) {
            throw new IllegalStateException("Esta mao nao possui aposta associada.");
        }
        return bet;
    }

    private void ensureActive() {
        if (status != HandStatus.ACTIVE) {
            throw new IllegalStateException("A mao nao esta mais ativa (status atual: " + status + ").");
        }
    }
}
