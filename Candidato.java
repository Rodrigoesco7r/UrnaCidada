package br.edu.unijorge.urnacidada.model;

/**
 * Representa um candidato fictício e temático (ação apartidária).
 * Herda de Pessoa e aplica Encapsulamento e Polimorfismo.
 */
public class Candidato extends Pessoa {
    private int numero;
    private String partidoTematico;
    private String propostaResumo;
    private TipoCargo cargo;

    public Candidato(int numero, String nome, String partidoTematico, String propostaResumo, TipoCargo cargo) {
        super(nome);
        this.numero = numero;
        this.partidoTematico = partidoTematico;
        this.propostaResumo = propostaResumo;
        this.cargo = cargo;
    }

    public int getNumero() {
        return numero;
    }

    public String getPartidoTematico() {
        return partidoTematico;
    }

    public String getPropostaResumo() {
        return propostaResumo;
    }

    public TipoCargo getCargo() {
        return cargo;
    }

    @Override
    public String getIdentificacao() {
        return String.format("%d - %s (%s)", numero, getNome(), partidoTematico);
    }
}
