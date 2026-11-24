package br.com.infnet.missioncontrol.service;

import br.com.infnet.missioncontrol.model.Astronauta;
import br.com.infnet.missioncontrol.model.StatusAstronauta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AstronautaService {
    private final ArrayList<Astronauta> astronautas = new ArrayList<>() ;
    private Long proximoId = 4L;
    public AstronautaService() {
        astronautas.add(new Astronauta(1L, "Ana Silva", "Brasil", 34, 8,
                StatusAstronauta.DISPONIVEL));

        astronautas.add(new Astronauta(2L, "John Carter", "Estados Unidos", 41, 15,
                StatusAstronauta.EM_MISSAO));

        astronautas.add(new Astronauta(3L, "Mei Lin", "China", 29, 5,
                StatusAstronauta.AFASTADO));
    }

    public List<Astronauta> listarTodas() {

        return astronautas;
    }
    public List<Astronauta> filtrarPorStatus(StatusAstronauta status) {

        List<Astronauta> resultado = new ArrayList<>();
        for(Astronauta astronauta : astronautas){
            if(astronauta.getStatus().equals(status)){
                resultado.add(astronauta);
            }
        }
        return resultado;
    }
    public Astronauta buscarPeloId(Long id) {
        for (Astronauta astronauta : astronautas) {
            if (astronauta.getId().equals(id)) {
                return astronauta;
            }
        }
        return null;
    }
    public boolean remover(Long id){
        boolean removeu = false;
        for(int i=0; i<astronautas.size(); i++){
            if(astronautas.get(i).getId().equals(id)){
                astronautas.remove(i);
                removeu = true;
            }
        }
        return removeu;
    }
    public void adicionar(Astronauta astronauta){
        astronauta.setId(proximoId++);
        astronautas.add(astronauta);
    }
    public String validar(Astronauta astronauta) {
        if (astronauta.getNome() == null || astronauta.getNome().isBlank()) {
            return "O nome é obrigatório";
        }
        if (astronauta.getPais() == null || astronauta.getPais().isBlank()) {
            return "O país é obrigatório";
        }
        if (astronauta.getIdade() == null || astronauta.getIdade() < 18) {
            return "A idade deve ser maior ou igual a 18";
        }
        if (astronauta.getAnosExperiencia() == null || astronauta.getAnosExperiencia() < 0) {
            return "Os anos de experiência não podem ser negativos";
        }
        if (astronauta.getStatus() == null) {
            return "O status é obrigatório";
        }
        return null;
    }
    public boolean existeNome(String nome) {
        for (Astronauta astronauta : astronautas) {
            if (astronauta.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }
}