package com.blackjack.testsupport;

import com.blackjack.domain.action.Action;
import com.blackjack.domain.action.GameContext;
import com.blackjack.domain.hand.Hand;
import com.blackjack.domain.player.DecisionProvider;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Set;

/**
 * {@link DecisionProvider} de teste cujas respostas sao previamente programadas,
 * permitindo simular um jogador humano de forma deterministica em testes de
 * integracao de {@code Round}.
 */
public final class ScriptedDecisionProvider implements DecisionProvider {

    private final Deque<Action> actions;
    private final Deque<Integer> insuranceAmounts = new ArrayDeque<>();

    public ScriptedDecisionProvider(Action... scriptedActions) {
        this.actions = new ArrayDeque<>(Arrays.asList(scriptedActions));
    }

    public ScriptedDecisionProvider withInsurance(int amount) {
        insuranceAmounts.add(amount);
        return this;
    }

    @Override
    public Action chooseAction(Hand hand, Set<Action> availableActions, GameContext context) {
        if (actions.isEmpty()) {
            throw new IllegalStateException("Nenhuma acao programada restante no script de teste.");
        }
        Action next = actions.poll();
        if (!availableActions.contains(next)) {
            throw new IllegalStateException(
                    "Acao programada " + next + " nao esta disponivel. Disponiveis: " + availableActions);
        }
        return next;
    }

    @Override
    public int chooseInsuranceAmount(int maxAmount) {
        return insuranceAmounts.isEmpty() ? 0 : insuranceAmounts.poll();
    }
}
