package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class TelaPrincipal extends JFrame implements ActionListener {
    // Variaveis da GUI
    private JButton bGravar, bAnotar, bPausar;
    private JLabel lTempo, lGravando;
    private JTextArea tAnotar;
    private JMenuBar menu;
    private JMenu opcoes, arquivo;
    private JMenuItem config, novo, abrir;

    // Variaveis do contador
    private int seg, min, hora;
    private int id = 1;

    // Variaveis de idioma
    private int optIdioma = 2;
    private ResourceBundle idioma = Idioma.idiomasBundles[optIdioma];

    // Variaveis das opcoes escolhidas
    private String caminho = ".", nome;
    private int microfonePos = 0;
    private ArrayList<String> anotacoes;
    private ArrayList<Integer> tempos;

    // Variaveis iniciais de verificacao
    private boolean primeiraAnotacao = true;
    private AtomicBoolean tempoZero = new AtomicBoolean(true);
    private AtomicBoolean pausado = new AtomicBoolean(true);
    private AtomicBoolean terminado = new AtomicBoolean(true);
    private boolean gravacaoTerminada;

    // Instanciacao de classes
    private ContadorRunnable cont;
    private Gravador g;
    private Thread gravador;
    private final CrudDB banco;

    public TelaPrincipal() {
        super("Gravador");
        setTitle(idioma.getString("tela1.titulo"));

        banco = new CrudDB();
        anotacoes = new ArrayList<>();
        tempos = new ArrayList<>();

        id = banco.pegarUltimoID();

        nome = "recording" + id;
        g = new Gravador(id, caminho, nome);

        iniciarElementos();
        setPreferredSize(new Dimension(600, 350));
        setMinimumSize(new Dimension(500, 300));
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    private void iniciarElementos() {
        // Iniciar botoes
        bGravar = new JButton(idioma.getString("tela1.botao.gravar"));
        bAnotar = new JButton(idioma.getString("tela1.botao.criar"));
        bAnotar.setEnabled(false);
        bPausar = new JButton(idioma.getString("tela1.botao.pausar"));
        bPausar.setEnabled(false);

        // Adicionar evento
        bGravar.addActionListener(this);
        bAnotar.addActionListener(this);
        bPausar.addActionListener(this);

        // Iniciar label
        lTempo = new JLabel("00:00:00");
        lTempo.setHorizontalAlignment(JLabel.CENTER);
        lTempo.setVerticalAlignment(JLabel.NORTH);
        lTempo.setFont(new Font(lTempo.getFont().getName(), lTempo.getFont().getStyle(), 40));

        lGravando = new JLabel(idioma.getString("tela1.label.status.inicio"));
        lGravando.setHorizontalAlignment((JLabel.CENTER));
        lGravando.setFont(new Font(lGravando.getFont().getName(), lGravando.getFont().getStyle(), 24));
        lGravando.setVerticalAlignment(JLabel.NORTH);

        // Iniciar caixa texto
        tAnotar = new JTextArea(idioma.getString("tela1.textfield"), 20, 50);
        tAnotar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                limparAnotacao();
            }
        });
        tAnotar.setEnabled(false);
        tAnotar.setLineWrap(true);
        tAnotar.setWrapStyleWord(true);

        menu = new JMenuBar();

        opcoes = new JMenu(idioma.getString("tela1.menu"));
        arquivo = new JMenu(idioma.getString("jmenu.arquivo"));

        config = new JMenuItem(idioma.getString("jmenu.configuracao"));
        config.addActionListener(this);
        novo = new JMenuItem(idioma.getString("jmenu.arquivo.novo"));
        novo.addActionListener(this);
        abrir = new JMenuItem(idioma.getString("jmenu.arquivo.abrir"));
        abrir.addActionListener(this);

        arquivo.add(novo);
        arquivo.add(abrir);
        opcoes.add(arquivo);
        opcoes.add(config);
        menu.add(opcoes);

        definirLayout();
    }

    private void definirLayout() {
        // Definir layout principal
        Container caixa = getContentPane();
        caixa.setLayout(new GridLayout(2, 2));
        caixa.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        setJMenuBar(menu);

        JPanel p1 = new JPanel(new GridLayout(3, 1));
        p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p1.add(lGravando);
        p1.add(lTempo);
        preencherVazio(p1, 1);

        caixa.add(p1);

        JScrollPane jsp = new JScrollPane(tAnotar);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setBorder(
                BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), jsp.getBorder()));
        caixa.add(jsp);

        JPanel p2 = new JPanel(new GridLayout(3, 3));
        p2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        preencherVazio(p2, 3);
        p2.add(bGravar);
        preencherVazio(p2, 1);
        p2.add(bPausar);
        preencherVazio(p2, 3);
        caixa.add(p2);

        JPanel p3 = new JPanel(new GridLayout(3, 1));
        p3.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        p3.add(bAnotar);
        preencherVazio(p3, 2);
        caixa.add(p3);
    }

    private void preencherVazio(JPanel p, int n) {
        for (int i = 0; i < n; i++) {
            p.add(new JLabel(""));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bGravar) {
            gravar();
        } else if (e.getSource() == bAnotar) {
            anotacao();
        } else if (e.getSource() == bPausar) {
            pausar();
        } else if (e.getSource() == config) {
            configuracao();
        } else if (e.getSource() == novo) {
            estadoInicial();
        } else if (e.getSource() == abrir) {
            abrirArquivo();
        }
    }

    private void gravar() { // CONTROLA O BOTAO DE GRAVAR
        if (tempoZero.get()) {
            if (id > 1) {
                nome = "recording" + id;
                g = new Gravador(id, caminho, nome);
            }
            gravacaoTerminada = false;
            opcoes.setEnabled(false);
            pausado.set(false);
            terminado.set(false);

            cont = new ContadorRunnable();

            Thread atualizador = new Thread(this::atualizador);
            gravador = new Thread(this::iniciarGravador);
            Thread parador = new Thread(this::parador);

            cont.setPausar(pausado.get());

            atualizador.start();
            gravador.start();
            parador.start();

            bGravar.setText(idioma.getString("tela1.botao.parar"));
            bPausar.setEnabled(true);
            bAnotar.setEnabled(true);
            tAnotar.setEnabled(true);

        } else {

            terminado.set(true);
            bGravar.setText(idioma.getString("tela1.botao.gravar"));
            bGravar.setEnabled(false);
            bPausar.setEnabled(false);
            bAnotar.setEnabled(false);
            tAnotar.setEnabled(false);
            opcoes.setEnabled(true);

            salvarDados();

            id++;
            /*
             * pausado.set(!pausado.get());
             * cont.setPausar(pausado.get());
             */

        }
    }

    private void salvarDados() {
        banco.id = id;
        banco.local = caminho + "\\" + nome + ".wav";
        banco.tempototal = cont.getTempoTotal();
        banco.tempos = tempos;
        banco.textos = anotacoes;
        banco.salvarDadosGravacao();
    }

    private void atualizador() { // CONTROLA O CRONOMETRO DE GRAVACAO
        while (!gravacaoTerminada) {
            while (!pausado.get()) {
                seg = cont.getSeg();
                min = cont.getMin();
                hora = cont.getHora();
                String segText, minText, horaText;
                if (seg < 10) {
                    segText = "0" + seg;
                } else {
                    segText = String.valueOf(seg);
                }
                if (min < 10) {
                    minText = "0" + min;
                } else {
                    minText = String.valueOf(min);
                }
                if (hora < 10) {
                    horaText = "0" + hora;
                } else {
                    horaText = String.valueOf(hora);
                }
                lTempo.setText(horaText + ":" + minText + ":" + segText);
            }
        }
        return;
    }

    private void limparAnotacao() { // LIMPAR A ANOTACAO NO PRIMEIRO CLIQUE
        if (primeiraAnotacao && tAnotar.isEnabled()) {
            primeiraAnotacao = !primeiraAnotacao;
            tAnotar.setText("");
        }
    }

    private void anotacao() { // CONTROLA O BOTAO DE ANOTACAO
        if (tAnotar.getText().isEmpty()) {
            anotacoes.add("!");
        } else if (primeiraAnotacao) {
            limparAnotacao();
            tAnotar.setText("");
        } else {
            anotacoes.add(tAnotar.getText());
        }
        tempos.add(cont.getTempoTotal());

        Thread cooldown = new Thread(() -> {
            bAnotar.setEnabled(false);
            bAnotar.setText(idioma.getString("tela1.botao.status.criado"));
            try {
                Thread.sleep(500);
                bAnotar.setText(".");
                Thread.sleep(200);
                bAnotar.setText("..");
                Thread.sleep(200);
                bAnotar.setText("...");
                Thread.sleep(200);
                bAnotar.setText(idioma.getString("tela1.botao.criar"));
                bAnotar.setEnabled(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        });
        cooldown.start();
        tAnotar.setText("");
    }

    private void pausar() { // CONTROLA O BOTAO DE PAUSAR
        if (pausado.get()) {
            bPausar.setText(idioma.getString("tela1.botao.pausar"));
        } else if (terminado.get()) {
            bPausar.setEnabled(false);
        } else {
            Thread cooldown = new Thread(() -> { // COOLDOWN DO BOTAO
                bPausar.setEnabled(false);
                bPausar.setText(idioma.getString("tela1.botao.status.pausado"));
                try {
                    Thread.sleep(500);
                    bPausar.setText(".");
                    Thread.sleep(200);
                    bPausar.setText("..");
                    Thread.sleep(200);
                    bPausar.setText("...");
                    Thread.sleep(200);
                    bPausar.setText(idioma.getString("tela1.botao.resumir"));
                    bPausar.setEnabled(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            });
            cooldown.start();
        }
        pausado.set(!pausado.get());
        cont.setPausar(pausado.get());
    }

    private void parador() { // FUNCAO DE THREAD USADA PARA PARAR A GRAVACAO
        AtomicBoolean pausa = new AtomicBoolean(false);
        while (!gravacaoTerminada) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (terminado.get()) {
                if (pausado.get() && pausa.get()) { // Gravacao finalizada enquanto pausado
                    lGravando.setText(idioma.getString("tela1.label.status.concluido"));
                    gravacaoTerminada = true;
                    g.terminar(1);
                    return;
                } else if (!pausado.get() && !pausa.get()) { // Gravacao finalizada sem pausar
                    lGravando.setText(idioma.getString("tela1.label.status.concluido"));
                    pausar();
                    gravacaoTerminada = true;
                    g.terminar(1);
                    return;
                }
            } else {
                if (pausado.get() && !pausa.get()) { // Pausar gravacao
                    pausa.set(true);
                    lGravando.setText(idioma.getString("tela1.label.status.pausado"));
                    g.terminar(0);
                } else if (!pausado.get() && pausa.get()) { // Retomar gravacao
                    pausa.set(false);
                    lGravando.setText(idioma.getString("tela1.label.status.gravando"));
                    gravador = new Thread(this::iniciarGravador);
                    gravador.start();
                }
            }
        }
    }

    private void iniciarGravador() { // FUNCAO DE THREAD PARA INICIAR GRAVACAO
        lGravando.setText(idioma.getString("tela1.label.status.gravando"));
        tempoZero.set(false);
        pausado.set(false);
        g.inicio();
    }

    private void configuracao() { // FUNCAO QUE CONTROLA O BOTAO DE CONFIGURACAO
        ConfiguracaoDialog config = new ConfiguracaoDialog(TelaPrincipal.this, g, microfonePos, caminho, optIdioma);
        if (config.isAplicado()) {
            caminho = config.getCaminho();
            microfonePos = config.getPos();
            optIdioma = config.getOptIdioma();
            g.setArqFinal(caminho);
            config.fechar();
        }
    };

    private void estadoInicial() { // FUNCAO PARA RETOMAR ESTADO INICIAL (NOVO ARQUIVO)

        tempoZero.set(true);
        pausado.set(true);

        bGravar.setEnabled(true);
        bPausar.setEnabled(false);
        bAnotar.setEnabled(false);
        tAnotar.setEnabled(false);

        lGravando.setText("Gravação não iniciada");
        tAnotar.setText("Digite sua anotação aqui");
        lTempo.setText("00:00:00");

        anotacoes.clear();
        tempos.clear();
    }

    private void abrirArquivo() {
        int qtd = banco.pegarUltimoID();
        new AbrirDialog(this, qtd, optIdioma);
    }
}