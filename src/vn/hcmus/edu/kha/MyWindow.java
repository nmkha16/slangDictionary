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

    private String[] function = new String[]{"Tìm kiếm","Lịch sử tìm kiếm","Khôi phục gốc","Random slang",
    "Slang Quiz","Definition Quiz"};
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
        try {
            slang = readSlang("slang.txt");
        }catch (Exception E){slang = new HashMap<>();}

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
                else if(event.equals("Khôi phục gốc")){
                    try {
                        slang = readSlang("slang.txt.bak");
                        JOptionPane.showMessageDialog(null,"Successfully Restored."
                                ,"Alert",JOptionPane.WARNING_MESSAGE);
                        saveSlang("slang.txt");
                        refreshSlangList();
                    }catch (Exception E){
                        JOptionPane.showMessageDialog(null,"Failed to Restore."
                                ,"Alert",JOptionPane.WARNING_MESSAGE);
                    }

                }
                else if (event.equals("Random slang")) {
                    Random random = new Random();
                    int rand = random.nextInt(slang.size());
                    String slangw = "";
                    ArrayList<String> slangd = null;
                    // now traverse through hash map
                    int i = 0;
                    for (Map.Entry<String, ArrayList<String>> entry : slang.entrySet()) {
                        if (i == rand) {
                            slangw = entry.getKey();
                            slangd = entry.getValue();
                        }
                        i++;
                    }
                    // build definition string
                    String output ="";
                    for (i = 0; i < slangd.size(); i++) {
                        output += " - " + slangd.get(i) + "\n";
                    }
                    // now show JOptionPane
                    JOptionPane.showMessageDialog(null,"Today slang is: "+slangw+"\nDefinition:\n" +
                            output);
                }
                else if(event.equals("Slang Quiz")) {
                    slangQuiz();
                }
                else if(event.equals("Definition Quiz")){
                    definitionQuiz();
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
        //Action event for delete button
        delete_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String slangw = slangList.getSelectedValue().toString();
                    int op = JOptionPane.showConfirmDialog(null,"Confirm to delete '"
                            +slangw+"'","Delete slang",JOptionPane.OK_CANCEL_OPTION);
                    if (op == JOptionPane.OK_OPTION){
                        slang.remove(slangw);
                        saveSlang("slang.txt");
                        JOptionPane.showMessageDialog(null,"Deleted '"
                                +slangw+"'");
                        refreshSlangList();
                    }
                }catch(NullPointerException nullPointerException){}
                catch(Exception E){}
            }
        });
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
                                            " in dictionary!\nDo you want to overwrite?\nWarning: Perform " +
                                            "overwriting will delete all former definitions and carry" +
                                            " current edited slang's definitions over!!!", "Duplicate warning",
                                    JOptionPane.OK_CANCEL_OPTION);
                            if (op == JOptionPane.OK_OPTION) {
                                slang.put(input, slang.get(slangList.getSelectedValue().toString()));
                                slang.remove(slangList.getSelectedValue().toString());
                                saveSlang("slang.txt");
                                refreshSlangList();
                            }
                        }
                        else{
                            slang.put(input, slang.get(slangList.getSelectedValue().toString()));
                            slang.remove(slangList.getSelectedValue().toString());
                            saveSlang("slang.txt");
                            refreshSlangList();
                        }
                    }
                }catch (NullPointerException nullPointerException){}
                catch(Exception E){}
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
                            try {
                                saveSlang("slang.txt");
                            }catch (Exception E){}
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
        JButton editDef_btn = new JButton("Edit Definition");
        editDef_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // allow performing definition edit here
                String slangw= slangList.getSelectedValue().toString();
                ArrayList<String> slangd = new ArrayList<>();

                // each definition is split by a \n
                String definitions = jTextArea.getText();

                for (String def : definitions.split("\n")){
                    slangd.add(def);
                }
                // now put them to hash map
                slang.remove(slangw);
                slang.put(slangw,slangd);
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
    private Map<String, ArrayList<String>> readSlang(String filename) throws IOException {
        Map<String, ArrayList<String>> result = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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
            return result;
        }
    }

    /**
     * save current slang to text file
     * @param filename String
     */
    private void saveSlang(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.write("Slag`Meaning\n"); // header for text file
        for (Map.Entry<String, ArrayList<String>> entry : slang.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();

            // build string for definition
            String output="";
            if (value.size()<=1){
                for (String s : value){
                    output+= s;
                }
            }
            else{
                for (String s: value) {
                    if (s.equals(value.get(value.size()-1))) {
                        output += s;
                        break;
                    }
                    output += s + "| ";
                }
            }

            writer.write(key+"`"+output+"\n");

        }
        writer.close();
        //JOptionPane.showMessageDialog(null,"Successfully Saved.","Alert",JOptionPane.WARNING_MESSAGE);
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

    /**
     * slang quiz function
     */
    private void slangQuiz(){
        Random random = new Random();
        int rand = random.nextInt(slang.size());
        String quest= null;
        String ans= null;
        // now traverse through hash map
        int i,k;
        i = k = 0;
        // create definition for 3 wrong answer
        String[] def_ans = new String[4];
        Integer[] def_ans_index = new Integer[]{random.nextInt(slang.size()),random.nextInt(slang.size()),
                random.nextInt(slang.size()),random.nextInt(slang.size())};
        Arrays.sort(def_ans_index);
        int correctChoiceIndex = random.nextInt(4);
        for (Map.Entry<String, ArrayList<String>> entry : slang.entrySet()) {
            if (i == rand) {
                quest = entry.getKey();
                ans = entry.getValue().get(random.nextInt(entry.getValue().size()));
                def_ans[correctChoiceIndex] = ans;
            }
            if (k < 4 && def_ans_index[k] == i){
                if (k == correctChoiceIndex){
                    k++;
                    continue;
                }
                def_ans[k] = entry.getValue().get(random.nextInt(entry.getValue().size()));
                k++;
            }
            i++;
        }
        int n = JOptionPane.showOptionDialog(null, "What does " + quest + " mean?"
                , "Slang Quiz",0,JOptionPane.QUESTION_MESSAGE, null,def_ans,def_ans[0]);
        if (n!= -1) {
            if (def_ans[n].equals(ans)) {
                JOptionPane.showMessageDialog(null, "Correct Answer!!!");
            } else {
                JOptionPane.showMessageDialog(null,"Wrong, correct answer for " + quest
                        + " is\n - " + ans,"Failed",0);
            }
        }
    }

    /**
     * Definition Quiz function
     */
    private void definitionQuiz(){
        Random random = new Random();
        int rand = random.nextInt(slang.size());
        String quest= null;
        String ans= null;
        // now traverse through hash map
        int i,k;
        i = k = 0;
        // create definition for 3 wrong answer
        String[] def_ans = new String[4];
        Integer[] def_ans_index = new Integer[]{random.nextInt(slang.size()),random.nextInt(slang.size()),
                random.nextInt(slang.size()),random.nextInt(slang.size())};
        Arrays.sort(def_ans_index);
        int correctChoiceIndex = random.nextInt(4);
        for (Map.Entry<String, ArrayList<String>> entry : slang.entrySet()) {
            if (i == rand) {
                quest = entry.getValue().get(random.nextInt(entry.getValue().size()));
                ans = entry.getKey();
                def_ans[correctChoiceIndex] = ans;
            }
            if (k < 4 && def_ans_index[k] == i){
                if (k == correctChoiceIndex){
                    k++;
                    continue;
                }
                def_ans[k] = entry.getKey();
                k++;
            }
            i++;
        }
        int n = JOptionPane.showOptionDialog(null, "Which answer below means " + quest + "?"
                , "Definition Quiz",0,JOptionPane.QUESTION_MESSAGE, null,def_ans,def_ans[0]);
        if (n!= -1) {
            if (def_ans[n].equals(ans)) {
                JOptionPane.showMessageDialog(null, "Correct Answer!!!");
            } else {
                JOptionPane.showMessageDialog(null,"Wrong, correct answer for " + quest
                        + " is\n - " + ans,"Failed",0);
            }
        }
    }
}
