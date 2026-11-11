package com.blackjack.testsupport;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.domain.player.ProvedorDeDecisao;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.round.Rodada;
import com.blackjack.ui.EntradaConsole;
import com.blackjack.ui.ProvedorDeDecisaoConsole;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Descreve de forma declarativa uma rodada de um unico jogador com cartas, acoes e
 * entradas controladas (P07 da especificacao dos testes) e a executa pela {@link Rodada} real.
 * As decisoes do jogador vem de um roteiro de acoes ou, quando {@link #entradaDeConsole} e
 * informada, do mesmo fluxo de console usado pelo jogo (validacao e recusa de comandos incluidas).
 */
public final class CenarioDeRodada {

    private final int bancaInicial;
    private final int apostaInicial;
    private List<Valor> cartasDoJogador = List.of(Valor.DEZ, Valor.SETE);
    private List<Valor> cartasDoBanqueiro = List.of(Valor.DEZ, Valor.SETE);
    private List<Valor> proximasCartas = List.of();
    private Acao[] acoes = new Acao[0];
    private final List<Integer> seguros = new ArrayList<>();
    private String entradaDeConsole;

    private CenarioDeRodada(int bancaInicial, int apostaInicial) {
        this.bancaInicial = bancaInicial;
        this.apostaInicial = apostaInicial;
    }

    public static CenarioDeRodada comBancaEAposta(int bancaInicial, int apostaInicial) {
        return new CenarioDeRodada(bancaInicial, apostaInicial);
    }

    public CenarioDeRodada jogador(Valor primeira, Valor segunda) {
        this.cartasDoJogador = List.of(primeira, segunda);
        return this;
    }

    public CenarioDeRodada banqueiro(Valor visivel, Valor oculta) {
        this.cartasDoBanqueiro = List.of(visivel, oculta);
        return this;
    }

    public CenarioDeRodada proximasCartas(Valor... valores) {
        this.proximasCartas = Arrays.asList(valores);
        return this;
    }

    public CenarioDeRodada acoes(Acao... acoesRoteirizadas) {
        this.acoes = acoesRoteirizadas;
        return this;
    }

    public CenarioDeRodada seguro(int valor) {
        this.seguros.add(valor);
        return this;
    }

    public CenarioDeRodada entradaDeConsole(String texto) {
        this.entradaDeConsole = texto;
        return this;
    }

    public ResultadoDaRodada executar() {
        Banca banca = new Banca(bancaInicial);
        VisaoRegistradora visao = new VisaoRegistradora();
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        JogadorHumano jogador = new JogadorHumano("Jogador", banca, criarProvedor(visao, saida));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = BaralhoDeTeste.paraRodada(cartasDoJogador, cartasDoBanqueiro, proximasCartas);

        new Rodada(baralho, List.of(jogador), banqueiro, Map.of(jogador, new Aposta(apostaInicial)),
                new AvaliadorDeAcoes(), new ResolvedorDeResultado(), new CalculadoraDePagamento(), visao).jogar();

        return new ResultadoDaRodada(banca, jogador, banqueiro, baralho, visao,
                saida.toString(StandardCharsets.UTF_8));
    }

    private ProvedorDeDecisao criarProvedor(VisaoRegistradora visao, ByteArrayOutputStream saida) {
        if (entradaDeConsole != null) {
            EntradaConsole entrada = new EntradaConsole(new Scanner(entradaDeConsole),
                    new PrintStream(saida, true, StandardCharsets.UTF_8));
            return new ProvedorDeDecisaoConsole(visao, entrada);
        }
        ProvedorDeDecisaoRoteirizado provedor = new ProvedorDeDecisaoRoteirizado(acoes);
        seguros.forEach(provedor::comSeguro);
        return provedor;
    }
}
