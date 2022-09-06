package com.shpp.p2p.cs.oholubovskyi.nameSurfer;

/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implement the viewer for
 * the baby-name database described in the assignment handout.
 */

import com.shpp.cs.a.simple.SimpleProgram;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class NameSurfer extends SimpleProgram implements NameSurferConstants {

    private NameSurferGraph graph;
    private NameSurferDataBase dataBase;
    private JTextField textField;
    static String dialogMessage = "No name in database";

    /**
     * This method has the responsibility for reading in the database
     * and initializing the interactors at the top of the window.
     */
    public void init() {
        dataBase = new NameSurferDataBase(NAMES_DATA_FILE);
        graph = new NameSurferGraph();
        add(graph);
        createButtons();
        addActionListeners();
    }

    /**
     * This method has the responsibility for reading in the database
     * and initializing the interactors at the top of the window.
     */
    private void createButtons() {

        add(new JLabel("Name: "), NORTH);

        textField = new JTextField(NAME_FIELD);
        textField.setActionCommand("Graph");
        textField.addActionListener(this);
        add(textField, NORTH);

        add(new JButton("Graph"), NORTH);
        add(new JButton("Clear"), NORTH);
    }

    /**
     * This class is responsible for detecting when the buttons are
     * clicked, so you will have to define a method to respond to
     * button actions.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("Graph")) {
            String name = textField.getText().toLowerCase();
            NameSurferEntry entry = dataBase.findEntry(name);
            if (entry != null) {
                graph.addEntry(entry);
                graph.update();
            } else {
                this.getDialog().showErrorMessage(dialogMessage + " ! ! !");
            }
        } else if (cmd.equals("Clear")) {
            graph.clear();
            textField.setText("");
        }
    }
}
