net_tester
==========
Measures ping between two machines over UDP and/or TCP

Requires Java 1.7

Usage
-----
A full list of commands can be found by running

    java -jar net_tester.jar --help

* Start the server on one machine. Specify UDP and/or TCP with the ports to listen on respectively. Examples

        java -jar net_tester.jar -s --udp 9998 --tcp 9997
        java -jar net_tester.jar -s -u 9998
        java -jar net_tester.jar -s --tcp 9997 --verbose
* Start the client on the other machine. Specify the protocols, ports and the server address. Examples

        java -jar net_tester -c my.server.com --udp 9998 --tcp 9997
        java -jar net_tester -c my.server.com -u 9998
        java -jar net_tester -c my.server.com --tcp 9997 --csv

#### All Options ####
Server mode only pays attention to `--udp`, `--tcp` and `--verbose`. Only one of `--csv` and `--verbose` can be specified for client.

 * `-c,--client <ADDRESS>` : Run in client mode
 * `-d,--delay <SECONDS>` : Delay between requests (Default 1s)
 * `-h,--help` : Display this help
 * `-l,--csv` : Output in csv format
 * `-n,--no-match` : Skip matching the result from server with the request
 * `-o,--timeout <MILLISECONDS>` : Timeout for requests (Default 10000ms)
 * `-s,--server` : Run in server mode
 * `-t,--tcp <PORT>` : Connect with TCP using PORT
 * `-u,--udp <PORT>` : Connect with UDP using PORT
 * `-v,--verbose ` : Output extra details when connecting
