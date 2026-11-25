package br.edu.unijorge.urnacidada.model;

/**
 * Classe abstrata base para demonstrar Abstração e Herança (POO).
 */
public abstract class Pessoa {
    private String nome;

    public Pessoa(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public abstract String getIdentificacao();
}
