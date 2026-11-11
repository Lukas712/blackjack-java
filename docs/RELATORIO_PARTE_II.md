# Relatorio de Execucao - Parte II (DCC168 - Teste de Software)

Grupo: Lukas Freitas, Gabriel Paes, Pedro Garbois.

> Rascunho gerado a partir da execucao real dos testes. A secao 6 (analise pessoal e TDD) e uma
> proposta de texto: cada integrante deve revisa-la com a sua propria experiencia.

## 1. Como reproduzir

```
mvn test                                           # roda os 179 testes e gera target/site/jacoco/index.html (JaCoCo, motor do EclEmma)
rm -f target/jacoco.exec && mvn test -Dtest='com.blackjack.func.*Test'   # somente TestSet-Func
```

O `jacoco.exec` acumula execucoes anteriores; apague-o antes de medir um subconjunto.

No Eclipse: botao direito no projeto > *Coverage As > JUnit Test* (EclEmma) e *Run As > JUnit Test with Baduino*
(fluxo de dados). Exporte os relatorios (*Export Session*) para anexar na entrega.

## 2. Organizacao dos testes

| Pacote de teste | Conteudo |
|---|---|
| `com.blackjack.func` | **TestSet-Func**: CT01-CT60 da Parte I, um `@Test` (JUnit 4) por caso, com o id no nome do metodo (`ctXX_...`) |
| `com.blackjack.estr` | **TestSet-Estr**: 59 testes adicionais para cobrir fluxo de controle e de dados |
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
| Linhas | 490 / 675 | 72,6 |
| Branches | 185 / 283 | 65,4 |
| Metodos | 122 / 170 | 71,8 |
| Instrucoes | 2280 / 3381 | 67,4 |
| Pares def-uso (BA-DUA, nucleo do Baduino) | 539 / 833 | 64,7 |

Lacunas do TestSet-Func: `main`, `VisaoConsole` (so impressao), `EntradaConsole` (varias validacoes e apelidos de
comando), excecoes defensivas de `Banca`, `Mao`, `Banqueiro`, `Baralho` e o construtor de `JogoDeBlackjack` com `Random`.

## 4. II-B - Cobertura apos o TestSet-Estr (179 testes no total)

| Metrica | Cobertos / Total | % |
|---|---|---|
| Linhas | 675 / 675 | **100** |
| Branches | 282 / 283 | 99,6 (1 inviavel) |
| Metodos | 170 / 170 | **100** |
| Pares def-uso (BA-DUA) | 814 / 833 | 97,7 (100% dos 814 viaveis) |

Casos estruturais adicionados (`com.blackjack.estr`): `EntradaConsoleEstruturalTest` (todos os ramos de
validacao/repeticao e apelidos de comando), `VisaoConsoleEstruturalTest` (toda a saida, capturando `System.out`),
`MainEstruturalTest` (ponto de entrada com 1 e 2 jogadores), `MaoEstruturalTest`, `BancaEstruturalTest`,
`CartaEstruturalTest`, `BanqueiroEstruturalTest`, `PontuacaoEstruturalTest` (mao vazia e As + tres figuras),
`RodadaEstruturalTest` (rodada sem jogadores, dois jogadores, acao inesperada), `JogoDeBlackjackEstruturalTest`,
`ObjetosDeValorEstruturalTest` (igualdade, hash e texto de `Carta`, `Aposta`, `Pontuacao`, `ContextoDoJogo`).

### 4.1 Itens inviaveis (nenhum teste consegue cobri-los)

Fluxo de controle (1 branch):
- `Mao.ehPrimeiraAcao()`: a condicao `!dobrada` nunca e falsa quando `estado == ATIVA && cartas == 2`, pois
  `dobrar` sempre resulta em 3 cartas. A condicao e redundante (candidata a remocao em refatoracao).

Fluxo de dados (19 pares def-uso):
- 7 pares em `CalculadoraDePontuacao.calcular`: os valores iniciais `total = 0` e `asesComoOnze = 0` chegam a usos
  que exigem `total > 21` ou `asesComoOnze > 0` sem nenhuma atualizacao no caminho.
- 3 pares em `Rodada.comprarCartasEmRodizio` (lacos com `participantes >= 1` e constante `2`),
  1 em `Rodada.ofertarSeguroATodos` (`ofertou` inicial `false` nunca satisfaz `else if (ofertou)`),
  1 em `Rodada.jogarTodasAsMaosDe` e 1 em `Rodada.liquidarMaos` (todo jogador tem ao menos uma mao),
  1 em `main` (`lerNumeroDeJogadores` garante ao menos 1 jogador) e 2 em `Mao.ehPrimeiraAcao` (mesma causa do branch acima).
- 2 pares em `Baralho.todasAsCartas` e 1 em `EntradaConsole.registrar`: o `for-each` sobre arrays
  (`Naipe.values()`, `Valor.values()`, apelidos) so tem a aresta de "array vazio" inviavel, pois esses arrays nunca sao vazios.

Observacao sobre o Baduino: o plugin embute o BA-DUA 0.2.0, que so instrumenta bytecode Java 8 (versao 52). Por isso
o codigo de producao e os testes foram compilados com `maven.compiler.release=8`: `record` virou classe final
imutavel (`Carta`, `Aposta`, `Pontuacao`, `ContextoDoJogo`, mesmos acessores) e os `switch` com `->` viraram
`switch` classico (o comportamento nao mudou). A medicao foi repetida no proprio Baduino 0.3.25 (BA-DUA 0.2.0,
Eclipse 2019-03 com JDK 8): **781 / 797 pares def-uso cobertos (98,0%)** com os 179 testes, e os 16 nao cobertos
sao exatamente os inviaveis listados acima em `Rodada` (6), `CalculadoraDePontuacao` (7), `Mao` (2) e `main` (1).
Os 3 pares de `Baralho.todasAsCartas`/`EntradaConsole.registrar` so aparecem na versao 0.8.0 do BA-DUA (833 pares no total).
Os arquivos do Baduino estao em `.baduino/` (`coverage.xml`).

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
cobriu 72,6% das linhas e 65,4% dos branches: validou todas as regras de negocio, mas nao tocou em codigo de
infraestrutura (console, `main`) nem em guardas defensivas, que existem para entradas que o enunciado nao
descreve. O conjunto estrutural levou as linhas a 100% com 59 testes adicionais, mas esses testes sao mais
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
