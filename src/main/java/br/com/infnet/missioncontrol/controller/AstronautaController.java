package br.com.infnet.missioncontrol.controller;

import br.com.infnet.missioncontrol.model.ErrorResponse;
import br.com.infnet.missioncontrol.model.Astronauta;
import br.com.infnet.missioncontrol.model.StatusAstronauta;
import br.com.infnet.missioncontrol.service.AstronautaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/astronautas")
public class AstronautaController {
    private final AstronautaService astronautaService;

    public AstronautaController(AstronautaService astronautaService) {
        this.astronautaService = astronautaService;
    }
    @GetMapping("/{id}")
    public ResponseEntity buscarPeloId(@PathVariable Long id) {
        if(id <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("ID invalido"));
        }
        Astronauta astronauta = astronautaService.buscarPeloId(id);
        if(astronauta != null) {
            return ResponseEntity.ok().body(astronauta);
        }else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Astronauta não localizado"));
        }
    }
    @GetMapping
    public List<Astronauta> listar(@RequestParam(required = false) StatusAstronauta status) {
        if(status == null){
            return  astronautaService.listarTodas();
        }
        return astronautaService.filtrarPorStatus(status);
    }
    @PostMapping
    public ResponseEntity salvar(@RequestBody Astronauta astronauta) {
        String erro = astronautaService.validar(astronauta);
        if (erro != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(erro));
        }
        if (astronautaService.existeNome(astronauta.getNome())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Ja existe um astronauta com esse nome"));
        }
        astronautaService.adicionar(astronauta);
        return ResponseEntity.status(HttpStatus.CREATED).body(astronauta);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable Long id){
        if(id <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("ID invalido"));
        }
        boolean removeu = astronautaService.remover(id);
        if(removeu) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Astronauta não encontrado"));
    }
    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable Long  id,
                                    @RequestBody Astronauta entrada){
        if(id <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Id invalido"));
        }
        Astronauta existente = astronautaService.buscarPeloId(id);
        if(existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Astronauta não encontrado"));
        }
        String erro = astronautaService.validar(entrada);
        if (erro != null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(erro));
        }
        existente.setNome(entrada.getNome());
        existente.setPais(entrada.getPais());
        existente.setIdade(entrada.getIdade());
        existente.setAnosExperiencia(entrada.getAnosExperiencia());
        existente.setStatus(entrada.getStatus());
        return ResponseEntity.ok().body(existente);

    }
}