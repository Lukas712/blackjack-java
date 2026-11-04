package com.blackjack.ui;

import com.blackjack.domain.action.Action;

import java.util.Set;

/**
 * Abstrai a leitura de dados do usuario, permitindo substituir a fonte de entrada
 * (console, scripts de teste) sem alterar a logica de jogo.
 */
public interface InputPort {

    int readPositiveInt(String prompt);

    int readBetNotExceeding(String prompt, int maxValue);

    int readNumberOfPlayers(int min, int max);

    Action readAction(Set<Action> availableActions);

    int readInsuranceAmount(int maxAmount);

    boolean readYesNo(String prompt);
}
