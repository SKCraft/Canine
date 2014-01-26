package com.skcraft.alicefixes.jsongenerator;

import javax.swing.*;

public class Generator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    new GeneratorFrame().setVisible(true);
                } catch(Throwable t) {

                }
            }
        });
    }
}
