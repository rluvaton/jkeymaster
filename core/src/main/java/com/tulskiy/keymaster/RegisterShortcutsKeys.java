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
import java.util.Arrays;
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
    public static final List<Integer> MODIFIERS = Arrays.asList(KeyEvent.VK_ALT, KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_META);

    //Create a file chooser
    final JFileChooser fc = new JFileChooser();

    public String pathToFile = null;

    private RegisterShortcutsKeys thisDialog;

    public RegisterShortcutsKeys()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.provider = Provider.getCurrentProvider(true);

        this.thisDialog = this;

        if (provider == null) {
            System.exit(1);
        }

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


        // Set System Tray Icon
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(toolkit.getImage("../../../../../../../Images/icon.png"));

        MediaTracker tracker = new MediaTracker(this);

        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components

        MenuItem addShortcut = new MenuItem("Add Shortcut");
        addShortcut.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                thisDialog.setVisible(true);
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                thisDialog.close();
            }
        });

        //Add components to pop-up menu
        popup.add(addShortcut);
        popup.addSeparator();
        popup.add(exit);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

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


    public void close() {
        System.exit(0);
    }

    public static void main(String[] args) {
        RegisterShortcutsKeys dialog = new RegisterShortcutsKeys();
        dialog.pack();
        // dialog.setVisible(true);
        // System.exit(0);

        /*frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                provider.reset();
                provider.stop();
                System.exit(0);
            }
        });*/
    }
}
