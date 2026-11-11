package com.blackjack.domain.hand;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Mao generica de cartas, reutilizada tanto pelos jogadores humanos quanto pelo banqueiro
 * (cuja mao nao possui aposta associada). Concentra o estado das cartas e do status da
 * mao, delegando o calculo de pontuacao ao {@link CalculadoraDePontuacao}. Essa generalizacao
 * permite que qualquer tipo de jogador (humano ou banqueiro) seja integrado a sua propria
 * mao atraves da mesma classe.
 */
public final class Mao {

    private final List<Carta> cartas = new ArrayList<>();
    private Aposta aposta;
    private EstadoMao estado = EstadoMao.ATIVA;
    private boolean dobrada;
    private boolean vindaDeDivisao;

    private Mao(Aposta aposta, boolean vindaDeDivisao) {
        this.aposta = aposta;
        this.vindaDeDivisao = vindaDeDivisao;
    }

    /**
     * Cria a mao inicial de 2 cartas de um jogador apostador.
     */
    public static Mao distribuirInicial(Carta primeira, Carta segunda, Aposta aposta) {
        Mao mao = new Mao(Objects.requireNonNull(aposta, "aposta"), false);
        mao.adicionarCartaInterno(primeira);
        mao.adicionarCartaInterno(segunda);
        return mao;
    }

    /**
     * Cria a mao inicial de 2 cartas do banqueiro, que nao possui aposta associada.
     */
    public static Mao distribuirInicialDoBanqueiro(Carta primeira, Carta segunda) {
        Mao mao = new Mao(null, false);
        mao.adicionarCartaInterno(primeira);
        mao.adicionarCartaInterno(segunda);
        return mao;
    }

    private static Mao criarMaoDividida(Carta carta, Aposta aposta) {
        Mao mao = new Mao(aposta, true);
        mao.adicionarCartaInterno(carta);
        return mao;
    }

    public void adicionarCarta(Carta carta) {
        garantirAtiva();
        adicionarCartaInterno(carta);
    }

    private void adicionarCartaInterno(Carta carta) {
        cartas.add(Objects.requireNonNull(carta, "carta"));
        if (pontuacaoAtual().estourou()) {
            estado = EstadoMao.ESTOURADA;
        }
    }

    public void parar() {
        garantirAtiva();
        estado = EstadoMao.PARADA;
    }

    /**
     * Dobra a aposta da mao, recebe exatamente mais uma carta e encerra a jogada,
     * esteja ela estourada ou nao.
     */
    public void dobrar(Carta carta) {
        if (!ehPrimeiraAcao()) {
            throw new IllegalStateException("Double Down so e permitido como primeira acao da mao.");
        }
        aposta = exigirAposta().dobrada();
        dobrada = true;
        adicionarCartaInterno(carta);
        if (estado == EstadoMao.ATIVA) {
            estado = EstadoMao.PARADA;
        }
    }

    /**
     * Divide a mao em duas: esta mao mantem a primeira carta (e passa a ser marcada
     * como oriunda de divisao, perdendo a elegibilidade a Blackjack Natural), e uma nova
     * mao e retornada contendo a segunda carta e uma aposta de igual valor.
     */
    public Mao dividir() {
        if (!ehPrimeiraAcao()) {
            throw new IllegalStateException("Split so e permitido como primeira acao da mao.");
        }
        if (!podeDividir()) {
            throw new IllegalStateException("As cartas desta mao nao permitem split.");
        }
        Aposta apostaAtual = exigirAposta();
        Carta cartaMovida = cartas.remove(1);
        vindaDeDivisao = true;
        return criarMaoDividida(cartaMovida, new Aposta(apostaAtual.valor()));
    }

    public Pontuacao pontuacaoAtual() {
        return CalculadoraDePontuacao.calcular(cartas);
    }

    /**
     * Blackjack Natural: exatamente as duas primeiras cartas da mao, sendo um As e uma
     * carta de valor 10, desde que a mao nao seja resultante de um split.
     */
    public boolean ehBlackjackNatural() {
        if (vindaDeDivisao || cartas.size() != 2) {
            return false;
        }
        Carta primeira = cartas.get(0);
        Carta segunda = cartas.get(1);
        return (primeira.ehAs() && segunda.valeDez()) || (segunda.ehAs() && primeira.valeDez());
    }

    public boolean ehPrimeiraAcao() {
        return estado == EstadoMao.ATIVA && cartas.size() == 2 && !dobrada;
    }

    public boolean podeDividir() {
        return cartas.size() == 2 && cartas.get(0).valor() == cartas.get(1).valor();
    }

    public boolean estaAtiva() {
        return estado == EstadoMao.ATIVA;
    }

    public boolean estaEstourada() {
        return estado == EstadoMao.ESTOURADA;
    }

    public boolean estaDobrada() {
        return dobrada;
    }

    public boolean veioDeDivisao() {
        return vindaDeDivisao;
    }

    public EstadoMao estado() {
        return estado;
    }

    public Optional<Aposta> aposta() {
        return Optional.ofNullable(aposta);
    }

    public List<Carta> cartas() {
        return Collections.unmodifiableList(new ArrayList<>(cartas));
    }

    private Aposta exigirAposta() {
        if (aposta == null) {
            throw new IllegalStateException("Esta mao nao possui aposta associada.");
        }
        return aposta;
    }

    private void garantirAtiva() {
        if (estado != EstadoMao.ATIVA) {
            throw new IllegalStateException("A mao nao esta mais ativa (estado atual: " + estado + ").");
        }
    }
}
