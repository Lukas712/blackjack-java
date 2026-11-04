package com.blackjack.round;

import com.blackjack.domain.action.Acao;
import com.blackjack.domain.action.AvaliadorDeAcoes;
import com.blackjack.domain.action.ContextoDoJogo;
import com.blackjack.domain.bet.Aposta;
import com.blackjack.domain.card.Carta;
import com.blackjack.domain.deck.Baralho;
import com.blackjack.domain.hand.Mao;
import com.blackjack.domain.player.Banqueiro;
import com.blackjack.domain.player.Jogador;
import com.blackjack.result.Resultado;
import com.blackjack.result.ResolvedorDeResultado;
import com.blackjack.result.CalculadoraDePagamento;
import com.blackjack.ui.VisaoDoJogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Orquestra uma rodada completa de Blackjack: distribuicao inicial, coleta de
 * seguros, turnos dos jogadores (incluindo maos geradas por split), turno do dealer
 * e liquidacao de todas as apostas.
 */
public final class Rodada {

    private final Baralho baralho;
    private final List<Jogador> jogadores;
    private final Banqueiro banqueiro;
    private final Map<Jogador, Aposta> apostasIniciais;
    private final AvaliadorDeAcoes avaliadorDeAcoes;
    private final ResolvedorDeResultado resolvedorDeResultado;
    private final CalculadoraDePagamento calculadoraDePagamento;
    private final VisaoDoJogo visao;

    public Rodada(Baralho baralho, List<Jogador> jogadores, Banqueiro banqueiro, Map<Jogador, Aposta> apostasIniciais,
                  AvaliadorDeAcoes avaliadorDeAcoes, ResolvedorDeResultado resolvedorDeResultado,
                  CalculadoraDePagamento calculadoraDePagamento, VisaoDoJogo visao) {
        this.baralho = baralho;
        this.jogadores = jogadores;
        this.banqueiro = banqueiro;
        this.apostasIniciais = apostasIniciais;
        this.avaliadorDeAcoes = avaliadorDeAcoes;
        this.resolvedorDeResultado = resolvedorDeResultado;
        this.calculadoraDePagamento = calculadoraDePagamento;
        this.visao = visao;
    }

    public void jogar() {
        distribuirMaosIniciais();
        visao.exibirCartaVisivelDoBanqueiro(banqueiro.cartaVisivel());
        jogadores.forEach(this::exibirMaoInicial);
        ofertarSeguroATodos();
        if (!banqueiroConfirmouBlackjackNatural()) {
            jogadores.forEach(this::jogarTodasAsMaosDe);
        }
        jogarTurnoDoBanqueiro();
        liquidarTodosOsJogadores();
    }

