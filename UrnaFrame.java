package br.edu.unijorge.urnacidada.ui;

import br.edu.unijorge.urnacidada.model.Candidato;
import br.edu.unijorge.urnacidada.model.TipoCargo;
import br.edu.unijorge.urnacidada.model.TipoVoto;
import br.edu.unijorge.urnacidada.model.Voto;
import br.edu.unijorge.urnacidada.service.EleicaoService;
import br.edu.unijorge.urnacidada.service.PersistenciaVoto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interface gráfica em Java Swing simulando o fluxo sequencial da urna eletrônica:
 * 1. Cargo Proporcional -> 2. Cargo Majoritário -> 3. Tela FIM + Apuração.
 */
public class UrnaFrame extends JFrame {
    private final EleicaoService eleicaoService;
    private final PersistenciaVoto persistenciaVoto;

    private final TipoCargo[] ordemCargos = {TipoCargo.PROPORCIONAL, TipoCargo.MAJORITARIO};
    private int indiceCargoAtual = 0;
    private StringBuilder numeroDigitado = new StringBuilder();
    private boolean votoBrancoSelecionado = false;
    private boolean votacaoFinalizada = false;

    // Componentes visuais da tela
    private JLabel lblTituloCargo;
    private JLabel lblInstrucaoDigitos;
    private JPanel painelCaixasDigitos;
    private JLabel[] caixasDigitos;
    private JLabel lblNomeCandidato;
    private JLabel lblPartido;
    private JLabel lblProposta;
    private JLabel lblAvisoRodape;
    private JLabel lblStatusNuvem;

    public UrnaFrame(EleicaoService eleicaoService, PersistenciaVoto persistenciaVoto) {
        this.eleicaoService = eleicaoService;
        this.persistenciaVoto = persistenciaVoto;

        configurarJanela();
        construirInterface();
        iniciarCargoAtual();
    }

    private void configurarJanela() {
        setTitle("Urna Cidadã - Simulador Educativo (UniJorge - POO 2026.2)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(940, 560);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirInterface() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(new Color(230, 232, 235));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Cabeçalho educativo
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 55, 95));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblBanner = new JLabel("SIMULADOR EDUCATIVO DE URNA ELETRÔNICA — PROJETO URNA CIDADÃ (APARTIDÁRIO)");
        lblBanner.setForeground(Color.WHITE);
        lblBanner.setFont(new Font("SansSerif", Font.BOLD, 13));

        lblStatusNuvem = new JLabel("Banco: " + persistenciaVoto.getModoOperacao());
        lblStatusNuvem.setForeground(new Color(180, 230, 255));
        lblStatusNuvem.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(lblBanner, BorderLayout.WEST);
        header.add(lblStatusNuvem, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Área Central: Tela da Urna (Esquerda) + Teclado Numérico (Direita)
        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);

        centro.add(criarPainelTela());
        centro.add(criarPainelTeclado());

        root.add(centro, BorderLayout.CENTER);

        // Rodapé com botões de apoio da oficina (Cola de candidatos e Apuração)
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        JButton btnCola = new JButton("Ver Candidatos Fictícios");
        btnCola.addActionListener(e -> exibirColaCandidatos());

        JButton btnApuracao = new JButton("Apurar Resultados da Oficina");
        btnApuracao.addActionListener(e -> exibirApuracao());

        JButton btnNovoEleitor = new JButton("Próximo Estudante (Reiniciar)");
        btnNovoEleitor.addActionListener(e -> reiniciarParaProximoEleitor());

