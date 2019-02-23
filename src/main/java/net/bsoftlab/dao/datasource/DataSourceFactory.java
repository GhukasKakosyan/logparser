package net.bsoftlab.dao.datasource;

import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

public class DataSourceFactory {
    public static DataSource getDataSource () {
        String urlDatabaseMysql = "jdbc:mysql://localhost:3306/access";
        String userNameDatabaseMysql = "root";
        String passwordDatabaseMysql = "gktj19760207";
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL(urlDatabaseMysql);
        mysqlDataSource.setUser(userNameDatabaseMysql);
        mysqlDataSource.setPassword(passwordDatabaseMysql);
        return mysqlDataSource;
    }
}
