package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Missao;
import br.com.infnet.missioncontrol.model.StatusMissao;
import org.springframework.stereotype.Service;

@Service
public class CoordenacaoMissoesService {
    private final MissaoService missaoService;

    public CoordenacaoMissoesService(MissaoService missaoService) {
        this.missaoService = missaoService;
    }


    public boolean existeConflitoDeAgenda(Missao primeira, Missao segunda) {
        validarMissao(primeira);
        validarMissao(segunda);

        if (primeira.getStatus() == StatusMissao.CONCLUIDA
                || segunda.getStatus() == StatusMissao.CONCLUIDA) {
            return false;
        }

        boolean mesmoDestino = primeira.getDestino().equals(segunda.getDestino());
        int diferencaEntreAnos = Math.abs(
                primeira.getAnoLancamento() - segunda.getAnoLancamento());

        return mesmoDestino && diferencaEntreAnos < 1;
    }

    public Missao selecionarPrioritaria(Missao primeira, Missao segunda) {
        validarMissao(primeira);
        validarMissao(segunda);

        int prioridadePrimeira = calcularPrioridadeDoStatus(primeira.getStatus());
        int prioridadeSegunda = calcularPrioridadeDoStatus(segunda.getStatus());

        if (prioridadePrimeira != prioridadeSegunda) {
            return prioridadePrimeira > prioridadeSegunda ? primeira : segunda;
        }

        if (!primeira.getAnoLancamento().equals(segunda.getAnoLancamento())) {
            return primeira.getAnoLancamento() < segunda.getAnoLancamento()
                    ? primeira
                    : segunda;
        }

        int quantidadePrimeira = quantidadeDeTripulantes(primeira);
        int quantidadeSegunda = quantidadeDeTripulantes(segunda);

        if (quantidadePrimeira != quantidadeSegunda) {
            return quantidadePrimeira < quantidadeSegunda ? primeira : segunda;
        }

        return primeira;
    }

    private int calcularPrioridadeDoStatus(StatusMissao status) {
        return switch (status) {
            case EM_ANDAMENTO -> 3;
            case PLANEJADA -> 2;
            case CONCLUIDA -> 1;
        };
    }

    private int quantidadeDeTripulantes(Missao missao) {
        if (missao.getTripulantes() == null) {
            return 0;
        }
        return missao.getTripulantes().size();
    }

    private void validarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("A missão é obrigatória");
        }
        if (missao.getDestino() == null || missao.getDestino().isBlank()) {
            throw new IllegalArgumentException("O destino da missão é obrigatório");
        }
        if (missao.getAnoLancamento() == null) {
            throw new IllegalArgumentException(
                    "O ano de lançamento da missão é obrigatório");
        }
        if (missao.getStatus() == null) {
            throw new IllegalArgumentException("O status da missão é obrigatório");
        }
    }




}
