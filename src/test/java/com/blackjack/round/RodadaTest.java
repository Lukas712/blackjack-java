package com.blackjack.round;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.bet.Banca;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.JogadorHumano;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.testsupport.VisaoNula;
import com.blackjack.testsupport.ProvedorDeDecisaoRoteirizado;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testes de integracao de {@link Rodada}, usando um baralho com ordem fixa
 * ({@link Baralho#baralhoOrdenado}) e um {@link ProvedorDeDecisaoRoteirizado} para
 * validar a orquestracao completa de uma rodada: saque/deposito na banca, criacao de
 * maos independentes via split, double down e liquidacao do seguro.
 */
class RodadaTest {

    private static Carta carta(Naipe naipe, Valor valor) {
        return new Carta(naipe, valor);
    }

    private Rodada novaRodada(Baralho baralho, JogadorHumano jogador, Banqueiro banqueiro, int apostaInicial) {
        return new Rodada(baralho, List.of(jogador), banqueiro, Map.of(jogador, new Aposta(apostaInicial)),
                new AvaliadorDeAcoes(), new ResolvedorDeResultado(), new CalculadoraDePagamento(), new VisaoNula());
    }

    @Test
    void blackjackNaturalSoPermiteStandEPagaTresParaDois() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca, new ProvedorDeDecisaoRoteirizado(Acao.PARAR));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.AS), carta(Naipe.COPAS, Valor.DEZ),
                carta(Naipe.ESPADAS, Valor.REI), carta(Naipe.COPAS, Valor.SEIS),
                carta(Naipe.PAUS, Valor.CINCO)));

        novaRodada(baralho, jogador, banqueiro, 10).jogar();

        assertEquals(115, banca.saldo());
    }

    @Test
    void estouroDoBanqueiroFazOJogadorVencerMesmoComPontuacaoBaixa() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca, new ProvedorDeDecisaoRoteirizado(Acao.PARAR));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.QUATRO), carta(Naipe.COPAS, Valor.DEZ),
                carta(Naipe.ESPADAS, Valor.OITO), carta(Naipe.COPAS, Valor.CINCO),
                carta(Naipe.PAUS, Valor.DEZ)));

        novaRodada(baralho, jogador, banqueiro, 10).jogar();

        assertEquals(110, banca.saldo());
    }

    @Test
    void dobrarDuplicaAApostaCompraUmaCartaELiquidaPelaNovaAposta() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca, new ProvedorDeDecisaoRoteirizado(Acao.DOBRAR));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.CINCO), carta(Naipe.COPAS, Valor.DEZ),
                carta(Naipe.ESPADAS, Valor.SEIS), carta(Naipe.COPAS, Valor.SETE),
                carta(Naipe.PAUS, Valor.DEZ)));

        novaRodada(baralho, jogador, banqueiro, 10).jogar();

        assertEquals(120, banca.saldo());
    }

    @Test
    void dividirCriaDuasMaosIndependentesCadaUmaComSuaPropriaApostaELiquidacao() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca,
                new ProvedorDeDecisaoRoteirizado(Acao.DIVIDIR, Acao.PARAR, Acao.PARAR));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.OITO), carta(Naipe.COPAS, Valor.DEZ),
                carta(Naipe.OUROS, Valor.OITO), carta(Naipe.COPAS, Valor.SEIS),
                carta(Naipe.ESPADAS, Valor.DEZ), carta(Naipe.OUROS, Valor.AS),
                carta(Naipe.PAUS, Valor.DOIS)));

        novaRodada(baralho, jogador, banqueiro, 10).jogar();

        assertEquals(110, banca.saldo());
    }

    @Test
    void seguroPagaDoisParaUmQuandoOBanqueiroConfirmaBlackjackNatural() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca,
                new ProvedorDeDecisaoRoteirizado(Acao.PARAR).comSeguro(10));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.DEZ), carta(Naipe.COPAS, Valor.AS),
                carta(Naipe.ESPADAS, Valor.SETE), carta(Naipe.COPAS, Valor.REI)));

        novaRodada(baralho, jogador, banqueiro, 20).jogar();

        assertEquals(100, banca.saldo());
    }

    @Test
    void seguroEhPerdidoQuandoOBanqueiroNaoConfirmaBlackjackNatural() {
        Banca banca = new Banca(100);
        JogadorHumano jogador = new JogadorHumano("Jogador", banca,
                new ProvedorDeDecisaoRoteirizado(Acao.PARAR).comSeguro(10));
        Banqueiro banqueiro = new Banqueiro();
        Baralho baralho = Baralho.baralhoOrdenado(List.of(
                carta(Naipe.ESPADAS, Valor.DEZ), carta(Naipe.COPAS, Valor.AS),
                carta(Naipe.ESPADAS, Valor.SEIS), carta(Naipe.COPAS, Valor.SETE)));

        novaRodada(baralho, jogador, banqueiro, 20).jogar();

        assertEquals(70, banca.saldo());
    }
}
