package br.edu.unijorge.urnacidada.model;

public enum TipoCargo {
    PROPORCIONAL("Deputado(a) Estudantil (Proporcional)", 4),
    MAJORITARIO("Representante Geral (Majoritário)", 2);

    private final String descricao;
    private final int digitos;

    TipoCargo(String descricao, int digitos) {
        this.descricao = descricao;
        this.digitos = digitos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getDigitos() {
        return digitos;
    }
}
