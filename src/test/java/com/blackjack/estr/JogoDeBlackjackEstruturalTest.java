package com.blackjack.estr;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.player.Jogador;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.domain.player.ProvedorDeDecisao;
import com.blackjack.round.JogoDeBlackjack;
import com.blackjack.testsupport.ConsoleDeTeste;
import com.blackjack.testsupport.VisaoRegistradora;
import com.blackjack.ui.ProvedorDeDecisaoConsole;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Estr: {@link JogoDeBlackjack} com baralho aleatorio (construtor com {@link Random}),
 * jogador sem banca ja na primeira iteracao e acesso ao total de rodadas.
 */
public class JogoDeBlackjackEstruturalTest {

    @Test
    public void jogadorSemBancaEhRemovidoAntesDeJogar() {
        VisaoRegistradora visao = new VisaoRegistradora();
        ConsoleDeTeste console = new ConsoleDeTeste("");
        ProvedorDeDecisao provedor = new ProvedorDeDecisaoConsole(visao, console.entrada());
        Jogador jogador = new JogadorHumano("Jogador", new Banca(0), provedor);

        JogoDeBlackjack jogo = new JogoDeBlackjack(Collections.singletonList(jogador),
                Collections.singletonMap(jogador, new Aposta(10)), visao,
                console.entrada(), new Random(1));
        jogo.iniciar();

        assertEquals(Collections.singletonList("Jogador"), visao.jogadoresRemovidos());
        assertEquals(0, jogo.rodadasJogadas());
        assertEquals(0, visao.rodadasIniciadas());
        assertEquals(0, visao.rodadasDoResumo());
    }

    @Test
    public void jogoSemJogadores() {
        VisaoRegistradora visao = new VisaoRegistradora();

        JogoDeBlackjack jogo = new JogoDeBlackjack(Collections.<Jogador>emptyList(),
                Collections.<Jogador, Aposta>emptyMap(), visao, new ConsoleDeTeste("").entrada(),
                new Random(1));
        jogo.iniciar();

        assertEquals(0, jogo.rodadasJogadas());
        assertEquals(0, visao.rodadasDoResumo());
        assertTrue(visao.jogadoresRemovidos().isEmpty());
    }

    @Test
    public void rodadaComBaralhoEmbaralhado() {
        VisaoRegistradora visao = new VisaoRegistradora();
        ConsoleDeTeste console = new ConsoleDeTeste("S\nN\n");
        Banca banca = new Banca(10);
        Jogador jogador = new JogadorHumano("Jogador", banca, new ProvedorDeDecisaoConsole(visao, console.entrada()));

        JogoDeBlackjack jogo = new JogoDeBlackjack(Collections.singletonList(jogador),
                Collections.singletonMap(jogador, new Aposta(10)), visao,
                console.entrada(), new Random(7));
        jogo.iniciar();

        assertEquals(1, jogo.rodadasJogadas());
        assertTrue(Arrays.asList(0, 10, 20, 25).contains(banca.saldo()));
    }
}
