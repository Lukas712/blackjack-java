package com.blackjack.result;

import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.card.Valor;
import com.blackjack.domain.card.Naipe;
import com.blackjack.domain.hand.Mao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResolvedorDeResultadoTest {

    private static Carta carta(Valor valor) {
        return new Carta(Naipe.COPAS, valor);
    }

    private final ResolvedorDeResultado resolvedor = new ResolvedorDeResultado();

    @Test
    void naturalDoJogadorContraBanqueiroSemNaturalVenceComoBlackjackNatural() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.OITO));

        assertEquals(Resultado.VITORIA_BLACKJACK_NATURAL, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void ambosComBlackjackNaturalResultaEmEmpate() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DAMA), carta(Valor.AS));

        assertEquals(Resultado.EMPATE, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void naturalDoBanqueiroVenceVinteEUmDoJogadorComTresCartas() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.SEIS), new Aposta(10));
        jogador.adicionarCarta(carta(Valor.CINCO));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.AS), carta(Valor.REI));

        assertEquals(Resultado.DERROTA, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void naturalDoJogadorVenceVinteEUmDoBanqueiroComTresCartas() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.AS), carta(Valor.REI), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.SEIS));
        banqueiro.adicionarCarta(carta(Valor.CINCO));

        assertEquals(Resultado.VITORIA_BLACKJACK_NATURAL, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void estouroDoJogadorEhSempreDerrotaMesmoSeOBanqueiroTambemEstourar() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.NOVE), new Aposta(10));
        jogador.adicionarCarta(carta(Valor.CINCO));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.NOVE));
        banqueiro.adicionarCarta(carta(Valor.CINCO));

        assertEquals(Resultado.DERROTA, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void estouroDoBanqueiroEhVitoriaParaUmaMaoAtivaDoJogador() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.QUATRO), carta(Valor.OITO), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.NOVE));
        banqueiro.adicionarCarta(carta(Valor.CINCO));

        assertEquals(Resultado.VITORIA, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void pontuacoesIguaisNaoNaturaisResultamEmEmpate() {
        Mao jogador = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.OITO), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.NOVE), carta(Valor.NOVE));

        assertEquals(Resultado.EMPATE, resolvedor.resolverMao(jogador, banqueiro));
    }

    @Test
    void pontuacaoNaoNaturalMaiorVenceEAMenorPerde() {
        Mao vencedor = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.NOVE), new Aposta(10));
        Mao perdedor = Mao.distribuirInicial(carta(Valor.DEZ), carta(Valor.SEIS), new Aposta(10));
        Mao banqueiro = Mao.distribuirInicialDoBanqueiro(carta(Valor.DEZ), carta(Valor.SETE));

        assertEquals(Resultado.VITORIA, resolvedor.resolverMao(vencedor, banqueiro));
        assertEquals(Resultado.DERROTA, resolvedor.resolverMao(perdedor, banqueiro));
    }

    @Test
    void seguroVenceApenasQuandoOBanqueiroTemBlackjackNatural() {
        Mao banqueiroComNatural = Mao.distribuirInicialDoBanqueiro(carta(Valor.AS), carta(Valor.REI));
        Mao banqueiroSemNatural = Mao.distribuirInicialDoBanqueiro(carta(Valor.AS), carta(Valor.SETE));

        assertEquals(Resultado.SEGURO_PAGO, resolvedor.resolverSeguro(banqueiroComNatural));
        assertEquals(Resultado.SEGURO_PERDIDO, resolvedor.resolverSeguro(banqueiroSemNatural));
    }
}
