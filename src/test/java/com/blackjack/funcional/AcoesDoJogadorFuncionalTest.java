package com.blackjack.funcional;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.hand.EstadoMao;
import com.blackjack.domain.hand.Mao;
import com.blackjack.result.Resultado;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Func: disponibilidade e execucao das acoes do jogador (Hit, Stand, Double, Split,
 * Seguro e menu), derivadas da tabela de decisao do grafo de causa-efeito.
 * Tentativas de acoes indisponiveis sao feitas pelo console real do jogo, que deve recusa-las
 * sem alterar a mao, a aposta ou o baralho (P05).
 */
class AcoesDoJogadorFuncionalTest {

    private static List<Valor> valores(Mao mao) {
        return mao.cartas().stream().map(carta -> carta.valor()).collect(Collectors.toList());
    }

    private static Set<Acao> primeiroMenu(ResultadoDaRodada rodada) {
        return rodada.visao().acoesExibidas().get(0);
    }

    @Test
    @DisplayName("CT05 - Double [10,5]+[4]: aposta total 100, saldo 0, uma unica carta e mao encerrada")
    void ct05_dobrarDuplicaAApostaRecebeUmaCartaEEncerraAMao() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 50)
                .jogador(DEZ, CINCO).banqueiro(DEZ, DEZ).proximasCartas(QUATRO).acoes(DOBRAR).executar();

        assertEquals(100, rodada.apostaDaMao(0));
        assertEquals(List.of(DEZ, CINCO, QUATRO), valores(rodada.primeiraMao()));
        assertEquals(19, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(0, rodada.saldoFinal());
        assertEquals(0, rodada.cartasRestantesNoBaralho());
    }

    @Test
    @DisplayName("CT06 - Saldo 0 apos a aposta: Double nao e oferecido e a tentativa e recusada")
    void ct06_dobrarComSaldoInsuficienteEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(50, 50)
                .jogador(DEZ, CINCO).banqueiro(DEZ, NOVE).entradaDeConsole("D\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DOBRAR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(50, rodada.apostaDaMao(0));
        assertEquals(0, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT07 - Split [8,8] com saldo 60: duas maos independentes, desconta 40, saldo fica 20")
    void ct07_dividirCriaDuasMaosIndependentesComApostasProprias() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).proximasCartas(TRES, DOIS)
                .acoes(DIVIDIR, PARAR, PARAR).executar();

        assertEquals(2, rodada.maosDoJogador().size());
        assertEquals(List.of(OITO, TRES), valores(rodada.maosDoJogador().get(0)));
        assertEquals(List.of(OITO, DOIS), valores(rodada.maosDoJogador().get(1)));
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(40, rodada.apostaDaMao(1));
        assertEquals(20, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT08 - [10,8] sem par elegivel: Split indisponivel, sem segunda mao nem debito extra")
    void ct08_dividirSemParElegivelEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, NOVE).entradaDeConsole("P\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DIVIDIR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT16 - Comandos '9' e 'X' sao rejeitados; opcoes listadas continuam validas; mao e banca intactas")
    void ct16_comandosInexistentesSaoRejeitadosSemAlterarEstado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SETE).entradaDeConsole("9\nX\nS\n").executar();

        assertEquals(2, rodada.quantidadeDeRecusas());
        assertTrue(primeiroMenu(rodada).containsAll(Set.of(PEDIR_CARTA, PARAR)));
        assertEquals(1, rodada.visao().acoesExibidas().size());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT38 - Seguro S=21 > A/2=20 e recusado; saldo e aposta principal mantidos")
    void ct38_seguroAcimaDoMaximoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).entradaDeConsole("21\n0\nS\n").executar();

        assertTrue(rodada.saidaDoConsole().contains("Informe um valor entre 0 e 20."));
        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT39 - Carta visivel do dealer e 9: Seguro nao e oferecido e a solicitacao nao tem efeito")
    void ct39_seguroNaoEhOferecidoQuandoADealerNaoMostraAs() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(NOVE, OITO).seguro(20).acoes(PARAR).executar();

        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertTrue(rodada.visao().resultadosDeSeguro().isEmpty());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT40 - Seguro apos um Hit nao pode ser adquirido; sem debito de seguro")
    void ct40_seguroForaDoMomentoInicialEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, CINCO).banqueiro(AS, SETE).proximasCartas(DOIS)
                .entradaDeConsole("0\nH\nI\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertTrue(rodada.visao().acoesExibidas().stream().noneMatch(acoes -> acoes.contains(Acao.SEGURO)));
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT41 - Saldo 10 < A/2=20: Seguro indisponivel")
    void ct41_seguroComSaldoInsuficienteNaoEhOferecido() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(50, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertEquals(90, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT42 - Double apos Hit nao e oferecido; nao ha aposta adicional")
    void ct42_dobrarAposAPrimeiraAcaoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, CINCO).banqueiro(DEZ, OITO).proximasCartas(DOIS)
                .entradaDeConsole("H\nD\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertFalse(rodada.visao().acoesExibidas().get(1).contains(DOBRAR));
        assertEquals(40, rodada.apostaDaMao(0));
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT43 - Double na fronteira de saldo (=A): aposta total 80, uma carta gera 20 e encerra")
    void ct43_dobrarComSaldoIgualAApostaEhPermitido() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(80, 40)
                .jogador(NOVE, CINCO).banqueiro(DEZ, NOVE).proximasCartas(SEIS).acoes(DOBRAR).executar();

        assertEquals(80, rodada.apostaDaMao(0));
        assertEquals(20, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(160, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT44 - Split de duas figuras iguais [J,J] com saldo 40: debita 40 e cria duas maos")
    void ct44_duasFigurasIguaisPodemSerDivididas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(80, 40)
                .jogador(VALETE, VALETE).banqueiro(DEZ, NOVE).proximasCartas(CINCO, SEIS)
                .acoes(DIVIDIR, PARAR, PARAR).executar();

        assertEquals(2, rodada.maosDoJogador().size());
        assertEquals(List.of(VALETE, CINCO), valores(rodada.maosDoJogador().get(0)));
        assertEquals(List.of(VALETE, SEIS), valores(rodada.maosDoJogador().get(1)));
        assertEquals(0, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT45 - Split apos Hit nao e oferecido, ainda que as cartas iniciais fossem iguais")
    void ct45_dividirAposAPrimeiraAcaoEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).proximasCartas(DOIS)
                .entradaDeConsole("H\nP\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertFalse(rodada.visao().acoesExibidas().get(1).contains(DIVIDIR));
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(60, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT46 - Saldo 20 < A=40: Split indisponivel")
    void ct46_dividirComSaldoInsuficienteEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(60, 40)
                .jogador(OITO, OITO).banqueiro(DEZ, NOVE).entradaDeConsole("P\nS\n").executar();

        assertFalse(primeiroMenu(rodada).contains(DIVIDIR));
        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(1, rodada.maosDoJogador().size());
        assertEquals(20, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT52 - Mao com 21 [9,9,3]: Hit nao fica disponivel; mao e pontos inalterados")
    void ct52_hitComVinteEUmEhRecusado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, NOVE).banqueiro(DEZ, SETE).proximasCartas(TRES)
                .entradaDeConsole("H\nH\nS\n").executar();

        assertEquals(1, rodada.quantidadeDeRecusas());
        assertEquals(Set.of(PARAR), rodada.visao().acoesExibidas().get(1));
        assertEquals(21, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.primeiraMao().cartas().size());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT54 - Apos Stand, Hit nao distribui carta nem altera o estado da mao")
    void ct54_hitAposStandEhRecusado() {
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
    @DisplayName("CT56 - Seguro nao positivo: -1 e rejeitado e 0 equivale a recusar; nenhuma ficha debitada")
    void ct56_seguroNaoPositivoNaoDebitaFichas() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).entradaDeConsole("-1\n0\nS\n").executar();

        assertTrue(rodada.saidaDoConsole().contains("Informe um valor entre 0 e 20."));
        assertTrue(rodada.jogador().apostaDeSeguro().isEmpty());
        assertEquals(140, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT57 - Seguro maximo S=20 com saldo 60: debita 20")
    void ct57_seguroNoLimiteMaximoEhAceitoEDebitado() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, NOVE).banqueiro(AS, SETE).seguro(20).acoes(PARAR).executar();

        assertEquals(20, rodada.jogador().apostaDeSeguro().orElseThrow().valor());
        assertEquals(List.of(Resultado.SEGURO_PERDIDO), rodada.visao().resultadosDeSeguro());
        assertEquals(120, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT58 - Stand na primeira acao: mao encerra com 18 sem novas cartas; dealer aplica a propria regra")
    void ct58_standNaPrimeiraAcaoEncerraAMaoEPassaAVezAoDealer() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(DEZ, OITO).banqueiro(DEZ, SEIS).proximasCartas(DOIS).acoes(PARAR).executar();

        assertEquals(EstadoMao.PARADA, rodada.primeiraMao().estado());
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(18, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(3, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(100, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT60 - Hit por comando listado e aceito: [9,8]+[2]=19 e a mao permanece ativa")
    void ct60_hitListadoEhAceitoEMantemAMaoAtiva() {
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, 40)
                .jogador(NOVE, OITO).banqueiro(DEZ, SETE).proximasCartas(DOIS)
                .entradaDeConsole("H\nS\n").executar();

        assertEquals(0, rodada.quantidadeDeRecusas());
        assertEquals(19, rodada.primeiraMao().pontuacaoAtual().valor());
        assertEquals(2, rodada.visao().acoesExibidas().size());
        assertTrue(rodada.visao().acoesExibidas().get(1).containsAll(Set.of(PEDIR_CARTA, PARAR)));
        assertEquals(140, rodada.saldoFinal());
    }
}
