import java.util.concurrent.LinkedBlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Recver extends MQConfig {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        final Connection connection = factory.newConnection();

        for (int i = 0; i < consumerCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Channel channel = connection.createChannel();
                        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                        LinkedBlockingQueue<QueueingConsumer.Delivery> blockingQueue = new LinkedBlockingQueue<QueueingConsumer.Delivery>();
                        QueueingConsumer consumer = new QueueingConsumer(channel, blockingQueue);
                        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
                        System.out.println(" [*] Waiting for messages.");

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            // System.out.println(" [x] Received '" + message + "'");
                            if (!autoAck) {
                                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                            }
                        }
                    } catch (Exception ignore) {
                    }
                }
            }).start();
        }

        while (true) {
            Thread.sleep(1000);
        }
        // connection.close();
    }
}
