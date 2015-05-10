import java.util.concurrent.CountDownLatch;

import com.rabbitmq.client.Channel;

public class BatchConfirmSender extends Sender {

    public static void main(String[] argv) throws Exception {
        final String message = getMsg();
        final CountDownLatch producerLatch = new CountDownLatch(producerCount);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < producerCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Channel channel = createConfirmChannel();
                        for (int i = 0; i < msgcount / producerCount / msgcountPerBatch; i++) {
                            for (int j = 0; j < msgcountPerBatch; j++) {
                                channel.basicPublish("", QUEUE_NAME, prop, message.getBytes());
                            }
                            if (!channel.waitForConfirms()) {
                                System.out.println(" [x] Sent message fail!");
                            }
                        }
                        producerLatch.countDown();
                        closeChannel(channel);
                    } catch (Exception ignore) {
                    }
                }
            }).start();
        }
        producerLatch.await();
        long elapseTime = System.currentTimeMillis() - startTime;
        System.out.println("Confirm Type: batch confirm.");
        System.out.println("Batch Fineness: " + msgcountPerBatch + " msg/batch");
        System.out.println("Message Size: " + msglen + " Bytes");
        System.out.println("Message Count: " + msgcount);
        System.out.println("Message Persistent: " + (prop.getDeliveryMode() == 2 ? "true" : "false"));
        System.out.println("Consumer Autoack: " + (autoAck ? "true" : "false"));
        System.out.println("Consumer Count: " + consumerCount);
        System.out.println("Producer Count: " + producerCount);
        System.out.println("Elapse Time: " + elapseTime / 1000 + " s");
        System.out.println("Publish Rate: " + msgcount * 1000 / elapseTime + " msg/s");
    }
}
