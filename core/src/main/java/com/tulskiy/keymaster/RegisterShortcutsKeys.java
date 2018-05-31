package com.tulskiy.keymaster;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;

public class RegisterShortcutsKeys extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton chooseProgram;
    private JTextField ShortcutListener;
    private JLabel labelPath;
    private JButton clearBtn;
    private JButton listenBtn;

    // File Chooser
    Provider provider;
    List<Integer> MODIFIERS;

    //Create a file chooser
    final JFileChooser fc = new JFileChooser();

    public String pathToFile = null;


    public RegisterShortcutsKeys(List<Integer> MODIFIERSInput, Provider providerInput)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.MODIFIERS = MODIFIERSInput;
        this.provider = providerInput;

        final List<Integer> MODIFIERS = this.MODIFIERS;
        final Provider provider = this.provider;

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ShortcutListener.setFont(ShortcutListener.getFont().deriveFont(Font.BOLD, 15f));
        ShortcutListener.setEditable(false);

        ShortcutListener.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                super.keyPressed(e);
                if (MODIFIERS.contains(e.getKeyCode()))
                    ShortcutListener.setText("");
                else
                    ShortcutListener.setText(KeyStroke.getKeyStrokeForEvent(e).toString().replaceAll("pressed ", ""));
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                super.keyReleased(e);
            }
        });



        listenBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShortcutListener.grabFocus();
            }
        });
        chooseProgram.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //In response to a button click:
                int returnVal = fc.showOpenDialog(chooseProgram);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    if(file.isDirectory()) {
                        JOptionPane.showMessageDialog(contentPane, "Please Choose a file");
                        setPathToFile(null);
                        return;
                    }

                    setPathToFile(file.getAbsolutePath());
                } else {
                    setPathToFile(null);
                }
            }
        });
        clearBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ShortcutListener.setText("");
                setPathToFile(null);
            }
        });
    }

    private void onOK()
    {
        // Create the Hot key listener action
        final HotKeyListener listener = new HotKeyListener() {
            public void onHotKey(final HotKey hotKey) {
                if(pathToFile == null) {
                    JOptionPane.showMessageDialog(contentPane, "Please Choose a file");
                    return;
                }

                File file = new File(pathToFile);
                try
                {
                    Desktop.getDesktop().open(file);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        };

        // add your code here
        String text = ShortcutListener.getText();
        if (text != null && text.length() > 0) {
            provider.reset();
            provider.register(KeyStroke.getKeyStroke(text), listener);
        }
        dispose();
    }

    private void onCancel()
    {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents()
    {
        // TODO: place custom component creation code here
    }

    public void setPathToFile(String pathToFile)
    {
        this.pathToFile = pathToFile;
        this.labelPath.setText((pathToFile != null) ? pathToFile : "No Program Selected");
    }
}
