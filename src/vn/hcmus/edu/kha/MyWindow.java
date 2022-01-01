package vn.hcmus.edu.kha;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MyWindow extends JFrame {
    JLabel label1 = new JLabel("Từ vựng");
    JLabel label2 = new JLabel("Định nghĩa");
    private JList slangList;
    private DefaultListModel listModel;

    private JTextArea jTextArea;
    private JTextField search_input;
    private Map<String, ArrayList<String>> slang;

    private String[] function = new String[]{"Tìm kiếm","Lịch sử tìm kiếm"};
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
        // read HashMap
        slang = readSlang();


        // set property of window
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
        JComboBox functionCB = new JComboBox(function);
        functionCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String event = functionCB.getSelectedItem().toString();
                if (event.equals("Lịch sử tìm kiếm")){
                    Scanner input = null;
                    JTextArea textArea = new JTextArea();
                    textArea.setEditable(false);
                    try {
                        input = new Scanner(new File("history.txt"));
                        input.useDelimiter("\\A");
                        if (input.hasNext()) {
                            textArea.append(input.next());
                        }
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    textArea.setColumns(30);
                    textArea.setRows(10);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);
                    JPanel panel = new JPanel();  // Create and modify this panel
                    JOptionPane.showOptionDialog(null,
                            panel,
                            "Lịch sử",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new Object[] {textArea},
                            null);
                }
                functionCB.setSelectedIndex(0);
            }
        });
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

        listModel = new DefaultListModel<Set<String>>();
        listModel.addAll(slang.keySet());
        slangList = new JList<Set<String>>(listModel);
        slangList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    ArrayList<String> result = slang.get(slangList.getSelectedValue().toString());
                    jTextArea.setText(null);
                    for (String str : result) {
                        jTextArea.append(str + "\n");
                    }
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        });
        JScrollPane slangListScroll = new JScrollPane(slangList);
        slangListScroll.setPreferredSize(new Dimension(200,270));
        gbc.gridx=0;
        left_pn.add(Box.createRigidArea(new Dimension(10, 0)),gbc);
        gbc.gridx++;
        left_pn.add(slangListScroll,gbc);
        gbc.gridy++;
        JPanel button_panel = new JPanel();
        button_panel.setLayout(new FlowLayout());
        JButton delete_btn = new JButton("Delete");
        JButton add_btn = new JButton("Add");
        JButton editSlang_btn = new JButton("Edit");
        // Action event for edit button
        editSlang_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String input = JOptionPane.showInputDialog(null, "Edit slang",
                            slangList.getSelectedValue().toString());
                    if (!input.equals(null)) {    //OK/Cancel option, cancel option return null
                        if (slang.containsKey(input)) {
                            int op = JOptionPane.showConfirmDialog(null, "New edited slang duplicates " +
                                            "'" +input +"'"+
                                            " in dictionary!\nDo you want to overwrite?\nWarning: Doing " +
                                            "overwrite will delete all former definitions and carry" +
                                            " current edited slang's definitions over!!!", "Duplicate warning",
                                    JOptionPane.OK_CANCEL_OPTION);
                            if (op == JOptionPane.OK_OPTION) {
                                slang.put(input, slang.get(slangList.getSelectedValue().toString()));
                                slang.remove(slangList.getSelectedValue().toString());
                                refreshSlangList();
                            }
                        }
                        else{
                            slang.put(input, slang.get(slangList.getSelectedValue().toString()));
                            slang.remove(slangList.getSelectedValue().toString());
                            refreshSlangList();
                        }
                    }
                }catch (NullPointerException nullPointerException){}
            }
        });
        // action event for add button
        add_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // add new slang here

                String input = JOptionPane.showInputDialog(null, "Add slang");
                if (!input.equals(null)){    //OK/Cancel option, cancel option return null
                    if (slang.containsKey(input)){
                        int op =JOptionPane.showConfirmDialog(null,"New slang found" +
                                " in dictionary\nDo you want to add new definition to " + input,"Slang confirmation",JOptionPane.OK_CANCEL_OPTION);
                        if (op == JOptionPane.OK_OPTION){
                            String newDef = JOptionPane.showInputDialog(null, "Add new definition to "+ input);
                            addSlangDefinition(input,newDef);
                        }
                    }
                }
            }
        });
        button_panel.add(add_btn);
        button_panel.add(editSlang_btn);
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
        jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(480,308));
        middle_pn.add(jScrollPane,gbc);
        add(middle_pn,BorderLayout.CENTER);

        //create bottom pannel
        JPanel bottom_pn = new JPanel();
        bottom_pn.setPreferredSize(new Dimension(100,70));
        bottom_pn.setLayout(new FlowLayout());

        JComboBox search_criteria = new JComboBox(new String[]{"Search by slang","Search by definition"});
        bottom_pn.add(search_criteria);
        //bottom_pn.add(Box.createRigidArea(new Dimension(50, 0)));
        search_input = new JTextField(30);
        bottom_pn.add(search_input);
        JButton search_btn = new JButton("Search");
        search_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (search_input.getText().equals("")){
                    refreshSlangList();
                    return;
                }
                if (search_criteria.getSelectedItem().toString().equals("Search by slang")) {
                    listModel.clear();
                    Pattern pattern = Pattern.compile(Pattern.quote(search_input.getText()), Pattern.CASE_INSENSITIVE);
                    for (String key : slang.keySet()) {
                        Matcher matcher = pattern.matcher(key);
                        if (matcher.find()) {
                            listModel.addElement(key);
                        }
                    }
                    // write to history file
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    try {
                        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("history.txt"),
                                true));
                        writer.append(formatter.format(date)+" : " + search_input.getText()+" (slang)\n");
                        writer.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        //done
                    }
                }
                else{
                    listModel.clear();
                    Pattern pattern = Pattern.compile(Pattern.quote(search_input.getText()), Pattern.CASE_INSENSITIVE);
                    for (Map.Entry<String, ArrayList<String>> entry : slang.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<String> value = entry.getValue();
                        for (String str : value) {
                            Matcher matcher = pattern.matcher(str);
                            if (matcher.find()) {
                                listModel.addElement(key);
                            }
                        }
                    }

                    // write to history file
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    try {
                        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("history.txt"),
                                true));
                        writer.append(formatter.format(date)+" : " + search_input.getText()+" (definition)\n");
                        writer.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        //done
                    }
                }
            }
        });
        bottom_pn.add(search_btn);
        JButton editDef_btn = new JButton("Edit");
        editDef_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        bottom_pn.add(editDef_btn);
        add(bottom_pn,BorderLayout.SOUTH);


        setLocationRelativeTo(null);
        pack();
    }

    /**
     * refresh JList
     */
    private void refreshSlangList(){
        listModel.clear();
        listModel.addAll(slang.keySet());
    }

    /**
     * Return a hashmap of slang and its definitions
     * Also set hashmap of definition and its slang
     * @return Map<String, ArrayList<String>>
     */
    private Map<String, ArrayList<String>> readSlang() {
        Map<String, ArrayList<String>> result = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("slang.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                // skip header
                if (line.equals("Slag`Meaning")) {
                    continue;
                }
                if (line.contains("`")) {
                    ArrayList<String> def = new ArrayList<>();
                    String[] token = line.split("(`)");
                    // first index is slang
                    // second index is definition
                    if (token[1].contains("|")) {
                        String[] definitions = token[1].split("\\| ");

                        for (String definition : definitions) {
                            def.add(definition);
                        }

                        result.put(token[0], def);
                    } else {
                        def.add(token[1]);
                        result.put(token[0], def);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Add new definition to a slang word, cancel add new definition if duplicate def
     * @param slangw String
     * @param slangd String
     */
    private void addSlangDefinition(String slangw,String slangd){
        ArrayList<String> definitions = slang.get(slangw);
        if (definitions.contains(slangd)){
            return;
        }
        slang.remove(slangw);
        slang.put(slangw,definitions);
    }

}