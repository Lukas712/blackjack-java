package com.blackjack.ui;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.Resultado;

import java.util.List;
import java.util.Set;

/**
 * Abstrai toda a exibicao de informacoes do jogo, permitindo trocar a saida (console,
 * testes) sem alterar a logica de dominio (ver RNF05 da especificacao).
 */
public interface VisaoDoJogo {

    void exibirBoasVindas();

    void exibirInicioDaRodada(int numeroDaRodada);

    void exibirMao(String nomeDoDono, Mao mao, int indiceDaMao, int totalDeMaos);

    void exibirCartaVisivelDoBanqueiro(Carta cartaVisivel);

    void exibirMaoFinalDoBanqueiro(Banqueiro banqueiro);

    void exibirAcoesDisponiveis(Set<Acao> acoes);

    void exibirMensagem(String mensagem);

    void exibirResultadoDaMao(String nomeDoDono, int indiceDaMao, int totalDeMaos, Resultado resultado,
                               int valorDaAposta, int pagamento);

    void exibirResultadoDoSeguro(String nomeDoDono, Resultado resultado, int valorDoSeguro, int pagamento);

    void exibirBanca(String nomeDoDono, int saldo);

    void exibirResumoDoJogo(int rodadasJogadas, List<Jogador> jogadores);

    void exibirJogadorRemovido(String nomeDoDono);
}
