package com.blackjack.funcional;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.testsupport.CenarioDeRodada;
import com.blackjack.testsupport.ConsoleDeTeste;
import com.blackjack.testsupport.ResultadoDaRodada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.blackjack.domain.action.Acao.PARAR;
import static com.blackjack.domain.card.Valor.DEZ;
import static com.blackjack.domain.card.Valor.OITO;
import static com.blackjack.domain.card.Valor.SETE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TestSet-Func: validacao dos parametros de entrada (banca inicial e aposta inicial),
 * casos CT01 a CT04 e CT19 a CT24. A entrada invalida e rejeitada pelo console, que repete
 * a pergunta, de modo que nenhuma partida e iniciada com o valor invalido.
 */
class ParametrosDeEntradaFuncionalTest {

    private static final String MENSAGEM_POSITIVO = "Informe um numero inteiro positivo.";
    private static final String MENSAGEM_NAO_INTEIRO = "Entrada invalida. Informe um numero inteiro.";

    @Test
    @DisplayName("CT01 - B=1, A=1 aceitos; distribui duas cartas a cada participante")
    void ct01_bancaEApostaMinimasSaoAceitasEDistribuemDuasCartasParaCadaParticipante() {
        ConsoleDeTeste console = new ConsoleDeTeste("1\n1\n");

        int banca = console.entrada().lerInteiroPositivo("Banca: ");
        int aposta = console.entrada().lerApostaLimitada("Aposta: ", banca);
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(banca, aposta)
                .jogador(DEZ, OITO).banqueiro(DEZ, SETE).acoes(PARAR).executar();

        assertEquals(1, banca);
        assertEquals(1, aposta);
        assertEquals(2, rodada.primeiraMao().cartas().size());
        assertEquals(2, rodada.maoDoBanqueiro().cartas().size());
        assertEquals(DEZ, rodada.visao().cartaVisivelExibida().valor());
        assertEquals(2, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT02 - B=0 e rejeitada")
    void ct02_bancaZeroEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("0\n5\n");

        int banca = console.entrada().lerInteiroPositivo("Banca: ");

        assertTrue(console.saida().contains(MENSAGEM_POSITIVO));
        assertEquals(5, banca);
    }

    @Test
    @DisplayName("CT03 - B=100, A=101 e rejeitada")
    void ct03_apostaMaiorQueABancaEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("101\n100\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);

        assertTrue(console.saida().contains("O valor nao pode exceder 100."));
        assertEquals(100, aposta);
    }

    @Test
    @DisplayName("CT04 - B=100, A=0 e rejeitada")
    void ct04_apostaZeroEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("0\n10\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);

        assertTrue(console.saida().contains(MENSAGEM_POSITIVO));
        assertEquals(10, aposta);
        assertThrows(IllegalArgumentException.class, () -> new Aposta(0));
    }

    @Test
    @DisplayName("CT19 - B=-1 e rejeitada")
    void ct19_bancaNegativaEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("-1\n1\n");

        int banca = console.entrada().lerInteiroPositivo("Banca: ");

        assertTrue(console.saida().contains(MENSAGEM_POSITIVO));
        assertEquals(1, banca);
        assertThrows(IllegalArgumentException.class, () -> new Banca(-1));
    }

    @Test
    @DisplayName("CT20 - B=100,5 (nao inteira) e rejeitada")
    void ct20_bancaNaoInteiraEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("100,5\n100\n");

        int banca = console.entrada().lerInteiroPositivo("Banca: ");

        assertTrue(console.saida().contains(MENSAGEM_NAO_INTEIRO));
        assertEquals(100, banca);
    }

    @Test
    @DisplayName("CT21 - A=-1 e rejeitada")
    void ct21_apostaNegativaEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("-1\n10\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);

        assertTrue(console.saida().contains(MENSAGEM_POSITIVO));
        assertEquals(10, aposta);
        assertThrows(IllegalArgumentException.class, () -> new Aposta(-1));
    }

    @Test
    @DisplayName("CT22 - A=10,5 (nao inteira) e rejeitada")
    void ct22_apostaNaoInteiraEhRejeitada() {
        ConsoleDeTeste console = new ConsoleDeTeste("10,5\n10\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);

        assertTrue(console.saida().contains(MENSAGEM_NAO_INTEIRO));
        assertEquals(10, aposta);
    }

    @Test
    @DisplayName("CT23 - B=100, A=100 e aceita; saldo disponivel apos debitar e 0")
    void ct23_apostaIgualABancaEhAceitaEDeixaSaldoZero() {
        ConsoleDeTeste console = new ConsoleDeTeste("100\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, aposta)
                .jogador(DEZ, SETE).banqueiro(DEZ, OITO).acoes(PARAR).executar();

        assertEquals(100, aposta);
        assertEquals(100, rodada.apostaDaMao(0));
        assertEquals(0, rodada.saldoFinal());
    }

    @Test
    @DisplayName("CT24 - B=100, A=99 e aceita; saldo disponivel apos debitar e 1")
    void ct24_apostaImediatamenteAbaixoDaBancaEhAceitaEDeixaSaldoUm() {
        ConsoleDeTeste console = new ConsoleDeTeste("99\n");

        int aposta = console.entrada().lerApostaLimitada("Aposta: ", 100);
        ResultadoDaRodada rodada = CenarioDeRodada.comBancaEAposta(100, aposta)
                .jogador(DEZ, SETE).banqueiro(DEZ, OITO).acoes(PARAR).executar();

        assertEquals(99, aposta);
        assertEquals(99, rodada.apostaDaMao(0));
        assertEquals(1, rodada.saldoFinal());
    }
}