        footer.add(btnCola);
        footer.add(btnApuracao);
        footer.add(btnNovoEleitor);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel criarPainelTela() {
        JPanel tela = new JPanel(new BorderLayout(10, 10));
        tela.setBackground(Color.WHITE);
        tela.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(120, 125, 130), 3),
                new EmptyBorder(18, 20, 15, 20)
        ));

        JPanel topoTela = new JPanel(new GridLayout(3, 1, 4, 4));
        topoTela.setOpaque(false);

        JLabel lblSeuVoto = new JLabel("SEU VOTO PARA");
        lblSeuVoto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSeuVoto.setForeground(Color.DARK_GRAY);

        lblTituloCargo = new JLabel("CARGO");
        lblTituloCargo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTituloCargo.setForeground(new Color(20, 45, 80));

        lblInstrucaoDigitos = new JLabel("Número:");
        lblInstrucaoDigitos.setFont(new Font("SansSerif", Font.PLAIN, 14));

        topoTela.add(lblSeuVoto);
        topoTela.add(lblTituloCargo);
        topoTela.add(lblInstrucaoDigitos);

        // Caixas dos dígitos
        painelCaixasDigitos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        painelCaixasDigitos.setOpaque(false);

        // Informações do candidato
        JPanel infoCandidato = new JPanel(new GridLayout(4, 1, 4, 4));
        infoCandidato.setOpaque(false);

        lblNomeCandidato = new JLabel("");
        lblNomeCandidato.setFont(new Font("SansSerif", Font.BOLD, 17));

        lblPartido = new JLabel("");
        lblPartido.setFont(new Font("SansSerif", Font.PLAIN, 14));

        lblProposta = new JLabel("");
        lblProposta.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblProposta.setForeground(new Color(70, 70, 70));

        infoCandidato.add(painelCaixasDigitos);
        infoCandidato.add(lblNomeCandidato);
        infoCandidato.add(lblPartido);
        infoCandidato.add(lblProposta);

        // Instruções de teclas no rodapé da tela
        lblAvisoRodape = new JLabel("<html><hr>Aperte a tecla:<br><b>CONFIRMA</b> para confirmar este voto<br><b>CORRIGE</b> para reiniciar este voto</html>");
        lblAvisoRodape.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblAvisoRodape.setForeground(Color.DARK_GRAY);

        tela.add(topoTela, BorderLayout.NORTH);
        tela.add(infoCandidato, BorderLayout.CENTER);
        tela.add(lblAvisoRodape, BorderLayout.SOUTH);

        return tela;
    }

    private JPanel criarPainelTeclado() {
        JPanel containerTeclado = new JPanel(new BorderLayout(10, 12));
        containerTeclado.setBackground(new Color(55, 58, 64));
        containerTeclado.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel lblCabecalhoUrna = new JLabel("JUSTIÇA ELEITORAL — SIMULAÇÃO EDUCATIVA", SwingConstants.CENTER);
        lblCabecalhoUrna.setForeground(Color.WHITE);
        lblCabecalhoUrna.setFont(new Font("SansSerif", Font.BOLD, 14));
        containerTeclado.add(lblCabecalhoUrna, BorderLayout.NORTH);

        // Grade numérica 1..9 e 0
        JPanel gridNumeros = new JPanel(new GridLayout(4, 3, 12, 12));
        gridNumeros.setOpaque(false);

        for (int i = 1; i <= 9; i++) {
            final String digito = String.valueOf(i);
            gridNumeros.add(criarBotaoNumerico(digito));
        }
        gridNumeros.add(new JLabel("")); // espaço vazio esquerda
        gridNumeros.add(criarBotaoNumerico("0"));
        gridNumeros.add(new JLabel("")); // espaço vazio direita

        containerTeclado.add(gridNumeros, BorderLayout.CENTER);

        // Botões de Ação: BRANCO, CORRIGE, CONFIRMA
        JPanel painelAcoes = new JPanel(new GridLayout(1, 3, 10, 0));
        painelAcoes.setOpaque(false);

        JButton btnBranco = new JButton("BRANCO");
        btnBranco.setBackground(Color.WHITE);
        btnBranco.setForeground(Color.BLACK);
        btnBranco.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBranco.setFocusPainted(false);
        btnBranco.addActionListener(e -> acionarBranco());

        JButton btnCorrige = new JButton("CORRIGE");
        btnCorrige.setBackground(new Color(230, 115, 35));
        btnCorrige.setForeground(Color.BLACK);
        btnCorrige.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCorrige.setFocusPainted(false);
        btnCorrige.addActionListener(e -> acionarCorrige());

        JButton btnConfirma = new JButton("CONFIRMA");
        btnConfirma.setBackground(new Color(45, 165, 75));
        btnConfirma.setForeground(Color.BLACK);
        btnConfirma.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnConfirma.setFocusPainted(false);
        btnConfirma.addActionListener(e -> acionarConfirma());

        painelAcoes.add(btnBranco);
        painelAcoes.add(btnCorrige);
        painelAcoes.add(btnConfirma);

        containerTeclado.add(painelAcoes, BorderLayout.SOUTH);
        return containerTeclado;
    }

    private JButton criarBotaoNumerico(String digito) {
        JButton btn = new JButton(digito);
        btn.setBackground(new Color(28, 30, 34));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> digitarNumero(digito));
        return btn;
    }

    private void iniciarCargoAtual() {
        if (indiceCargoAtual >= ordemCargos.length) {
            exibirTelaFim();
            return;
        }

        votacaoFinalizada = false;
        votoBrancoSelecionado = false;
        numeroDigitado.setLength(0);

        TipoCargo cargo = ordemCargos[indiceCargoAtual];
        lblTituloCargo.setText(cargo.getDescricao());
        lblInstrucaoDigitos.setText("Digite o número com " + cargo.getDigitos() + " dígitos:");

        painelCaixasDigitos.removeAll();
        caixasDigitos = new JLabel[cargo.getDigitos()];
        for (int i = 0; i < cargo.getDigitos(); i++) {
            JLabel box = new JLabel("", SwingConstants.CENTER);
            box.setPreferredSize(new Dimension(38, 46));
            box.setFont(new Font("SansSerif", Font.BOLD, 24));
            box.setBorder(new LineBorder(Color.BLACK, 2));
            caixasDigitos[i] = box;
            painelCaixasDigitos.add(box);
        }

        lblNomeCandidato.setText("");
        lblPartido.setText("");
        lblProposta.setText("");
        lblAvisoRodape.setVisible(false);

        painelCaixasDigitos.revalidate();
        painelCaixasDigitos.repaint();
    }

    private void digitarNumero(String digito) {
        if (votacaoFinalizada || votoBrancoSelecionado) return;

        TipoCargo cargo = ordemCargos[indiceCargoAtual];
        if (numeroDigitado.length() < cargo.getDigitos()) {
            numeroDigitado.append(digito);
            atualizarTelaDigitacao();
        }
    }

    private void atualizarTelaDigitacao() {
        TipoCargo cargo = ordemCargos[indiceCargoAtual];
        for (int i = 0; i < caixasDigitos.length; i++) {
            if (i < numeroDigitado.length()) {
                caixasDigitos[i].setText(String.valueOf(numeroDigitado.charAt(i)));
            } else {
                caixasDigitos[i].setText("");
            }
        }

        if (numeroDigitado.length() == cargo.getDigitos()) {
            int numero = Integer.parseInt(numeroDigitado.toString());
            Optional<Candidato> candidatoOpt = eleicaoService.buscarCandidato(cargo, numero);

            if (candidatoOpt.isPresent()) {
                Candidato c = candidatoOpt.get();
                lblNomeCandidato.setText("Nome: " + c.getNome());
                lblPartido.setText("Grupo Temático: " + c.getPartidoTematico());
                lblProposta.setText("Proposta: " + c.getPropostaResumo());
            } else {
                lblNomeCandidato.setText("NÚMERO ERRADO");
                lblPartido.setText("VOTO NULO");
                lblProposta.setText("Nenhum candidato fictício cadastrado com este número.");
            }
            lblAvisoRodape.setVisible(true);
        }
    }

    private void acionarBranco() {
        if (votacaoFinalizada) return;
        if (numeroDigitado.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Para votar em BRANCO, o campo de número deve estar vazio.\nAperte CORRIGE primeiro.",
                    "Aviso da Urna", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        votoBrancoSelecionado = true;
        lblNomeCandidato.setText("VOTO EM BRANCO");
        lblPartido.setText("");
        lblProposta.setText("Você optou por votar em branco neste cargo.");
        lblAvisoRodape.setVisible(true);
    }

    private void acionarCorrige() {
        if (votacaoFinalizada) return;
        iniciarCargoAtual();
    }

    private void acionarConfirma() {
        if (votacaoFinalizada) return;

        TipoCargo cargo = ordemCargos[indiceCargoAtual];

        if (votoBrancoSelecionado) {
            persistenciaVoto.registrarVoto(new Voto(cargo, TipoVoto.BRANCO, null, "BRANCO"));
            avancarCargo();
            return;
        }

        if (numeroDigitado.length() < cargo.getDigitos()) {
            JOptionPane.showMessageDialog(this,
                    "Digite todos os " + cargo.getDigitos() + " dígitos ou aperte BRANCO.",
                    "Voto Incompleto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int numero = Integer.parseInt(numeroDigitado.toString());
        Optional<Candidato> candidatoOpt = eleicaoService.buscarCandidato(cargo, numero);

        if (candidatoOpt.isPresent()) {
            Candidato c = candidatoOpt.get();
            persistenciaVoto.registrarVoto(new Voto(cargo, TipoVoto.NOMINAL, c.getNumero(), c.getNome()));
        } else {
            persistenciaVoto.registrarVoto(new Voto(cargo, TipoVoto.NULO, numero, "NULO"));
        }

        avancarCargo();
    }

    private void avancarCargo() {
        Toolkit.getDefaultToolkit().beep(); // Som característico de confirmação
        indiceCargoAtual++;
        iniciarCargoAtual();
    }

    private void exibirTelaFim() {
        votacaoFinalizada = true;
        painelCaixasDigitos.removeAll();
        painelCaixasDigitos.repaint();

        lblTituloCargo.setText("VOTAÇÃO CONCLUÍDA");
        lblInstrucaoDigitos.setText("");
        lblNomeCandidato.setText("FIM");
        lblNomeCandidato.setFont(new Font("SansSerif", Font.BOLD, 54));
        lblPartido.setText("Seu voto simulado foi registrado com sucesso.");
        lblProposta.setText("Clique em 'Próximo Estudante (Reiniciar)' para o próximo colega votar.");
        lblAvisoRodape.setVisible(false);
    }

    private void reiniciarParaProximoEleitor() {
        lblNomeCandidato.setFont(new Font("SansSerif", Font.BOLD, 17));
        indiceCargoAtual = 0;
        iniciarCargoAtual();
    }

    private void exibirColaCandidatos() {
        StringBuilder sb = new StringBuilder("=== CANDIDATOS FICTÍCIOS DA OFICINA (APARTIDÁRIO) ===\n\n");
        for (TipoCargo cargo : ordemCargos) {
            sb.append(cargo.getDescricao()).append(":\n");
            List<Candidato> lista = eleicaoService.listarPorCargo(cargo);
            for (Candidato c : lista) {
                sb.append("  • ").append(c.getIdentificacao()).append(" — ").append(c.getPropostaResumo()).append("\n");
            }
            sb.append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Cola de Candidatos Fictícios", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exibirApuracao() {
        Map<String, Integer> resultados = persistenciaVoto.apurarResultados();
        StringBuilder sb = new StringBuilder("=== BOLETIM DE URNA SIMULADO (APURAÇÃO AO VIVO) ===\n");
        sb.append("Fonte de dados: ").append(persistenciaVoto.getModoOperacao()).append("\n\n");

        if (resultados.isEmpty()) {
            sb.append("Nenhum voto registrado até o momento.\n");
        } else {
            resultados.forEach((chave, total) -> sb.append(String.format("%-45s : %d voto(s)\n", chave, total)));
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Apuração dos Votos", JOptionPane.INFORMATION_MESSAGE);
    }
}
