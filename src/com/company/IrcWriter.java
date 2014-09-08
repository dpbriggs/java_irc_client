package com.company;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by david on 06/09/14.
 */
public class IrcWriter {
    BufferedWriter writer;
    String channel;

    public IrcWriter(BufferedWriter writer, String channel) {
        this.writer = writer;
        this.channel = channel;
    }


    public void sendMessage(String message) throws IOException {
        if (message.startsWith("/join ")) {
            writer.write("JOIN " + message.substring(6) + "\r\n");
            this.channel = message.substring(6);

            writer.flush();
            System.out.println();
        } else {
            writer.write("PRIVMSG " + channel + " :" + message + "\r\n");
            writer.flush();
        }
        //System.out.println("PRIVMSG " + channel + " :" + message +"\r\n");
    }
}
