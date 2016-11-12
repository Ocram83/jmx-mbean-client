# jmx-mbean-client
This repository contains an example application that performs a query on the value of an MBean published on a remote machine an plot it in a chart.

## Technology used
The application is written in Java and make use open source project such as Jopt and JFreeChart.
It can be compiled with maven.
During compilation a JUnit test case tests the main JMX connection class.

## Usage
Run <code>mvn package</code> to generate an executable jar with dependencies included.

Run <code>mvn exec:java -Dexec.args=""</code> to execute the program direcly, note that some program arguments are required.

Here is a list of available options

* host: the host machine listening for jmx connection
* port: the remote port of the server
* type: the type of the MBean
* attribute: the name of the MBean
* compositeVar: in case of multivalued MBean, the key of the value we want to retrieve
* range: the range of value which the MBean value have to occur \[minval:maxval\] (a warining is printed on std output otherwise)

other options are not implemented or not completly functional.

#### Example usage
Run <code>mvn exec:java -Dexec.args="-help"</code> for system Help

Run <code>mvn exec:java -Dexec.args="-port=9999 -host=localhost -type=java.lang:type=Memory -attribute=HeapMemoryUsage -compositeVar=used "</code>
to connect on port 9999 on a local machine and verify the used Java Heap Memory
