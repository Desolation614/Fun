/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.notes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import net.runelite.client.plugins.notes.NotesConfig;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NotesPanel
extends PluginPanel {
    private static final Logger log = LoggerFactory.getLogger(NotesPanel.class);
    private final JTextArea notesEditor = new JTextArea();
    private final UndoManager undoRedo = new UndoManager();

    NotesPanel() {
    }

    void init(final NotesConfig config) {
        this.getParent().setLayout(new BorderLayout());
        this.getParent().add((Component)this, "Center");
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);
        this.notesEditor.setTabSize(2);
        this.notesEditor.setLineWrap(true);
        this.notesEditor.setWrapStyleWord(true);
        JPanel notesContainer = new JPanel();
        notesContainer.setLayout(new BorderLayout());
        notesContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.notesEditor.setOpaque(false);
        String data = config.notesData();
        this.notesEditor.setText(data);
        this.undoRedo.setLimit(500);
        this.notesEditor.getDocument().addUndoableEditListener(e -> this.undoRedo.addEdit(e.getEdit()));
        this.notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        this.notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        this.notesEditor.getActionMap().put("Undo", new AbstractAction("Undo"){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (NotesPanel.this.undoRedo.canUndo()) {
                        NotesPanel.this.undoRedo.undo();
                    }
                }
                catch (CannotUndoException ex) {
                    log.warn("Notes Document Unable To Undo: " + String.valueOf(ex));
                }
            }
        });
        this.notesEditor.getActionMap().put("Redo", new AbstractAction("Redo"){

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (NotesPanel.this.undoRedo.canRedo()) {
                        NotesPanel.this.undoRedo.redo();
                    }
                }
                catch (CannotUndoException ex) {
                    log.warn("Notes Document Unable To Redo: " + String.valueOf(ex));
                }
            }
        });
        this.notesEditor.addFocusListener(new FocusListener(){

            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                this.notesChanged(NotesPanel.this.notesEditor.getDocument());
            }

            private void notesChanged(Document doc) {
                try {
                    String data = doc.getText(0, doc.getLength());
                    config.notesData(data);
                }
                catch (BadLocationException ex) {
                    log.warn("Notes Document Bad Location: " + String.valueOf(ex));
                }
            }
        });
        notesContainer.add((Component)this.notesEditor, "Center");
        notesContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add((Component)notesContainer, "Center");
    }

    void setNotes(String data) {
        this.notesEditor.setText(data);
    }
}

