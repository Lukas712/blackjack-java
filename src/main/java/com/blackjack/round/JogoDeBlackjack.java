package com.blackjack.round;

import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.deck.FabricaDeBaralho;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.ui.VisaoDoJogo;
import com.blackjack.ui.PortaDeEntrada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Controla o laco principal do jogo: repete rodadas enquanto os jogadores tiverem
 * banca suficiente para a aposta minima e desejarem continuar, encerrando com a
 * exibicao do saldo final e do total de rodadas jogadas.
 */
public final class JogoDeBlackjack {

    private static final int APOSTA_MINIMA = 1;

    private final List<Jogador> todosOsJogadores;
    private final List<Jogador> jogadoresAtivos;
    private final Map<Jogador, Aposta> apostasBase;
    private final Banqueiro banqueiro = new Banqueiro();
    private final AvaliadorDeAcoes avaliadorDeAcoes = new AvaliadorDeAcoes();
    private final ResolvedorDeResultado resolvedorDeResultado = new ResolvedorDeResultado();
    private final CalculadoraDePagamento calculadoraDePagamento = new CalculadoraDePagamento();
    private final VisaoDoJogo visao;
    private final PortaDeEntrada entrada;
    private final FabricaDeBaralho fabricaDeBaralho;
    private final Map<Jogador, Integer> saldosIniciais = new HashMap<>();
    private int rodadasJogadas;

    public JogoDeBlackjack(List<Jogador> jogadores, Map<Jogador, Aposta> apostasBase, VisaoDoJogo visao,
                            PortaDeEntrada entrada, Random aleatorio) {
        this(jogadores, apostasBase, visao, entrada, () -> Baralho.novoBaralhoEmbaralhado(aleatorio));
    }

    public JogoDeBlackjack(List<Jogador> jogadores, Map<Jogador, Aposta> apostasBase, VisaoDoJogo visao,
                            PortaDeEntrada entrada, FabricaDeBaralho fabricaDeBaralho) {
        this.todosOsJogadores = Collections.unmodifiableList(new ArrayList<>(jogadores));
        this.jogadoresAtivos = new ArrayList<>(jogadores);
        this.apostasBase = new HashMap<>(apostasBase);
        jogadores.forEach(jogador -> saldosIniciais.put(jogador, jogador.banca().saldo()));
        this.visao = visao;
        this.entrada = entrada;
        this.fabricaDeBaralho = fabricaDeBaralho;
    }

    public void iniciar() {
        while (!jogadoresAtivos.isEmpty()) {
            removerJogadoresComSaldoInsuficiente();
            if (jogadoresAtivos.isEmpty()) {
                break;
            }
            atualizarApostas();
            jogarUmaRodada();
            visao.exibirMensagem("");
            if (!entrada.lerSimOuNao("Deseja jogar outra rodada? (S/N) ")) {
                break;
            }
        }
        visao.exibirResumoDoJogo(rodadasJogadas, todosOsJogadores, saldosIniciais);
    }

    private void removerJogadoresComSaldoInsuficiente() {
        List<Jogador> remover = new ArrayList<>();
        for (Jogador jogador : jogadoresAtivos) {
            if (!jogador.banca().possuiPeloMenos(APOSTA_MINIMA)) {
                remover.add(jogador);
                visao.exibirJogadorRemovido(jogador.nome());
            }
        }
        jogadoresAtivos.removeAll(remover);
    }

    private void atualizarApostas() {
        if (rodadasJogadas == 0) {
            return;
        }
        for (Jogador jogador : jogadoresAtivos) {
            int saldo = jogador.banca().saldo();
            int atual = Math.min(apostasBase.get(jogador).valor(), saldo);
            int nova = entrada.lerApostaDaRodada("Aposta de " + jogador.nome() + " (1 a " + saldo
                    + ", Enter mantem " + atual + "): ", atual, saldo);
            apostasBase.put(jogador, new Aposta(nova));
        }
    }

    private void jogarUmaRodada() {
        rodadasJogadas++;
        visao.exibirInicioDaRodada(rodadasJogadas);
        Baralho baralho = fabricaDeBaralho.novoBaralho();
        Rodada rodada = new Rodada(baralho, jogadoresAtivos, banqueiro, apostasBase, avaliadorDeAcoes,
                resolvedorDeResultado, calculadoraDePagamento, visao);
        rodada.jogar();
    }

    public int rodadasJogadas() {
        return rodadasJogadas;
    }
}
