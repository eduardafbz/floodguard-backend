package main.java.com.floodguard.controller;

import com.floodguard.model.NivelRisco;
import com.floodguard.model.Regiao;
import com.floodguard.repository.RegiaoRepository;
import com.floodguard.service.OcorrenciaService;
import com.floodguard.service.RiscoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regioes")
@CrossOrigin(origins = "*")
public class RegiaoController {

    private final RegiaoRepository repository;
    private final RiscoService riscoService;
    private final OcorrenciaService ocorrenciaService;

    public RegiaoController(
            RegiaoRepository repository,
            RiscoService riscoService,
            OcorrenciaService ocorrenciaService) {

        this.repository = repository;
        this.riscoService = riscoService;
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping
    public List<Regiao> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Regiao criar(@RequestBody Regiao regiao) {
        return repository.save(regiao);
    }

    @PutMapping("/{id}/risco")
    public Regiao atualizarRisco(
            @PathVariable Long id,
            @RequestParam double chuvaPorHora) {

        Regiao regiao = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Região não encontrada"));

        int ocorrencias =
                (int) ocorrenciaService.contarOcorrenciasRecentes();

        NivelRisco risco =
                riscoService.calcularRisco(
                        chuvaPorHora,
                        ocorrencias
                );

        regiao.setChuvaPorHora(chuvaPorHora);
        regiao.setNivelRisco(risco);

        return repository.save(regiao);
    }
}