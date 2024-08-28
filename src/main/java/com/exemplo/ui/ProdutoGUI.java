package com.exemplo.ui;

import com.exemplo.dao.ProdutoDAO;
import com.exemplo.model.Produto;
import com.exemplo.util.EAN13Validator;
import com.exemplo.util.TextFieldUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ProdutoGUI extends JFrame {

    private JTextField descricaoField;
    private JFormattedTextField valorField;
    private JTextField ean13Field;
    private JComboBox<String> statusField;
    private JFormattedTextField percentualImpostoField;
    private JFormattedTextField valorImpostoField;
    //private JTextArea textArea;
    private JTable table;
    private DefaultTableModel tableModel;
    private ProdutoDAO produtoDAO;
    private Produto produto;
    private JButton salvarButton;
    private JButton editarButton;
    private JButton excluirButton;
    private Boolean isEtitando = false;

    public ProdutoGUI() {
        produtoDAO = new ProdutoDAO();

        setTitle("CRUD de Produtos");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2));

        panel.add(new JLabel("   Descrição:"));
        descricaoField = new JTextField();
        panel.add(descricaoField);

        panel.add(new JLabel("   Valor:"));
        valorField = criarCampoValor();
        panel.add(valorField);

        panel.add(new JLabel("   EAN-13:"));
        ean13Field = TextFieldUtils.createNumericTextField(13);
        panel.add(ean13Field);

        panel.add(new JLabel("   Status:"));
        statusField = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        panel.add(statusField);
        statusField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajustarCamposComBaseNoStatus();
            }
        });


        panel.add(new JLabel("   Percentual de Imposto:"));
        percentualImpostoField = criarCampoPercentual();
        panel.add(percentualImpostoField);
        percentualImpostoField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calcularValorImposto();
            }
        });
        percentualImpostoField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calcularValorImposto();
            }
        });


        panel.add(new JLabel("   Valor do Imposto:"));
        valorImpostoField = criarCampoValor();
        valorImpostoField.setEnabled(false);
        panel.add(valorImpostoField);



        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salvarButton = new JButton("Salvar");
        salvarButton.setHorizontalAlignment(SwingConstants.RIGHT);
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (validarCampos()) {
                        if (isEtitando) {
                            produto.setDescricao(descricaoField.getText());
                            produto.setValor(((Number) valorField.getValue()).doubleValue());
                            produto.setEan13(ean13Field.getText());
                            produto.setStatus(statusField.getSelectedItem().equals("Ativo") ? "1" : "0");
                            produto.setPercentualImposto(((Number) percentualImpostoField.getValue()).doubleValue());
                            produtoDAO.atualizar(produto);
                        }
                        else {
                            produto = new Produto(
                                    descricaoField.getText(),
                                    ((Number) valorField.getValue()).doubleValue(),
                                    ean13Field.getText(),
                                    statusField.getSelectedItem().equals("Ativo") ? "1" : "0",
                                    ((Number) percentualImpostoField.getValue()).doubleValue()
                            );
                            produtoDAO.salvar(produto);
                        }
                        isEtitando = false;
                        JOptionPane.showMessageDialog(null, "Produto salvo com sucesso!");
                        limparCampos();
                        listarProdutos();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao salvar o produto.");
                }
            }
        });
        buttonPanel.add(salvarButton);
        panel.add(buttonPanel);

        add(panel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Descrição", "Valor", "EAN-13", "Status", "Imposto %", "Imposto" }, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


        // Painel de botões para Editar e Excluir abaixo da tabela
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        editarButton = new JButton("Editar");
        excluirButton = new JButton("Excluir");

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    editarProduto(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um produto para editar.");
                }
            }
        });

        excluirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    excluirProduto(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um produto para excluir.");
                }
            }
        });

        actionPanel.add(editarButton);
        actionPanel.add(excluirButton);
        add(actionPanel, BorderLayout.SOUTH);


        listarProdutos();

        setVisible(true);

        listarProdutos();

        setVisible(true);
    }

    private JFormattedTextField criarCampoValor() {
        NumberFormat format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);

        return TextFieldUtils.createFormattedNumericField(format, Integer.MAX_VALUE);
    }

    private JFormattedTextField criarCampoPercentual() {
        NumberFormat format = DecimalFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);
        return new JFormattedTextField(format);
    }

    private void limparCampos() {
        descricaoField.setText("");
        valorField.setValue(null);
        ean13Field.setText("");
        statusField.setSelectedIndex(0);
        percentualImpostoField.setValue(null);
    }

    private void listarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listar();
            tableModel.setRowCount(0);
            for (Produto produto : produtos) {
                tableModel.addRow(new Object[]{
                        produto.getId(),
                        produto.getDescricao(),
                        produto.getValor(),
                        produto.getEan13(),
                        produto.getStatus().equals("1") ? "Ativo" : "Inativo",
                        produto.getPercentualImposto(),
                        produto.getValorImposto()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void ajustarCamposComBaseNoStatus() {
        boolean isAtivo = statusField.getSelectedItem().equals("Ativo");
        descricaoField.setEnabled(isAtivo);
        valorField.setEnabled(isAtivo);
        ean13Field.setEnabled(isAtivo);
        percentualImpostoField.setEnabled(isAtivo);
    }

    private boolean validarCampos() {
        if (descricaoField.getText().trim().isEmpty() ||
                valorField.getValue() == null ||
                ean13Field.getText().trim().isEmpty() ||
                percentualImpostoField.getValue() == null) {
            JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.");
            return false;
        }
        if (!EAN13Validator.isValidEAN13(ean13Field.getText()) ) {
            JOptionPane.showMessageDialog(null, "EAN13 invalido.");
            ean13Field.requestFocusInWindow();
            return false;
        }

        if (descricaoField.getText().length() < 3 || descricaoField.getText().length() > 80) {
            JOptionPane.showMessageDialog(null, "A descrição deve ter entre 3 e 80 caracteres.");
            return false;
        }

        return true;
    }

    private void calcularValorImposto() {
        if (valorField.getValue() != null && percentualImpostoField.getValue() != null) {
            double valor = ((Number) valorField.getValue()).doubleValue();
            double percentualImposto = ((Number) percentualImpostoField.getValue()).doubleValue();
            double valorImposto = valor * (percentualImposto / 100);
            valorImpostoField.setValue(valorImposto);
        } else {
            valorImpostoField.setValue(0.00);
        }
        valorImpostoField.revalidate();
        valorImpostoField.repaint();
    }

    private void editarProduto(int rowIndex) {
        int produtoId = (int) tableModel.getValueAt(rowIndex, 0);
        try {
            produto = produtoDAO.buscarPorId(produtoId);
            descricaoField.setText(produto.getDescricao());
            valorField.setValue(produto.getValor());
            ean13Field.setText(produto.getEan13());
            statusField.setSelectedItem(produto.getStatus().equals("1") ? "Ativo" : "Inativo");
            percentualImpostoField.setValue(produto.getPercentualImposto());
            table.clearSelection();
            isEtitando = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void excluirProduto(int rowIndex) {
        int produtoId = (int) tableModel.getValueAt(rowIndex, 0);
        try {
            produtoDAO.deletar(produtoId);
            JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!");
            listarProdutos();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao excluir o produto.");
        }
    }






    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProdutoGUI();
            }
        });
    }
}

