package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Missao;
import br.com.infnet.missioncontrol.model.StatusMissao;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlanejamentoService {

    public static final int ANO_MAXIMO = 2100;

    public boolean podeSerLancada(Missao missao) {
        validarMissao(missao);

        return missao.getStatus() == StatusMissao.PLANEJADA
                && missao.getTripulantes() != null
                && !missao.getTripulantes().isEmpty()
                && missao.getAnoLancamento() >= Year.now().getValue()
                && missao.getAnoLancamento() <= ANO_MAXIMO;
    }

    public void adiarLancamento(Missao missao, int quantidadeAnos) {
        validarMissao(missao);

        if (missao.getStatus() != StatusMissao.PLANEJADA) {
            throw new IllegalStateException("Somente missões planejadas podem ser adiadas");
        }
        if (quantidadeAnos <= 0) {
            throw new IllegalArgumentException("A quantidade de anos deve ser positiva");
        }

        long novoAno = (long) missao.getAnoLancamento() + quantidadeAnos;
        if (novoAno > ANO_MAXIMO) {
            throw new IllegalArgumentException("O lançamento não pode ultrapassar o ano 2100");
        }

        missao.setAnoLancamento((int) novoAno);
    }

    private void validarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("A missão é obrigatória");
        }
        if (missao.getAnoLancamento() == null) {
            throw new IllegalArgumentException("O ano de lançamento é obrigatório");
        }
    }
}
