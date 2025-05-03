package org.novalearn.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.novalearn.Entity.*;
import org.novalearn.services.BlogService;

import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class BlogFront  implements Initializable {

    @FXML
    private VBox postBackground;

    @FXML
    private Pagination pagination;

    private final BlogService blogService = new BlogService();
    private final int POSTS_PER_PAGE = 3;
    private int totalBlogs = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            totalBlogs = blogService.readAll().size();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int pageCount = (int) Math.ceil((double) totalBlogs / POSTS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        VBox box = new VBox(10);
        try {
            int offset = pageIndex * POSTS_PER_PAGE;
            List<Blog> blogs = blogService.getPaginatedBlogs(offset, POSTS_PER_PAGE);

            for (Blog blog : blogs) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/novalearn/blog-views/blog-item.fxml"));
                VBox postNode = loader.load();
                BlogItem controller = loader.getController();
                controller.setBlogData(blog);
                box.getChildren().add(postNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return box;
    }
}