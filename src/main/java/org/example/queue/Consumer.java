package org.example.queue;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Consumer{
    private final static String QUEUE_NAME = "hello";
    String JDBC_USER = "admin";
    String JDBC_PASSWORD = "12345678";
    // JDBC URL
    String JDBC_URL = "jdbc:mysql://database-1.cxbykd0hqw1f.us-west-2.rds.amazonaws.com:3306/albumstore?useSSL=false";

    public void consume() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("54.184.133.208");
        // 这里可以设置更多的连接属性，比如端口、用户名、密码等
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("12345678");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            java.sql.Connection conn = null;
            // 加载MySQL驱动
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // 创建连接
                conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                // 执行数据插入
                for (int i = 0; i < 3; i++) {
                    String sql = "INSERT INTO rate (person, islike,title) VALUES (?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, "user-"+i);
                    preparedStatement.setInt(2, i%2);
                    preparedStatement.setString(3, message);
                    preparedStatement.executeUpdate();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                try {
                    if (conn != null) {
                        conn.close(); // 关闭数据库连接
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}
