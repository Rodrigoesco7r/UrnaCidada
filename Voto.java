package br.edu.unijorge.urnacidada.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de registro anônimo de voto (sem coleta de dados pessoais/identificadores).
 */
public class Voto {
    private TipoCargo cargo;
    private TipoVoto tipoVoto;
    private Integer numeroCandidato;
    private String nomeCandidato;
    private String timestamp;

    public Voto(TipoCargo cargo, TipoVoto tipoVoto, Integer numeroCandidato, String nomeCandidato) {
        this.cargo = cargo;
        this.tipoVoto = tipoVoto;
        this.numeroCandidato = numeroCandidato;
        this.nomeCandidato = nomeCandidato;
        this.timestamp = Instant.now().toString();
    }

    public TipoCargo getCargo() {
        return cargo;
    }

    public TipoVoto getTipoVoto() {
        return tipoVoto;
    }

    public Integer getNumeroCandidato() {
        return numeroCandidato;
    }

    public String getNomeCandidato() {
        return nomeCandidato;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> toFirestoreMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("cargo", cargo.name());
        data.put("tipoVoto", tipoVoto.name());
        data.put("numeroCandidato", numeroCandidato);
        data.put("nomeCandidato", nomeCandidato != null ? nomeCandidato : tipoVoto.name());
        data.put("timestamp", timestamp);
        return data;
    }
}
