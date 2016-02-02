package kcl.teamIndexZero.traffic.gui;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel("Hello, World!"), BorderLayout.CENTER);
        frame.pack();
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
