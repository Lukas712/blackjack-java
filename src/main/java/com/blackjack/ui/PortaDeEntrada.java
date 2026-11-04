package com.blackjack.ui;

import com.blackjack.domain.action.Acao;

import java.util.Set;

/**
 * Abstrai a leitura de dados do usuario, permitindo substituir a fonte de entrada
 * (console, scripts de teste) sem alterar a logica de jogo.
 */
public interface PortaDeEntrada {

    int lerInteiroPositivo(String mensagem);

    int lerApostaLimitada(String mensagem, int valorMaximo);

    int lerApostaDaRodada(String mensagem, int apostaAtual, int valorMaximo);

    int lerNumeroDeJogadores(int minimo, int maximo);

    Acao lerAcao(Set<Acao> acoesDisponiveis);

    int lerValorDoSeguro(int valorMaximo);

    boolean lerSimOuNao(String mensagem);
}
