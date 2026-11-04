package com.blackjack.ui;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.ProvedorDeDecisao;

import java.util.Objects;
import java.util.Set;

/**
 * Implementacao de {@link ProvedorDeDecisao} que obtem as decisoes de um jogador
 * humano atraves da visao e entrada de console. Mantida no pacote {@code ui} (e nao
 * em {@code domain}) para que a camada de dominio permaneca livre de dependencias
 * de entrada/saida (RNF05).
 */
public final class ProvedorDeDecisaoConsole implements ProvedorDeDecisao {

    private final VisaoDoJogo visao;
    private final PortaDeEntrada entrada;

    public ProvedorDeDecisaoConsole(VisaoDoJogo visao, PortaDeEntrada entrada) {
        this.visao = Objects.requireNonNull(visao, "visao");
        this.entrada = Objects.requireNonNull(entrada, "entrada");
    }

    @Override
    public Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
        visao.exibirAcoesDisponiveis(acoesDisponiveis);
        return entrada.lerAcao(acoesDisponiveis);
    }

    @Override
    public int escolherValorDoSeguro(int valorMaximo) {
        return entrada.lerValorDoSeguro(valorMaximo);
    }
}
