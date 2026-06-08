package gui;

import dao.RelatorioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioGUI extends JFrame {

    private JComboBox<String> cbTipoRelatorio;
    private JComboBox<String> cbMes;
    private JLabel lblMes;
    private JButton btnGerar, btnSalvar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblResultado;

    private RelatorioDAO relatorioDAO;
    private List<String[]> dadosAtuais;
    private String[] cabecalhoAtual;

    private static final String[] MESES = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    private static final String[] TIPOS = {
            "7.1 - Todos os Clientes e seus Animais",
            "7.2 - Animais Aniversariantes por Mês",
            "7.3 - Clientes Aniversariantes por Mês"
    };

    public RelatorioGUI() {
        relatorioDAO = new RelatorioDAO();

        setTitle("Sistema Veterinário - Gerar Relatórios");
        setSize(820, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel(new BorderLayout(10, 10));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // painel de opções
        JPanel painelOpcoes = new JPanel(new GridBagLayout());
        painelOpcoes.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Opções do Relatório"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbTipoRelatorio = new JComboBox<>(TIPOS);
        cbMes           = new JComboBox<>(MESES);
        lblMes          = new JLabel("Mês:");
        btnGerar        = new JButton("Gerar Relatório");
        btnSalvar       = new JButton("Salvar como .txt");
        btnGerar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setEnabled(false); // só habilita depois de gerar

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelOpcoes.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        painelOpcoes.add(cbTipoRelatorio, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelOpcoes.add(lblMes, gbc);
        gbc.gridx = 1; gbc.weightx = 0;
        painelOpcoes.add(cbMes, gbc);
        gbc.gridx = 2; painelOpcoes.add(btnGerar, gbc);
        gbc.gridx = 3; painelOpcoes.add(btnSalvar, gbc);

        lblMes.setVisible(false);
        cbMes.setVisible(false);

        painelFundo.add(painelOpcoes, BorderLayout.NORTH);

        // tabela pra dar um preview
        modeloTabela = new DefaultTableModel(0, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Prévia do Relatório"));
        painelFundo.add(scrollTabela, BorderLayout.CENTER);

        // rodapé
        lblResultado = new JLabel(" ");
        lblResultado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblResultado.setForeground(new Color(80, 80, 80));
        lblResultado.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        painelFundo.add(lblResultado, BorderLayout.SOUTH);

        add(painelFundo);


        cbTipoRelatorio.addActionListener(e -> {
            boolean precisaMes = cbTipoRelatorio.getSelectedIndex() > 0;
            lblMes.setVisible(precisaMes);
            cbMes.setVisible(precisaMes);
            modeloTabela.setRowCount(0);
            modeloTabela.setColumnCount(0);
            lblResultado.setText(" ");
            btnSalvar.setEnabled(false);
            dadosAtuais = null;
        });

        btnGerar.addActionListener(e -> gerarRelatorio());
        btnSalvar.addActionListener(e -> salvarTxt());
    }


    private void gerarRelatorio() {
        try {
            int tipo = cbTipoRelatorio.getSelectedIndex();
            int mes  = cbMes.getSelectedIndex() + 1; // Janeiro = 1

            modeloTabela.setRowCount(0);
            modeloTabela.setColumnCount(0);

            switch (tipo) {
                case 0 -> {
                    cabecalhoAtual = new String[]{"Cliente", "CPF", "Animal", "Raça", "Nasc. Animal"};
                    dadosAtuais = relatorioDAO.relatorioClientesEAnimais();
                }
                case 1 -> {
                    cabecalhoAtual = new String[]{"Animal", "Cliente", "Telefone", "Nascimento"};
                    dadosAtuais = relatorioDAO.relatorioAnimaisAniversariantes(mes);
                }
                case 2 -> {
                    cabecalhoAtual = new String[]{"Nome", "CPF", "Nascimento", "Telefone"};
                    dadosAtuais = relatorioDAO.relatorioClientesAniversariantes(mes);
                }
                default -> { return; }
            }

            // monta tabela com cabecalho
            for (String col : cabecalhoAtual) modeloTabela.addColumn(col);
            for (String[] linha : dadosAtuais) modeloTabela.addRow(linha);

            if (dadosAtuais.isEmpty()) {
                lblResultado.setText("Nenhum registro encontrado para os filtros selecionados.");
                btnSalvar.setEnabled(false);
            } else {
                lblResultado.setText(dadosAtuais.size() + " registro(s) encontrado(s). " +
                        "Clique em \"Salvar como .txt\" para exportar.");
                btnSalvar.setEnabled(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarTxt() {
        if (dadosAtuais == null || dadosAtuais.isEmpty()) return;

        // sugere nome de arquivo com timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nomeArquivo = "relatorio_" + (cbTipoRelatorio.getSelectedIndex() + 1) + "_" + timestamp + ".txt";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(nomeArquivo));
        int opcao = fileChooser.showSaveDialog(this);

        if (opcao != JFileChooser.APPROVE_OPTION) return;

        java.io.File arquivo = fileChooser.getSelectedFile();
        // garante extensão .txt
        if (!arquivo.getName().toLowerCase().endsWith(".txt"))
            arquivo = new java.io.File(arquivo.getAbsolutePath() + ".txt");

        try (FileWriter fw = new FileWriter(arquivo)) {
            String titulo = TIPOS[cbTipoRelatorio.getSelectedIndex()];
            String separador = "=".repeat(80);
            String subSep    = "-".repeat(80);

            fw.write(separador + "\n");
            fw.write("  SISTEMA VETERINÁRIO - FATEC\n");
            fw.write("  Relatório: " + titulo + "\n");
            fw.write("  Gerado em: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
            if (cbTipoRelatorio.getSelectedIndex() > 0)
                fw.write("  Mês: " + MESES[cbMes.getSelectedIndex()] + "\n");
            fw.write(separador + "\n\n");

            // cabeçalho da tabela
            fw.write(formatarLinha(cabecalhoAtual) + "\n");
            fw.write(subSep + "\n");

            // linha dos dados
            for (String[] linha : dadosAtuais) {
                fw.write(formatarLinha(linha) + "\n");
            }

            fw.write("\n" + subSep + "\n");
            fw.write("Total de registros: " + dadosAtuais.size() + "\n");
            fw.write(separador + "\n");

            JOptionPane.showMessageDialog(this,
                    "Relatório salvo com sucesso!\n" + arquivo.getAbsolutePath(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar arquivo: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // formata um array de strings em colunas de largura fixa para o .txt
    private String formatarLinha(String[] campos) {
        int[] larguras = {25, 18, 20, 15};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            String valor = campos[i] != null ? campos[i] : "-";
            int largura  = i < larguras.length ? larguras[i] : 20;
            // Trunca se passar da largura
            if (valor.length() > largura) valor = valor.substring(0, largura - 1) + ".";
            sb.append(String.format("%-" + largura + "s", valor));
            if (i < campos.length - 1) sb.append(" | ");
        }
        return sb.toString();
    }
}