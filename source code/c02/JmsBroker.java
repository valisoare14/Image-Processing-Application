package jmsbroker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.activemq.broker.BrokerService;

public class JmsBroker {
	public static void main(String[] args) {
        try {
            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://172.17.0.3:61616"); //localhost
            broker.setBrokerName("MyEmbeddedBroker");
            broker.start();

            System.out.println("ActiveMQ Broker started");
            System.out.println("Press Ctrl + C to stop the broker manually.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    		while (true) {
                     System.out.println("Type 'Q' for closing JMS Broker service from ActiveMQ - KahaDB - Apache TomEE Server");
                     try {
    		   String input = reader.readLine();
                       if ("Q".equalsIgnoreCase(input.trim())) {
                            break;
    		   }
                     } catch (IOException ioe) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
