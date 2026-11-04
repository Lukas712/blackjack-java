package com.blackjack.round;

import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.ui.VisaoDoJogo;
import com.blackjack.ui.PortaDeEntrada;

import java.util.ArrayList;
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

    private final List<Jogador> todosOsJogadores;
    private final List<Jogador> jogadoresAtivos;
    private final Map<Jogador, Aposta> apostasBase;
    private final Banqueiro banqueiro = new Banqueiro();
    private final AvaliadorDeAcoes avaliadorDeAcoes = new AvaliadorDeAcoes();
    private final ResolvedorDeResultado resolvedorDeResultado = new ResolvedorDeResultado();
    private final CalculadoraDePagamento calculadoraDePagamento = new CalculadoraDePagamento();
    private final VisaoDoJogo visao;
    private final PortaDeEntrada entrada;
    private final Random aleatorio;
    private int rodadasJogadas;

    public JogoDeBlackjack(List<Jogador> jogadores, Map<Jogador, Aposta> apostasBase, VisaoDoJogo visao,
                            PortaDeEntrada entrada, Random aleatorio) {
        this.todosOsJogadores = List.copyOf(jogadores);
        this.jogadoresAtivos = new ArrayList<>(jogadores);
        this.apostasBase = new HashMap<>(apostasBase);
        this.visao = visao;
        this.entrada = entrada;
        this.aleatorio = aleatorio;
    }

    public void iniciar() {
        while (!jogadoresAtivos.isEmpty()) {
            removerJogadoresComSaldoInsuficiente();
            if (jogadoresAtivos.isEmpty()) {
                break;
            }
            jogarUmaRodada();
            if (!entrada.lerSimOuNao("Deseja jogar outra rodada? (S/N) ")) {
                break;
            }
        }
        visao.exibirResumoDoJogo(rodadasJogadas, todosOsJogadores);
    }

    private void removerJogadoresComSaldoInsuficiente() {
        List<Jogador> remover = new ArrayList<>();
        for (Jogador jogador : jogadoresAtivos) {
            int apostaMinima = apostasBase.get(jogador).valor();
            if (!jogador.banca().possuiPeloMenos(apostaMinima)) {
                remover.add(jogador);
                visao.exibirJogadorRemovido(jogador.nome());
            }
        }
        jogadoresAtivos.removeAll(remover);
    }

    private void jogarUmaRodada() {
        rodadasJogadas++;
        visao.exibirInicioDaRodada(rodadasJogadas);
        Baralho baralho = Baralho.novoBaralhoEmbaralhado(aleatorio);
        Rodada rodada = new Rodada(baralho, jogadoresAtivos, banqueiro, apostasBase, avaliadorDeAcoes,
                resolvedorDeResultado, calculadoraDePagamento, visao);
        rodada.jogar();
    }

    public int rodadasJogadas() {
        return rodadasJogadas;
    }
}
