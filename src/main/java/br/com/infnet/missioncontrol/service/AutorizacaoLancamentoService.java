package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.CondicoesClimaticas;
import br.com.infnet.missioncontrol.model.OpenMeteoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AutorizacaoLancamentoService {
    private ClimaClient client;

    public AutorizacaoLancamentoService(ClimaClient client) {
        this.client = client;
    }

    public boolean podeAutorizarLancamento() {
        CondicoesClimaticas condicoesClimaticas = client.consultarClima();
        return condicoesClimaticas.velocidadeDoVento() < 35 && condicoesClimaticas.precipitacao() == 0;
    }
}
