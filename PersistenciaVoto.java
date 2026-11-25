package br.edu.unijorge.urnacidada.service;

import br.edu.unijorge.urnacidada.model.Voto;
import java.util.List;
import java.util.Map;

/**
 * Interface para desacoplamento e Polimorfismo na persistência dos votos.
 */
public interface PersistenciaVoto {
    void registrarVoto(Voto voto);
    List<Voto> listarVotos();
    Map<String, Integer> apurarResultados();
    String getModoOperacao();
}
