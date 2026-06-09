package gui;

import dao.ConsultaDAO;
import modelo.Animal;
import modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ConsultaGUI extends JFrame {

    private JTextField txtFiltro;
    private JRadioButton rbNome, rbCpf;
    private JButton btnBuscar, btnLimpar;
    private JLabel lblClienteInfo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private ConsultaDAO consultaDAO;
    private List<Cliente> clientesEncontrados;

    public ConsultaGUI() {
        consultaDAO = new ConsultaDAO();

        setTitle("Sistema Veterinário - Consulta de Animais por Cliente");
        setSize(800, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelFundo = new JPanel(new BorderLayout(10, 10));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel painelFiltro = new JPanel(new GridBagLayout());
        painelFiltro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filtro de Busca"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        rbNome = new JRadioButton("Por Nome", true);
        rbCpf  = new JRadioButton("Por CPF");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbNome);
        grupo.add(rbCpf);

        txtFiltro  = new JTextField(25);
        btnBuscar  = new JButton("Buscar");
        btnLimpar  = new JButton("Limpar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        painelFiltro.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1; painelFiltro.add(rbNome, gbc);
        gbc.gridx = 2; painelFiltro.add(rbCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painelFiltro.add(new JLabel("Valor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        painelFiltro.add(txtFiltro, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 3; painelFiltro.add(btnBuscar, gbc);
        gbc.gridx = 4; painelFiltro.add(btnLimpar, gbc);

        lblClienteInfo = new JLabel(" ");
        lblClienteInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblClienteInfo.setForeground(new Color(80, 80, 80));

        JPanel painelSuperior = new JPanel(new BorderLayout(5, 5));
        painelSuperior.add(painelFiltro, BorderLayout.NORTH);
        painelSuperior.add(lblClienteInfo, BorderLayout.SOUTH);
        painelFundo.add(painelSuperior, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new Object[]{"Animal", "Raça", "Sexo", "Idade", "Cor", "Status", "Cliente"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);

        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Animais Encontrados"));
        painelFundo.add(scrollTabela, BorderLayout.CENTER);

        add(painelFundo);

        btnBuscar.addActionListener(e -> realizarBusca());
        btnLimpar.addActionListener(e -> limpar());

        // Enter no campo de texto faz a busca
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) realizarBusca();
            }
        });

        // quando trocar o tipo de busca, limpa o campo
        rbNome.addActionListener(e -> txtFiltro.setText(""));
        rbCpf.addActionListener(e -> txtFiltro.setText(""));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) { txtFiltro.requestFocus(); }
        });
    }


    private void realizarBusca() {
        String valor = txtFiltro.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Digite um valor para buscar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (rbNome.isSelected()) {
                clientesEncontrados = consultaDAO.buscarClientesPorNome(valor);
            } else {
                clientesEncontrados = consultaDAO.buscarClientesPorCpf(valor);
            }

            modeloTabela.setRowCount(0);

            if (clientesEncontrados.isEmpty()) {
                lblClienteInfo.setText("Nenhum cliente encontrado para: \"" + valor + "\"");
                return;
            }

            // pesquisando um cliente, mostra os animais relacionados
            int totalAnimais = 0;
            for (Cliente c : clientesEncontrados) {
                List<Animal> animais = consultaDAO.buscarAnimaisPorCliente(c.getIdCliente());
                for (Animal a : animais) {
                    String idade  = a.getDataNascimento() != null ? a.getIdade() + " ano(s)" : "-";
                    String status = a.isStatus() ? "Ativo" : "Inativo";
                    modeloTabela.addRow(new Object[]{
                            a.getNome(),
                            a.getNomeRaca(),
                            a.getSexo(),
                            idade,
                            a.getCor() != null ? a.getCor() : "-",
                            status,
                            c.getNome()
                    });
                    totalAnimais++;
                }
                // se o cliente não tiver animais, mostra essa msg
                if (animais.isEmpty()) {
                    modeloTabela.addRow(new Object[]{
                            "(sem animais cadastrados)", "-", "-", "-", "-", "-", c.getNome()
                    });
                }
            }

            lblClienteInfo.setText(clientesEncontrados.size() + " cliente(s) encontrado(s) · " +
                    totalAnimais + " animal(is) no total");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na busca: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpar() {
        txtFiltro.setText("");
        modeloTabela.setRowCount(0);
        lblClienteInfo.setText(" ");
        rbNome.setSelected(true);
        txtFiltro.requestFocus();
    }
}