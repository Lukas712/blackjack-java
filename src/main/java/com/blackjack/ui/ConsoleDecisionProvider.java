package com.blackjack.ui;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.DecisionProvider;

import java.util.Objects;
import java.util.Set;

/**
 * Implementacao de {@link DecisionProvider} que obtem as decisoes de um jogador
 * humano atraves da visao e entrada de console. Mantida no pacote {@code ui} (e nao
 * em {@code domain}) para que a camada de dominio permaneca livre de dependencias
 * de entrada/saida (RNF05).
 */
public final class ConsoleDecisionProvider implements DecisionProvider {

    private final GameView view;
    private final InputPort input;

    public ConsoleDecisionProvider(GameView view, InputPort input) {
        this.view = Objects.requireNonNull(view, "view");
        this.input = Objects.requireNonNull(input, "input");
    }

    @Override
    public Action chooseAction(Hand hand, Set<Action> availableActions, GameContext context) {
        view.showAvailableActions(availableActions);
        return input.readAction(availableActions);
    }

    @Override
    public int chooseInsuranceAmount(int maxAmount) {
        return input.readInsuranceAmount(maxAmount);
    }
}
