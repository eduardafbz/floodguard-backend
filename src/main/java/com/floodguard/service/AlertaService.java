package main.java.com.floodguard.service;

import com.floodguard.model.Alerta;
import com.floodguard.model.NivelRisco;
import com.floodguard.repository.AlertaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository repository;

    public AlertaService(AlertaRepository repository) {
        this.repository = repository;
    }

    public Alerta criarAlerta(
            NivelRisco nivel,
            String mensagem,
            double latitude,
            double longitude) {

        Alerta alerta = new Alerta();

        alerta.setTitulo(
                nivel == NivelRisco.EMERGENCIA
                        ? "🚨 Risco elevado de alagamento"
                        : "⚠️ Alerta de alagamento"
        );

        alerta.setMensagem(mensagem);
        alerta.setNivelRisco(nivel);
        alerta.setDataHora(LocalDateTime.now());
        alerta.setLatitude(latitude);
        alerta.setLongitude(longitude);

        return repository.save(alerta);
    }

    public List<Alerta> listar() {
        return repository.findAll();
    }
}