//
//package com.exemplo.ui;
//
//import com.exemplo.dao.ProdutoDAO;
//import com.exemplo.model.Produto;
//import com.exemplo.util.TextFieldUtils;
//
//import javax.swing.*;
//        import java.awt.*;
//        import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
//import java.sql.SQLException;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.util.List;
//
//public class ProdutoGUI extends JFrame {
//
//    private JTextField descricaoField;
//    private JFormattedTextField valorField;
//    private JTextField ean13Field;
//    private JComboBox<String> statusField;
//    private JFormattedTextField percentualImpostoField;
//    private JFormattedTextField valorImpostoField;
//    private JTextArea textArea;
//    private ProdutoDAO produtoDAO;
//    private JButton salvarButton;
//
//    public ProdutoGUI() {
//        produtoDAO = new ProdutoDAO();
//
//        setTitle("CRUD de Produtos");
//        setSize(350, 400);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JPanel panel = new JPanel(new GridLayout(7, 2));
//
//        panel.add(new JLabel("   Descrição:"));
//        descricaoField = new JTextField();
//        panel.add(descricaoField);
//
//        panel.add(new JLabel("   Valor:"));
//        valorField = criarCampoValor();
//        panel.add(valorField);
//
//        panel.add(new JLabel("   EAN-13:"));
//        ean13Field = TextFieldUtils.createNumericTextField(13);
//        panel.add(ean13Field);
//
//        panel.add(new JLabel("   Status:"));
//        statusField = new JComboBox<>(new String[]{"Ativo", "Inativo"});
//        panel.add(statusField);
//        statusField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                ajustarCamposComBaseNoStatus();
//            }
//        });
//
//
//        panel.add(new JLabel("   Percentual de Imposto:"));
//        percentualImpostoField = criarCampoPercentual();
//        panel.add(percentualImpostoField);
//        percentualImpostoField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                calcularValorImposto();
//            }
//        });
//        percentualImpostoField.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                calcularValorImposto();
//            }
//        });
//
//
//        panel.add(new JLabel("   Valor do Imposto:"));
//        valorImpostoField = criarCampoValor();
//        valorImpostoField.setEnabled(false);
//        panel.add(valorImpostoField);
//
//
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        salvarButton = new JButton("Salvar");
//        salvarButton.setHorizontalAlignment(SwingConstants.RIGHT);
//        salvarButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    if (validarCampos()) {
//                        Produto produto = new Produto(
//                                descricaoField.getText(),
//                                ((Number) valorField.getValue()).doubleValue(),
//                                ean13Field.getText(),
//                                statusField.getSelectedItem().equals("Ativo") ? "1" : "0",
//                                ((Number) percentualImpostoField.getValue()).doubleValue()
//                        );
//                        produtoDAO.salvar(produto);
//                        JOptionPane.showMessageDialog(null, "Produto salvo com sucesso!");
//                        limparCampos();
//                        listarProdutos();
//                    }
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                    JOptionPane.showMessageDialog(null, "Erro ao salvar o produto.");
//                }
//            }
//        });
//        buttonPanel.add(salvarButton);
//        panel.add(buttonPanel);
//
//        add(panel, BorderLayout.NORTH);
//
//        textArea = new JTextArea();
//        textArea.setEditable(false);
//        add(new JScrollPane(textArea), BorderLayout.CENTER);
//
//
//
//        listarProdutos();
//
//        setVisible(true);
//    }
//
//    private JFormattedTextField criarCampoValor() {
//        NumberFormat format = DecimalFormat.getNumberInstance();
//        format.setMaximumFractionDigits(2);
//        format.setMinimumFractionDigits(2);
//        format.setGroupingUsed(false);
//
//        return TextFieldUtils.createFormattedNumericField(format, Integer.MAX_VALUE);
//    }
//
//    private JFormattedTextField criarCampoPercentual() {
//        NumberFormat format = DecimalFormat.getNumberInstance();
//        format.setMaximumFractionDigits(2);
//        format.setMinimumFractionDigits(2);
//        format.setGroupingUsed(false);
//        return new JFormattedTextField(format);
//    }
//
//    private void limparCampos() {
//        descricaoField.setText("");
//        valorField.setValue(null);
//        ean13Field.setText("");
//        statusField.setSelectedIndex(0);
//        percentualImpostoField.setValue(null);
//    }
//
//    private void listarProdutos() {
//        try {
//            List<Produto> produtos = produtoDAO.listar();
//            textArea.setText("");
//            for (Produto produto : produtos) {
//                textArea.append(produto.getId() + " - " +
//                        produto.getDescricao() + " - " +
//                        produto.getValor() + " - " +
//                        produto.getEan13() + " - " +
//                        (produto.getStatus() == "1" ? "Ativo" : "Inativo") + " - " +
//                        produto.getPercentualImposto() + " - " +
//                        produto.getValorImposto() + "\n");
//            }
//
//            textArea.revalidate();
//            textArea.repaint();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void ajustarCamposComBaseNoStatus() {
//        boolean isAtivo = statusField.getSelectedItem().equals("Ativo");
//
//        descricaoField.setEnabled(isAtivo);
//        valorField.setEnabled(isAtivo);
//        ean13Field.setEnabled(isAtivo);
//        percentualImpostoField.setEnabled(isAtivo);
//        salvarButton.setEnabled(isAtivo);
//    }
//
//    private boolean validarCampos() {
//        if (descricaoField.getText().trim().isEmpty() ||
//                valorField.getValue() == null ||
//                ean13Field.getText().trim().isEmpty() ||
//                percentualImpostoField.getValue() == null) {
//            JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.");
//            return false;
//        }
//
//        if (descricaoField.getText().length() < 3 || descricaoField.getText().length() > 80) {
//            JOptionPane.showMessageDialog(null, "A descrição deve ter entre 3 e 80 caracteres.");
//            return false;
//        }
//
//        return true;
//    }
//
//    private void calcularValorImposto() {
//        if (valorField.getValue() != null && percentualImpostoField.getValue() != null) {
//            double valor = ((Number) valorField.getValue()).doubleValue();
//            double percentualImposto = ((Number) percentualImpostoField.getValue()).doubleValue();
//            double valorImposto = valor * (percentualImposto / 100);
//            valorImpostoField.setValue(valorImposto);
//        } else {
//            valorImpostoField.setValue(0.00);
//        }
//        valorImpostoField.revalidate();
//        valorImpostoField.repaint();
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new ProdutoGUI();
//            }
//        });
//    }
//}
//
//
