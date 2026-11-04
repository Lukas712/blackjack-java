package com.blackjack.result;

/**
 * Resultado possivel de uma mao (ou de uma aposta de seguro) ao final da rodada.
 */
public enum Outcome {
    NATURAL_BLACKJACK_WIN,
    WIN,
    PUSH,
    LOSS,
    INSURANCE_WIN,
    INSURANCE_LOSS
}
