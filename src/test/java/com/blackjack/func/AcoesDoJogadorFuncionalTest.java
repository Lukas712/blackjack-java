package com.blackjack.func;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.EstadoMao;
import com.blackjack.domain.hand.Mao;
import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.blackjack.domain.action.Acao.DIVIDIR;
import static com.blackjack.domain.action.Acao.DOBRAR;
import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.action.Acao.PEDIR_CARTA;
import static com.blackjack.domain.card.Valor.AS;
import static com.blackjack.domain.card.Valor.CINCO;
import static com.blackjack.domain.card.Valor.DAMA;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.DOIS;
import static com.blackjack.domain.card.Valor.NOVE;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.QUATRO;
import static com.blackjack.domain.card.Valor.SEIS;
import static com.blackjack.domain.card.Valor.SETE;
import static com.blackjack.domain.card.Valor.TRES;
import static com.blackjack.domain.card.Valor.VALETE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * TestSet-Func: disponibilidade e execucao das acoes do jogador (Hit, Stand, Double, Split,
 * Seguro e menu), derivadas da tabela de decisao do grafo de causa-efeito.
 * Tentativas de acoes indisponiveis sao feitas pelo console real do jogo, que deve recusa-las
 * sem alterar a mao, a aposta ou o baralho (P05).
 */
public class AcoesDoJogadorFuncionalTest {

    private static List<Valor> valores(Mao mao) {
        return mao.cartas().stream().map(carta -> carta.valor()).collect(Collectors.toList());
    }

    private static Set<Acao> primeiroMenu(ResultadoDaRodada rodada) {
        return rodada.visao().acoesExibidas().get(0);
    }

