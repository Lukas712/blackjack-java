package com.blackjack.domain.deck;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Baralho convencional de 52 cartas (4 naipes x 13 valores), compartilhado entre todos
 * os jogadores e o banqueiro de uma mesma rodada. Uma nova instancia e criada e embaralhada
 * a cada rodada.
 */
public final class Baralho {

    private final Deque<Carta> cartas = new ArrayDeque<>();

    private Baralho() {
    }

    /**
     * Cria um baralho completo de 52 cartas e o embaralha usando a fonte de
     * aleatoriedade informada (injetavel para permitir testes deterministicos).
     */
    public static Baralho novoBaralhoEmbaralhado(Random aleatorio) {
        Baralho baralho = new Baralho();
        List<Carta> cartasNovas = todasAsCartas();
        Collections.shuffle(cartasNovas, aleatorio);
        baralho.cartas.addAll(cartasNovas);
        return baralho;
    }

    /**
     * Cria um baralho com as cartas na ordem exata informada (a primeira da lista e
     * a primeira a ser distribuida). Util para cenarios de teste deterministicos.
     */
    public static Baralho baralhoOrdenado(List<Carta> cartas) {
        Baralho baralho = new Baralho();
        baralho.cartas.addAll(cartas);
        return baralho;
    }

    private static List<Carta> todasAsCartas() {
        List<Carta> resultado = new ArrayList<>(52);
        for (Naipe naipe : Naipe.values()) {
            for (Valor valor : Valor.values()) {
                resultado.add(new Carta(naipe, valor));
            }
        }
        return resultado;
    }

    public Carta comprar() {
        Carta carta = cartas.pollFirst();
        if (carta == null) {
            throw new IllegalStateException("O baralho esta vazio.");
        }
        return carta;
    }

    public int cartasRestantes() {
        return cartas.size();
    }
}
