package gui;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipalGUI extends JFrame {

    public MenuPrincipalGUI() {
        setTitle("Sistema Veterinário - Fatec");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel painelFundo = new JPanel(new BorderLayout(15, 15));
        painelFundo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Título do Menu
        JLabel lblTitulo = new JLabel("Menu Principal", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        painelFundo.add(lblTitulo, BorderLayout.NORTH);

        // Painel para organizar os botões em lista
        JPanel painelBotoes = new JPanel(new GridLayout(6, 1, 10, 15));

        JButton btnClientes = new JButton("Cadastro de Clientes");
        JButton btnRacas = new JButton("Cadastro de Raças");
        JButton btnAnimais = new JButton("Cadastro de Animais");
        JButton btnConsultas = new JButton("Consulta de Animais");
        JButton btnRelatorios = new JButton("Gerar Relatórios");
        JButton btnSair = new JButton("Sair do Sistema");

        // So uma mexida nas fontes dos botoes, so visual
        Font fonteBotoes = new Font("Segoe UI", Font.PLAIN, 16);
        btnClientes.setFont(fonteBotoes);
        btnRacas.setFont(fonteBotoes);
        btnAnimais.setFont(fonteBotoes);
        btnConsultas.setFont(fonteBotoes);
        btnRelatorios.setFont(fonteBotoes);
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSair.setForeground(new Color(220, 53, 69)); // Vermelho para o Sair

        // add os botões ao painel
        painelBotoes.add(btnClientes);
        painelBotoes.add(btnRacas);
        painelBotoes.add(btnAnimais);
        painelBotoes.add(btnConsultas);
        painelBotoes.add(btnRelatorios);
        painelBotoes.add(btnSair);

        painelFundo.add(painelBotoes, BorderLayout.CENTER);
        add(painelFundo);

        // BOTOES

        // Abrem as telas que ja existem
        btnClientes.addActionListener(e -> new ClienteGUI().setVisible(true));
        btnRacas.addActionListener(e -> new RacaGUI().setVisible(true));

        // Telas pendentes (apenas mostram aviso)
        btnAnimais.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "em breve.", "Em desenvolvimento", JOptionPane.INFORMATION_MESSAGE));

        btnConsultas.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "em breve.", "Em desenvolvimento", JOptionPane.INFORMATION_MESSAGE));

        btnRelatorios.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "em breve.", "Em desenvolvimento", JOptionPane.INFORMATION_MESSAGE));

        // Fecha a aplicação
        btnSair.addActionListener(e -> System.exit(0));
    }
}