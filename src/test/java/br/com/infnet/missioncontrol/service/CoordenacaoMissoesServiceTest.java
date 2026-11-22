package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Missao;
import br.com.infnet.missioncontrol.model.StatusMissao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class CoordenacaoMissoesServiceTest {
    @Autowired
    private CoordenacaoMissoesService service;
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Antes de todo mundo, apenas uma vez!");
    }
    @BeforeEach
    public void setUp() {
        System.out.println("Executando antes de cada um!");
    }
    @Test
    @DisplayName("Não deve existir conflitos com Destino Diferentes")
    public void testaDestinosDiferentes() {
        System.out.println("Executando Teste!");

        //Arrange
        Missao primeira = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);
        Missao segunda = criarMissao("Lua", 2030, StatusMissao.PLANEJADA);
        //Act
        boolean resultado = service.existeConflitoDeAgenda(primeira, segunda);
        //Assert
        assertFalse(resultado);

    }
    @Test
    @DisplayName("Deve retornar conflito de agenda para anos iguais e mesmo destino")
    public void deveVerificarConflito(){
        System.out.println("Executando Teste!");

        Missao primeira = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);
        Missao segunda = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);
        boolean resultado = service.existeConflitoDeAgenda(primeira, segunda);
        assertTrue(resultado);

    }
    @Test
    @DisplayName("Não deve existir Conflito em anos diferentes")
    public void deveVerificarAnosDiferentes(){
        System.out.println("Executando Teste!");

        Missao primeira = criarMissao("Marte", 2035, StatusMissao.PLANEJADA);
        Missao segunda = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);

        assertFalse(service.existeConflitoDeAgenda(primeira, segunda));

    }
    @Test
    @DisplayName("Não deve existir Conflito se pelo menos uma tiver Concluida")
    public void naoDeveTerConflitoConcluida(){
        System.out.println("Executando Teste!");

        Missao primeira = criarMissao("Marte", 2030, StatusMissao.CONCLUIDA);
        Missao segunda = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);

        assertFalse(service.existeConflitoDeAgenda(primeira, segunda));


        primeira = criarMissao("Marte", 2031, StatusMissao.CONCLUIDA);
        segunda = criarMissao("Marte2", 2030, StatusMissao.CONCLUIDA);

        assertFalse(service.existeConflitoDeAgenda(primeira, segunda));

        primeira = criarMissao("Marte2", 2031, StatusMissao.CONCLUIDA);
        segunda = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);

        assertFalse(service.existeConflitoDeAgenda(primeira, segunda));
    }


    @Test
    @DisplayName("Deve Rejeitar uma missão nula")
    void deveRejeitarMissaoNula(){
        Missao missao = criarMissao("Marte", 2030, StatusMissao.PLANEJADA);
        IllegalArgumentException exp = assertThrows(IllegalArgumentException.class, () -> {
            boolean resultado = service.existeConflitoDeAgenda(null, missao);
        });
        assertEquals("A missão é obrigatória",
                exp.getMessage());

    }
    @Test
    @DisplayName("Deve priorizar a missão em andamento.")
    void devePriorizarMissaoEmAndamento(){
        Missao primeira = criarMissao(
                "Marte", 2030, StatusMissao.PLANEJADA
        );

        Missao segunda = criarMissao(
                "Lua", 2035, StatusMissao.EM_ANDAMENTO
        );
        Missao resultado = service.selecionarPrioritaria(primeira, segunda);

        assertSame(segunda,resultado);

    }

    @Test
    @DisplayName("Deve priorizar a missão mais antiga")
    void devePriorizarMaisAntiga(){
        Missao primeira = criarMissao(
                "Marte", 2035, StatusMissao.PLANEJADA
        );

        Missao segunda = criarMissao(
                "Lua", 2030, StatusMissao.PLANEJADA
        );
        Missao resultado = service.selecionarPrioritaria(primeira, segunda);

        assertSame(segunda,resultado);

    }
    @Test
    @DisplayName("Deve priorizar missao com menos Tripulantes")
    void devePriorizarComMenosTripulantes(){
        Missao primeira = criarMissao(
                "Marte", 2030, StatusMissao.PLANEJADA
        );

        Missao segunda = criarMissao(
                "Lua", 2030, StatusMissao.PLANEJADA
        );
        primeira.setTripulantes(new ArrayList<>());
        segunda.setTripulantes(new ArrayList<>(
                java.util.Collections.nCopies(3,null)
        ));
        Missao resultado = service.selecionarPrioritaria(primeira, segunda);
        assertSame(primeira,resultado);


    }





    @ParameterizedTest
    @CsvSource({
            "Marte, 2030, PLANEJADA, Marte, 2030, PLANEJADA, true",
            "Marte, 2030, PLANEJADA, Marte, 2030, EM_ANDAMENTO, true",
            "Marte, 2030, PLANEJADA, Lua, 2030, PLANEJADA, false",
            "Marte, 2030, PLANEJADA, Marte, 2031, PLANEJADA, false",
            "Marte, 2030, CONCLUIDA, Marte, 2030, PLANEJADA, false",
            "Marte, 2030, PLANEJADA, Marte, 2030, CONCLUIDA, false"
    })
    void deveVerificarConflitoDeAgendamento(String destinoPrimeira,
                                             int anoPrimeira,
                                             StatusMissao statusPrimeira,
                                             String destinoSegunda,
                                             int anoSegunda,
                                             StatusMissao statusSegunda,
                                             boolean esperado){

        Missao primeira = criarMissao(
                destinoPrimeira,
                anoPrimeira,
                statusPrimeira
        );

        Missao segunda = criarMissao(
                destinoSegunda,
                anoSegunda,
                statusSegunda
        );
        boolean resultado = service.existeConflitoDeAgenda(primeira, segunda);
        assertEquals(esperado, resultado);

    }
    @ParameterizedTest
    @CsvFileSource(resources = "/conflitos-missoes2.csv", numLinesToSkip = 1, delimiter = ';')
    void deveVerificarConflitoDeAgendamentoDoCsv(String destinoPrimeira,
                                            int anoPrimeira,
                                            StatusMissao statusPrimeira,
                                            String destinoSegunda,
                                            int anoSegunda,
                                            StatusMissao statusSegunda,
                                            boolean esperado){
        Missao primeira = criarMissao(
                destinoPrimeira,
                anoPrimeira,
                statusPrimeira
        );

        Missao segunda = criarMissao(
                destinoSegunda,
                anoSegunda,
                statusSegunda
        );
        boolean resultado = service.existeConflitoDeAgenda(primeira, segunda);
        assertEquals(esperado, resultado);

    }
    @ParameterizedTest
    @MethodSource("cenariosDeConflito")
    public void deveVerificarConflitoDeAgendamentoComOMetodoExterno(Missao primeira,
                                                                    Missao segunda,
                                                                    boolean esperado){
        boolean resultado = service.existeConflitoDeAgenda(primeira, segunda);
        assertEquals(esperado, resultado);

    }
    static Stream<Arguments> cenariosDeConflito() {
        return Stream.of(
                Arguments.of(
                        criarMissao2("Marte", 2030, StatusMissao.PLANEJADA),
                        criarMissao2("Marte", 2030, StatusMissao.EM_ANDAMENTO),
                        true
                ),
                Arguments.of(
                        criarMissao2("Marte", 2030, StatusMissao.PLANEJADA),
                        criarMissao2("Lua", 2030, StatusMissao.PLANEJADA),
                        false
                )

        );

    }






    @AfterEach
    public void tearDown() {
        System.out.println("Executando ao final de cada um!");
    }
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Executando uma vez ao final!");
    }

    private Missao criarMissao(String destino,
                               Integer ano,
                               StatusMissao status) {
        Missao missao = new Missao();
        missao.setDestino(destino);
        missao.setAnoLancamento(ano);
        missao.setStatus(status);
        return missao;
    }
    private static Missao criarMissao2(String destino,
                               Integer ano,
                               StatusMissao status) {
        Missao missao = new Missao();
        missao.setDestino(destino);
        missao.setAnoLancamento(ano);
        missao.setStatus(status);
        return missao;
    }


}