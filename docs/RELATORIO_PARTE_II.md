# Relatorio de Execucao - Parte II (DCC168 - Teste de Software)

Grupo: Lukas Freitas, Gabriel Paes, Pedro Garbois.

> Rascunho gerado a partir da execucao real dos testes. A secao 6 (analise pessoal e TDD) e uma
> proposta de texto: cada integrante deve revisa-la com a sua propria experiencia.

## 1. Como reproduzir

```
mvn test                                           # roda os 195 testes e gera target/site/jacoco/index.html (JaCoCo, motor do EclEmma)
rm -f target/jacoco.exec && mvn test -Dtest='com.blackjack.funcional.*Test'   # somente TestSet-Func
```

O `jacoco.exec` acumula execucoes anteriores; apague-o antes de medir um subconjunto.

No Eclipse: botao direito no projeto > *Coverage As > JUnit Test* (EclEmma) e *Run As > JUnit Test with Baduino*
(fluxo de dados). Exporte os relatorios (*Export Session*) para anexar na entrega.

## 2. Organizacao dos testes

| Pacote de teste | Conteudo |
|---|---|
| `com.blackjack.funcional` | **TestSet-Func**: CT01-CT60 da Parte I, um `@Test` por caso, com `@DisplayName("CTxx ...")` |
| `com.blackjack.estrutural` | **TestSet-Estr**: 75 testes adicionais para cobrir fluxo de controle e de dados |
| `com.blackjack.testsupport` | Infraestrutura (`CenarioDeRodada`, `BaralhoDeTeste`, `VisaoRegistradora`, `ConsoleDeTeste`, `ResultadoDaRodada`) |
| demais pacotes | 60 testes unitarios ja existentes no repositorio |

Distribuicao do TestSet-Func (60 testes): `ParametrosDeEntradaFuncionalTest` (10), `PontuacaoFuncionalTest` (6),
`AcoesDoJogadorFuncionalTest` (20), `BanqueiroFuncionalTest` (7), `PagamentosFuncionalTest` (13),
`JogoFuncionalTest` (4).

Mudanca de producao: `FabricaDeBaralho` (interface funcional) injetada em `JogoDeBlackjack`
(Dependency Inversion). O construtor com `Random` foi mantido e delega para ela, entao o comportamento do jogo
nao mudou; os testes fornecem baralhos de ordem conhecida (premissa P07 da Parte I).

## 3. II-A - Cobertura do TestSet-Func (60 testes, todos passando)

| Metrica (JaCoCo/EclEmma) | Cobertos / Total | % |
|---|---|---|
| Linhas | 474 / 626 | 75,7 |
| Branches | 189 / 261 | 72,4 |
| Metodos | 116 / 148 | 78,4 |
| Instrucoes | 2115 / 2909 | 72,7 |
| Pares def-uso (BA-DUA, nucleo do Baduino) | 515 / 721 | 71,4 |

Lacunas do TestSet-Func: `main`, `VisaoConsole` (so impressao), `EntradaConsole` (varias validacoes e apelidos de
comando), excecoes defensivas de `Banca`, `Mao`, `Banqueiro`, `Baralho` e o construtor de `JogoDeBlackjack` com `Random`.

## 4. II-B - Cobertura apos o TestSet-Estr (195 testes no total)

| Metrica | Cobertos / Total | % |
|---|---|---|
| Linhas | 626 / 626 | **100** |
| Branches | 260 / 261 | 99,6 (1 inviavel) |
| Metodos | 148 / 148 | **100** |
| Pares def-uso (BA-DUA) | 699 / 721 | 96,9 (100% dos 699 viaveis) |

Casos estruturais adicionados (`com.blackjack.estrutural`): `EntradaConsoleEstruturalTest` (todos os ramos de
validacao/repeticao e apelidos de comando), `VisaoConsoleEstruturalTest` (toda a saida, capturando `System.out`),
`MainEstruturalTest` (ponto de entrada com 1 e 2 jogadores), `MaoEstruturalTest`, `BancaEstruturalTest`,
`CartaEstruturalTest`, `BanqueiroEstruturalTest`, `PontuacaoEstruturalTest` (mao vazia e As + tres figuras),
`RodadaEstruturalTest` (rodada sem jogadores, dois jogadores, acao inesperada), `JogoDeBlackjackEstruturalTest`.

### 4.1 Itens inviaveis (nenhum teste consegue cobri-los)

Fluxo de controle (1 branch):
- `Mao.ehPrimeiraAcao()`: a condicao `!dobrada` nunca e falsa quando `estado == ATIVA && cartas == 2`, pois
  `dobrar` sempre resulta em 3 cartas. A condicao e redundante (candidata a remocao em refatoracao).

