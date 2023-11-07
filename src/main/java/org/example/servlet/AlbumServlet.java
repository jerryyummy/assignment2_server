package org.example.servlet;import com.google.gson.Gson;import org.example.bean.Album;import org.example.bean.Profile;import org.example.bean.Status;import org.apache.commons.dbcp.BasicDataSource;import javax.imageio.ImageIO;import javax.servlet.ServletException;import javax.servlet.annotation.MultipartConfig;import javax.servlet.http.HttpServlet;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import javax.servlet.http.Part;import java.awt.image.BufferedImage;import java.io.ByteArrayOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.PrintWriter;import java.sql.*;import java.util.ArrayList;import java.util.List;@MultipartConfig(        fileSizeThreshold = 1024 * 1024, // 设置内存缓冲区大小        maxFileSize = 1024 * 1024 * 50, // 设置最大文件大小 (5 MB)        maxRequestSize = 1024 * 1024 * 100 // 设置最大请求大小 (10 MB))public class AlbumServlet extends HttpServlet {    // RDS 实例连接信息    String endpoint = "mydatabase-2.coahmfjvijpz.us-east-1.rds.amazonaws.com"; // RDS 实例的 Endpoint    String port = "3306";    String databaseName = "albumstore";    String JDBC_USER = "admin";    String JDBC_PASSWORD = "12345678";    // JDBC URL    String JDBC_URL = "jdbc:mysql://database-1.cxbykd0hqw1f.us-west-2.rds.amazonaws.com:3306/albumstore?useSSL=false";    @Override    public void doPost(HttpServletRequest request, HttpServletResponse response)            throws IOException {        response.setContentType("application/json");        Gson gson = new Gson();        Connection conn = null;        try {            Part imagePart = request.getPart("image");            InputStream imageInputStream = imagePart.getInputStream();            byte[] imageData = new byte[(int) imagePart.getSize()];            Profile profile = new Profile(request.getParameter("artist"),                    request.getParameter("year"), request.getParameter("title"));            Album album = new Album( imageData, profile);            Status status = new Status();            // 加载MySQL驱动            Class.forName("com.mysql.cj.jdbc.Driver");            // 创建连接            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);            // 执行数据插入            String sql = "INSERT INTO album (image_data, artist, year,title) VALUES (?, ?, ?, ?)";            PreparedStatement preparedStatement = conn.prepareStatement(sql);            preparedStatement.setBlob(1, imageInputStream);            preparedStatement.setString(2, album.getProfile().getArtist());            preparedStatement.setString(3, album.getProfile().getYear());            preparedStatement.setString(4, album.getProfile().getTitle());            int rowsInserted = preparedStatement.executeUpdate();            if (rowsInserted>0) {                status.setSuccess(true);                status.setDescription("success");            } else {                status.setSuccess(false);                status.setDescription("fail");            }            response.getOutputStream().print(gson.toJson(status));            response.getOutputStream().flush();        } catch (Exception ex) {            ex.printStackTrace();            Status status = new Status();            status.setSuccess(false);            status.setDescription(ex.getMessage());            response.getOutputStream().print(gson.toJson(status));            response.getOutputStream().flush();        }finally {            try {                if (conn != null) {                    conn.close(); // 关闭数据库连接                }            } catch (SQLException e) {                e.printStackTrace();            }        }    }        @Override    public void doGet(            HttpServletRequest request,            HttpServletResponse response) throws IOException, ServletException {            try {                Class.forName("com.mysql.cj.jdbc.Driver");            } catch (ClassNotFoundException e) {                throw new RuntimeException(e);            }            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)){                System.out.println("xx");                String sql = "select * from album where album_id=604";                PreparedStatement preparedStatement = conn.prepareStatement(sql);                ResultSet resultSet = preparedStatement.executeQuery();                List<Album> albumList = new ArrayList<>();                while (resultSet.next()) {                    int album_id = resultSet.getInt("album_id");                    String artist = resultSet.getString("artist");                    String year = resultSet.getString("year");                    String title = resultSet.getString("title");                    Blob blob = resultSet.getBlob("image_data");                    byte[] image = blob.getBytes(1, (int) blob.length());                    albumList.add(new Album(image,new Profile(artist,year,title)));                }                for (Album album:albumList){                    String albumJsonString = new Gson().toJson(album);                    PrintWriter out = response.getWriter();                    response.setContentType("application/json");                    response.setCharacterEncoding("UTF-8");                    out.print(albumJsonString);                    out.flush();                }            } catch (Exception ex) {                ex.printStackTrace();                }    }    private byte[] convertImageToByteArray(BufferedImage image) throws IOException {        ByteArrayOutputStream baos = new ByteArrayOutputStream();        ImageIO.write(image, "jpeg", baos);        return baos.toByteArray();    }}