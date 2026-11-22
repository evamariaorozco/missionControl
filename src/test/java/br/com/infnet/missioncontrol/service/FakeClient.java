package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.CondicoesClimaticas;

public class FakeClient implements ClimaClient {
    @Override
    public CondicoesClimaticas consultarClima() {
        return new CondicoesClimaticas(0.1,30);
    }
}
