package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Instrumento;
import br.com.infnet.missioncontrol.model.Sonda;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManifestoCargaService {

    public static final long CAPACIDADE_MAXIMA = 20L;

    public long calcularQuantidadeTotal(Sonda sonda) {
        validarSonda(sonda);

        if (sonda.getInstrumentos() == null) {
            return 0L;
        }

        long quantidadeTotal = 0L;
        for (Instrumento instrumento : sonda.getInstrumentos()) {
            validarInstrumento(instrumento);
            quantidadeTotal += instrumento.getQuantidade();
        }

        return quantidadeTotal;
    }

    public boolean possuiInstrumento(Sonda sonda, String nome) {
        validarSonda(sonda);
        String nomeNormalizado = validarENormalizarNome(nome);

        if (sonda.getInstrumentos() == null) {
            return false;
        }

        return sonda.getInstrumentos().stream()
                .anyMatch(instrumento -> instrumento.getNome().equals(nomeNormalizado));
    }

    public void adicionarInstrumento(Sonda sonda, Instrumento instrumento) {
        validarSonda(sonda);
        validarInstrumento(instrumento);

        if (sonda.getInstrumentos() == null) {
            sonda.setInstrumentos(new ArrayList<>());
        }

        Instrumento instrumentoExistente = localizarInstrumento(
                sonda.getInstrumentos(), instrumento.getNome());

        if (instrumentoExistente != null) {
            long novaQuantidade = instrumentoExistente.getQuantidade()
                    + instrumento.getQuantidade();
            instrumentoExistente.setQuantidade(novaQuantidade);

            if (calcularQuantidadeTotal(sonda) >= CAPACIDADE_MAXIMA) {
                throw new IllegalStateException(
                        "A capacidade máxima da sonda é de 20 instrumentos");
            }
            return;
        }

        long novaQuantidadeTotal = calcularQuantidadeTotal(sonda)
                + instrumento.getQuantidade();

        if (novaQuantidadeTotal >= CAPACIDADE_MAXIMA) {
            throw new IllegalStateException(
                    "A capacidade máxima da sonda é de 20 instrumentos");
        }

        List<Instrumento> instrumentos = new ArrayList<>(sonda.getInstrumentos());
        instrumentos.add(instrumento);
        sonda.setInstrumentos(instrumentos);
    }

    private Instrumento localizarInstrumento(List<Instrumento> instrumentos, String nome) {
        String nomeNormalizado = nome.trim();

        for (Instrumento instrumento : instrumentos) {
            if (instrumento.getNome().equals(nomeNormalizado)) {
                return instrumento;
            }
        }
        return null;
    }

    private void validarSonda(Sonda sonda) {
        if (sonda == null) {
            throw new IllegalArgumentException("A sonda é obrigatória");
        }
    }

    private void validarInstrumento(Instrumento instrumento) {
        if (instrumento == null) {
            throw new IllegalArgumentException("O instrumento é obrigatório");
        }
        validarENormalizarNome(instrumento.getNome());
        if (instrumento.getQuantidade() == null || instrumento.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade do instrumento deve ser positiva");
        }
    }

    private String validarENormalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do instrumento é obrigatório");
        }
        return nome.trim();
    }
}
