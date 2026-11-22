package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Missao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TripulacaoService {

    public static final int MAXIMO_TRIPULANTES = 5;

    public void adicionarTripulante(Missao missao, String nome) {
        validarMissao(missao);
        String nomeNormalizado = validarENormalizarNome(nome);

        if (missao.getTripulantes() == null) {
            missao.setTripulantes(new ArrayList<>());
        }

        boolean nomeDuplicado = missao.getTripulantes().stream()
                .anyMatch(nomeExistente -> nomeExistente.equalsIgnoreCase(nomeNormalizado));

        if (nomeDuplicado) {
            throw new IllegalArgumentException("O tripulante já pertence à missão");
        }
        if (missao.getTripulantes().size() >= MAXIMO_TRIPULANTES) {
            throw new IllegalStateException("A missão já atingiu o limite de tripulantes");
        }

        missao.getTripulantes().add(nomeNormalizado);
    }

    public boolean removerTripulante(Missao missao, String nome) {
        validarMissao(missao);
        String nomeNormalizado = validarENormalizarNome(nome);

        if (missao.getTripulantes() == null) {
            return false;
        }

        return missao.getTripulantes().removeIf(
                nomeExistente -> nomeExistente.equalsIgnoreCase(nomeNormalizado));
    }

    public boolean possuiTripulacaoMinima(Missao missao) {
        validarMissao(missao);
        return missao.getTripulantes() != null && !missao.getTripulantes().isEmpty();
    }

    private void validarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("A missão é obrigatória");
        }
    }

    private String validarENormalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do tripulante é obrigatório");
        }
        return nome.trim();
    }
}