Fluxo de dados (22 pares def-uso):
- 6 pares: ramo `default` implicito dos `switch` sobre enum (`VisaoConsole.descreverResultado`,
  `VisaoConsole.descreverAcao`, `CalculadoraDePagamento.pagamentoDaMao`); nenhum valor do enum o alcanca.
- 7 pares em `CalculadoraDePontuacao.calcular`: os valores iniciais `total = 0` e `asesComoOnze = 0` chegam a usos
  que exigem `total > 21` ou `asesComoOnze > 0` sem nenhuma atualizacao no caminho.
- 3 pares em `Rodada.comprarCartasEmRodizio` (lacos com `participantes >= 1` e constante `2`),
  1 em `Rodada.ofertarSeguroATodos` (`ofertou` inicial `false` nunca satisfaz `else if (ofertou)`),
  1 em `Rodada.jogarTodasAsMaosDe` e 1 em `Rodada.liquidarMaos` (todo jogador tem ao menos uma mao),
  1 em `main` (`lerNumeroDeJogadores` garante ao menos 1 jogador) e 2 em `Mao.ehPrimeiraAcao` (mesma causa do branch acima).

Observacao sobre o Baduino: ele usa o mesmo nucleo do BA-DUA. Como o BA-DUA 0.8.0 nao instrumenta classes
Java 17 nem roda como agente na JDK 25, a medicao acima foi feita por instrumentacao offline de uma copia das classes
com a versao do bytecode reescrita para 52. Os numeros do Baduino no Eclipse devem ser equivalentes, mas **gere e
anexe o relatorio proprio do Baduino** (o enunciado exige o relatorio da ferramenta).

## 5. Defeitos e divergencias encontrados (II-A)

1. **CT44 / V24 / premissa P06 (Parte I) - divergencia na especificacao de teste, nao no codigo.** A Parte I
   afirma que duas figuras *diferentes* (ex.: [J,Q]) permitem Split. A regra correta do jogo e: **Split exige duas
   cartas iguais** ([K,K], [J,J], [9,9], [2,2]...). `Mao.podeDividir()` ja implementa isso, entao o CT44 foi
   executado com [J,J]. Recomenda-se corrigir o texto de P06/CT44 no relatorio da Parte I. O caso [J,Q] nao
   divisivel ficou registrado em `MaoEstruturalTest`.
2. **21 mantem a mao ativa.** Ao atingir 21 a mao continua `ATIVA` com apenas Stand disponivel (Hit removido).
   A Parte I diz que a mao "encerra"; os testes que chegam a 21 roteirizam o `PARAR` seguinte. O resultado de jogo
   e o mesmo.
3. **Condicao redundante `!dobrada`** em `Mao.ehPrimeiraAcao()` (secao 4.1), achado da analise estrutural.

Nenhum defeito em pagamento, pontuacao, seguro, Double ou comportamento do banqueiro foi encontrado: os 59
demais casos passaram na primeira execucao.

## 6. II-C - Analise (proposta de texto)

**Funcional x estrutural.** O conjunto funcional (60 casos, derivados de classes de equivalencia e valores-limite)
cobriu 75,7% das linhas e 72,4% dos branches: validou todas as regras de negocio, mas nao tocou em codigo de
infraestrutura (console, `main`) nem em guardas defensivas, que existem para entradas que o enunciado nao
descreve. O conjunto estrutural levou as linhas a 100% com 75 testes adicionais, mas esses testes sao mais
acoplados a implementacao (mensagens, excecoes, formato de saida) e menos orientados a requisito. Os dois sao
complementares: o funcional da confianca de que o jogo faz o que a especificacao pede; o estrutural mostra onde o
funcional nao olhou. A analise de fluxo de dados achou lacunas que linhas e branches escondem (ex.: `calcular`
com mao vazia e com As insuficiente para evitar estouro; rodada sem jogadores) e tambem os itens inviaveis, que
apontam codigo redundante.

**Eficiencia x eficacia (insumo para a Parte III).** Eficacia = capacidade de detectar defeitos (escore de
mutantes, Parte III); eficiencia = ganho de cobertura/defeitos por teste. Proposta: comparar o escore do PITest do
TestSet-Func isolado com o do conjunto completo e relatar se a cobertura estrutural adicional o elevou.

**Pergunta sobre TDD** (rascunho; ajustar a experiencia real do grupo): os casos funcionais foram definidos antes
da automatizacao e orientaram a forma do codigo de teste e uma pequena mudanca de producao: a necessidade de
controlar a ordem das cartas (P07) levou a extrair `FabricaDeBaralho` e injeta-la em `JogoDeBlackjack`, melhorando
a testabilidade sem alterar o comportamento. Como o jogo ja existia, nao houve ciclo red-green-refactor classico;
a influencia foi em testabilidade e na separacao `VisaoDoJogo`/`PortaDeEntrada`, que permitiu simular o console.
