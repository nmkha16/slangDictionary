package vn.hcmus.edu.kha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

class MyWindow extends JFrame {
    JLabel label1 = new JLabel("Từ vựng");
    JLabel label2 = new JLabel("Định nghĩa");
    public MyWindow() {
        super("Slang Dictionary");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MyWindow.this.setVisible(false);
                MyWindow.this.dispose();
            }
        });
        setPreferredSize(new Dimension(720, 480));
        setMinimumSize(new Dimension(720,480));
        setLayout(new BorderLayout());
        // set gridbaglayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // create top panel for list of function
        // including Label, ComboBox
        JPanel top_pn = new JPanel();
        top_pn.setLayout(new FlowLayout());
        top_pn.add(new JLabel("Chức năng"),gbc);
        JComboBox functionCB = new JComboBox();
        top_pn.add(Box.createRigidArea(new Dimension(25, 0)));
        functionCB.setPreferredSize(new Dimension(300,20));
        top_pn.add(functionCB,gbc);
        add(top_pn,BorderLayout.NORTH);

        // create left panel for list of word
        // left panel
        JPanel left_pn = new JPanel();
        left_pn.setLayout(new GridBagLayout());
        left_pn.add(Box.createRigidArea(new Dimension(10, 0)),gbc);
        gbc.gridx++;
        left_pn.add(label1,gbc);
        gbc.gridy++;
        JList functionList = new JList();
        JScrollPane functionListScroll = new JScrollPane(functionList);
        functionListScroll.setPreferredSize(new Dimension(200,312));
        gbc.gridx=0;
        left_pn.add(Box.createRigidArea(new Dimension(10, 0)),gbc);
        gbc.gridx++;
        left_pn.add(functionListScroll,gbc);
        gbc.gridy++;
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new FlowLayout());
        JButton delete_btn = new JButton("Delete");
        JButton add_btn = new JButton("Add");
        button_panel.add(add_btn);
        button_panel.add(delete_btn);
        gbc.gridx=0;
        left_pn.add(Box.createRigidArea(new Dimension(10, 0)),gbc);
        gbc.gridx++;
        left_pn.add(button_panel,gbc);
        add(left_pn,BorderLayout.WEST);

        gbc.gridy=0;
        gbc.gridx=0;

        // create middle pannel for
        JPanel middle_pn = new JPanel();
        middle_pn.setLayout(new GridBagLayout());
        middle_pn.add(label2,gbc);
        gbc.gridy++;
        JTextArea jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(480,350));
        middle_pn.add(jScrollPane,gbc);
        add(middle_pn,BorderLayout.CENTER);

        //create bottom pannel
        JPanel bottom_pn = new JPanel();
        bottom_pn.setLayout(new FlowLayout());
        bottom_pn.add(Box.createRigidArea(new Dimension(143, 0)));
        JTextArea textArea = new JTextArea(2, 25);
        JScrollPane jsp1 = new JScrollPane(textArea);
        bottom_pn.add(jsp1);
        JButton search_btn = new JButton("Search");
        bottom_pn.add(search_btn);
        JButton edit_btn = new JButton("Edit");
        bottom_pn.add(edit_btn);
        add(bottom_pn,BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        pack();
    }
}
