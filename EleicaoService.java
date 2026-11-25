package br.edu.unijorge.urnacidada.service;

import br.edu.unijorge.urnacidada.model.Candidato;
import br.edu.unijorge.urnacidada.model.TipoCargo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Gerencia o catálogo apartidário de candidatos fictícios e temáticos da oficina.
 */
public class EleicaoService {
    private final List<Candidato> candidatos = new ArrayList<>();

    public EleicaoService() {
        carregarCandidatosFicticios();
    }

    private void carregarCandidatosFicticios() {
        // Cargo 1: Proporcional (4 dígitos)
        candidatos.add(new Candidato(1010, "Clara Luz", "Partido da Ciência (PCi)", "Laboratórios e internet livre nas escolas", TipoCargo.PROPORCIONAL));
        candidatos.add(new Candidato(2020, "Mateus Veras", "Partido da Cultura (PCul)", "Oficinas de arte, música e esporte juvenil", TipoCargo.PROPORCIONAL));
        candidatos.add(new Candidato(3030, "Sofia Rios", "Partido Verde Jovem (PVJ)", "Arborização e coleta seletiva nos bairros", TipoCargo.PROPORCIONAL));
        candidatos.add(new Candidato(4040, "Lucas Rocha", "Partido do Código Aberto (PCA)", "Inclusão digital e programação no Ensino Médio", TipoCargo.PROPORCIONAL));

        // Cargo 2: Majoritário (2 dígitos)
        candidatos.add(new Candidato(10, "Helena Santos", "Frente Educação & Futuro", "Grêmios estudantis ativos e bibliotecas digitais", TipoCargo.MAJORITARIO));
        candidatos.add(new Candidato(20, "Rafael Costa", "Aliança Cidadania Viva", "Combate à desinformação e projetos comunitários", TipoCargo.MAJORITARIO));
        candidatos.add(new Candidato(30, "Beatriz Lima", "Movimento Inovação Jovem", "Feiras de tecnologia e orientação profissional", TipoCargo.MAJORITARIO));
    }

    public Optional<Candidato> buscarCandidato(TipoCargo cargo, int numero) {
        return candidatos.stream()
                .filter(c -> c.getCargo() == cargo && c.getNumero() == numero)
                .findFirst();
    }

    public List<Candidato> listarPorCargo(TipoCargo cargo) {
        return candidatos.stream()
                .filter(c -> c.getCargo() == cargo)
                .toList();
    }
}