    private void distribuirMaosIniciais() {
        List<List<Carta>> lotes = comprarCartasEmRodizio(jogadores.size() + 1);
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador jogador = jogadores.get(i);
            Aposta aposta = apostasIniciais.get(jogador);
            jogador.banca().sacar(aposta.valor());
            List<Carta> cartas = lotes.get(i);
            jogador.iniciarNovaRodada(Mao.distribuirInicial(cartas.get(0), cartas.get(1), aposta));
        }
        List<Carta> cartasDoBanqueiro = lotes.get(jogadores.size());
        banqueiro.iniciarNovaRodada(Mao.distribuirInicialDoBanqueiro(cartasDoBanqueiro.get(0), cartasDoBanqueiro.get(1)));
    }

    private List<List<Carta>> comprarCartasEmRodizio(int participantes) {
        List<List<Carta>> lotes = new ArrayList<>();
        for (int i = 0; i < participantes; i++) {
            lotes.add(new ArrayList<>());
        }
        for (int numeroDaCarta = 0; numeroDaCarta < 2; numeroDaCarta++) {
            for (int i = 0; i < participantes; i++) {
                lotes.get(i).add(baralho.comprar());
            }
        }
        return lotes;
    }

    private void exibirMaoInicial(Jogador jogador) {
        Mao mao = jogador.maos().get(0);
        visao.exibirMao(jogador.nome(), mao, 1, 1);
        if (mao.ehBlackjackNatural()) {
            visao.exibirMensagem(jogador.nome() + " tem Blackjack natural!");
        }
    }

    // A carta oculta so e verificada quando a visivel e um As; com 10/J/Q/K ela permanece oculta.
    private boolean banqueiroConfirmouBlackjackNatural() {
        return banqueiro.cartaVisivel().ehAs() && banqueiro.mao().ehBlackjackNatural();
    }

    private void ofertarSeguroATodos() {
        boolean ofertou = false;
        for (Jogador jogador : jogadores) {
            ofertou |= ofertarSeguro(jogador);
        }
        if (banqueiroConfirmouBlackjackNatural()) {
            visao.exibirMensagem("O banqueiro verificou a carta oculta: TEM Blackjack natural! "
                    + "A rodada termina sem novas jogadas.");
        } else if (ofertou) {
            visao.exibirMensagem("O banqueiro verificou a carta oculta: NAO tem Blackjack natural.");
        }
    }

    private boolean ofertarSeguro(Jogador jogador) {
        if (jogador.maos().get(0).ehBlackjackNatural()) {
            return false;
        }
        int apostaInicial = apostasIniciais.get(jogador).valor();
        if (!avaliadorDeAcoes.seguroDisponivel(banqueiro.cartaVisivel(), jogador.banca(), apostaInicial)) {
            return false;
        }
        int valorMaximo = avaliadorDeAcoes.valorMaximoDoSeguro(apostaInicial);
        visao.exibirMensagem(jogador.nome() + ": a carta visivel do banqueiro e um As. "
                + "Voce pode apostar um seguro de ate " + valorMaximo + ".");
        int valor = jogador.decidirValorDoSeguro(valorMaximo);
        if (valor > 0) {
            jogador.banca().sacar(valor);
            jogador.apostarSeguro(new Aposta(valor));
        }
        return true;
    }

    private void jogarTodasAsMaosDe(Jogador jogador) {
        ContextoDoJogo contexto = new ContextoDoJogo(banqueiro.cartaVisivel());
        int indice = 0;
        while (indice < jogador.maos().size()) {
            jogarMao(jogador, jogador.maos().get(indice), contexto);
            indice++;
        }
    }

    private void jogarMao(Jogador jogador, Mao mao, ContextoDoJogo contexto) {
        while (mao.estaAtiva()) {
            Set<Acao> disponiveis = avaliadorDeAcoes.acoesDaMao(mao, jogador.banca());
            visao.exibirMao(jogador.nome(), mao, jogador.maos().indexOf(mao) + 1, jogador.maos().size());
            Acao acao = jogador.decidir(mao, disponiveis, contexto);
            aplicarAcao(jogador, mao, acao);
            if (!mao.estaAtiva()) {
                exibirMaoEncerrada(jogador, mao, acao);
            }
        }
    }

    private void exibirMaoEncerrada(Jogador jogador, Mao mao, Acao acao) {
        if (acao == Acao.PARAR) {
            return;
        }
        visao.exibirMao(jogador.nome(), mao, jogador.maos().indexOf(mao) + 1, jogador.maos().size());
        if (mao.estaEstourada()) {
            visao.exibirMensagem(jogador.nome() + " estourou!");
        }
    }

    private void jogarMaoDoBanqueiro() {
        ContextoDoJogo contexto = new ContextoDoJogo(banqueiro.cartaVisivel());
        Mao mao = banqueiro.mao();
        while (mao.estaAtiva()) {
            Set<Acao> disponiveis = avaliadorDeAcoes.acoesDaMao(mao, banqueiro.banca());
            aplicarAcao(banqueiro, mao, banqueiro.decidir(mao, disponiveis, contexto));
        }
    }

    private void aplicarAcao(Jogador jogador, Mao mao, Acao acao) {
        switch (acao) {
            case PEDIR_CARTA -> mao.adicionarCarta(baralho.comprar());
            case PARAR -> mao.parar();
            case DOBRAR -> aplicarDobrar(jogador, mao);
            case DIVIDIR -> aplicarDivisao(jogador, mao);
            default -> throw new IllegalStateException("Acao inesperada durante a jogada da mao: " + acao);
        }
    }

    private void aplicarDobrar(Jogador jogador, Mao mao) {
        jogador.banca().sacar(mao.aposta().orElseThrow().valor());
        mao.dobrar(baralho.comprar());
    }

    private void aplicarDivisao(Jogador jogador, Mao mao) {
        jogador.banca().sacar(mao.aposta().orElseThrow().valor());
        Mao maoIrma = mao.dividir();
        mao.adicionarCarta(baralho.comprar());
        maoIrma.adicionarCarta(baralho.comprar());
        jogador.adicionarMao(maoIrma);
    }

    private void jogarTurnoDoBanqueiro() {
        jogarMaoDoBanqueiro();
        visao.exibirMaoFinalDoBanqueiro(banqueiro);
    }

    private void liquidarTodosOsJogadores() {
        jogadores.forEach(this::liquidarJogador);
    }

    private void liquidarJogador(Jogador jogador) {
        liquidarSeguro(jogador);
        liquidarMaos(jogador);
        visao.exibirBanca(jogador.nome(), jogador.banca().saldo());
    }

    private void liquidarSeguro(Jogador jogador) {
        jogador.apostaDeSeguro().ifPresent(seguro -> {
            Resultado resultado = resolvedorDeResultado.resolverSeguro(banqueiro.mao());
            int pagamento = calculadoraDePagamento.pagamentoDoSeguro(seguro, resultado);
            jogador.banca().depositar(pagamento);
            visao.exibirResultadoDoSeguro(jogador.nome(), resultado, seguro.valor(), pagamento);
        });
    }

    private void liquidarMaos(Jogador jogador) {
        List<Mao> maos = jogador.maos();
        for (int i = 0; i < maos.size(); i++) {
            liquidarMao(jogador, maos.get(i), i + 1, maos.size());
        }
    }

    private void liquidarMao(Jogador jogador, Mao mao, int indiceDaMao, int totalDeMaos) {
        Resultado resultado = resolvedorDeResultado.resolverMao(mao, banqueiro.mao());
        Aposta aposta = mao.aposta().orElseThrow();
        int pagamento = calculadoraDePagamento.pagamentoDaMao(aposta, resultado);
        jogador.banca().depositar(pagamento);
        visao.exibirResultadoDaMao(jogador.nome(), indiceDaMao, totalDeMaos, resultado, aposta.valor(), pagamento);
    }
}
