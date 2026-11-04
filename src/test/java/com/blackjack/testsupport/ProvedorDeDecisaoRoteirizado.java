package com.blackjack.testsupport;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.ProvedorDeDecisao;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Set;

/**
 * {@link ProvedorDeDecisao} de teste cujas respostas sao previamente programadas,
 * permitindo simular um jogador humano de forma deterministica em testes de
 * integracao de {@code Rodada}.
 */
public final class ProvedorDeDecisaoRoteirizado implements ProvedorDeDecisao {

    private final Deque<Acao> acoes;
    private final Deque<Integer> valoresDoSeguro = new ArrayDeque<>();

    public ProvedorDeDecisaoRoteirizado(Acao... acoesRoteirizadas) {
        this.acoes = new ArrayDeque<>(Arrays.asList(acoesRoteirizadas));
    }

    public ProvedorDeDecisaoRoteirizado comSeguro(int valor) {
        valoresDoSeguro.add(valor);
        return this;
    }

    @Override
    public Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
        if (acoes.isEmpty()) {
            throw new IllegalStateException("Nenhuma acao programada restante no script de teste.");
        }
        Acao proxima = acoes.poll();
        if (!acoesDisponiveis.contains(proxima)) {
            throw new IllegalStateException(
                    "Acao programada " + proxima + " nao esta disponivel. Disponiveis: " + acoesDisponiveis);
        }
        return proxima;
    }

    @Override
    public int escolherValorDoSeguro(int valorMaximo) {
        return valoresDoSeguro.isEmpty() ? 0 : valoresDoSeguro.poll();
    }
}
