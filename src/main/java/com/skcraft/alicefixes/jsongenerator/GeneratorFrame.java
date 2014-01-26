package com.skcraft.alicefixes.jsongenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class GeneratorFrame extends JFrame {

    private PatchList patchList = new PatchList();

    public GeneratorFrame() {
        super("Alice Fixes - JSON Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        addComponents();
    }

    private void addComponents() {
        final JTextField className = new JTextField(25);
        final JTextField methodName = new JTextField(25);
        final JTextField methodType = new JTextField(25);

        JLabel classLabel = new JLabel("Class Name:");
        classLabel.setLabelFor(className);
        JLabel methodLabel = new JLabel("Method Name:");
        methodLabel.setLabelFor(methodName);
        JLabel typeLabel = new JLabel("Return Type:");
        typeLabel.setLabelFor(methodType);

        JPanel textPanel = new JPanel(new SpringLayout());
        textPanel.add(classLabel);
        textPanel.add(className);
        textPanel.add(methodLabel);
        textPanel.add(methodName);
        textPanel.add(typeLabel);
        textPanel.add(methodType);
        makeCompactGrid(textPanel, 3, 2, 6, 6, 6, 6);

        final JTextArea paramsBox = new JTextArea();
        paramsBox.setLineWrap(false);
        JScrollPane paramsScrollPane = new JScrollPane(paramsBox);
        paramsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        paramsScrollPane.setPreferredSize(new Dimension(310, 100));
        paramsScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Method Parameters (one per line)"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                        paramsScrollPane.getBorder()));

        final JCheckBox blackList = new JCheckBox("Add a blacklist?");
        final JCheckBox tileCheck = new JCheckBox("Is this a TileEntity?");
        final JTextField facingField = new JTextField(25);
        facingField.setMaximumSize(new Dimension(100, 20));
        final JTextField facingType = new JTextField(25);
        facingType.setMaximumSize(new Dimension(100, 20));
        facingType.setEditable(false);
        facingField.setEditable(false);

        JLabel facingLabel = new JLabel("Facing field name:");
        facingLabel.setLabelFor(facingField);
        JLabel ftypeLabel = new JLabel("Facing field type:");
        ftypeLabel.setLabelFor(facingType);

        JPanel blackListPanel = new JPanel(new SpringLayout());
        blackListPanel.add(facingLabel);
        blackListPanel.add(facingField);
        blackListPanel.add(ftypeLabel);
        blackListPanel.add(facingType);
        makeCompactGrid(blackListPanel, 2, 2, 6, 6, 6, 6);

        JPanel midPanel = new JPanel(new SpringLayout());
        midPanel.add(paramsScrollPane);
        midPanel.add(blackList);
        midPanel.add(tileCheck);
        midPanel.add(blackListPanel);
        makeCompactGrid(midPanel, 4, 1, 6, 6, 6, 6);

        JButton generateButton = new JButton("Generate...");

        final JTextArea jsonOutput = new JTextArea();
        jsonOutput.setEditable(false);
        jsonOutput.setLineWrap(false);
        JScrollPane jsonScrollPane = new JScrollPane(jsonOutput);
        jsonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsonScrollPane.setPreferredSize(new Dimension(310, 440));
        jsonScrollPane.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Generated JSON"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                        jsonScrollPane.getBorder()));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(textPanel, BorderLayout.NORTH);
        leftPanel.add(midPanel, BorderLayout.CENTER);
        leftPanel.add(generateButton, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(jsonScrollPane);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateJSON(className.getText(), methodName.getText(), methodType.getText(), paramsBox.getText(),
                        blackList.isSelected(), tileCheck.isSelected(), facingField.getText(), facingType.getText(), jsonOutput);
            }
        });

        tileCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tileCheck.isSelected()) {
                    facingField.setEditable(true);
                    facingType.setEditable(true);
                } else {
                    facingField.setText("");
                    facingField.setEditable(false);
                    facingType.setText("");
                    facingType.setEditable(false);
                }
            }
        });
    }

    private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    //Stole this from Oracle...don't think they'll mind.
    public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout)parent.getLayout();
        } catch (ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        //Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width,
                        getConstraintsForCell(r, c, parent, cols).
                                getWidth());
            }
            for (int r = 0; r < rows; r++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        //Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height,
                        getConstraintsForCell(r, c, parent, cols).
                                getHeight());
            }
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints constraints =
                        getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        //Set the parent's size.
        SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }

    private void generateJSON(String className, String methodName, String methodType, String params, boolean blacklist,
                              boolean tileEntity, String facingVar, String facingType, JTextArea output) {
        patchList.add(className + ":" + methodName, new JsonHelperObject(className, methodName, methodType, params,
                blacklist, tileEntity, facingVar, facingType));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        output.setText(gson.toJson(patchList));
    }

    public static class PatchList {
        private Map<String, JsonHelperObject> patches = new HashMap();

        public void add(String key, JsonHelperObject obj) {
            patches.put(key, obj);
        }

        public Map getPatches() {
            return patches;
        }
    }
}
