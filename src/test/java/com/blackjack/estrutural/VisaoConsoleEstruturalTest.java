package com.blackjack.estrutural;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.result.Resultado;
import com.blackjack.testsupport.ProvedorDeDecisaoRoteirizado;
import com.blackjack.ui.VisaoConsole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: toda a saida de {@link VisaoConsole}, capturada de {@code System.out}.
 */
class VisaoConsoleEstruturalTest {

    private final PrintStream saidaOriginal = System.out;
    private final ByteArrayOutputStream capturada = new ByteArrayOutputStream();
    private final VisaoConsole visao = new VisaoConsole();

    @BeforeEach
    void redirecionarSaida() {
        System.setOut(new PrintStream(capturada, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurarSaida() {
        System.setOut(saidaOriginal);
    }

    private String saida() {
        return capturada.toString(StandardCharsets.UTF_8);
    }

    private static Carta carta(Valor valor, Naipe naipe) {
        return new Carta(naipe, valor);
    }

    private static Mao mao(Valor primeira, Valor segunda) {
        return Mao.distribuirInicial(carta(primeira, Naipe.COPAS), carta(segunda, Naipe.OUROS), new Aposta(10));
    }

    private static Jogador jogadorComSaldo(String nome, int saldo) {
        return new JogadorHumano(nome, new Banca(saldo), new ProvedorDeDecisaoRoteirizado());
    }

    @Test
    @DisplayName("Boas-vindas, inicio de rodada e mensagem livre")
    void mensagensGerais() {
        visao.exibirBoasVindas();
        visao.exibirInicioDaRodada(3);
        visao.exibirMensagem("mensagem livre");

        assertTrue(saida().contains("BLACKJACK (VINTE E UM)"));
        assertTrue(saida().contains("Rodada 3"));
        assertTrue(saida().contains("mensagem livre"));
    }

    @Test
    @DisplayName("exibirMao: mao unica sem sufixo, pontuacao dura")
    void maoUnica() {
        visao.exibirMao("Jogador", mao(Valor.DEZ, Valor.SETE), 1, 1);

        assertTrue(saida().contains("Jogador: 10\u2665 7\u2666 | Pontuacao: 17"));
        assertFalse(saida().contains("mao 1/1"));
    }

    @Test
    @DisplayName("exibirMao: varias maos mostram indice e pontuacao suave")
    void variasMaosEPontuacaoSuave() {
        visao.exibirMao("Jogador", mao(Valor.AS, Valor.SEIS), 2, 3);

        assertTrue(saida().contains("Jogador (mao 2/3)"));
        assertTrue(saida().contains("Pontuacao: 17 (suave)"));
    }

    @Test
    @DisplayName("Carta visivel do banqueiro e a mao final com e sem estouro")
    void cartasDoBanqueiro() {
        Banqueiro banqueiro = new Banqueiro();
        banqueiro.iniciarNovaRodada(Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ, Naipe.PAUS),
                carta(Valor.SETE, Naipe.ESPADAS)));
        visao.exibirCartaVisivelDoBanqueiro(banqueiro.cartaVisivel());
        visao.exibirMaoFinalDoBanqueiro(banqueiro);
        assertTrue(saida().contains("Banqueiro mostra: 10\u2663 + [carta oculta]"));
        assertTrue(saida().contains("Pontuacao final: 17"));
        assertFalse(saida().contains("ESTOUROU"));

        Mao estourada = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ, Naipe.PAUS), carta(Valor.SEIS, Naipe.PAUS));
        estourada.adicionarCarta(carta(Valor.REI, Naipe.COPAS));
        Banqueiro estourado = new Banqueiro();
        estourado.iniciarNovaRodada(estourada);
        visao.exibirMaoFinalDoBanqueiro(estourado);
        assertTrue(saida().contains("Pontuacao final: 26 (ESTOUROU)"));
    }

    @Test
    @DisplayName("Acoes disponiveis aparecem na ordem do enum com os rotulos de cada acao")
    void acoesDisponiveis() {
        visao.exibirAcoesDisponiveis(EnumSet.allOf(Acao.class));

        assertTrue(saida().contains(
                "Acoes disponiveis: [H]it, [S]tand, [D]ouble Down, s[P]lit, [I]nsurance"));
    }

    @Test
    @DisplayName("Resultado da mao: todos os resultados, sufixo de maos multiplas e sinal do saldo")
    void resultadosDaMao() {
        visao.exibirResultadoDaMao("Jogador", 1, 1, Resultado.VITORIA_BLACKJACK_NATURAL, 10, 25);
        visao.exibirResultadoDaMao("Jogador", 1, 1, Resultado.VITORIA, 10, 20);
        visao.exibirResultadoDaMao("Jogador", 1, 1, Resultado.EMPATE, 10, 10);
        visao.exibirResultadoDaMao("Jogador", 2, 2, Resultado.DERROTA, 10, 0);
        visao.exibirResultadoDaMao("Jogador", 1, 1, Resultado.SEGURO_PAGO, 10, 0);
        visao.exibirResultadoDaMao("Jogador", 1, 1, Resultado.SEGURO_PERDIDO, 10, 0);

        String saida = saida();
        assertTrue(saida.contains("BLACKJACK NATURAL! Vitoria (3:2) | Aposta: 10 | Saldo da jogada: +15"));
        assertTrue(saida.contains("Vitoria (1:1)"));
        assertTrue(saida.contains("Empate (Push)"));
        assertTrue(saida.contains("Jogador (mao 2/2): Derrota | Aposta: 10 | Saldo da jogada: -10"));
        assertTrue(saida.contains("SEGURO_PAGO"));
        assertTrue(saida.contains("SEGURO_PERDIDO"));
    }

    @Test
    @DisplayName("Resultado do seguro: pago (2:1) e perdido")
    void resultadosDoSeguro() {
        visao.exibirResultadoDoSeguro("Jogador", Resultado.SEGURO_PAGO, 5, 15);
        visao.exibirResultadoDoSeguro("Jogador", Resultado.SEGURO_PERDIDO, 5, 0);

        assertTrue(saida().contains("Seguro PAGO (2:1) | Valor do seguro: 5 | Saldo do seguro: +10"));
        assertTrue(saida().contains("Seguro PERDIDO | Valor do seguro: 5 | Saldo do seguro: -5"));
    }

    @Test
    @DisplayName("Banca do jogador")
    void banca() {
        visao.exibirBanca("Jogador", 80);

        assertTrue(saida().contains("Banca de Jogador: 80 fichas"));
    }

    @Test
    @DisplayName("Resumo do jogo: lucro, prejuizo e saldo inalterado")
    void resumoDoJogo() {
        Jogador lucrou = jogadorComSaldo("Ana", 150);
        Jogador perdeu = jogadorComSaldo("Beto", 40);
        Jogador igual = jogadorComSaldo("Caio", 100);

        visao.exibirResumoDoJogo(4, List.of(lucrou, perdeu, igual), Map.of(lucrou, 100, perdeu, 100, igual, 100));

        String saida = saida();
        assertTrue(saida.contains("FIM DE JOGO"));
        assertTrue(saida.contains("Total de rodadas jogadas: 4"));
        assertTrue(saida.contains("Ana lucrou 50 fichas (saldo inicial: 100)."));
        assertTrue(saida.contains("Beto teve prejuizo de 60 fichas (saldo inicial: 100)."));
        assertTrue(saida.contains("Saldo final de Caio: 100 fichas"));
        assertFalse(saida.contains("Caio lucrou"));
        assertFalse(saida.contains("Caio teve prejuizo"));
    }

    @Test
    @DisplayName("Jogador removido da mesa")
    void jogadorRemovido() {
        visao.exibirJogadorRemovido("Jogador");

        assertTrue(saida().contains("Jogador ficou sem banca suficiente para a aposta minima e saiu da mesa."));
    }
}
