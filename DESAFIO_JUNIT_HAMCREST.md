# 🚀 Desafio JUnit: Operação Órbita Segura

> O Centro de Controle recebeu dois novos módulos para coordenar sondas e missões espaciais. Os módulos parecem funcionar, mas ainda não possuem uma bateria de testes confiável. Sua equipe deverá verificar cada regra antes que as próximas missões sejam autorizadas.

## 🎯 Objetivos

Neste desafio, você deverá:

- Criar testes unitários com JUnit 5.
- Utilizar `@Test`, `@BeforeEach` e `@DisplayName`.
- Verificar exceções com `assertThrows`.
- Escrever verificações com Hamcrest.
- Testar valores de retorno e alterações nos objetos.
- Explorar casos normais, valores-limite e entradas inválidas.
- Garantir que cada teste possa ser executado de forma independente.

## 📜 Regras gerais

- Não utilize Mockito.
- Não utilize `@SpringBootTest`.
- Não inicialize o contexto do Spring.
- Não altere os services durante a criação dos testes.
- Instancie os services diretamente com `new`.
- Cada teste deve verificar um comportamento bem definido.
- Utilize nomes que descrevam claramente o comportamento esperado.
- Utilize Hamcrest nas verificações sempre que possível.

Os testes deverão ser criados em:

```text
src/test/java/br/com/infnet/missioncontrol/service/
```

---

# 🛰️ Desafio 1 — Manifesto de carga da sonda

Classe que deverá ser testada:

```java
ManifestoCargaService
```

Ela controla os instrumentos transportados por uma `Sonda`.

## Métodos disponíveis

```java
long calcularQuantidadeTotal(Sonda sonda);

boolean possuiInstrumento(Sonda sonda, String nome);

void adicionarInstrumento(Sonda sonda, Instrumento instrumento);
```

## Regra de capacidade

Uma sonda pode transportar, no máximo, **20 unidades de instrumentos**.

- Uma carga total menor que 20 é permitida.
- Uma carga total exatamente igual a 20 é permitida.
- Uma carga total maior que 20 deve ser rejeitada.

## `calcularQuantidadeTotal`

O método deve:

1. Somar as quantidades de todos os instrumentos.
2. Retornar `0` quando a lista de instrumentos for nula ou vazia.
3. Lançar `IllegalArgumentException` quando a sonda for nula.

### Exemplos

| Instrumentos | Resultado esperado |
|---|---:|
| Nenhum instrumento | `0` |
| 2 câmeras e 3 sensores | `5` |
| 10 câmeras e 10 sensores | `20` |

## `possuiInstrumento`

O método deve:

1. Retornar `true` quando o instrumento estiver presente.
2. Retornar `false` quando o instrumento não estiver presente.
3. Ignorar diferenças entre letras maiúsculas e minúsculas.
4. Ignorar espaços antes e depois do nome pesquisado.
5. Retornar `false` quando a lista de instrumentos for nula ou vazia.
6. Rejeitar um nome nulo, vazio ou formado somente por espaços.

### Exemplos

| Instrumento armazenado | Pesquisa | Resultado esperado |
|---|---|---:|
| `Câmera` | `Câmera` | `true` |
| `Câmera` | `câmera` | `true` |
| `Câmera` | `  CÂMERA  ` | `true` |
| `Câmera` | `Radar` | `false` |

## `adicionarInstrumento`

O método deve:

1. Adicionar um instrumento novo à sonda.
2. Criar a lista quando `sonda.getInstrumentos()` for nulo.
3. Somar as quantidades quando já existir um instrumento com o mesmo nome.
4. Ignorar maiúsculas e minúsculas ao procurar um instrumento existente.
5. Permitir que a quantidade total fique exatamente igual a 20.
6. Lançar `IllegalStateException` quando a nova carga ultrapassar 20.
7. Manter a carga original sem alterações quando a operação for rejeitada.

O método deve lançar `IllegalArgumentException` quando:

