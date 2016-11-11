# jmx-mbean-client
This repository contains an example application that performs a query on the value of an MBean published on a remote machine an plot it in a chart.

## Technology used
The application is written in Java and make use open source project such as Jopt and JFreeChart.
It can be compiled with maven.

## Usage
Run <code>mvn exec:java -Dexec.args="-help"</code> for system Help

Run <code>mvn exec:java -Dexec.args="-port=9999 -host=localhost -type=java.lang:type=Memory -attribute=HeapMemoryUsage -compositeVar=used "</code>
to connect on port 9999 on a local machine and verify the used Java Heap Memory
