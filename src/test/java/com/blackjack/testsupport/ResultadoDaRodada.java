package com.blackjack.testsupport;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.JogadorHumano;

import java.util.List;

/**
 * Estado observavel de uma rodada ja executada por {@link CenarioDeRodada}.
 */
public final class ResultadoDaRodada {

    private final Banca banca;
    private final JogadorHumano jogador;
    private final Banqueiro banqueiro;
    private final Baralho baralho;
    private final VisaoRegistradora visao;
    private final String saidaDoConsole;

    ResultadoDaRodada(Banca banca, JogadorHumano jogador, Banqueiro banqueiro, Baralho baralho,
                      VisaoRegistradora visao, String saidaDoConsole) {
        this.banca = banca;
        this.jogador = jogador;
        this.banqueiro = banqueiro;
        this.baralho = baralho;
        this.visao = visao;
        this.saidaDoConsole = saidaDoConsole;
    }

    public int saldoFinal() {
        return banca.saldo();
    }

    public JogadorHumano jogador() {
        return jogador;
    }

    public List<Mao> maosDoJogador() {
        return jogador.maos();
    }

    public Mao primeiraMao() {
        return jogador.maos().get(0);
    }

    public Mao maoDoBanqueiro() {
        return banqueiro.mao();
    }

    public int apostaDaMao(int indice) {
        return jogador.maos().get(indice).aposta().map(Aposta::valor).orElseThrow();
    }

    public int cartasRestantesNoBaralho() {
        return baralho.cartasRestantes();
    }

    public VisaoRegistradora visao() {
        return visao;
    }

    public String saidaDoConsole() {
        return saidaDoConsole;
    }

    public int quantidadeDeRecusas() {
        return saidaDoConsole.split("Acao invalida", -1).length - 1;
    }
}
