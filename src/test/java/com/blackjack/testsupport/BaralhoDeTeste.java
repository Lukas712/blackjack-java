package com.blackjack.testsupport;

import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.deck.Baralho;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Monta baralhos de ordem fixa a partir apenas dos valores desejados, escolhendo
 * naipes distintos para que nunca exista a mesma carta fisica repetida no baralho.
 */
public final class BaralhoDeTeste {

    private BaralhoDeTeste() {
    }

    public static Baralho ordenado(Valor... valores) {
        Map<Valor, Integer> usos = new EnumMap<>(Valor.class);
        List<Carta> cartas = new ArrayList<>();
        for (Valor valor : valores) {
            int uso = usos.merge(valor, 1, Integer::sum) - 1;
            cartas.add(new Carta(Naipe.values()[uso % Naipe.values().length], valor));
        }
        return Baralho.baralhoOrdenado(cartas);
    }

    /**
     * Ordem de distribuicao de uma rodada com um unico jogador: primeira carta do jogador,
     * primeira do banqueiro (visivel), segunda do jogador, segunda do banqueiro (oculta)
     * e, em seguida, as demais cartas na ordem em que forem compradas.
     */
    public static Baralho paraRodada(List<Valor> jogador, List<Valor> banqueiro, List<Valor> proximas) {
        List<Valor> ordem = new ArrayList<>();
        ordem.add(jogador.get(0));
        ordem.add(banqueiro.get(0));
        ordem.add(jogador.get(1));
        ordem.add(banqueiro.get(1));
        ordem.addAll(proximas);
        return ordenado(ordem.toArray(new Valor[0]));
    }
}
