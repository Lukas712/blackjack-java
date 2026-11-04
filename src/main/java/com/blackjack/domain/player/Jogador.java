package com.blackjack.domain.player;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.hand.Mao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Jogador generico de Blackjack: possui nome, banca e uma ou mais maos (uma lista,
 * para suportar multiplas maos resultantes de split). A tomada de decisao e delegada
 * a um {@link ProvedorDeDecisao}, que pode representar um humano via console ou uma
 * regra fixa de IA (banqueiro) — ambos substituiveis onde se espera um {@code Jogador}.
 */
public abstract class Jogador {

    private final String nome;
    private final Banca banca;
    private final List<Mao> maos = new ArrayList<>();
    private final ProvedorDeDecisao provedorDeDecisao;
    private Aposta apostaDeSeguro;

    protected Jogador(String nome, Banca banca, ProvedorDeDecisao provedorDeDecisao) {
        this.nome = Objects.requireNonNull(nome, "nome");
        this.banca = Objects.requireNonNull(banca, "banca");
        this.provedorDeDecisao = Objects.requireNonNull(provedorDeDecisao, "provedorDeDecisao");
    }

    public String nome() {
        return nome;
    }

    public Banca banca() {
        return banca;
    }

    public List<Mao> maos() {
        return Collections.unmodifiableList(maos);
    }

    /**
     * Reinicia o estado do jogador para uma nova rodada, com uma unica mao inicial
     * e nenhuma aposta de seguro pendente.
     */
    public void iniciarNovaRodada(Mao maoInicial) {
        maos.clear();
        maos.add(Objects.requireNonNull(maoInicial, "maoInicial"));
        apostaDeSeguro = null;
    }

    public void adicionarMao(Mao mao) {
        maos.add(Objects.requireNonNull(mao, "mao"));
    }

    public Acao decidir(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
        return provedorDeDecisao.escolherAcao(mao, acoesDisponiveis, contexto);
    }

    public int decidirValorDoSeguro(int valorMaximo) {
        return provedorDeDecisao.escolherValorDoSeguro(valorMaximo);
    }

    public void apostarSeguro(Aposta aposta) {
        this.apostaDeSeguro = Objects.requireNonNull(aposta, "aposta");
    }

    public Optional<Aposta> apostaDeSeguro() {
        return Optional.ofNullable(apostaDeSeguro);
    }
}
