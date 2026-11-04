package com.blackjack.domain.player;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.hand.Hand;

import java.util.Set;

/**
 * Abstrai como a decisao de uma acao (ou do valor de um seguro) e obtida para um
 * jogador, permitindo substituir a origem da decisao (console, regra fixa do dealer,
 * scripts de teste) sem alterar {@link Player}. Satisfaz a inversao de dependencia:
 * {@link Player} depende apenas desta abstracao.
 */
public interface DecisionProvider {

    Action chooseAction(Hand hand, Set<Action> availableActions, GameContext context);

    int chooseInsuranceAmount(int maxAmount);
}
