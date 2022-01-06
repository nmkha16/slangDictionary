package vn.hcmus.edu.kha;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    private static MyWindow wnd = null;
    public static void main(String[] args) {
        // write your code here
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                wnd = new MyWindow();
                wnd.setVisible(true);
            }
        });
    }
}


