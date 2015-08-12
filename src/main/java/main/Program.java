package main;

import client.Client;
import client.TCPClient;
import client.UDPClient;
import org.apache.commons.cli.*;


import server.Server;
import server.TCPServer;
import server.UDPServer;

import java.util.concurrent.*;


/**
 * Created by alutman on 10-Aug-15.
 */
public class Program {

    private static final int DEFAULT_TIMEOUT_MS = 10000;
    private static final long DEFAULT_DELAY_S = 1l;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "display this help");
        options.addOption(OptionBuilder.withLongOpt("udp")
                .withDescription("connect with UDP using PORT")
                .hasArg()
                .withArgName("PORT")
                .create("u"));
        options.addOption(OptionBuilder.withLongOpt("tcp")
                .withDescription("connect with TCP using PORT")
                .hasArg()
                .withArgName("PORT")
                .create("t"));
        options.addOption(OptionBuilder.withLongOpt("client")
                .withDescription("run in client mode")
                .hasArg()
                .withArgName("ADDRESS")
                .create("c"));
        options.addOption(OptionBuilder.withLongOpt("delay")
                .withDescription("delay between requests (Default 1s)")
                .hasArg()
                .withArgName("SECONDS")
                .create());
        options.addOption(OptionBuilder.withLongOpt("timeout")
                .withDescription("timeout for requests (Default 10000ms)")
                .hasArg()
                .withArgName("MILLISECONDS")
                .create());
        options.addOption(OptionBuilder.withLongOpt("csv")
                .withDescription("output in csv format")
                .create());
        options.addOption("s", "server", false, "run in server mode");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("net_tester [OPTIONS]",
                        "Measures ping between two machines over UDP and/or TCP",
                        options,
                        "Server mode only pays attention to --udp and --tcp options");
                System.exit(0);
            }

            /* GET PROTOCOL/PORT */
            int udpPort = -1;
            int tcpPort = -1;
            try {
                if(cmd.hasOption("udp")) {
                    udpPort = Integer.parseInt(cmd.getOptionValue("udp"));
                }
                if(cmd.hasOption("tcp")) {
                    tcpPort = Integer.parseInt(cmd.getOptionValue("tcp"));
                }
            }
            catch(NumberFormatException nfe) {
                close("Invalid PORT");
            }
            if(udpPort < 0 && tcpPort < 0) {
                close("At least one protocol must be specified (--udp PORT and/or --tcp PORT)");
            }

            /* GET MODE */
            if(cmd.hasOption("client") && !cmd.hasOption("server")) {
                int timeout = getTimeout(cmd);
                Long delay = getDelay(cmd);
                String address = cmd.getOptionValue("client");
                boolean asCsv = cmd.hasOption("csv");
                if(!asCsv) {
                    System.out.println("Running client to " + address);
                }
                if(udpPort > 0) {
                    if(!asCsv) {
                        System.out.println("\tUDP PORT : "+udpPort);
                    }

                    startUDPClient(address, udpPort, timeout, delay, asCsv);
                }
                if(tcpPort > 0) {
                    if(!asCsv) {
                        System.out.println("\tTCP PORT : "+tcpPort);
                    }
                    startTCPClient(address, tcpPort, timeout, delay, asCsv);
                }
            }
            else if(cmd.hasOption("server") && !cmd.hasOption("client")) {
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
                close("client OR server must be specified");
            }

        } catch (UnrecognizedOptionException e) {
            close("Invalid option: " + e.getOption());
        } catch (MissingArgumentException e) {
            close("Argument "+e.getOption().getArgName()+" missing for option " + e.getOption().getLongOpt());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private static int getTimeout(CommandLine cmd)  {
        if(cmd.hasOption("timeout")) {
            try {
                return Math.max(Integer.parseInt(cmd.getOptionValue("timeout")), 1);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Invalid timeout: "+cmd.getOptionValue("timeout")+". Using default "+DEFAULT_TIMEOUT_MS);
            }

        }
        return DEFAULT_TIMEOUT_MS;
    }

    private static Long getDelay(CommandLine cmd)  {
        if(cmd.hasOption("delay")) {
            try {
                return Math.max(Long.parseLong(cmd.getOptionValue("delay")), 1L);
            }
            catch(NumberFormatException nfe) {
                System.out.println("Invalid delay: "+cmd.getOptionValue("delay")+". Using default "+DEFAULT_DELAY_S);
            }

        }
        return DEFAULT_DELAY_S;
    }


    private static void close(String message) {
        System.err.println(message);
        System.err.println("Run with --help for more details");
        System.exit(1);
    }

    private static void startUDPServer(int udpPort) {
        Server udpServer = new UDPServer(udpPort);
        executorService.execute(udpServer);
    }
    private static void startTCPServer(int tcpPort) {
        Server tcpServer = new TCPServer(tcpPort);
        executorService.execute(tcpServer);
    }
    private static void startUDPClient(String address, int udpPort, int timeout, long delay, boolean asCsv) {
        Client udpClient = new UDPClient(address, udpPort, timeout, asCsv);
        executorService.scheduleAtFixedRate(udpClient, 0, delay, TimeUnit.SECONDS);
    }

    private static void startTCPClient(String address, int tcpPort, int timeout, long delay, boolean asCsv) {
        Client tcpClient = new TCPClient(address, tcpPort, timeout, asCsv);
        executorService.scheduleAtFixedRate(tcpClient, 0, delay, TimeUnit.SECONDS);
    }

}
