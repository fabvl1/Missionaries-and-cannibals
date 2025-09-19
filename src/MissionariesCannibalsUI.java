import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

/**
 * Interfaz mejorada para el problema Misioneros y Caníbales.
 * - UI moderna con SplitPane (gráficos + controles)
 * - Entradas con JSpinner, botones estilizados, tooltips
 * - Historial de movimientos con posibilidad de deshacer
 * - Animación de la barca con Swing Timer
 * - Validaciones y mensajes amigables
 *
 * Compilar:
 *   javac InterfazManual.java
 * Ejecutar:
 *   java InterfazManual
 */
public class InterfazManual extends JFrame {
    private Estado estadoActual = new Estado(3, 3, 0, 0, 0);
    private int paso = 0;

    private JLabel lblPaso, lblBarca;
    private JTextArea txtMensajes;
    private JSpinner spnMisioneros, spnCanibales;
    private JButton btnMover, btnReset, btnUndo, btnHint;
    private PanelGrafico panelGrafico;
    private DefaultListModel<String> historialModel;
    private JList<String> historialList;

    // Para deshacer
    private Stack<Estado> pilaEstados = new Stack<>();

    public InterfazManual() {
        super("Misioneros y Caníbales — Modo Manual (Mejorado)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(25, 43, 77));
        topBar.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel title = new JLabel("Misioneros y Caníbales");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        topBar.add(title, BorderLayout.WEST);

        lblPaso = new JLabel();
        lblPaso.setForeground(Color.WHITE);
        lblPaso.setFont(new Font("SansSerif", Font.PLAIN, 14));
        topBar.add(lblPaso, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Main split: left = graphic, right = controls
        panelGrafico = new PanelGrafico();
        panelGrafico.setPreferredSize(new Dimension(600, 400));

        JPanel rightPanel = crearPanelControles();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelGrafico, rightPanel);
        split.setResizeWeight(0.72);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);

        actualizarEstado();

        // Key binding: Enter to move
        getRootPane().setDefaultButton(btnMover);
    }

    private JPanel crearPanelControles() {
        JPanel cont = new JPanel();
        cont.setLayout(new BorderLayout());
        cont.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Panel superior con entradas
        JPanel entradas = new JPanel();
        entradas.setLayout(new GridBagLayout());
        entradas.setBorder(BorderFactory.createTitledBorder("Controles"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        entradas.add(new JLabel("Misioneros:"), gbc);
        gbc.gridx = 1;
        spnMisioneros = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1));
        spnMisioneros.setToolTipText("Número de misioneros a mover (0-2)");
        entradas.add(spnMisioneros, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        entradas.add(new JLabel("Caníbales:"), gbc);
        gbc.gridx = 1;
        spnCanibales = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1));
        spnCanibales.setToolTipText("Número de caníbales a mover (0-2)");
        entradas.add(spnCanibales, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        btnMover = crearBotonPrincipal("Mover");
        btnMover.setToolTipText("Ejecutar movimiento con los valores seleccionados");
        entradas.add(btnMover, gbc);

        gbc.gridy = 3;
        JPanel smallButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnUndo = crearBotonSecundario("Deshacer");
        btnReset = crearBotonSecundario("Reiniciar");
        btnHint = crearBotonSecundario("Sugerencia");
        smallButtons.add(btnUndo);
        smallButtons.add(btnReset);
        smallButtons.add(btnHint);
        entradas.add(smallButtons, gbc);

        cont.add(entradas, BorderLayout.NORTH);

        // Centro: historial y mensajes
        JPanel centro = new JPanel(new BorderLayout(6, 6));
        centro.setBorder(new EmptyBorder(8,0,0,0));

        historialModel = new DefaultListModel<>();
        historialList = new JList<>(historialModel);
        historialList.setBorder(BorderFactory.createTitledBorder("Historial"));
        JScrollPane scHist = new JScrollPane(historialList);
        scHist.setPreferredSize(new Dimension(220, 180));

        centro.add(scHist, BorderLayout.CENTER);

        txtMensajes = new JTextArea(6, 20);
        txtMensajes.setEditable(false);
        txtMensajes.setLineWrap(true);
        txtMensajes.setWrapStyleWord(true);
        txtMensajes.setBorder(BorderFactory.createTitledBorder("Mensajes"));
        txtMensajes.setBackground(new Color(245, 247, 250));
        JScrollPane scMsg = new JScrollPane(txtMensajes);
        scMsg.setPreferredSize(new Dimension(220, 120));

        centro.add(scMsg, BorderLayout.SOUTH);

        cont.add(centro, BorderLayout.CENTER);

        // Bottom bar with quick info
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(8, 0, 0, 0));
        lblBarca = new JLabel();
        bottom.add(lblBarca, BorderLayout.WEST);
        cont.add(bottom, BorderLayout.SOUTH);

