# Regras dos services de missão

Este documento apresenta as regras das classes criadas para a prática de testes unitários com JUnit.

Os exemplos foram pensados para trabalhar inicialmente com anotações e asserções básicas do JUnit, sem utilizar mocks ou carregar o contexto do Spring.

## `NivelRisco`

O enum `NivelRisco` representa o resultado da análise de risco de uma missão.

Valores possíveis:

- `BAIXO`
- `MEDIO`
- `ALTO`

---

## `CalculadoraRiscoService`

Responsável por analisar as informações de uma missão e determinar seu nível de risco.

### Método

```java
NivelRisco calcularRisco(Missao missao)
```

### Regras

1. A missão é obrigatória.
2. O destino é obrigatório e não pode ser vazio.
3. O ano de lançamento é obrigatório.
4. O status da missão é obrigatório.
5. Uma missão concluída sempre possui risco `BAIXO`.
6. Uma missão sem tripulantes possui risco `ALTO`.
7. Marte e Europa são considerados destinos distantes.
8. Um lançamento no ano atual ou no próximo ano é considerado próximo.
9. Destino distante junto com lançamento próximo resulta em risco `ALTO`.
10. Apenas destino distante ou apenas lançamento próximo resulta em risco `MEDIO`.
11. Quando nenhuma condição de risco é encontrada, o resultado é `BAIXO`.

### Exceções

Uma `IllegalArgumentException` será lançada quando:

- A missão for nula.
- O destino for nulo ou vazio.
- O ano de lançamento for nulo.
- O status for nulo.

### Exemplos lúdicos

| Situação | Resultado esperado |
|---|---|
| A missão vai para a Lua, mas ninguém entrou na nave | `ALTO` |
| A missão vai para Marte e será lançada no próximo ano | `ALTO` |
| A missão vai para Marte, mas o lançamento ainda está distante | `MEDIO` |
| A missão vai para a Lua, possui tripulantes e acontecerá daqui a vários anos | `BAIXO` |
| A missão já foi concluída | `BAIXO` |

---

## `TripulacaoService`

Responsável por adicionar, remover e verificar os tripulantes de uma missão.

### Métodos

```java
void adicionarTripulante(Missao missao, String nome)

boolean removerTripulante(Missao missao, String nome)

boolean possuiTripulacaoMinima(Missao missao)
```

### Regras para adicionar um tripulante

1. A missão é obrigatória.
2. O nome é obrigatório e não pode conter somente espaços.
3. Os espaços existentes antes e depois do nome são removidos.
4. Não são permitidos tripulantes duplicados.
5. A comparação de nomes ignora letras maiúsculas e minúsculas.
6. Uma missão pode possuir no máximo cinco tripulantes.
7. Se a lista de tripulantes estiver nula, uma nova lista será criada.

### Regras para remover um tripulante

1. A missão e o nome são obrigatórios.
2. A comparação do nome ignora letras maiúsculas e minúsculas.
3. Quando o tripulante é encontrado e removido, o método retorna `true`.
4. Quando o tripulante não existe, o método retorna `false`.
5. Quando a lista de tripulantes é nula, o método retorna `false`.

### Regra de tripulação mínima

Uma missão possui tripulação mínima quando existe pelo menos uma pessoa em sua lista de tripulantes.

### Exceções

Uma `IllegalArgumentException` será lançada quando:

- A missão for nula.
- O nome do tripulante for nulo, vazio ou formado somente por espaços.
- O tripulante já fizer parte da missão.

Uma `IllegalStateException` será lançada quando a missão já possuir cinco tripulantes e houver uma tentativa de adicionar outra pessoa.

### Exemplos lúdicos

| Situação | Resultado esperado |
|---|---|
| Ana entra em uma nave vazia | Ana é adicionada à lista |
| Ana já está na missão e tentam adicionar `ana` | `IllegalArgumentException` |
| A nave possui cinco pessoas e Carlos tenta entrar | `IllegalStateException` |
| João deixa a missão | O método retorna `true` |
| Tentam remover alguém que não está na missão | O método retorna `false` |
| O nome informado contém somente espaços | `IllegalArgumentException` |

---

## `PlanejamentoService`

Responsável por verificar se uma missão está pronta para o lançamento e por adiar lançamentos.

### Métodos

```java
boolean podeSerLancada(Missao missao)

void adiarLancamento(Missao missao, int quantidadeAnos)
```

### Regras para autorizar o lançamento

Uma missão pode ser lançada somente quando todas as condições abaixo forem atendidas:

1. O status da missão é `PLANEJADA`.
2. A missão possui pelo menos um tripulante.
3. O ano de lançamento é igual ou posterior ao ano atual.
4. O ano de lançamento não ultrapassa 2100.

Se alguma dessas condições não for atendida, o método retorna `false`.

### Regras para adiar o lançamento

1. A missão é obrigatória.
2. O ano de lançamento é obrigatório.
3. Somente uma missão com status `PLANEJADA` pode ser adiada.
4. A quantidade de anos do adiamento deve ser maior que zero.
5. O novo ano de lançamento não pode ultrapassar 2100.
6. Quando o adiamento é válido, o ano da própria missão é alterado.

### Exceções

Uma `IllegalArgumentException` será lançada quando:

- A missão for nula.
- O ano de lançamento for nulo.
- A quantidade de anos for zero ou negativa.
- O novo ano de lançamento ultrapassar 2100.

Uma `IllegalStateException` será lançada quando houver uma tentativa de adiar uma missão que não esteja com o status `PLANEJADA`.

### Exemplos lúdicos

| Situação | Resultado esperado |
|---|---|
| A missão está planejada, possui tripulação e será lançada no próximo ano | `true` |
| A missão está planejada, mas ninguém entrou na nave | `false` |
| A missão foi programada para um ano que já passou | `false` |
| Uma missão de 2028 é adiada por dois anos | O ano passa a ser 2030 |
| Tentam adiar a missão por zero ou por menos três anos | `IllegalArgumentException` |
| Tentam adiar uma missão que já foi concluída | `IllegalStateException` |
| O adiamento faria o lançamento ultrapassar 2100 | `IllegalArgumentException` |

---

## Sugestão de progressão dos testes

1. Usar `assertEquals` para verificar os níveis de risco.
2. Usar `assertTrue` e `assertFalse` para verificar planejamento e remoção de tripulantes.
3. Usar `assertThrows` para verificar entradas inválidas e limites.
4. Usar `@BeforeEach` para preparar uma nova missão antes de cada teste.
5. Usar Hamcrest para verificar o conteúdo e o tamanho da lista de tripulantes.
6. Usar `@ParameterizedTest` para experimentar diferentes destinos e anos de lançamento.
