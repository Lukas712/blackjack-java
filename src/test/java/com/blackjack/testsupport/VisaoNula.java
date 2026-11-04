package com.blackjack.testsupport;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.Resultado;
import com.blackjack.ui.VisaoDoJogo;

import java.util.List;
import java.util.Set;

/**
 * {@link VisaoDoJogo} de teste que nao produz nenhuma saida, usada para manter os
 * testes de integracao de {@code Rodada} silenciosos e focados no estado final do jogo.
 */
public final class VisaoNula implements VisaoDoJogo {

    @Override
    public void exibirBoasVindas() {
    }

    @Override
    public void exibirInicioDaRodada(int numeroDaRodada) {
    }

    @Override
    public void exibirMao(String nomeDoDono, Mao mao, int indiceDaMao, int totalDeMaos) {
    }

    @Override
    public void exibirCartaVisivelDoBanqueiro(Carta cartaVisivel) {
    }

    @Override
    public void exibirMaoFinalDoBanqueiro(Banqueiro banqueiro) {
    }

    @Override
    public void exibirAcoesDisponiveis(Set<Acao> acoes) {
    }

    @Override
    public void exibirMensagem(String mensagem) {
    }

    @Override
    public void exibirResultadoDaMao(String nomeDoDono, int indiceDaMao, int totalDeMaos, Resultado resultado,
                                      int valorDaAposta, int pagamento) {
    }

    @Override
    public void exibirResultadoDoSeguro(String nomeDoDono, Resultado resultado, int valorDoSeguro, int pagamento) {
    }

    @Override
    public void exibirBanca(String nomeDoDono, int saldo) {
    }

    @Override
    public void exibirResumoDoJogo(int rodadasJogadas, List<Jogador> jogadores) {
    }

    @Override
    public void exibirJogadorRemovido(String nomeDoDono) {
    }
}
