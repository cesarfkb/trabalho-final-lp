package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DialogAbrir extends JDialog implements ActionListener {

    private final int qtd;
    private final HashMap<Integer, JButton> botoes;
    private final HashMap<Integer, JLabel> labels;
    private ResourceBundle idioma;

    public DialogAbrir(JFrame fr, int i, ResourceBundle idioma) {
        super(fr);
        this.qtd = i;
        botoes = new HashMap<>();
        labels = new HashMap<>();
        this.idioma = idioma;
        iniciarElementos();
        definirLayout();
        setSize(new Dimension(150, 500));
        setVisible(true);
        setLocationRelativeTo(null);
        setTitle(idioma.getString("gravacao.titulo"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 1; i <= qtd; i++) {
            if (botoes.get(i).equals(e.getSource())) {
                pegarAnotacoes(i);
            }
        }
    }

    private void iniciarElementos() {
        for (int i = 1; i <= qtd; i++) {
            JButton botao = new JButton(idioma.getString("gravacao.nome") + i);
            botao.addActionListener(this);
            botoes.put(i, botao);
        }
    }

    private void definirLayout() {
        Container c = getContentPane();
        c.setLayout(new GridLayout(1, 1));

        GridLayout layout = new GridLayout(qtd, 2);
        layout.setVgap(10);
        layout.setHgap(10);

        JPanel painel = new JPanel(layout);
        painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10),
                painel.getBorder()));

        for (int i = 1; i <= qtd; i++) {
            painel.add(botoes.get(i));
        }

        JScrollPane jsp = new JScrollPane(painel);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(jsp);
    }

    private void pegarAnotacoes(int i) {
        CrudDB banco = new CrudDB();

        Object[] dados = banco.pegarDadosGravacao(i);
        if (dados == null) {
            JOptionPane.showMessageDialog(null, idioma.getString("gravacao.erro"));
            return;
        }
        ArrayList<String> tempos = gerarLista((String) dados[1]);
        int[] tempoTotal = RunnableContador.calculaTempo((Integer) dados[0]);
        ArrayList<String> anotacoes = gerarLista((String) dados[2]);

        JPanel notas = new JPanel(new GridLayout(anotacoes.size() + 1, 1));
        JPanel header = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("Gravação " + i);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.NORTH);
        header.add(label);

        String duracao = arrumarStringTempo(tempoTotal);
        label = new JLabel("Duração total: " + duracao);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.NORTH);
        header.add(label);
        notas.add(header);

        if (tempos.size() == 1 && tempos.get(0).isEmpty()) {
            label = new JLabel("NO NOTES");
            label.setHorizontalAlignment(JLabel.CENTER);
            notas.add(label);
        } else {
            for (int j = 0; j < anotacoes.size(); j++) {
                duracao = arrumarStringTempo(RunnableContador.calculaTempo(Integer.parseInt(tempos.get(j))));
                label = new JLabel(duracao + " - " + anotacoes.get(j));
                label.setHorizontalAlignment(JLabel.CENTER);
                labels.put(j, label);
                label.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        aumentarLabel(e);
                    }
                });
                notas.add(label);
            }
        }

        JScrollPane jsp = new JScrollPane(notas);

        Container c = getContentPane();
        c.removeAll();
        c.setLayout(new GridLayout(1, 1));
        c.add(jsp);
        this.invalidate();
        this.validate();
        this.repaint();
        this.setSize(400, 500);
        this.setTitle(idioma.getString("gravacao.nome") + i);
    }

    private ArrayList<String> gerarLista(String textos) {
        textos = textos.substring(1, textos.length() - 1);
        textos = textos.replace("\"", "");

        return new ArrayList<>(Arrays.asList(textos.split(", ")));
    }

    private String arrumarStringTempo(int[] tempo) {
        String duracao = "";
        if (tempo[0] >= 10) {
            duracao += tempo[0];
        } else {
            duracao += "0" + tempo[0];
        }
        duracao += " : ";
        if (tempo[1] >= 10) {
            duracao += tempo[1];
        } else {
            duracao += "0" + tempo[1];
        }
        duracao += " : ";
        if (tempo[2] >= 10) {
            duracao += tempo[2];
        } else {
            duracao += "0" + tempo[2];
        }
        return duracao;
    }

    private void aumentarLabel(MouseEvent e) {
        for (int i = 0; i < labels.size(); i++) {
            if (e.getSource() == labels.get(i)) {
                JOptionPane.showMessageDialog(null, labels.get(i).getText());
            }
        }
    }
}
