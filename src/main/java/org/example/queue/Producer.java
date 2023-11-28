package org.example.queue;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class Producer {
    private final static String QUEUE_NAME = "hello";
    private final Connection connection;
    public Producer(Connection connection){
        this.connection = connection;
    }

    public void produce(String message) throws Exception {

        // 使用连接...
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        channel.close();

    }
}
