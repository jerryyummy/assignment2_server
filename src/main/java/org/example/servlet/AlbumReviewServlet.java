package org.example.servlet;

import com.google.gson.Gson;
import com.rabbitmq.client.ConnectionFactory;
import org.example.bean.Album;
import org.example.bean.Profile;
import org.example.bean.Status;
import org.example.queue.Consumer;
import org.example.queue.Producer;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 设置内存缓冲区大小
        maxFileSize = 1024 * 1024 * 50, // 设置最大文件大小 (5 MB)
        maxRequestSize = 1024 * 1024 * 100 // 设置最大请求大小 (10 MB)
)
public class AlbumReviewServlet extends HttpServlet{
    Producer producer ;
    String JDBC_USER = "admin";
    String JDBC_PASSWORD = "12345678";
    // JDBC URL
    String JDBC_URL = "jdbc:mysql://database-1.cxbykd0hqw1f.us-west-2.rds.amazonaws.com:3306/albumstore?useSSL=false";
    private final static String QUEUE_NAME = "hello";

    Connection connection;

    private final static String HOST = "54.184.133.208"; // 或者RabbitMQ服务器的地址
    @Override
    public void init() {
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            // 这里可以设置更多的连接属性，比如端口、用户名、密码等
            factory.setPort(5672);
            factory.setUsername("admin");
            factory.setPassword("12345678");
            connection = factory.newConnection();
            producer = new Producer(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        Profile profile = new Profile(request.getParameter("artist"),
                request.getParameter("year"), request.getParameter("title"));
        String message = profile.getTitle();

        try {//call a Producer
            producer.produce(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void destroy(){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

