package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Missao;
import br.com.infnet.missioncontrol.model.NivelRisco;
import br.com.infnet.missioncontrol.model.StatusMissao;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Set;

@Service
public class CalculadoraRiscoService {

    private static final Set<String> DESTINOS_DISTANTES = Set.of("MARTE", "EUROPA");

    public NivelRisco calcularRisco(Missao missao) {
        validarMissao(missao);

        if (missao.getStatus() == StatusMissao.CONCLUIDA) {
            return NivelRisco.BAIXO;
        }

        boolean semTripulantes = missao.getTripulantes() == null
                || missao.getTripulantes().isEmpty();
        boolean destinoDistante = DESTINOS_DISTANTES.contains(
                missao.getDestino().trim().toUpperCase());
        boolean lancamentoProximo = missao.getAnoLancamento() <= Year.now().getValue() + 1;

        if (semTripulantes || (destinoDistante && lancamentoProximo)) {
            return NivelRisco.ALTO;
        }

        if (destinoDistante || lancamentoProximo) {
            return NivelRisco.MEDIO;
        }

        return NivelRisco.ALTO;
    }

    private void validarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("A missão é obrigatória");
        }
        if (missao.getDestino() == null || missao.getDestino().isBlank()) {
            throw new IllegalArgumentException("O destino da missão é obrigatório");
        }
        if (missao.getAnoLancamento() == null) {
            throw new IllegalArgumentException("O ano de lançamento é obrigatório");
        }
        if (missao.getStatus() == null) {
            throw new IllegalArgumentException("O status da missão é obrigatório");
        }
    }
}
