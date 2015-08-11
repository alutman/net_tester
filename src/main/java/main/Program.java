package main;

import client.Client;
import client.TCPClient;
import client.UDPClient;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;


import server.Server;
import server.TCPServer;
import server.UDPServer;


/**
 * Created by alutman on 10-Aug-15.
 */
public class Program {

    private static final int DEFAULT_TIMEOUT = 10000;
    private static final long DEFAULT_DELAY = 1000l;

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "display this help");
        options.addOption(OptionBuilder.withLongOpt("udp")
                .withDescription("connect with UDP using PORT")
                .hasArg()
                .withArgName("PORT")
                .create("t"));
        options.addOption(OptionBuilder.withLongOpt("tcp")
                .withDescription("connect with TCP using PORT")
                .hasArg()
                .withArgName("PORT")
                .create("u"));
        options.addOption(OptionBuilder.withLongOpt("client")
                .withDescription("run in client mode")
                .hasArg()
                .withArgName("ADDRESS")
                .create("c"));
        options.addOption(OptionBuilder.withLongOpt("delay")
                .withDescription("delay between requests (Default 1)")
                .hasArg()
                .withArgName("SECONDS")
                .create());
        options.addOption(OptionBuilder.withLongOpt("timeout")
                .withDescription("timeout for requests (Default 10)")
                .hasArg()
                .withArgName("SECONDS")
                .create());
        options.addOption("s", "server", false, "run in server mode");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("net_tester [OPTIONS]",
                        "Net health testing tool",
                        options,
                        "");
                System.exit(0);
            }

            /* GET PROTOCOL/PORT */
            int udpPort = 0;
            int tcpPort = 0;
            try {
                if(cmd.hasOption("udp")) {
                    udpPort = Integer.parseInt(cmd.getOptionValue("udp"));
                }
                if(cmd.hasOption("tcp")) {
                    tcpPort = Integer.parseInt(cmd.getOptionValue("tcp"));
                }
            }
            catch(NumberFormatException nfe) {
                close("invalid PORT");
            }
            if(udpPort == 0 && tcpPort == 0) {
                close("At least one protocol must be specified (--udp PORT and/or --tcp PORT");
            }

            /* GET MODE */
            if(cmd.hasOption("client")) {
                int timeout = getTimeout(cmd);
                Long delay = getDelay(cmd);
                String address = cmd.getOptionValue("client");
                System.out.println("Running client to "+address);
                if(udpPort > 0) {
                    System.out.println("\tUDP PORT : "+udpPort); 
                    startUDPClient(address, udpPort, timeout, delay);
                }
                if(tcpPort > 0) {
                    System.out.println("\tTCP PORT : "+tcpPort); 
                    startTCPClient(address, tcpPort, timeout, delay);
                }
            }
            else if(cmd.hasOption("server")) {
                System.out.println("Running server on localhost");
                if(udpPort > 0) {
                    System.out.println("\tUDP PORT : "+udpPort); 
                    startUDPServer(udpPort);
                }
                if(tcpPort > 0) {
                    System.out.println("\tTCP PORT : "+tcpPort);
                    startTCPServer(tcpPort);
                }
            }
            else {
                close("client or server must be specified");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private static int getTimeout(CommandLine cmd)  {
        if(cmd.hasOption("timeout")) {
            try {
                return Math.max(Integer.parseInt(cmd.getOptionValue("timeout")) * 1000, 1000);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Invalid timeout: "+cmd.getOptionValue("timeout")+". Using default "+DEFAULT_TIMEOUT/1000);
            }

        }
        return DEFAULT_TIMEOUT;
    }

    private static Long getDelay(CommandLine cmd)  {
        if(cmd.hasOption("delay")) {
            try {
                return Math.max(Long.parseLong(cmd.getOptionValue("delay")) * 1000L, 1000L);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Invalid delay: "+cmd.getOptionValue("delay")+". Using default "+DEFAULT_DELAY/1000);
            }

        }
        return DEFAULT_DELAY;
    }


    private static void close(String message) {
        System.out.println(message);
        System.exit(1);
    }

    private static void startUDPServer(int udpPort) {
        Server udpServer = new UDPServer(udpPort);
        Thread udpServerThread = new Thread(udpServer);
        udpServerThread.start();
        
    }
    private static void startTCPServer(int tcpPort) {
        Server tcpServer = new TCPServer(tcpPort);
        Thread tcpServerThread = new Thread(tcpServer);
        tcpServerThread.start();
        
    }
    private static void startUDPClient(String address, int udpPort, int timeout, long delay) {
        Client udpClient = new UDPClient(address, udpPort, timeout, delay);
        Thread udpClientThread = new Thread(udpClient);
        udpClientThread.start();
    }

    private static void startTCPClient(String address, int tcpPort, int timeout, long delay) {
        Client tcpClient = new TCPClient(address, tcpPort, timeout, delay);
        Thread tcpClientThread = new Thread(tcpClient);
        tcpClientThread.start();
    }

}
