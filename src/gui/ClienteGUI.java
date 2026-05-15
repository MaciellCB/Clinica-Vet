package gui;

import dao.ClienteDAO;
import modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteGUI extends JFrame {
    private JTextField txtNome, txtEndereco, txtBairro, txtCidade, txtEstado;
    private JFormattedTextField txtCpf, txtDataNasc, txtTelefone, txtCep;
    private JButton btnSalvar, btnExcluir, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private ClienteDAO clienteDAO;
    private int idClienteSelecionado = -1;
    private List<Cliente> listaClientesAtual; // Guarda todos os dados vindos do banco

    public ClienteGUI() {
        clienteDAO = new ClienteDAO();
        setTitle("Sistema Veterinário - Cadastro de Clientes");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel(new BorderLayout(10, 10));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // painel formulario
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dados do Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(); setApenasLetras(txtNome);
        txtCpf = criarMascara("###.###.###-##");
        txtDataNasc = criarMascara("##/##/####");
        txtTelefone = criarMascara("(##) #####-####");
        txtEndereco = new JTextField();
        txtBairro = new JTextField(); setApenasLetras(txtBairro);
        txtCidade = new JTextField(); setApenasLetras(txtCidade);
        txtEstado = new JTextField(2); setFiltroUF(txtEstado);
        txtCep = criarMascara("#####-###");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; painelForm.add(txtNome, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; painelForm.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.5; painelForm.add(txtCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(new JLabel("Nascimento:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1; painelForm.add(txtDataNasc, gbc);
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0; painelForm.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.5; painelForm.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; painelForm.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3; painelForm.add(txtEndereco, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; painelForm.add(new JLabel("Bairro:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1; painelForm.add(txtBairro, gbc);
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0; painelForm.add(new JLabel("Cidade:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; gbc.weightx = 0.5; painelForm.add(txtCidade, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; painelForm.add(new JLabel("Estado (UF):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1; painelForm.add(txtEstado, gbc);
        gbc.gridx = 2; gbc.gridy = 4; gbc.weightx = 0; painelForm.add(new JLabel("CEP:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; gbc.weightx = 0.5; painelForm.add(txtCep, gbc);

        // painel dos botoes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSalvar = new JButton("Salvar Cadastro");
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpar = new JButton("Limpar Tela");
        btnExcluir = new JButton("Excluir Cliente");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelForm, BorderLayout.NORTH);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);
        painelFundo.add(painelSuperior, BorderLayout.NORTH);

        // painel tabela
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "CPF", "Telefone", "Bairro", "Cidade"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Clientes Cadastrados"));
        painelFundo.add(scrollTabela, BorderLayout.CENTER);

        add(painelFundo);

        // aq é pra quando clicar com o botao direito, eu nao sabia muito bem oq por alem do botao de copiar
        JPopupMenu menuDireito = new JPopupMenu();
        JMenuItem itemCopiar = new JMenuItem("Copiar Dados");
        menuDireito.add(itemCopiar);
        itemCopiar.addActionListener(e -> copiarDadosDaLinha());

        // botao pra limpeza automatica etc
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
            public void mousePressed(MouseEvent e) {
                verificarAlteracoesELimpar(); // Clicou no fundo cinza
            }
        });

        // salvar, excluir e limpar
        btnSalvar.addActionListener(e -> salvarCliente());
        btnLimpar.addActionListener(e -> verificarAlteracoesELimpar());
        btnExcluir.addActionListener(e -> excluirCliente());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormulario();
            }
        });

        atualizarTabela();
    }

    // aqui ele faz com que quando atualize uma informaçao e etente sair, ele perguntra se quer salvar

    private void verificarAlteracoesELimpar() {
        if (houveAlteracao()) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Você tem alterações não salvas. Deseja salvar agora antes de limpar?",
                    "Atenção", JOptionPane.YES_NO_CANCEL_OPTION);

            if (resposta == JOptionPane.YES_OPTION) {
                salvarCliente();
                return; // esse é so para limpar quando faz o salvamento
            } else if (resposta == JOptionPane.CANCEL_OPTION || resposta == JOptionPane.CLOSED_OPTION) {
                return;
            }
        }
        limparCampos();
    }

    private boolean houveAlteracao() {
        if (idClienteSelecionado == -1) {
            // Se for cadastro novo, verifica se digitou pelo menos o nome ou endereço
            return !txtNome.getText().trim().isEmpty() || !txtEndereco.getText().trim().isEmpty();
        } else {
            // Se estiver editando, compara com os dados originais da tabela
            int linha = tabela.getSelectedRow();
            if (linha != -1 && listaClientesAtual != null) {
                Cliente c = listaClientesAtual.get(linha);
                if (!txtNome.getText().trim().equals(c.getNome() != null ? c.getNome() : "")) return true;
                if (!txtEndereco.getText().trim().equals(c.getEndereco() != null ? c.getEndereco() : "")) return true;
                if (!txtBairro.getText().trim().equals(c.getBairro() != null ? c.getBairro() : "")) return true;
                if (!txtCidade.getText().trim().equals(c.getCidade() != null ? c.getCidade() : "")) return true;
                if (!txtEstado.getText().trim().equals(c.getEstado() != null ? c.getEstado() : "")) return true;
            }
        }
        return false;
    }

    private void copiarDadosDaLinha() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            String nome = (String) tabela.getValueAt(linha, 1);
            String cpf = (String) tabela.getValueAt(linha, 2);
            String textoCopiado = "Nome: " + nome + " | CPF: " + cpf;
            StringSelection selection = new StringSelection(textoCopiado);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        }
    }

    private JFormattedTextField criarMascara(String mascara) {
        try {
            MaskFormatter mf = new MaskFormatter(mascara);
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (Exception e) {
            return new JFormattedTextField();
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

    private void setFiltroUF(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                text = text.replaceAll("[^a-zA-Z]", "");
                int tamanhoAtual = fb.getDocument().getLength();
                int espacoLivre = 2 - tamanhoAtual + length;

                if (espacoLivre > 0) {
                    if (text.length() > espacoLivre) text = text.substring(0, espacoLivre);
                    super.replace(fb, offset, length, text.toUpperCase(), attrs);
                }
            }
        });
    }

    //aq é so pro banco, pra salvar

    private void salvarCliente() {
        try {
            Cliente c = new Cliente();
            c.setNome(txtNome.getText());
            c.setCpf(txtCpf.getText());

            String dataNasc = txtDataNasc.getText().replace("_", "").replace("/", "").trim();
            if (!dataNasc.isEmpty()) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                c.setDataNascimento(LocalDate.parse(txtDataNasc.getText(), fmt));
            }

            c.setTelefone(txtTelefone.getText());
            c.setEndereco(txtEndereco.getText());
            c.setBairro(txtBairro.getText());
            c.setCidade(txtCidade.getText());
            c.setEstado(txtEstado.getText());
            c.setCep(txtCep.getText());

            if (idClienteSelecionado == -1) {
                clienteDAO.cadastrar(c);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                c.setIdCliente(idClienteSelecionado);
                clienteDAO.alterar(c);
                JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            limparCampos(); // Limpa a tela automaticamente após salvar
            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Atenção", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCliente() {
        if (idClienteSelecionado != -1) {
            int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este cliente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                clienteDAO.excluirLogico(idClienteSelecionado);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
                limparCampos();
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.");
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        listaClientesAtual = clienteDAO.listarAtivos(); // Guarda a lista completa com todos os dados

        for (Cliente c : listaClientesAtual) {
            modeloTabela.addRow(new Object[]{c.getIdCliente(), c.getNome(), c.getCpf(), c.getTelefone(), c.getBairro(), c.getCidade()});
        }
    }

    private void preencherFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha != -1) {
            Cliente c = listaClientesAtual.get(linha); // Pega o cliente completo direto da lista

            idClienteSelecionado = c.getIdCliente();
            txtNome.setText(c.getNome());
            txtCpf.setText(c.getCpf());

            if (c.getDataNascimento() != null) {
                txtDataNasc.setText(c.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                txtDataNasc.setValue(null);
            }

            txtTelefone.setText(c.getTelefone());
            txtEndereco.setText(c.getEndereco() != null ? c.getEndereco() : "");
            txtBairro.setText(c.getBairro() != null ? c.getBairro() : "");
            txtCidade.setText(c.getCidade() != null ? c.getCidade() : "");
            txtEstado.setText(c.getEstado() != null ? c.getEstado() : "");
            txtCep.setText(c.getCep() != null ? c.getCep() : "");

            // aq ele muda o botao quando tiver pra atualizar,do uma alteraçao visual pra ficar pratico/intuitivo
            btnSalvar.setText("Atualizar Cadastro");
        }
    }

    private void limparCampos() {
        idClienteSelecionado = -1;
        txtNome.setText(""); txtCpf.setValue(null); txtDataNasc.setValue(null);
        txtTelefone.setValue(null); txtEndereco.setText(""); txtBairro.setText("");
        txtCidade.setText(""); txtEstado.setText(""); txtCep.setValue(null);

        // Volta o botão ao normal
        btnSalvar.setText("Salvar Cadastro");
        tabela.clearSelection();
    }
}