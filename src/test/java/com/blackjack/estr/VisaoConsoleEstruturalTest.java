package com.blackjack.estr;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: toda a saida de {@link VisaoConsole}, capturada de {@code System.out}.
 */
public class VisaoConsoleEstruturalTest {

    private final PrintStream saidaOriginal = System.out;
    private final ByteArrayOutputStream capturada = new ByteArrayOutputStream();
    private final VisaoConsole visao = new VisaoConsole();

    @Before
    public void redirecionarSaida() throws Exception {
        System.setOut(new PrintStream(capturada, true, "UTF-8"));
    }

    @After
    public void restaurarSaida() {
        System.setOut(saidaOriginal);
    }

    private String saida() {
        return new String(capturada.toByteArray(), StandardCharsets.UTF_8);
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
    public void mensagensGerais() {
        visao.exibirBoasVindas();
        visao.exibirInicioDaRodada(3);
        visao.exibirMensagem("mensagem livre");

        assertTrue(saida().contains("BLACKJACK (VINTE E UM)"));
        assertTrue(saida().contains("Rodada 3"));
        assertTrue(saida().contains("mensagem livre"));
    }

    @Test
    public void maoUnica() {
        visao.exibirMao("Jogador", mao(Valor.DEZ, Valor.SETE), 1, 1);

        assertTrue(saida().contains("Jogador: 10\u2665 7\u2666 | Pontuacao: 17"));
        assertFalse(saida().contains("mao 1/1"));
    }

    @Test
    public void variasMaosEPontuacaoSuave() {
        visao.exibirMao("Jogador", mao(Valor.AS, Valor.SEIS), 2, 3);

        assertTrue(saida().contains("Jogador (mao 2/3)"));
        assertTrue(saida().contains("Pontuacao: 17 (suave)"));
    }

    @Test
    public void cartasDoBanqueiro() {
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
    public void acoesDisponiveis() {
        visao.exibirAcoesDisponiveis(EnumSet.allOf(Acao.class));

        assertTrue(saida().contains(
                "Acoes disponiveis: [H]it, [S]tand, [D]ouble Down, s[P]lit, [I]nsurance"));
    }

    @Test
    public void resultadosDaMao() {
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
    public void resultadosDoSeguro() {
        visao.exibirResultadoDoSeguro("Jogador", Resultado.SEGURO_PAGO, 5, 15);
        visao.exibirResultadoDoSeguro("Jogador", Resultado.SEGURO_PERDIDO, 5, 0);

        assertTrue(saida().contains("Seguro PAGO (2:1) | Valor do seguro: 5 | Saldo do seguro: +10"));
        assertTrue(saida().contains("Seguro PERDIDO | Valor do seguro: 5 | Saldo do seguro: -5"));
    }

    @Test
    public void banca() {
        visao.exibirBanca("Jogador", 80);

        assertTrue(saida().contains("Banca de Jogador: 80 fichas"));
    }

    @Test
    public void resumoDoJogo() {
        Jogador lucrou = jogadorComSaldo("Ana", 150);
        Jogador perdeu = jogadorComSaldo("Beto", 40);
        Jogador igual = jogadorComSaldo("Caio", 100);

        Map<Jogador, Integer> saldosIniciais = new HashMap<>();
        saldosIniciais.put(lucrou, 100);
        saldosIniciais.put(perdeu, 100);
        saldosIniciais.put(igual, 100);

        visao.exibirResumoDoJogo(4, Arrays.asList(lucrou, perdeu, igual), saldosIniciais);

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
    public void jogadorRemovido() {
        visao.exibirJogadorRemovido("Jogador");

        assertTrue(saida().contains("Jogador ficou sem banca suficiente para a aposta minima e saiu da mesa."));
    }
}
