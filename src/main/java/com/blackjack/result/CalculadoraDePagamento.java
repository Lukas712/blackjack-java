package com.blackjack.result;

import com.blackjack.domain.bet.Aposta;

/**
 * Servico sem estado que calcula o valor total a ser creditado na banca do jogador
 * (aposta devolvida + eventual ganho) a partir do {@link Resultado} de uma mao ou de
 * uma aposta de seguro. Para derrota/seguro perdido, o valor ja foi debitado da
 * banca no momento em que a aposta foi feita, portanto o credito e zero.
 */
public final class CalculadoraDePagamento {

    public int pagamentoDaMao(Aposta aposta, Resultado resultado) {
        return switch (resultado) {
            case VITORIA_BLACKJACK_NATURAL -> aposta.valor() + (aposta.valor() * 3) / 2;
            case VITORIA -> aposta.valor() * 2;
            case EMPATE -> aposta.valor();
            case DERROTA -> 0;
            case SEGURO_PAGO, SEGURO_PERDIDO ->
                    throw new IllegalArgumentException("Resultado de seguro nao se aplica a uma mao: " + resultado);
        };
    }

    public int pagamentoDoSeguro(Aposta apostaDeSeguro, Resultado resultado) {
        return switch (resultado) {
            case SEGURO_PAGO -> apostaDeSeguro.valor() * 3;
            case SEGURO_PERDIDO -> 0;
            default -> throw new IllegalArgumentException("Resultado de mao nao se aplica a um seguro: " + resultado);
        };
    }
}
