import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.MessageProperties;

public class MQConfig {

    protected final static String host = "127.0.0.1"; // broker host

    protected final static int port = 5672; // broker port

    protected final static String username = "guest"; // broker username

    protected final static String password = "guest"; // broker password

    protected final static String QUEUE_NAME = "test"; // test queue name

    protected final static int msglen = 1000; // message length during perf test

    protected final static int consumerCount = 100; // consumer count

    protected final static int producerCount = 100; // producer count

    protected final static boolean autoAck = false; // consumer auto ack mode

    protected final static BasicProperties prop = MessageProperties.PERSISTENT_TEXT_PLAIN; // message persistent mode
    // protected final static BasicProperties prop = MessageProperties.TEXT_PLAIN;

    protected final static int msgcount = 500000; // message count during perf test

    protected final static int msgcountPerBatch = 100; // message count per batch in batch confirm mode.
}
