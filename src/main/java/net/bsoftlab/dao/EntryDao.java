package net.bsoftlab.dao;

import net.bsoftlab.dao.exception.DataAccessException;
import net.bsoftlab.model.Entry;

import java.util.Date;
import java.util.List;

public interface EntryDao {
    void insertEntries(List<Entry> entryList) throws DataAccessException;
    void insertBlockedIPs(
            List<String> IPAddressList, Date startDate,
            Date endDate, Integer threshold) throws DataAccessException;
    List<String> getBlockedIPs(
            Date startDate, Date endDate, Integer threshold) throws DataAccessException;
}
