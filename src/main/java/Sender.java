import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Sender extends MQConfig {

    private static Connection connection = null;
    private static Set<Channel> cset = Collections.synchronizedSet(new HashSet<Channel>());

    private static void initConnection() throws Exception {
        if (connection == null) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            connection = factory.newConnection();
        }
    }

    protected static Channel createConfirmChannel() throws Exception {
        Channel channel = null;
        synchronized (Sender.class) {
            initConnection();
            channel = connection.createChannel();
        }
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.confirmSelect();
        cset.add(channel);
        return channel;
    }

    protected static void closeChannel(Channel c) {
        try {
            cset.remove(c);
            c.close();
            if (cset.isEmpty()) {
                connection.close();
            }
        } catch (Exception ignore) {
        }
    }

    protected static String getMsg() {
        return getMsg(msglen);
    }

    private static String getMsg(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append('a');
        }
        return sb.toString();
    }

}
