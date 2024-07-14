package src.main.java.org.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientForm extends JFrame {
    private static final String FIRST_FLOW="CTRL_A";
    private static final String SECOND_FLOW="CTRL_B";
    private static final String END_FLOW="CTRL_N";
    private static final int width=800;
    private static final int height=500;

    private double startValue;
    private double endValue;

    private final ReentrantLock lock;
    private final Condition condition;
    private Thread sinThread;
    private Thread cosThread;
    private boolean running;

    ClientForm(final KeySendListener keySendListener) throws HeadlessException {
        setTitle("Client");
        setSize(width,height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        final JPanel mainPanel = new JPanel(new BorderLayout());

        final JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel projectTitleField = new JLabel("Побудова графіків sin() і cos()", SwingConstants.LEFT);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        p.add(projectTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        p.add(Box.createVerticalStrut(100), gbc);

        // Resetting grid width for other components
        gbc.gridwidth = 1;

        JLabel label1 = new JLabel("Початкове значення:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(label1, gbc);

        JTextField textField1 = new JTextField(10);
        addNumberValidation(textField1);
        gbc.gridx = 1;
        gbc.gridy = 2;
        p.add(textField1, gbc);

        JLabel label2 = new JLabel("Кінцеве значення:");
        gbc.gridx = 2;
        gbc.gridy = 2;
        p.add(label2, gbc);

        JTextField textField2 = new JTextField(10);
        addNumberValidation(textField2);
        gbc.gridx = 3;
        gbc.gridy = 2;
        p.add(textField2, gbc);

        // Add some vertical space before the button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        p.add(Box.createVerticalStrut(20), gbc);

        // Resetting gridwidth for the button
        gbc.gridwidth = 1;

        JButton startButton = new JButton("OK");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField1.getText().isEmpty() && !textField2.getText().isEmpty()) {
                    startValue = Double.parseDouble(textField1.getText());
                    endValue = Double.parseDouble(textField2.getText());
                    System.out.println("Start Value: " + startValue);
                    System.out.println("End Value: " + endValue);
                    mainPanel.requestFocusInWindow();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both start and end values.");
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        p.add(startButton, gbc);

        KeyStroke ctrlAKeyStroke=KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlBKeyStroke=KeyStroke.getKeyStroke(KeyEvent.VK_B,KeyEvent.CTRL_DOWN_MASK);
        KeyStroke ctrlNKeyStroke=KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_DOWN_MASK);

        p.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlAKeyStroke, FIRST_FLOW);
        p.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlBKeyStroke, SECOND_FLOW);
        p.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ctrlNKeyStroke, END_FLOW);

        lock = new ReentrantLock();
        condition = lock.newCondition();

        p.getActionMap().put(FIRST_FLOW, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action: Ctrl + A is pressed");
                startSinThread();
            }
        });
        p.getActionMap().put(SECOND_FLOW, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action: Ctrl + B is pressed");
                startCosThread();
            }
        });
        p.getActionMap().put(END_FLOW, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action: Ctrl + N is pressed");
                keySendListener.send();
                stopThreads();
            }
        });
        mainPanel.add(p, BorderLayout.NORTH);

        add(mainPanel);

    }
    private void startSinThread() {
        if (sinThread == null || !sinThread.isAlive()) {
            running = true;
            sinThread = new Thread(() -> {
                lock.lock();
                try {
                    while (running) {
                        MathFuncs.calculateSinArray(startValue, endValue, MathFuncs.sinX);
                        System.out.println("Calculated Sin values: " + MathFuncs.sinX);
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted sin thread");
                        }
                    }
                } finally {
                    lock.unlock();
                }
            });
            sinThread.start();
        }else {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
    private void startCosThread() {
        if (cosThread == null || !cosThread.isAlive()) {
            running = true;
            cosThread = new Thread(() -> {
                lock.lock();
                try {
                    while (running) {
                        MathFuncs.calculateCosArray(startValue, endValue, MathFuncs.cosX);
                        System.out.println("Calculated Cos values: " + MathFuncs.cosX);
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted cos thread");
                        }
                    }
                } finally {
                    lock.unlock();
                }
            });
            cosThread.start();
        }else {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
    private void stopThreads() {
        lock.lock();
        try {
            System.out.println("Sin values: " + MathFuncs.sinX);
            System.out.println("Cos values: " + MathFuncs.cosX);
        } finally {
            lock.unlock();
        }
    }
    private void addNumberValidation(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '-' && c != '.' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }
}
