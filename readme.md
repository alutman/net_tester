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
        java -jar net_tester.jar -s --tcp 9997
* Start the client on the other machine. Specify the protocols, ports and the server address. Examples

        java -jar net_tester -c my.server.com --udp 9998 --tcp 9997
        java -jar net_tester -c my.server.com -u 9998
        java -jar net_tester -c my.server.com --tcp 9997
        
You can specify the following options as well when running in client mode

* `--csv` : Output results in CSV format (`DATE,PROTOCOL,RESULT,MESSAGE/TIME`)
* `--delay <SECONDS>` : Seconds of delay between each request to a port. The default and minimum is 1 second.
* `--timeout <MILLISECONDS>` : Connection timeout threshold. Once this is reached, the connection is aborted and
another connection is tried after the delay