- A sonda for nula.
- O instrumento for nulo.
- O nome do instrumento for nulo ou vazio.
- A quantidade for nula, zero ou negativa.

## Cenários obrigatórios

Crie testes para, no mínimo:

- [ ] Calcular o total de uma lista vazia.
- [ ] Calcular o total de vários instrumentos.
- [ ] Localizar um instrumento pelo nome exato.
- [ ] Localizar um instrumento ignorando maiúsculas e minúsculas.
- [ ] Retornar `false` para um instrumento inexistente.
- [ ] Adicionar um instrumento novo.
- [ ] Somar a quantidade de um instrumento já existente.
- [ ] Permitir uma carga total exatamente igual a 20.
- [ ] Rejeitar uma carga total maior que 20.
- [ ] Verificar que a carga não foi alterada depois de uma operação rejeitada.
- [ ] Rejeitar quantidade zero.
- [ ] Rejeitar quantidade negativa.

## Esqueleto sugerido

```java
class ManifestoCargaServiceTest {

    private ManifestoCargaService service;
    private Sonda sonda;

    @BeforeEach
    void prepararCenario() {
        service = new ManifestoCargaService();

        // Crie uma nova sonda para cada teste.
    }

    @Test
    @DisplayName("Descrição do comportamento esperado")
    void nomeDoTeste() {
        // Arrange: prepare os dados.

        // Act: execute o método.

        // Assert: verifique o resultado.
    }
}
```

## Matchers que podem ajudar

```java
is(...)
hasSize(...)
hasItem(...)
contains(...)
empty()
hasProperty(...)
not(...)
```

---

# 🪐 Desafio 2 — Coordenação das missões

Classe que deverá ser testada:

```java
CoordenacaoMissoesService
```

Ela identifica conflitos de agenda e escolhe qual missão possui prioridade.

## Métodos disponíveis

```java
boolean existeConflitoDeAgenda(
    Missao primeira,
    Missao segunda
);

Missao selecionarPrioritaria(
    Missao primeira,
    Missao segunda
);
```

## `existeConflitoDeAgenda`

Existe conflito quando todas as condições abaixo são verdadeiras:

1. Nenhuma das missões está concluída.
2. As duas missões possuem o mesmo destino.
3. A comparação do destino ignora maiúsculas e minúsculas.
4. Os lançamentos ocorrem no mesmo ano ou possuem apenas um ano de diferença.

Não existe conflito quando:

- Alguma missão está concluída.
- Os destinos são diferentes.
- A diferença entre os anos de lançamento é maior que um.

### Exemplos

| Primeira missão | Segunda missão | Resultado esperado |
|---|---|---:|
| Marte em 2028 | Marte em 2028 | `true` |
| Marte em 2028 | Marte em 2029 | `true` |
| Marte em 2028 | Marte em 2030 | `false` |
| Marte em 2028 | `MARTE` em 2029 | `true` |
| Marte em 2028 | Lua em 2028 | `false` |
| Marte planejada | Marte concluída | `false` |

## `selecionarPrioritaria`

A missão prioritária deve ser selecionada utilizando os critérios abaixo, exatamente nesta ordem:

1. Prioridade pelo status:
   - `EM_ANDAMENTO`
   - `PLANEJADA`
   - `CONCLUIDA`
2. Se os status forem iguais, vence a missão com o ano de lançamento mais próximo.
3. Se os status e os anos forem iguais, vence a missão com mais tripulantes.
4. Se todos os critérios empatarem, a primeira missão recebida deve ser retornada.

O método deve retornar uma das mesmas instâncias recebidas. Ele não deve criar uma nova missão.

### Exemplos

| Primeira missão | Segunda missão | Missão prioritária |
|---|---|---|
| `PLANEJADA` | `EM_ANDAMENTO` | Segunda |
| `PLANEJADA` | `CONCLUIDA` | Primeira |
| Planejada para 2028 | Planejada para 2029 | Primeira |
| 2 tripulantes | 4 tripulantes | Segunda |
| Empate em todos os critérios | Empate em todos os critérios | Primeira |