    @Test
    public void ct05_dobrarDuplicaAApostaRecebeUmaCartaEEncerraAMao() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 50)
                .jogador(DEZ, CINCO).banqueiro(DEZ, DEZ).proximasCartas(QUATRO).acoes(DOBRAR).executar();

        assertEquals(100, rodada.apostaDaMao(0));
        assertEquals(Arrays.asList(DEZ, CINCO, QUATRO), valores(rodada.primeiraMao()));
        assertEquals(19, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(0, rodada.saldoFinal());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct06_dobrarComSaldoInsuficienteEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(50, 50)
                .jogador(DEZ, CINCO).banqueiro(DEZ, NOVE).entradaDeConsole("D\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DOBRAR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(50, rodada.apostaDaMao(0));
        assertEquals(0, rodada.saldoFinal());
    }

    @Test
    public void ct07_dividirCriaDuasMaosIndependentesComApostasProprias() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).proximasCartas(TRES, DOIS)
                .acoes(DIVIDIR, PARAR, PARAR).executar();

        assertEquals(2, rodada.maosDoJogador().size());
        assertEquals(Arrays.asList(OITO, TRES), valores(rodada.maosDoJogador().get(0)));
        assertEquals(Arrays.asList(OITO, DOIS), valores(rodada.maosDoJogador().get(1)));
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(40, rodada.apostaDaMao(1));
        assertEquals(20, rodada.saldoFinal());
    }

    @Test
    public void ct08_dividirSemParElegivelEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, NOVE).entradaDeConsole("P\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DIVIDIR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct16_comandosInexistentesSaoRejeitadosSemAlterarEstado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SETE).entradaDeConsole("9\nX\nS\n").executar();

        assertEquals(2, rodada.quantidadeDeRecusas());
        assertTrue(primeiroMenu(rodada).containsAll(EnumSet.of(PEDIR_CARTA, PARAR)));
        assertEquals(1, rodada.visao().acoesExibidas().size());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct38_seguroAcimaDoMaximoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).entradaDeConsole("21\n0\nS\n").executar();

        assertTrue(rodada.saidaDoConsole().contains("Informe um valor entre 0 e 20."));
        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct39_seguroNaoEhOferecidoQuandoADealerNaoMostraAs() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(NOVE, OITO).seguro(20).acoes(PARAR).executar();

        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertTrue(rodada.visao().resultadosDeSeguro().isEmpty());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct40_seguroForaDoMomentoInicialEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, CINCO).banqueiro(AS, SETE).proximasCartas(DOIS)
                .entradaDeConsole("0\nH\nI\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertTrue(rodada.visao().acoesExibidas().stream().noneMatch(acoes -> acoes.contains(Acao.SEGURO)));
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct41_seguroComSaldoInsuficienteNaoEhOferecido() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(50, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertEquals(90, rodada.saldoFinal());
    }

    @Test
    public void ct42_dobrarAposAPrimeiraAcaoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, CINCO).banqueiro(DEZ, OITO).proximasCartas(DOIS)
                .entradaDeConsole("H\nD\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertFalse(rodada.visao().acoesExibidas().get(1).contains(DOBRAR));
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct43_dobrarComSaldoIgualAApostaEhPermitido() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(80, 40)
                .jogador(NOVE, CINCO).banqueiro(DEZ, NOVE).proximasCartas(SEIS).acoes(DOBRAR).executar();

        assertEquals(80, rodada.apostaDaMao(0));
        assertEquals(20, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(160, rodada.saldoFinal());
    }


    @Test
    public void ct44_duasFigurasDiferentesPodemSerDivididas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(80, 40)
                .jogador(VALETE, DAMA)
                .banqueiro(DEZ, NOVE)
                .proximasCartas(CINCO, SEIS)
                .acoes(DIVIDIR, PARAR, PARAR)
                .executar();

        assertEquals(2, rodada.maosDoJogador().size());

        assertEquals(
            Arrays.asList(VALETE, CINCO),
            valores(rodada.maosDoJogador().get(0))
        );

        assertEquals(
            Arrays.asList(DAMA, SEIS),
            valores(rodada.maosDoJogador().get(1))
        );

        assertEquals(0, rodada.saldoFinal());
    }


    @Test
    public void ct45_dividirAposAPrimeiraAcaoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).proximasCartas(DOIS)
                .entradaDeConsole("H\nP\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertFalse(rodada.visao().acoesExibidas().get(1).contains(DIVIDIR));
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    public void ct46_dividirComSaldoInsuficienteEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(60, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).entradaDeConsole("P\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DIVIDIR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(20, rodada.saldoFinal());
    }


    @Test
    public void ct52_hitComVinteEUmEhRecusado() {
        ResultadoDaRodada rodada =
            CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, NOVE)
                .banqueiro(DEZ, SETE)
                .proximasCartas(TRES)
                .entradaDeConsole("H\n")
                .executar();

        Mao mao = rodada.primeiraMao();

        assertEquals(EstadoMao.PARADA, mao.estado());
        assertEquals(1, rodada.visao().acoesExibidas().size());

        assertThrows(
            IllegalStateException.class,
            () -> mao.adicionarCarta(
                new Carta(Naipe.PAUS, QUATRO)
            )
        );

        assertEquals(21, mao.pontuacaoAtual().valor());
        assertEquals(3, mao.cartas().size());
        assertEquals(140, rodada.saldoFinal());
    }


    @Test
    public void ct54_hitAposStandEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, SEIS).banqueiro(DEZ, OITO).acoes(PARAR).executar();
        Mao mao = rodada.primeiraMao();

        assertThrows(IllegalStateException.class, () -> mao.adicionarCarta(new Carta(Naipe.PAUS, QUATRO)));

        assertEquals(EstadoMao.PARADA, mao.estado());
        assertEquals(2, mao.cartas().size());
        assertEquals(16, mao.pontuacaoAtual().valor());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    public void ct56_seguroNaoPositivoNaoDebitaFichas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).entradaDeConsole("-1\n0\nS\n").executar();

        assertTrue(rodada.saidaDoConsole().contains("Informe um valor entre 0 e 20."));
        assertTrue(!rodada.jogador().apostaDeSeguro().isPresent());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    public void ct57_seguroNoLimiteMaximoEhAceitoEDebitado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertEquals(20, rodada.jogador().apostaDeSeguro().orElseThrow(NoSuchElementException::new).valor());
        assertEquals(Collections.singletonList(Resultado.SEGURO_PERDIDO), rodada.visao().resultadosDeSeguro());
        assertEquals(120, rodada.saldoFinal());
    }

    @Test
    public void ct58_standNaPrimeiraAcaoEncerraAMaoEPassaAVezAoDealer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SEIS).proximasCartas(DOIS).acoes(PARAR).executar();

        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(18, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    public void ct60_hitListadoEhAceitoEMantemAMaoAtiva() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, OITO).banqueiro(DEZ, SETE).proximasCartas(DOIS)
                .entradaDeConsole("H\nS\n").executar();

        assertEquals(0, rodada.quantidadeDeRecusas());
        assertEquals(19, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(2, rodada.visao().acoesExibidas().size());
        assertTrue(rodada.visao().acoesExibidas().get(1).containsAll(EnumSet.of(PEDIR_CARTA, PARAR)));
        assertEquals(140, rodada.saldoFinal());
    }
}
