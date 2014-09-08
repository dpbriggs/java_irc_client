package com.company;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.Buffer;

/**
 * Created by david on 06/09/14.
 */
public class IrcGui extends JFrame {
    private JTextField ircChatInput;
    private JList Properties;
    private JPanel rootPanel;
    private JTextArea ircTextBox;

    private BufferedWriter writer;
    private String channel, nick;

    public IrcGui(BufferedWriter writer, String channel, String nick) {
        super("Irc Client");

        //Get channel related things out of the way
        this.writer = writer;
        this.channel = channel;
        this.nick = nick;

        // Various GUI settings
        rootPanel.setPreferredSize(new Dimension(600, 600));
        ircTextBox.setEditable(false);
        //ircTextBox.setLineWrap(true);
        ircTextBox.setMargin(new Insets(0,5,0,0));
        //Caret

        DefaultCaret caret = (DefaultCaret) ircTextBox.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setContentPane(rootPanel);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

        ircChatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //super.keyPressed(keyEvent);
                if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER && ircChatInput.getText() != null){
                    try {
                        // Send message and add message to textbox, clear input.
                        sendMessage(ircChatInput.getText());
                        ircTextBox.append(ircChatInput.getText() + "\n");
                        ircChatInput.setText("");

                        //ircTextBox.setCaretPosition(ircTextBox.getDocument().getLength());
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendMessage(String message) throws IOException {
        if (message.startsWith("/join ")) {
            String newChannel = message.substring(6);
            writer.write("JOIN " + newChannel + "\r\n");
            writer.flush();
            this.channel = newChannel;
            ircTextBox.setText("");
            addLine("Joining channel " + newChannel);
            revalidate();
        } else {
            writer.write("PRIVMSG " + channel + " :" + message + "\r\n");
            writer.flush();
            revalidate();
        }
        //System.out.println("PRIVMSG " + channel + " :" + message +"\r\n");
        revalidate();
    }
    public void addLine(final String line){
        ircTextBox.append(line + "\n");
        revalidate();
    }
}
