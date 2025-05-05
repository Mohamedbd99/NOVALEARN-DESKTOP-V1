package org.novalearn.service.coursEexercice;

import org.novalearn.Entity.Commentaire;

import java.sql.SQLException;
import java.util.List;

public interface IService<T> {

    void create(T t) throws SQLException;

    boolean update(T t) throws SQLException;


    void delete(T t) throws SQLException;

    List<T> readAll() throws SQLException;

    List<Commentaire> getCommentsByBlogIdPaginated(int blogId, int offset, int limit);
}
