package com.blackjack.domain.deck;

/**
 * Fornece um baralho novo a cada rodada, permitindo substituir a origem das cartas
 * (embaralhamento aleatorio, ordem fixa em testes) sem alterar o laco principal do jogo.
 */
@FunctionalInterface
public interface FabricaDeBaralho {

    Baralho novoBaralho();
}
