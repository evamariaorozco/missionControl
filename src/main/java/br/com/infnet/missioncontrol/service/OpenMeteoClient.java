package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.CondicoesClimaticas;
import br.com.infnet.missioncontrol.model.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenMeteoClient implements ClimaClient {
    private final RestClient restClient;

    public OpenMeteoClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.open-meteo.com/")
                .build();
    }
    @Override
    public CondicoesClimaticas consultarClima() {

        OpenMeteoResponse resposta = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", -22.9)
                        .queryParam("longitude", -43.2)
                        .queryParam(
                                "current",
                                "wind_speed_10m,precipitation"
                        )
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);

        if (resposta == null || resposta.current() == null) {
            throw new IllegalStateException(
                    "Resposta meteorológica inválida"
            );
        }
        System.out.println("Resposta: " + resposta.current());
        double vento = resposta.current().velocidadeVento();
        double chuva = resposta.current().precipitacao();

        return new CondicoesClimaticas(chuva, vento);
    }

}
