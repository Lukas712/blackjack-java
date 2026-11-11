package com.blackjack.funcional;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.domain.player.ProvedorDeDecisao;
import com.blackjack.round.JogoDeBlackjack;
import com.blackjack.testsupport.BaralhoDeTeste;
import com.blackjack.testsupport.ConsoleDeTeste;
import com.blackjack.testsupport.ProvedorDeDecisaoRoteirizado;
import com.blackjack.testsupport.VisaoRegistradora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Func: laco principal do jogo - continuidade entre rodadas, encerramento por banca
 * insuficiente ou por escolha do jogador e resumo final (CT17, CT48, CT49 e CT53).
 */
class JogoFuncionalTest {

    private static final class ProvedorQueRegistraSaldo implements ProvedorDeDecisao {

        private final ProvedorDeDecisao delegado;
        private final Banca banca;
        private final List<Integer> saldosNasDecisoes = new ArrayList<>();

        ProvedorQueRegistraSaldo(ProvedorDeDecisao delegado, Banca banca) {
            this.delegado = delegado;
            this.banca = banca;
        }

        @Override
        public Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
            saldosNasDecisoes.add(banca.saldo());
            return delegado.escolherAcao(mao, acoesDisponiveis, contexto);
        }

        @Override
        public int escolherValorDoSeguro(int valorMaximo) {
            return delegado.escolherValorDoSeguro(valorMaximo);
        }
    }

    private static final class Partida {

        private final Banca banca;
        private final ProvedorQueRegistraSaldo provedor;
        private final VisaoRegistradora visao = new VisaoRegistradora();
        private final JogoDeBlackjack jogo;

        Partida(int bancaInicial, int aposta, String entradaDoUsuario, Acao[] acoes, Baralho... baralhos) {
            this.banca = new Banca(bancaInicial);
            this.provedor = new ProvedorQueRegistraSaldo(new ProvedorDeDecisaoRoteirizado(acoes), banca);
            JogadorHumano jogador = new JogadorHumano("Jogador", banca, provedor);
            Iterator<Baralho> fila = List.of(baralhos).iterator();
            this.jogo = new JogoDeBlackjack(List.of(jogador), Map.of(jogador, new Aposta(aposta)), visao,
                    new ConsoleDeTeste(entradaDoUsuario).entrada(), fila::next);
        }

        void jogar() {
            jogo.iniciar();
        }
    }

    private static Baralho rodada(List<Valor> jogador, List<Valor> banqueiro) {
        return BaralhoDeTeste.paraRodada(jogador, banqueiro, List.of());
    }

    @Test
    @DisplayName("CT17 - Saldo 0 ao fim da rodada: nao permite nova aposta e apresenta saldo final 0 e total de rodadas")
    void ct17_bancaZeradaEncerraOJogoSemNovaRodada() {
        Partida partida = new Partida(50, 50, "S\n", new Acao[] {PARAR}, rodada(List.of(DEZ, SEIS), List.of(DEZ, OITO)));

        partida.jogar();

        assertEquals(0, partida.banca.saldo());
        assertEquals(List.of("Jogador"), partida.visao.jogadoresRemovidos());
        assertEquals(1, partida.visao.rodadasIniciadas());
        assertEquals(1, partida.visao.rodadasDoResumo());
    }

    @Test
    @DisplayName("CT48 - Apos vitoria (banca 120) o jogador continua; nova aposta 20 reduz o saldo a 100 e redistribui cartas")
    void ct48_continuarAposVitoriaDebitaNovaApostaERedistribuiCartas() {
        Partida partida = new Partida(100, 20, "S\n\nN\n", new Acao[] {PARAR, PARAR},
                rodada(List.of(DEZ, DEZ), List.of(DEZ, SETE)), rodada(List.of(DEZ, OITO), List.of(DEZ, SETE)));

        partida.jogar();

        assertEquals(List.of(80, 100), partida.provedor.saldosNasDecisoes);
        assertEquals(120, partida.visao.saldosExibidos().get(0));
        assertEquals(2, partida.visao.rodadasIniciadas());
        assertEquals(2, partida.visao.rodadasDoResumo());
        assertEquals(140, partida.banca.saldo());
    }

    @Test
    @DisplayName("CT49 - Sair apos a vitoria: banca final 120, 1 rodada e nenhuma nova carta comprada")
    void ct49_sairAposARodadaEncerraComSaldoETotalDeRodadas() {
        Baralho baralho = rodada(List.of(DEZ, OITO), List.of(DEZ, SETE));
        Partida partida = new Partida(100, 20, "N\n", new Acao[] {PARAR}, baralho);

        partida.jogar();

        assertEquals(120, partida.banca.saldo());
        assertEquals(1, partida.visao.rodadasDoResumo());
        assertEquals(0, baralho.cartasRestantes());
        assertTrue(partida.visao.jogadoresRemovidos().isEmpty());
    }

    @Test
    @DisplayName("CT53 - A=100, [10,6] perde para [10,8]: saldo 0, sem nova rodada e total de rodadas = 1")
    void ct53_derrotaComTodaABancaImpedeNovaRodada() {
        Partida partida = new Partida(100, 100, "S\n", new Acao[] {PARAR}, rodada(List.of(DEZ, SEIS), List.of(DEZ, OITO)));

        partida.jogar();

        assertEquals(0, partida.banca.saldo());
        assertEquals(1, partida.visao.rodadasIniciadas());
        assertEquals(1, partida.visao.rodadasDoResumo());
        assertEquals(List.of("Jogador"), partida.visao.jogadoresRemovidos());
    }
}
