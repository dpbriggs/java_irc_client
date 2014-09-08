package com.company;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by david on 06/09/14.
 */
public class IrcReader implements Runnable {
    BufferedReader reader;
    BufferedWriter writer;
    String channel;
    IrcGui ircGui;


    String topic;
    String[] channelList;
    public IrcReader(BufferedWriter writer, BufferedReader reader, String channel, IrcGui ircGui) {
        this.reader = reader;
        this.writer = writer;
        this.channel = channel;
        this.ircGui = ircGui;
    }

    private void handleProtocol(String line, Integer code) {
        switch (code){
            // MOTD start, MOTD
            case 375:
            case 372: ircGui.addLine(line.substring(line.lastIndexOf(":-")+2));
                      break;

            // End of /MOTD command
            case 376: ircGui.addLine(line.substring(line.lastIndexOf(":")+1));
                      break;

            // Channel topic
            case 332: this.topic = line.substring(line.lastIndexOf(this.channel+" :")+2+this.channel.length());
                      //System.out.println(this.topic);
                      break;

            // Server list
            case 353: this.channelList = line.substring(line.lastIndexOf(this.channel+" :")+2+this.channel.length()).split(" ");
                      System.out.println(this.channelList);
                      break;


            // Useless irc protocols
            case 366: // End of /NAMES list.
            case 333: // elvum
            case 002: // Host information (may use later)
            case 005: // channel information, don't need right now
            case 252: // Channel operators online/offline
            case 253: // Number of unknown connections
            case 254: // Number of channels formed
            case 255: // Number of clients vs servers
            case 265: // Number of local users and local max users
            case 266: // Number of global users and global max users
            case 250: // Highest connection count
            case 251: // number of users and invisible users
            case 412: // No text to send
                break;


            default: ircGui.addLine(line);


        }
    }
    private void handleCommand(String line, String command){
        if(command.equals("PRIVMSG")){
            System.out.println("Made it here");
            ircGui.addLine("PRIVMSG WORKED!");
        }
    }


    // IRC command functions

    private String privmsg(String line){
        // Extract username who authored line


        // Extract channel name OR username if private message
        return "";
    }

    private String containsword(String[] words, String line){
        for (String s: words){
            if(line.contains(s)){
                return s;
            }
        }
        return "";
    }

    @Override
    public void run() {
        // Keep reading lines from the server.
        Pattern protocol = Pattern.compile("\\b\\d{3}\\b");
        final String[] commands = {"PRIVMSG", "MODE", "JOIN"};
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().startsWith("PING ")) {
                    // We must respond to PINGs to avoid being disconnected.
                    writer.write("PONG " + line.substring(5) + "\r\n");
                    writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                    writer.flush();
                } else {
                    // Find any matches for protocols or commands
                    final Matcher m = protocol.matcher(line);
                    final String command = containsword(commands, line);
                    // Line given from server
                    final String finalLine = line;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Check if there's protocol present
                            if (m.find()) {
                                handleProtocol(finalLine, Integer.valueOf(m.group()));

                            // Check if we can get a command instead (PRIVMSG, etc)
                            } else if(command != ""){
                                handleCommand(finalLine, command);
                                ircGui.addLine(finalLine);
                            // Otherwise give up and poop it into chat so we can fix for later
                            } else {
                                ircGui.addLine(finalLine);
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
