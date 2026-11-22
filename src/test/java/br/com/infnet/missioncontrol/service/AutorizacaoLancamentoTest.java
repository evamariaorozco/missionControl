package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.CondicoesClimaticas;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutorizacaoLancamentoTest {
    @InjectMocks
    private AutorizacaoLancamentoService service;
    @Mock
    private ClimaClient cliente;

    @Test
    @DisplayName("Deve autorizar lancamento para valores validos")
    public void deveAutorizarLancamento() {
        //Arrange
            when(cliente.consultarClima())
                    .thenReturn(new CondicoesClimaticas(0,15));
        //Act
        boolean resultado = service.podeAutorizarLancamento();
        //Assert
        assertTrue(resultado);

    }
    @Test
    @DisplayName("Não Deve autorizar lancamento para chuva > 0")
    public void naoDeveAutorizarLancamento() {
        //Arrange
        when(cliente.consultarClima())
                .thenReturn(new CondicoesClimaticas(1,15));
        //Act
        boolean resultado = service.podeAutorizarLancamento();
        //Assert
        assertFalse(resultado);
    }
    @Test
    @DisplayName("Não Deve autorizar lancamento para vento > 30")
    public void naoDeveAutorizarLancamentoCOmVento() {
        //Arrange
        when(cliente.consultarClima())
                .thenReturn(new CondicoesClimaticas(0.01,30));
        //Act
        boolean resultado = service.podeAutorizarLancamento();
        verify(cliente).consultarClima();
        verify(cliente,times(1)).consultarClima();
        //Assert
        assertFalse(resultado);
    }
    @Test
    @DisplayName("Deve autorizar lancamento para valores validos")
    public void devePropagarErroQuandoConsultaClimaFalha() {
        //Arrange
        when(cliente.consultarClima())
                .thenThrow(new IllegalStateException("Resposta meteorológica inválida"));
        //Act e Assert
        assertThrows(IllegalStateException.class, () -> service.podeAutorizarLancamento());

    }


}
