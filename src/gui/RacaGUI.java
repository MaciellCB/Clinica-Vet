package gui;

import dao.RacaDAO;
import modelo.Raca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class RacaGUI extends JFrame {
    private JTextField txtNomeRaca;
    private JComboBox<String> cbTipoAnimal;
    private JButton btnSalvar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private RacaDAO racaDAO;
    private int idRacaSelecionada = -1;
    private List<Raca> listaRacasAtual;

    public RacaGUI() {
        racaDAO = new RacaDAO();
        setTitle("Sistema Veterinário - Cadastro de Raças");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel(new BorderLayout(10, 10));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PAINEL DO FORMULÁRIO ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dados da Raça"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNomeRaca = new JTextField();
        setApenasLetras(txtNomeRaca);
        cbTipoAnimal = new JComboBox<>(new String[]{"Cachorro", "Gato"});

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(new JLabel("Nome da Raça:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; painelForm.add(txtNomeRaca, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(new JLabel("Tipo de Animal:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1; painelForm.add(cbTipoAnimal, gbc);

        // --- PAINEL DE BOTÕES ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSalvar = new JButton("Salvar Raça");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar = new JButton("Limpar Tela");
        btnExcluir = new JButton("Excluir Raça");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelForm, BorderLayout.NORTH);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);
        painelFundo.add(painelSuperior, BorderLayout.NORTH);

        // --- PAINEL DA TABELA ---
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome da Raça", "Tipo de Animal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Raças Cadastradas"));
        painelFundo.add(scrollTabela, BorderLayout.CENTER);

        add(painelFundo);

        // --- MENU DE CONTEXTO ---
        JPopupMenu menuDireito = new JPopupMenu();
        JMenuItem itemCopiar = new JMenuItem("Copiar Nome");
        menuDireito.add(itemCopiar);
        itemCopiar.addActionListener(e -> copiarDadosDaLinha());

        // --- EVENTOS DE CLIQUE E DESSELEÇÃO ---
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                if (row == -1) {
                    verificarAlteracoesELimpar();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    tabela.setRowSelectionInterval(row, row);
                    menuDireito.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollTabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { verificarAlteracoesELimpar(); }
        });

        btnSalvar.addActionListener(e -> salvarRaca());
        btnLimpar.addActionListener(e -> verificarAlteracoesELimpar());
        btnExcluir.addActionListener(e -> excluirRaca());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        // --- FOCO INICIAL ---
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtNomeRaca.requestFocus();
            }
        });

        atualizarTabela();
    }

    private void verificarAlteracoesELimpar() {
        if (houveAlteracao()) {
            int resposta = JOptionPane.showConfirmDialog(this, "Deseja salvar a raça antes de limpar?", "Atenção", JOptionPane.YES_NO_CANCEL_OPTION);
            if (resposta == JOptionPane.YES_OPTION) { salvarRaca(); return; }
            else if (resposta == JOptionPane.CANCEL_OPTION) return;
        }
        limparCampos();
    }

    private boolean houveAlteracao() {
        if (idRacaSelecionada == -1) {
            return !txtNomeRaca.getText().trim().isEmpty();
        } else {
            int linha = tabela.getSelectedRow();
            if (linha != -1 && listaRacasAtual != null) {
                Raca r = listaRacasAtual.get(linha);
                return !txtNomeRaca.getText().trim().equals(r.getNomeRaca());
            }
        }
        return false;
    }

    private void copiarDadosDaLinha() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            String nome = (String) tabela.getValueAt(linha, 1);
            StringSelection selection = new StringSelection(nome);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        }
    }

    private void setApenasLetras(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null || text.matches("[a-zA-ZÀ-ÿ\\s]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void salvarRaca() {
        try {
            if (txtNomeRaca.getText().trim().isEmpty()) throw new RuntimeException("O nome da raça é obrigatório!");

            Raca r = new Raca();
            r.setNomeRaca(txtNomeRaca.getText().trim());
            r.setTipoAnimal((String) cbTipoAnimal.getSelectedItem());

            if (idRacaSelecionada == -1) {
                racaDAO.cadastrar(r);
                JOptionPane.showMessageDialog(this, "Raça cadastrada!");
            } else {
                r.setIdRaca(idRacaSelecionada);
                racaDAO.alterar(r);
                JOptionPane.showMessageDialog(this, "Raça atualizada!");
            }
            limparCampos();
            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirRaca() {
        if (idRacaSelecionada != -1) {
            if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluir esta raça?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                racaDAO.excluirLogico(idRacaSelecionada);
                limparCampos();
                atualizarTabela();
            }
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        listaRacasAtual = racaDAO.listarAtivas();
        for (Raca r : listaRacasAtual) {
            modeloTabela.addRow(new Object[]{r.getIdRaca(), r.getNomeRaca(), r.getTipoAnimal()});
        }
    }

    private void preencherFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            Raca r = listaRacasAtual.get(linha);
            idRacaSelecionada = r.getIdRaca();
            txtNomeRaca.setText(r.getNomeRaca());
            cbTipoAnimal.setSelectedItem(r.getTipoAnimal());
            btnSalvar.setText("Atualizar Raça");
        }
    }

    private void limparCampos() {
        idRacaSelecionada = -1;
        txtNomeRaca.setText("");
        cbTipoAnimal.setSelectedIndex(0);
        btnSalvar.setText("Salvar Raça");
        tabela.clearSelection();
    }
}