        // Listeners
        btnMover.addActionListener(e -> moverAction());
        btnReset.addActionListener(e -> reiniciar());
        btnUndo.addActionListener(e -> deshacer());
        btnHint.addActionListener(e -> mostrarSugerencia());

        return cont;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(46, 125, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(236, 239, 241));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return btn;
    }

    private void moverAction() {
        int m = (int) spnMisioneros.getValue();
        int c = (int) spnCanibales.getValue();

        if (m + c == 0 || m + c > 2) {
            mostrarError("Movimiento inválido: la barca debe llevar 1 o 2 personas.");
            return;
        }

        Estado nuevo = moverManual(estadoActual, m, c);
        if (nuevo == null || !esEstadoValido(nuevo)) {
            mostrarError("Ese movimiento no es posible en la posición actual o deja a los misioneros en peligro.");
            return;
        }

        // Guardar actual para poder deshacer
        pilaEstados.push(estadoActual);
        estadoActual = nuevo;
        paso++;
        historialModel.addElement("Paso " + paso + ": mover " + m + "M, " + c + "C -> " + (estadoActual.getPosicion_barca() == 0 ? "Izq" : "Der"));
        txtMensajes.setText("Movimiento ejecutado correctamente.");
        actualizarEstado();

        panelGrafico.animateBoat();

        if (estadoActual.getMisioneros_isq() == 0 && estadoActual.getCanibales_isq() == 0 && estadoActual.getPosicion_barca() == 1) {
            mostrarVictoria();
            bloquearControles();
        }
    }

    private void mostrarSugerencia() {
        // Sugerencia simple: mostrar todos los movimientos válidos desde el estado actual
        StringBuilder sb = new StringBuilder();
        sb.append("Movimientos válidos desde aquí:\n");
        int[][] opciones = {{2,0},{0,2},{1,1},{1,0},{0,1}};
        boolean any=false;
        for (int[] op : opciones) {
            Estado prueba = moverManual(estadoActual, op[0], op[1]);
            if (prueba != null && esEstadoValido(prueba)) {
                sb.append(String.format("- %d misioneros, %d caníbales -> %s\n", op[0], op[1], prueba.getPosicion_barca()==0?"Izq":"Der"));
                any=true;
            }
        }
        if (!any) sb.append("(Sin movimientos válidos)");
        JOptionPane.showMessageDialog(this, sb.toString(), "Sugerencias", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deshacer() {
        if (pilaEstados.isEmpty()) {
            txtMensajes.setText("Nada que deshacer.");
            return;
        }
        estadoActual = pilaEstados.pop();
        paso = Math.max(0, paso - 1);
        if (!historialModel.isEmpty()) historialModel.remove(historialModel.size()-1);
        txtMensajes.setText("Última acción deshecha.");
        actualizarEstado();
        panelGrafico.animateBoat();
    }

    private void reiniciar() {
        estadoActual = new Estado(3,3,0,0,0);
        paso = 0;
        pilaEstados.clear();
        historialModel.clear();
        txtMensajes.setText("Juego reiniciado.");
        actualizarEstado();
        panelGrafico.repaint();
        desbloquearControles();
    }

    private void bloquearControles() {
        btnMover.setEnabled(false);
        btnReset.setEnabled(true);
        btnUndo.setEnabled(false);
        spnMisioneros.setEnabled(false);
        spnCanibales.setEnabled(false);
    }

    private void desbloquearControles() {
        btnMover.setEnabled(true);
        btnUndo.setEnabled(true);
        spnMisioneros.setEnabled(true);
        spnCanibales.setEnabled(true);
    }

    private void mostrarVictoria() {
        JOptionPane.showMessageDialog(this, "¡Felicidades, resolviste el problema en " + paso + " pasos!", "Victoria", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String msg) {
        txtMensajes.setText(msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void actualizarEstado() {
        lblPaso.setText("Paso: " + paso + "    |    Barca: " + (estadoActual.getPosicion_barca() == 0 ? "Izquierda" : "Derecha"));
        lblBarca.setText(String.format("Izq: M=%d C=%d    —    Der: M=%d C=%d",
                estadoActual.getMisioneros_isq(), estadoActual.getCanibales_isq(),
                estadoActual.getMisioneros_der(), estadoActual.getCanibales_der()));
        panelGrafico.repaint();
    }

    // Lógica de movimiento (igual que antes)
    private static Estado moverManual(Estado estado, int misioneros, int canibales) {
        if (misioneros + canibales == 0 || misioneros + canibales > 2) return null;
        if (estado.getPosicion_barca() == 0) {
            // Barca va a la derecha
            if (estado.getMisioneros_isq() >= misioneros && estado.getCanibales_isq() >= canibales) {
                return new Estado(
                        estado.getMisioneros_isq() - misioneros,
                        estado.getCanibales_isq() - canibales,
                        1,
                        estado.getMisioneros_der() + misioneros,
                        estado.getCanibales_der() + canibales
                );
            }
        } else {
            // Barca va a la izquierda
            if (estado.getMisioneros_der() >= misioneros && estado.getCanibales_der() >= canibales) {
                return new Estado(
                        estado.getMisioneros_isq() + misioneros,
                        estado.getCanibales_isq() + canibales,
                        0,
                        estado.getMisioneros_der() - misioneros,
                        estado.getCanibales_der() - canibales
                );
            }
        }
        return null;
    }

    private static boolean esEstadoValido(Estado estado) {
        int mi = estado.getMisioneros_isq();
        int ci = estado.getCanibales_isq();
        int md = estado.getMisioneros_der();
        int cd = estado.getCanibales_der();
        if (mi < 0 || ci < 0 || md < 0 || cd < 0) return false;
        if (mi > 3 || ci > 3 || md > 3 || cd > 3) return false;
        if (mi > 0 && ci > mi) return false;
        if (md > 0 && cd > md) return false;
        return true;
    }

    /**
     * Panel gráfico con animaciones y dibujo mejorado.
     */
    private class PanelGrafico extends JPanel {
        // animación
        private int animBoatX = -1;
        private Timer animationTimer;

        public PanelGrafico() {
            setBackground(new Color(240, 248, 255));
            setPreferredSize(new Dimension(600, 500));
        }

        public void animateBoat() {
            // Si hay animación en curso, reinicia
            if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();

            int leftX = getWidth() / 6 - 40;
            int rightX = getWidth() * 5 / 6 - 40;
            int startX = estadoActual.getPosicion_barca() == 0 ? rightX : leftX; // flip: start = previous position
            int targetX = estadoActual.getPosicion_barca() == 0 ? leftX : rightX; // move to new pos

            // Si animBoatX aún no inicializado, posiciónalo en start
            if (animBoatX == -1) animBoatX = startX;

            final int steps = 18;
            final int dx = (targetX - animBoatX) / steps;

            animationTimer = new Timer(18, null);
            animationTimer.addActionListener(new ActionListener() {
                int count = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (count >= steps) {
                        animBoatX = targetX;
                        animationTimer.stop();
                        repaint();
                        return;
                    }
                    animBoatX += dx;
                    count++;
                    repaint();
                }
            });
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Cielo gradient
            GradientPaint sky = new GradientPaint(0, 0, new Color(200, 225, 255), 0, h/3, new Color(240,248,255));
            g2.setPaint(sky);
            g2.fillRect(0, 0, w, h/3);

            // Río con ligero degradado
            GradientPaint river = new GradientPaint(w/3f, h/3f, new Color(64,164,223), w/2f, h/2f, new Color(30,144,255));
            int riverX = w/3; int riverW = w/3; int riverY = h/3; int riverH = h/3;
            g2.setPaint(river);
            g2.fillRoundRect(riverX, riverY, riverW, riverH, 30, 30);

            // Orillas
            g2.setColor(new Color(69, 160, 78));
            g2.fillRoundRect(0, riverY, riverX, riverH, 20, 20);
            g2.fillRoundRect(riverX+riverW, riverY, riverX, riverH, 20, 20);

            // Dibujar personajes con iconos circulares y sombra
            drawPeopleGroup(g2, estadoActual.getMisioneros_isq(), estadoActual.getCanibales_isq(), 40, riverY + 20);
            drawPeopleGroup(g2, estadoActual.getMisioneros_der(), estadoActual.getCanibales_der(), w - 140, riverY + 20);

            // Barco
            int barcaY = riverY + riverH - 30;
            int leftBoat = w/6 - 40;
            int rightBoat = w*5/6 - 40;
            int barX = animBoatX == -1 ? (estadoActual.getPosicion_barca()==0?leftBoat:rightBoat) : animBoatX;

            // sombra
            g2.setColor(new Color(0,0,0,30));
            g2.fillOval(barX+8, barcaY+30, 64, 10);

            // cuerpo del barco
            g2.setColor(new Color(139,69,19));
            g2.fillRoundRect(barX, barcaY, 80, 34, 12, 12);
            g2.setColor(new Color(255, 255, 255, 180));
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("BARCA", barX+18, barcaY+22);

            // Si hay gente en la barca (no modelado exacto) dibujar pequeños círculos sobre ella
            int onBoatM = 0, onBoatC = 0;
            // Determinar aproximación: si barca se está moviendo hacia derecha, entonces personas salieron de izquierda
            // (no tracking exacto por simplicidad). Esto es puramente visual.

            // Labels de conteo grandes
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(new Color(40,40,40));
            g2.drawString("Izquierda → M: " + estadoActual.getMisioneros_isq() + "  C: " + estadoActual.getCanibales_isq(), 12, 24);
            g2.drawString("Derecha →  M: " + estadoActual.getMisioneros_der() + "  C: " + estadoActual.getCanibales_der(), w-220, 24);

            g2.dispose();
        }

        private void drawPeopleGroup(Graphics2D g2, int m, int c, int startX, int startY) {
            int gap = 42;
            int x = startX;
            // Misioneros
            for (int i = 0; i < m; i++) {
                drawPersonIcon(g2, x, startY + i*22, 'M', new Color(64, 156, 255), new Color(255, 235, 205));
            }
            // Caníbales
            for (int i = 0; i < c; i++) {
                int y = startY + (i + Math.max(0,m)) * 22;
                drawPersonIcon(g2, x + gap, y, 'C', new Color(220, 80, 80), new Color(205,133,63));
            }
        }

        private void drawPersonIcon(Graphics2D g2, int x, int y, char label, Color colorPrimary, Color skin) {
            // sombra
            g2.setColor(new Color(0,0,0,30));
            g2.fillOval(x+2, y+22, 34, 34);

            // cabeza
            g2.setColor(skin);
            g2.fillOval(x, y, 24, 24);
            // cuerpo
            g2.setColor(colorPrimary);
            g2.fillRoundRect(x, y+18, 24, 18, 8, 8);
            // contorno
            g2.setColor(new Color(40,40,40,120));
            g2.drawOval(x, y, 24, 24);
            g2.drawRoundRect(x, y+18, 24, 18, 8, 8);
            // letra
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String s = String.valueOf(label);
            int tx = x + (24 - fm.stringWidth(s))/2;
            int ty = y + 18;
            g2.drawString(s, tx, ty);
        }
    }

    // Clase Estado (no pública) para mantener el estado del juego
    static class Estado {
        private int misioneros_isq, canibales_isq, posicion_barca, misioneros_der, canibales_der;

        public Estado(int mi, int ci, int pos, int md, int cd) {
            this.misioneros_isq = mi;
            this.canibales_isq = ci;
            this.posicion_barca = pos;
            this.misioneros_der = md;
            this.canibales_der = cd;
        }

        public int getMisioneros_isq() { return misioneros_isq; }
        public int getCanibales_isq() { return canibales_isq; }
        public int getPosicion_barca() { return posicion_barca; }
        public int getMisioneros_der() { return misioneros_der; }
        public int getCanibales_der() { return canibales_der; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazManual ui = new InterfazManual();
            ui.setVisible(true);
        });
    }
}
