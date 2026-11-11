package com.blackjack.testsupport;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.Resultado;
import com.blackjack.ui.VisaoDoJogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * {@link VisaoDoJogo} de teste que nao imprime nada, mas registra o que foi exibido
 * para que os testes possam verificar o comportamento observavel do jogo.
 */
public final class VisaoRegistradora implements VisaoDoJogo {

    private final List<Set<Acao>> acoesExibidas = new ArrayList<>();
    private final List<String> mensagens = new ArrayList<>();
    private final List<Resultado> resultadosDeMaos = new ArrayList<>();
    private final List<Resultado> resultadosDeSeguro = new ArrayList<>();
    private final List<Integer> saldosExibidos = new ArrayList<>();
    private final List<String> jogadoresRemovidos = new ArrayList<>();
    private int rodadasIniciadas;
    private Integer rodadasDoResumo;
    private Map<Jogador, Integer> saldosIniciaisDoResumo;
    private Carta cartaVisivelExibida;
    private boolean maoFinalDoBanqueiroExibida;
    private boolean boasVindasExibidas;

    public List<Set<Acao>> acoesExibidas() {
        return acoesExibidas;
    }

    public List<String> mensagens() {
        return mensagens;
    }

    public List<Resultado> resultadosDeMaos() {
        return resultadosDeMaos;
    }

    public List<Resultado> resultadosDeSeguro() {
        return resultadosDeSeguro;
    }

    public List<Integer> saldosExibidos() {
        return saldosExibidos;
    }

    public List<String> jogadoresRemovidos() {
        return jogadoresRemovidos;
    }

    public int rodadasIniciadas() {
        return rodadasIniciadas;
    }

    public int rodadasDoResumo() {
        return rodadasDoResumo;
    }

    public Map<Jogador, Integer> saldosIniciaisDoResumo() {
        return saldosIniciaisDoResumo;
    }

    public Carta cartaVisivelExibida() {
        return cartaVisivelExibida;
    }

    public boolean maoFinalDoBanqueiroExibida() {
        return maoFinalDoBanqueiroExibida;
    }

    public boolean boasVindasExibidas() {
        return boasVindasExibidas;
    }

    @Override
    public void exibirBoasVindas() {
        boasVindasExibidas = true;
    }

    @Override
    public void exibirInicioDaRodada(int numeroDaRodada) {
        rodadasIniciadas++;
    }

    @Override
    public void exibirMao(String nomeDoDono, Mao mao, int indiceDaMao, int totalDeMaos) {
    }

    @Override
    public void exibirCartaVisivelDoBanqueiro(Carta cartaVisivel) {
        cartaVisivelExibida = cartaVisivel;
    }

    @Override
    public void exibirMaoFinalDoBanqueiro(Banqueiro banqueiro) {
        maoFinalDoBanqueiroExibida = true;
    }

    @Override
    public void exibirAcoesDisponiveis(Set<Acao> acoes) {
        acoesExibidas.add(acoes);
    }

    @Override
    public void exibirMensagem(String mensagem) {
        mensagens.add(mensagem);
    }

    @Override
    public void exibirResultadoDaMao(String nomeDoDono, int indiceDaMao, int totalDeMaos, Resultado resultado,
                                      int valorDaAposta, int pagamento) {
        resultadosDeMaos.add(resultado);
    }

    @Override
    public void exibirResultadoDoSeguro(String nomeDoDono, Resultado resultado, int valorDoSeguro, int pagamento) {
        resultadosDeSeguro.add(resultado);
    }

    @Override
    public void exibirBanca(String nomeDoDono, int saldo) {
        saldosExibidos.add(saldo);
    }

    @Override
    public void exibirResumoDoJogo(int rodadasJogadas, List<Jogador> jogadores,
                                   Map<Jogador, Integer> saldosIniciais) {
        rodadasDoResumo = rodadasJogadas;
        saldosIniciaisDoResumo = saldosIniciais;
    }

    @Override
    public void exibirJogadorRemovido(String nomeDoDono) {
        jogadoresRemovidos.add(nomeDoDono);
    }
}
