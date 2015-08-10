package main;

import client.UDPClient;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;


import server.UDPServer;


/**
 * Created by alutman on 10-Aug-15.
 */
public class Program {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "display this help");
        options.addOption(OptionBuilder.withLongOpt("port")
                .withDescription("port to connect with")
                .hasArg()
                .withArgName("PORT")
                .create("p"));
        options.addOption("u", "udp", false, "connect with UDP");
        options.addOption("t", "tcp", false, "connect with TCP");
        options.addOption(OptionBuilder.withLongOpt("client")
                .withDescription("run in client mode")
                .hasArg()
                .withArgName("ADDRESS")
                .create("c"));
        options.addOption("s", "server", false, "run in server mode");

        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(options, args);


            if (cmd.hasOption("help")) {
                HelpFormatter hf = new HelpFormatter();
                hf.printHelp("net_tester [OPTIONS] PORT",
                        "Net health testing tool",
                        options,
                        "");
                System.exit(0);
            }

            /* GET PORT */
            int port = 0;
            try {
                 port = Integer.parseInt(cmd.getArgs()[0]);
            }
            catch(ArrayIndexOutOfBoundsException aioobe) {
                System.out.println("PORT is required");
                System.exit(1);
            }
            catch(NumberFormatException nfe) {
                System.out.println("invalid PORT");
                System.exit(1);
            }

            /* GET PROTOCOL */
            boolean useUdp = false;
            if(cmd.hasOption("udp") && cmd.hasOption("tcp")) {
                System.out.println("only udp OR tcp can be specified");
                System.exit(1);
            }
            else if(cmd.hasOption("udp")) {
                useUdp = true;
            }

            /* GET MODE */
            if(cmd.hasOption("client")) {
                String address = cmd.getOptionValue("client");
                String protocol = useUdp ? "udp" : "tcp";
                System.out.println("Running client to "+address+":"+port+" using "+protocol);
                //RUN CLIENT
            }
            else if(cmd.hasOption("server")) {
                String protocol = useUdp ? "udp" : "tcp";
                System.out.println("Running server on localhost:"+port+" using "+protocol);
            }
            else {
                System.out.println("client or server must be specified");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    private static void blah() {
        UDPServer server = new UDPServer(9998);

        Thread t = new Thread(server);
        t.start();
        System.out.println("Starting client");
        UDPClient client = new UDPClient("localhost", 9998, 1000l);
        client.run();
    }

}
