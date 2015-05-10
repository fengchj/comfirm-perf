import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;

public class AsyncConfirmSender extends Sender {

    private final static Map<Channel, SortedSet<Long>> map = new ConcurrentHashMap<Channel, SortedSet<Long>>();

    public static void main(String[] argv) throws Exception {
        final String message = getMsg();
        final CountDownLatch msgcountLatch = new CountDownLatch(msgcount);
        final CountDownLatch producerLatch = new CountDownLatch(producerCount);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < producerCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        final Channel channel = createConfirmChannel();
                        map.put(channel, Collections.synchronizedSortedSet(new TreeSet<Long>()));
                        channel.addConfirmListener(new ConfirmListener() { // called by connection thread.
                            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                                System.out.println("Nack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
                                if (multiple) {
                                    map.get(channel).headSet(deliveryTag + 1).clear();
                                } else {
                                    map.get(channel).remove(deliveryTag);
                                }
                            }

                            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                                // System.out.println("Ack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
                                if (multiple) {
                                    map.get(channel).headSet(deliveryTag + 1).clear();
                                } else {
                                    map.get(channel).remove(deliveryTag);
                                }
                            }
                        });
                        while (msgcountLatch.getCount() > 0) {
                            msgcountLatch.countDown();
                            long nextSeqNo = channel.getNextPublishSeqNo();
                            channel.basicPublish("", QUEUE_NAME, prop, message.getBytes());
                            map.get(channel).add(nextSeqNo);
                        }
                        while (!map.get(channel).isEmpty()) {
                            // System.out.println("unconfirmedSet size: " + unconfirmedSet.size());
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
        System.out.println("Confirm Type: async confirm.");
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
