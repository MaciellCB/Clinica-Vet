package gui;

import dao.AnimalDAO;
import dao.ClienteDAO;
import dao.RacaDAO;
import modelo.Animal;
import modelo.Cliente;
import modelo.Raca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AnimalGUI extends JFrame {

    private JTextField txtNome, txtCor, txtObservacoes;
    private JFormattedTextField txtDataNascimento; 
    private JComboBox<String> cbSexo;
    private JComboBox<Cliente> cbCliente;
    private JComboBox<Raca> cbRaca;
    private JButton btnSalvar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private AnimalDAO animalDAO;
    private ClienteDAO clienteDAO;
    private RacaDAO racaDAO;

    private int idAnimalSelecionado = -1;
    private List<Animal> listaAnimaisAtual;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AnimalGUI() {
        animalDAO  = new AnimalDAO();
        clienteDAO = new ClienteDAO();
        racaDAO    = new RacaDAO();

        setTitle("Sistema Veterinário - Cadastro de Animais");
        setSize(750, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel(new BorderLayout(10, 10));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dados do Animal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome           = new JTextField();
        txtDataNascimento = criarMascara("##/##/####"); // Usando a máscara
        txtCor            = new JTextField();
        txtObservacoes    = new JTextField();
        cbSexo            = new JComboBox<>(new String[]{"M", "F"});

        // Só letras no nome e na cor
        setApenasLetras(txtNome);
        setApenasLetras(txtCor);

        cbCliente = new JComboBox<>();
        carregarClientes();

        cbRaca = new JComboBox<>();
        carregarRacas();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; painelForm.add(txtNome, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(new JLabel("Sexo:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(cbSexo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(new JLabel("Nascimento:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(txtDataNascimento, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(new JLabel("Cor:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1; painelForm.add(txtCor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; painelForm.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1; gbc.gridwidth = 3; painelForm.add(cbCliente, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; painelForm.add(new JLabel("Raça:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1; gbc.gridwidth = 3; painelForm.add(cbRaca, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; painelForm.add(new JLabel("Observações:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1; gbc.gridwidth = 3; painelForm.add(txtObservacoes, gbc);
        gbc.gridwidth = 1;

        // botoes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSalvar  = new JButton("Salvar Animal");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar  = new JButton("Limpar Tela");
        btnExcluir = new JButton("Excluir Animal");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelForm, BorderLayout.NORTH);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);
        painelFundo.add(painelSuperior, BorderLayout.NORTH);

        // tabela
        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Nome", "Cliente", "Raça", "Sexo", "Idade", "Cor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50); // ID pequeno

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Animais Cadastrados"));
        painelFundo.add(scrollTabela, BorderLayout.CENTER);

        add(painelFundo);

        JPopupMenu menuDireito = new JPopupMenu();
        JMenuItem itemCopiar = new JMenuItem("Copiar Nome");
        menuDireito.add(itemCopiar);
        itemCopiar.addActionListener(e -> copiarNomeDaLinha());

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

        btnSalvar.addActionListener(e -> salvarAnimal());
        btnLimpar.addActionListener(e -> verificarAlteracoesELimpar());
        btnExcluir.addActionListener(e -> excluirAnimal());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) { txtNome.requestFocus(); }
        });

        atualizarTabela();
    }

    // ações

    private void salvarAnimal() {
        try {
            if (txtNome.getText().trim().isEmpty())
                throw new RuntimeException("O nome do animal é obrigatório.");
            if (cbCliente.getSelectedItem() == null)
                throw new RuntimeException("Selecione um cliente.");
            if (cbRaca.getSelectedItem() == null)
                throw new RuntimeException("Selecione uma raça.");

            Animal a = new Animal();
            a.setNome(txtNome.getText().trim());
            a.setSexo((String) cbSexo.getSelectedItem());
            a.setCor(txtCor.getText().trim());
            a.setObservacoes(txtObservacoes.getText().trim());
            a.setIdCliente(((Cliente) cbCliente.getSelectedItem()).getIdCliente());
            a.setIdRaca(((Raca) cbRaca.getSelectedItem()).getIdRaca());

            // Remove a máscara e checa se o usuário digitou algo
            String dataTexto = txtDataNascimento.getText().replace("_", "").replace("/", "").trim();
            if (!dataTexto.isEmpty()) {
                if (dataTexto.length() < 8) {
                    throw new RuntimeException("Data de nascimento incompleta.");
                }
                try {
                    a.setDataNascimento(LocalDate.parse(txtDataNascimento.getText(), FMT));
                } catch (DateTimeParseException ex) {
                    throw new RuntimeException("Data inválida. Verifique se o dia e mês estão corretos.");
                }
            }

            if (idAnimalSelecionado == -1) {
                animalDAO.cadastrar(a);
                JOptionPane.showMessageDialog(this, "Animal cadastrado com sucesso!");
            } else {
                a.setIdAnimal(idAnimalSelecionado);
                animalDAO.alterar(a);
                JOptionPane.showMessageDialog(this, "Animal atualizado com sucesso!");
            }
            limparCampos();
            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirAnimal() {
        if (idAnimalSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um animal na tabela para excluir.");
            return;
        }
        int resp = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este animal?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            animalDAO.excluirLogico(idAnimalSelecionado);
            limparCampos();
            atualizarTabela();
        }
    }

    private void verificarAlteracoesELimpar() {
        if (houveAlteracao()) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Deseja salvar o animal antes de limpar?", "Atenção",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (resp == JOptionPane.YES_OPTION) { salvarAnimal(); return; }
            else if (resp == JOptionPane.CANCEL_OPTION) return;
        }
        limparCampos();
    }

    private boolean houveAlteracao() {
        if (idAnimalSelecionado == -1) {
            return !txtNome.getText().trim().isEmpty();
        } else {
            int linha = tabela.getSelectedRow();
            if (linha != -1 && listaAnimaisAtual != null) {
                Animal a = listaAnimaisAtual.get(linha);
                return !txtNome.getText().trim().equals(a.getNome());
            }
        }
        return false;
    }

    private void preencherFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1 || listaAnimaisAtual == null) return;

        Animal a = listaAnimaisAtual.get(linha);
        idAnimalSelecionado = a.getIdAnimal();

        txtNome.setText(a.getNome());
        txtCor.setText(a.getCor() != null ? a.getCor() : "");
        txtObservacoes.setText(a.getObservacoes() != null ? a.getObservacoes() : "");
        cbSexo.setSelectedItem(a.getSexo());

        if (a.getDataNascimento() != null)
            txtDataNascimento.setText(a.getDataNascimento().format(FMT));
        else
            txtDataNascimento.setValue(null); // Reseta a máscara

        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getIdCliente() == a.getIdCliente()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cbRaca.getItemCount(); i++) {
            if (cbRaca.getItemAt(i).getIdRaca() == a.getIdRaca()) {
                cbRaca.setSelectedIndex(i);
                break;
            }
        }

        btnSalvar.setText("Atualizar Animal");
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        listaAnimaisAtual = animalDAO.listarAtivos();
        for (Animal a : listaAnimaisAtual) {
            String idade = a.getDataNascimento() != null ? a.getIdade() + " ano(s)" : "-";
            modeloTabela.addRow(new Object[]{
                    a.getIdAnimal(),
                    a.getNome(),
                    a.getNomeCliente(),
                    a.getNomeRaca(),
                    a.getSexo(),
                    idade,
                    a.getCor() != null ? a.getCor() : ""
            });
        }
    }

    private void carregarClientes() {
        cbCliente.removeAllItems();
        for (Cliente c : clienteDAO.listarAtivos()) {
            cbCliente.addItem(c);
        }

        cbCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) setText(((Cliente) value).getNome());
                return this;
            }
        });
    }

    private void carregarRacas() {
        cbRaca.removeAllItems();
        for (Raca r : racaDAO.listarAtivas()) {
            cbRaca.addItem(r);
        }

        cbRaca.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Raca r) setText(r.getNomeRaca() + " (" + r.getTipoAnimal() + ")");
                return this;
            }
        });
    }

    private void limparCampos() {
        idAnimalSelecionado = -1;
        txtNome.setText("");
        txtDataNascimento.setValue(null); // Reseta a máscara corretamente
        txtCor.setText("");
        txtObservacoes.setText("");
        cbSexo.setSelectedIndex(0);
        if (cbCliente.getItemCount() > 0) cbCliente.setSelectedIndex(0);
        if (cbRaca.getItemCount() > 0)    cbRaca.setSelectedIndex(0);
        btnSalvar.setText("Salvar Animal");
        tabela.clearSelection();
        txtNome.requestFocus();
    }

    private void copiarNomeDaLinha() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            String nome = (String) tabela.getValueAt(linha, 1);
            java.awt.datatransfer.StringSelection sel =
                    new java.awt.datatransfer.StringSelection(nome);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        }
    }

    private void setApenasLetras(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length,
                                String text, AttributeSet attrs) throws BadLocationException {
                if (text == null || text.matches("[a-zA-ZÀ-ÿ\\s]*"))
                    super.replace(fb, offset, length, text, attrs);
            }
        });
    }
    
    // Método para criar a máscara de data
    private JFormattedTextField criarMascara(String mascara) {
        try {
            MaskFormatter mf = new MaskFormatter(mascara);
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (Exception e) {
            return new JFormattedTextField();
        }
    }
}