## Entradas inválidas

Os dois métodos devem lançar `IllegalArgumentException` quando alguma missão:

- For nula.
- Não possuir destino.
- Não possuir ano de lançamento.
- Não possuir status.

## Cenários obrigatórios

Crie testes para, no mínimo:

- [ ] Detectar conflito no mesmo destino e no mesmo ano.
- [ ] Detectar conflito com exatamente um ano de diferença.
- [ ] Detectar conflito ignorando maiúsculas e minúsculas no destino.
- [ ] Não detectar conflito com dois anos de diferença.
- [ ] Não detectar conflito entre destinos diferentes.
- [ ] Não detectar conflito quando uma missão estiver concluída.
- [ ] Escolher a missão em andamento em vez da planejada.
- [ ] Escolher a missão planejada em vez da concluída.
- [ ] Desempatar pelo ano de lançamento.
- [ ] Desempatar pela maior quantidade de tripulantes.
- [ ] Retornar a primeira missão em caso de empate completo.
- [ ] Verificar que a missão retornada é a mesma instância recebida.
- [ ] Rejeitar uma missão nula.

## Esqueleto sugerido

```java
class CoordenacaoMissoesServiceTest {

    private CoordenacaoMissoesService service;
    private Missao primeira;
    private Missao segunda;

    @BeforeEach
    void prepararCenario() {
        service = new CoordenacaoMissoesService();

        // Prepare duas missões válidas antes de cada teste.
    }

    @Test
    @DisplayName("Descrição do comportamento esperado")
    void nomeDoTeste() {
        // Arrange

        // Act

        // Assert
    }
}
```

## Matchers que podem ajudar

```java
is(true)
is(false)
sameInstance(...)
not(...)
allOf(...)
hasProperty(...)
hasSize(...)
```

---

# 🧪 JUnit e Hamcrest trabalhando juntos

Utilize o JUnit para capturar exceções:

```java
IllegalArgumentException excecao = assertThrows(
    IllegalArgumentException.class,
    () -> service.executarOperacaoInvalida()
);
```

Depois, utilize o Hamcrest para verificar o resultado:

```java
assertThat(excecao.getMessage(), containsString("obrigatória"));
```

Imports mais utilizados:

```java
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
```

---

# ⭐ Missões bônus

Depois de concluir os cenários obrigatórios:

1. Verifique as mensagens das exceções.
2. Crie métodos auxiliares para construir sondas e missões.
3. Use `allOf()` para verificar várias características de um objeto.
4. Verifique se uma operação rejeitada preserva todo o estado anterior.
5. Crie testes para listas nulas e listas vazias.
6. Teste nomes contendo espaços antes e depois do texto.
7. Execute cada classe de teste isoladamente.

---

# 📦 Entrega

Entregue as duas classes:

```text
ManifestoCargaServiceTest.java
CoordenacaoMissoesServiceTest.java
```

## Checklist final

- [ ] O projeto compila.
- [ ] Os testes utilizam JUnit 5.
- [ ] Os testes utilizam Hamcrest.
- [ ] Não foi utilizado Mockito.
- [ ] Não foi utilizado `@SpringBootTest`.
- [ ] Cada teste pode ser executado individualmente.
- [ ] Existem testes para os cenários de sucesso.
- [ ] Existem testes para entradas inválidas.
- [ ] Os valores-limite foram testados.
- [ ] As exceções esperadas foram verificadas.
- [ ] Os nomes dos testes explicam os comportamentos esperados.

## Executando todos os testes

```bash
./mvnw test
```

## Executando apenas uma classe

```bash
./mvnw -Dtest=ManifestoCargaServiceTest test
```

```bash
./mvnw -Dtest=CoordenacaoMissoesServiceTest test
```

---

> Bons testes não servem apenas para confirmar que o programa funciona. Eles também ajudam a descobrir em quais situações ele deixa de funcionar. Boa missão! 🚀
