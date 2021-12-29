package vn.hcmus.edu.kha;

import javax.swing.*;

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


