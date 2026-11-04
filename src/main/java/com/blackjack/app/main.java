package com.blackjack.app;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.domain.player.Jogador;
import com.blackjack.round.JogoDeBlackjack;
import com.blackjack.ui.ProvedorDeDecisaoConsole;
import com.blackjack.ui.VisaoConsole;
import com.blackjack.ui.EntradaConsole;
import com.blackjack.ui.VisaoDoJogo;
import com.blackjack.ui.PortaDeEntrada;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicacao: configura de 1 a 3 jogadores a partir da entrada do
 * usuario (banca inicial e aposta inicial de cada um) e inicia o laco principal do jogo.
 */
public final class main {

    private static final int MINIMO_DE_JOGADORES = 1;
    private static final int MAXIMO_DE_JOGADORES = 3;

    private main() {
    }

    public static void main(String[] argumentos) {
        Scanner leitor = new Scanner(System.in);
        VisaoDoJogo visao = new VisaoConsole();
        PortaDeEntrada entrada = new EntradaConsole(leitor, System.out);

        visao.exibirBoasVindas();
        int quantidadeDeJogadores = entrada.lerNumeroDeJogadores(MINIMO_DE_JOGADORES, MAXIMO_DE_JOGADORES);

        List<Jogador> jogadores = new ArrayList<>();
        Map<Jogador, Aposta> apostasBase = new LinkedHashMap<>();
        for (int i = 1; i <= quantidadeDeJogadores; i++) {
            String nome = quantidadeDeJogadores == 1 ? "Jogador" : "Jogador " + i;
            int banca = entrada.lerInteiroPositivo("Banca inicial de " + nome + ": ");
            int aposta = entrada.lerApostaLimitada("Aposta inicial de " + nome + " (<= " + banca + "): ", banca);
            JogadorHumano jogador = new JogadorHumano(nome, new Banca(banca),
                    new ProvedorDeDecisaoConsole(visao, entrada));
            jogadores.add(jogador);
            apostasBase.put(jogador, new Aposta(aposta));
        }

        JogoDeBlackjack jogo = new JogoDeBlackjack(jogadores, apostasBase, visao, entrada, new Random());
        jogo.iniciar();
    }
}
