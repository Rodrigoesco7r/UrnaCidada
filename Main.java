package br.edu.unijorge.urnacidada;

import br.edu.unijorge.urnacidada.service.EleicaoService;
import br.edu.unijorge.urnacidada.service.FirebaseVotoService;
import br.edu.unijorge.urnacidada.service.PersistenciaVoto;
import br.edu.unijorge.urnacidada.ui.UrnaFrame;

import javax.swing.*;

/**
 * Classe principal do Simulador Educativo de Urna Eletrônica.
 * Projeto de Extensão Curricular - Engenharia de Software (UniJorge - 2026.2)
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            EleicaoService eleicaoService = new EleicaoService();
            PersistenciaVoto persistenciaVoto = new FirebaseVotoService();

            UrnaFrame frame = new UrnaFrame(eleicaoService, persistenciaVoto);
            frame.setVisible(true);
        });
    }
}
