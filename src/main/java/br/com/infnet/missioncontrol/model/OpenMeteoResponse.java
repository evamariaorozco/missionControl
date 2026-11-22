package br.com.infnet.missioncontrol.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenMeteoResponse(
        Current current
) {
    public record Current(
            @JsonProperty("wind_speed_10m")
            double velocidadeVento,

            @JsonProperty("precipitation")
            double precipitacao
    ) {}
}