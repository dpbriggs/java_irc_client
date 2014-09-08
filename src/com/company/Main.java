package com.company;

import java.net.*;
import java.io.*;

public class Main {

    public static void main(String[] args) throws Exception {

        // The server to connect to and our details.
        String server = "irc.freenode.net";
        String nick = "simple_bot_6";
        String login = "simple_bot";

        // The channel which the bot will join.
        String channel = "#irchacks";

        // Connect directly to the IRC server.
        Socket socket = new Socket(server, 6667);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream( )));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream( )));

        // Get GUI tools out of the way

        IrcGui ircGui = new IrcGui(writer, channel, nick);

        // Start irc channel reader
        Thread first = new Thread(new IrcReader(writer, reader, channel, ircGui));
        first.start();

        ircGui.addLine("Welcome!");

        ircGui.addLine("Logging in...");
        // Log on to the server.
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * : Java IRC Client\r\n");
        writer.flush( );



        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null) {
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                System.out.print("Logged in!");
                break;
            }
            else if (line.indexOf("433") >= 0) {
                ircGui.addLine("Nickname: " + nick +" is already in use.");
                System.out.println("Nickname is already in use.");
                return;
            }
        }


        // Join the channel.
        ircGui.addLine("Joining Channel");

        writer.write("JOIN " + channel + "\r\n");
        writer.flush( );


    }


}