package net.bsoftlab.dao;

import net.bsoftlab.dao.exception.DataAccessException;
import net.bsoftlab.model.Entry;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryDaoImpl implements EntryDao {

    private DataSource dataSource;

    public EntryDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void closeConnection (Connection connection) {
        try {
            if(connection != null) connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    private void closePreparedStatement(PreparedStatement preparedStatement) {
        try {
            if(preparedStatement != null) preparedStatement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    private void closeResultSet(ResultSet resultSet) {
        try {
            if(resultSet != null) resultSet.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
    private void rollbackConnection(Connection connection, Savepoint savepoint)
            throws DataAccessException {
        try {
            if(connection != null) connection.rollback(savepoint);
        } catch (SQLException rollbackSqlException) {
            throw new DataAccessException(rollbackSqlException.getMessage());
        }
    }

    @Override
    public void insertEntries(List<Entry> entryList) throws DataAccessException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Savepoint savepoint = null;

        try {
            String entryInsertStatementSql =
                    "INSERT INTO access.entries " +
                            "(datetime, ip, request, status, client) " +
                            "VALUES (?, ?, ?, ?, ?)";

            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(entryInsertStatementSql);

            for (int index = 0; index < entryList.size(); index++) {
                Entry entry = entryList.get(index);
                if (entry.getId() != null) entry.setId(null);
                Timestamp timestamp = new Timestamp(entry.getDateTime().getTime());
                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setString(2, entry.getIp());
                preparedStatement.setString(3, entry.getRequest());
                preparedStatement.setInt(4, entry.getStatus());
                preparedStatement.setString(5, entry.getClient());
                preparedStatement.addBatch();
                if (index % 1000 == 0 || index == entryList.size() - 1) {
                    preparedStatement.executeBatch();
                }
            }
            connection.commit();

        } catch (SQLException sqlException) {
            this.rollbackConnection(connection, savepoint);
        } finally {
            this.closePreparedStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public void insertBlockedIPs(
            List<String> ipAddressList, Date startDate,
            Date endDate, Integer threshold) throws DataAccessException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Savepoint savepoint = null;

        try {
            String blockedIpInsertStatementSql =
                    "INSERT INTO access.blockedips " +
                            "(ip, startDateTime, endDateTime, threshold, comment) " +
                            "VALUES (?, ?, ?, ?, ?)";

            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(blockedIpInsertStatementSql);

            for (String ipAddress : ipAddressList) {
                preparedStatement.setString(1, ipAddress);
                preparedStatement.setTimestamp(2, new Timestamp(startDate.getTime()));
                preparedStatement.setTimestamp(3, new Timestamp(endDate.getTime()));
                preparedStatement.setInt(4, threshold);
                preparedStatement.setString(5, "IP address blocked because in period: " +
                        startDate + " - " + endDate + " host address generated more than: " +
                        threshold + " requests");
                preparedStatement.executeUpdate();
            }
            connection.commit();

        } catch (SQLException sqlException) {
            this.rollbackConnection(connection, savepoint);
        } finally {
            this.closePreparedStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public List<String> getBlockedIPs(
            Date startDate, Date endDate, Integer threshold) throws DataAccessException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Savepoint savepoint = null;

        List<String> ipAddressList = null;

        try {
            String blockedIpsSelectStatementSql =
                    "SELECT entries.ip " +
                            "FROM entries " +
                            "WHERE entries.datetime >= ? " +
                            "AND entries.datetime < ? " +
                            "GROUP BY entries.ip " +
                            "HAVING COUNT(entries.id) >= ? " +
                            "ORDER BY entries.ip ";

            connection = this.dataSource.getConnection();
            connection.setAutoCommit(false);
            savepoint = connection.setSavepoint();
            preparedStatement = connection.prepareStatement(blockedIpsSelectStatementSql);
            preparedStatement.setTimestamp(1, new Timestamp(startDate.getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(endDate.getTime()));
            preparedStatement.setInt(3, threshold);
            resultSet = preparedStatement.executeQuery();

            if (resultSet != null) {
                ipAddressList = new ArrayList<>();
                while (resultSet.next()) {
                    ipAddressList.add(resultSet.getString("ip"));
                }
                if (ipAddressList.isEmpty()) ipAddressList = null;
            }

        } catch (SQLException sqlException) {
            this.rollbackConnection(connection, savepoint);
        } finally {
            this.closeResultSet(resultSet);
            this.closePreparedStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return ipAddressList;
    }
}
