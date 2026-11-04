package com.blackjack.ui;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.Resultado;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementacao de {@link VisaoDoJogo} que imprime as informacoes do jogo no terminal.
 */
public final class VisaoConsole implements VisaoDoJogo {

    @Override
    public void exibirBoasVindas() {
        System.out.println("=".repeat(50));
        System.out.println("           BLACKJACK (VINTE E UM)");
        System.out.println("=".repeat(50));
    }

    @Override
    public void exibirInicioDaRodada(int numeroDaRodada) {
        System.out.println();
        System.out.println("-".repeat(50));
        System.out.println("Rodada " + numeroDaRodada);
        System.out.println("-".repeat(50));
    }

    @Override
    public void exibirMao(String nomeDoDono, Mao mao, int indiceDaMao, int totalDeMaos) {
        System.out.println();
        String cartas = formatarCartas(mao);
        String sufixo = totalDeMaos > 1 ? " (mao " + indiceDaMao + "/" + totalDeMaos + ")" : "";
        var pontuacao = mao.pontuacaoAtual();
        String rotuloDaPontuacao = pontuacao.ehSuave() ? pontuacao.valor() + " (suave)" : String.valueOf(pontuacao.valor());
        System.out.printf("%s%s: %s | Pontuacao: %s%n", nomeDoDono, sufixo, cartas, rotuloDaPontuacao);
    }

    @Override
    public void exibirCartaVisivelDoBanqueiro(Carta cartaVisivel) {
        System.out.println("Banqueiro mostra: " + cartaVisivel.exibir() + " + [carta oculta]");
    }

    @Override
    public void exibirMaoFinalDoBanqueiro(Banqueiro banqueiro) {
        Mao mao = banqueiro.mao();
        System.out.println();
        System.out.printf("Banqueiro revela: %s | Pontuacao final: %d%s%n",
                formatarCartas(mao), mao.pontuacaoAtual().valor(),
                mao.estaEstourada() ? " (ESTOUROU)" : "");
    }

    @Override
    public void exibirAcoesDisponiveis(Set<Acao> acoes) {
        String opcoes = acoes.stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .map(this::descreverAcao)
                .collect(Collectors.joining(", "));
        System.out.println("Acoes disponiveis: " + opcoes);
    }

    @Override
    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    @Override
    public void exibirResultadoDaMao(String nomeDoDono, int indiceDaMao, int totalDeMaos, Resultado resultado,
                                      int valorDaAposta, int pagamento) {
        String sufixo = totalDeMaos > 1 ? " (mao " + indiceDaMao + "/" + totalDeMaos + ")" : "";
        int saldo = pagamento - valorDaAposta;
        System.out.println();
        System.out.printf("%s%s: %s | Aposta: %d | Saldo da jogada: %+d%n",
                nomeDoDono, sufixo, descreverResultado(resultado), valorDaAposta, saldo);
    }

    @Override
    public void exibirResultadoDoSeguro(String nomeDoDono, Resultado resultado, int valorDoSeguro, int pagamento) {
        String descricao = resultado == Resultado.SEGURO_PAGO ? "Seguro PAGO (2:1)" : "Seguro PERDIDO";
        int saldo = pagamento - valorDoSeguro;
        System.out.println();
        System.out.printf("%s: %s | Valor do seguro: %d | Saldo do seguro: %+d%n",
                nomeDoDono, descricao, valorDoSeguro, saldo);
    }

    @Override
    public void exibirBanca(String nomeDoDono, int saldo) {
        System.out.printf("Banca de %s: %d fichas%n", nomeDoDono, saldo);
    }

    @Override
    public void exibirResumoDoJogo(int rodadasJogadas, List<Jogador> jogadores,
                                   Map<Jogador, Integer> saldosIniciais) {
        System.out.println();
        System.out.println("=".repeat(50));
        System.out.println("FIM DE JOGO");
        System.out.println("Total de rodadas jogadas: " + rodadasJogadas);
        for (Jogador jogador : jogadores) {
            int saldoFinal = jogador.banca().saldo();
            System.out.printf("Saldo final de %s: %d fichas%n", jogador.nome(), saldoFinal);
            int variacao = saldoFinal - saldosIniciais.get(jogador);
            if (variacao > 0) {
                System.out.printf("%s lucrou %d fichas (saldo inicial: %d).%n", jogador.nome(), variacao,
                        saldosIniciais.get(jogador));
            } else if (variacao < 0) {
                System.out.printf("%s teve prejuizo de %d fichas (saldo inicial: %d).%n", jogador.nome(), -variacao,
                        saldosIniciais.get(jogador));
            }
        }
        System.out.println("=".repeat(50));
    }

    @Override
    public void exibirJogadorRemovido(String nomeDoDono) {
        System.out.println(nomeDoDono + " ficou sem banca suficiente para a aposta minima e saiu da mesa.");
    }

    private String formatarCartas(Mao mao) {
        return mao.cartas().stream().map(Carta::exibir).collect(Collectors.joining(" "));
    }

    private String descreverResultado(Resultado resultado) {
        return switch (resultado) {
            case VITORIA_BLACKJACK_NATURAL -> "BLACKJACK NATURAL! Vitoria (3:2)";
            case VITORIA -> "Vitoria (1:1)";
            case EMPATE -> "Empate (Push)";
            case DERROTA -> "Derrota";
            case SEGURO_PAGO, SEGURO_PERDIDO -> resultado.name();
        };
    }

    private String descreverAcao(Acao acao) {
        return switch (acao) {
            case PEDIR_CARTA -> "[H]it";
            case PARAR -> "[S]tand";
            case DOBRAR -> "[D]ouble Down";
            case DIVIDIR -> "s[P]lit";
            case SEGURO -> "[I]nsurance";
        };
    }
}
