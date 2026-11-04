package com.blackjack.result;

/**
 * Resultado possivel de uma mao (ou de uma aposta de seguro) ao final da rodada.
 */
public enum Resultado {
    VITORIA_BLACKJACK_NATURAL,
    VITORIA,
    EMPATE,
    DERROTA,
    SEGURO_PAGO,
    SEGURO_PERDIDO
}
