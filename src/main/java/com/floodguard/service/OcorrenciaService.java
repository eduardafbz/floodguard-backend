package main.java.com.floodguard.service;

import com.floodguard.dto.OcorrenciaRequest;
import com.floodguard.model.Ocorrencia;
import com.floodguard.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public Ocorrencia criar(OcorrenciaRequest request) {

        Ocorrencia ocorrencia = new Ocorrencia();

        ocorrencia.setDescricao(request.getDescricao());
        ocorrencia.setLatitude(request.getLatitude());
        ocorrencia.setLongitude(request.getLongitude());
        ocorrencia.setDataHora(LocalDateTime.now());

        return repository.save(ocorrencia);
    }

    public List<Ocorrencia> listar() {
        return repository.findAll();
    }

    public long contarOcorrenciasRecentes() {

        LocalDateTime limite =
                LocalDateTime.now().minusHours(2);

        return repository
                .findByDataHoraAfter(limite)
                .size();
    }
}