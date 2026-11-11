package com.blackjack.estrutural;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.domain.player.ProvedorDeDecisao;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.round.Rodada;
import com.blackjack.testsupport.BaralhoDeTeste;
import com.blackjack.testsupport.ProvedorDeDecisaoRoteirizado;
import com.blackjack.testsupport.VisaoNula;
import com.blackjack.testsupport.VisaoRegistradora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.NOVE;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Estr: ramo defensivo de {@link Rodada#jogar()} para uma acao que nao pertence a
 * jogada da mao (inalcancavel com um provedor que respeite as acoes disponiveis).
 */
class RodadaEstruturalTest {

    @Test
    @DisplayName("Rodada sem jogadores: apenas o banqueiro recebe e joga a mao")
    void rodadaSemJogadores() {
        Banqueiro banqueiro = new Banqueiro();
        VisaoRegistradora visao = new VisaoRegistradora();
        Rodada rodada = new Rodada(BaralhoDeTeste.ordenado(DEZ, SETE), List.of(), banqueiro, Map.of(),
                new AvaliadorDeAcoes(), new ResolvedorDeResultado(), new CalculadoraDePagamento(), visao);

        rodada.jogar();

        assertEquals(17, banqueiro.mao().pontuacaoAtual().valor());
        assertTrue(visao.resultadosDeMaos().isEmpty());
    }

    @Test
    @DisplayName("Dois jogadores: cartas distribuidas em rodizio e cada mao liquidada contra o banqueiro")
    void doisJogadoresNaMesmaRodada() {
        Banca bancaDoPrimeiro = new Banca(100);
        Banca bancaDoSegundo = new Banca(100);
        JogadorHumano primeiro = new JogadorHumano("Jogador 1", bancaDoPrimeiro,
                new ProvedorDeDecisaoRoteirizado(Acao.PARAR));
        JogadorHumano segundo = new JogadorHumano("Jogador 2", bancaDoSegundo,
                new ProvedorDeDecisaoRoteirizado(Acao.PARAR));
        Rodada rodada = new Rodada(BaralhoDeTeste.ordenado(DEZ, DEZ, DEZ, NOVE, SEIS, SETE),
                List.of(primeiro, segundo), new Banqueiro(),
                Map.of(primeiro, new Aposta(10), segundo, new Aposta(10)), new AvaliadorDeAcoes(),
                new ResolvedorDeResultado(), new CalculadoraDePagamento(), new VisaoNula());

        rodada.jogar();

        assertEquals(110, bancaDoPrimeiro.saldo());
        assertEquals(90, bancaDoSegundo.saldo());
    }

    @Test
    @DisplayName("Provedor que devolve SEGURO durante a jogada da mao provoca IllegalStateException")
    void acaoInesperadaDuranteAJogadaDaMao() {
        ProvedorDeDecisao provedorInvalido = new ProvedorDeDecisao() {
            @Override
            public Acao escolherAcao(Mao mao, Set<Acao> acoesDisponiveis, ContextoDoJogo contexto) {
                return Acao.SEGURO;
            }

            @Override
            public int escolherValorDoSeguro(int valorMaximo) {
                return 0;
            }
        };
        JogadorHumano jogador = new JogadorHumano("Jogador", new Banca(100), provedorInvalido);
        Rodada rodada = new Rodada(BaralhoDeTeste.paraRodada(List.of(DEZ, SETE), List.of(DEZ, SETE), List.of()),
                List.of(jogador), new Banqueiro(), Map.of(jogador, new Aposta(10)), new AvaliadorDeAcoes(),
                new ResolvedorDeResultado(), new CalculadoraDePagamento(), new VisaoNula());

        assertThrows(IllegalStateException.class, rodada::jogar);
    }
}
