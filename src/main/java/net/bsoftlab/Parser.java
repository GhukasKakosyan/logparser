package net.bsoftlab;

import net.bsoftlab.dao.EntryDao;
import net.bsoftlab.dao.EntryDaoImpl;
import net.bsoftlab.dao.datasource.DataSourceFactory;
import net.bsoftlab.dao.exception.DataAccessException;
import net.bsoftlab.model.Entry;

import javax.sql.DataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Parser {
    public static void main(String[] arguments) {
        if (arguments == null || arguments.length != 4) {
            System.out.println("You have entered incorrect quantity of arguments.");
            System.out.println("You must enter four arguments in following pattern:");
            System.out.println("accesslog=filePath  startDate=yyyy-MM-dd.HH:mm:ss  duration=hourly|dayly  threshold=integer");
            return;
        }

        if (!arguments[0].contains("=")) {
            System.out.println("You have entered incorrect first argument.");
            System.out.println("First argument must be in pattern: accesslog=filePath");
            return;
        }
        String[] accesslogArgument = arguments[0].split("[=]");
        if (accesslogArgument.length != 2 ||
                accesslogArgument[0] == null || accesslogArgument[0].isEmpty() ||
                accesslogArgument[1] == null || accesslogArgument[1].isEmpty()) {
            System.out.println("You have entered incorrect first argument.");
            System.out.println("First argument must be in pattern: accesslog=filePath");
            return;
        }
        if (!accesslogArgument[0].equals("accesslog")) {
            System.out.println("You have entered incorrect name of first argument.");
            System.out.println("First argument name must be accesslog");
            return;
        }
        File file = new File(accesslogArgument[1]);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("You have entered incorrect file.");
            System.out.println("File does not exist or this is directory.");
            return;
        }

        if (!arguments[1].contains("=")) {
            System.out.println("You have entered incorrect second argument.");
            System.out.println("Second argument must be in pattern: startDate=yyyy-MM-dd.HH:mm:ss");
            return;
        }
        String[] startDateArgument = arguments[1].split("[=]");
        if (startDateArgument.length != 2 ||
                startDateArgument[0] == null || startDateArgument[0].isEmpty() ||
                startDateArgument[1] == null || startDateArgument[1].isEmpty()) {
            System.out.println("You have entered incorrect second argument.");
            System.out.println("Second argument must be in pattern: startDate=yyyy-MM-dd.HH:mm:ss");
            return;
        }
        if (!startDateArgument[0].equals("startDate")) {
            System.out.println("You have entered incorrect name of second argument.");
            System.out.println("Second argument name must be startDate");
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
        Date startDate;
        try {
            startDate = simpleDateFormat.parse(startDateArgument[1]);
        } catch (ParseException parseException) {
            System.out.println("You have entered incorrect value of startDate argument.");
            return;
        }

        if (!arguments[2].contains("=")) {
            System.out.println("You have entered incorrect third argument.");
            System.out.println("Third argument must be in pattern: duration=hourly or duration=dayly");
            return;
        }
        String[] durationArgument = arguments[2].split("[=]");
        if (durationArgument.length != 2 ||
                durationArgument[0] == null || durationArgument[0].isEmpty() ||
                durationArgument[1] == null || durationArgument[1].isEmpty()) {
            System.out.println("You have entered incorrect third argument.");
            System.out.println("Third argument must be in pattern: duration=hourly|dayly");
            return;
        }
        if (!durationArgument[0].equals("duration")) {
            System.out.println("You have entered incorrect name of third argument.");
            System.out.println("Third argument name must be duration");
            return;
        }
        Date endDate;
        switch (durationArgument[1]) {
            case "hourly":
                long oneHour = 3600 * 1000;
                endDate = new Date(startDate.getTime() + oneHour);
                break;
            case "dayly":
                long oneDay = 24 * 3600 * 1000;
                endDate = new Date(startDate.getTime() + oneDay);
                break;
            default:
                System.out.println("You have entered incorrect value of duration argument.");
                System.out.println("Third argument duration have to contain 'hourly' or 'dayly' values.");
                return;
        }

        if (!arguments[3].contains("=")) {
            System.out.println("You have entered incorrect forth argument.");
            System.out.println("Forth argument must be in pattern: threshold=integer");
            return;
        }
        String[] thresholdArgument = arguments[3].split("[=]");
        if (thresholdArgument.length != 2 ||
                thresholdArgument[0] == null || thresholdArgument[0].isEmpty() ||
                thresholdArgument[1] == null || thresholdArgument[1].isEmpty()) {
            System.out.println("You have entered incorrect forth argument.");
            System.out.println("Forth argument must be in pattern: threshold=integer");
            return;
        }
        if (!thresholdArgument[0].equals("threshold")) {
            System.out.println("You have entered incorrect name of forth argument.");
            System.out.println("Forth argument name must be threshold");
            return;
        }
        Integer threshold;
        try {
            threshold = Integer.valueOf(thresholdArgument[1]);
        } catch (NumberFormatException numberFormatException) {
            System.out.println("You have entered incorrect value of threshold argument.");
            System.out.println("You must enter integer value for threshold argument");
            return;
        }

        BufferedReader bufferedReader;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch (IOException ioException) {
            System.out.println("Unable to read file: ");
            return;
        }

        String line;
        int indexLine = 1;
        List<Entry> entryList = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty() && line.contains("|")) {
                    String[] record = line.split("[|]");
                    if (record.length != 5) {
                        System.out.println("Every line have to contain 5 cells delimited by |");
                        System.out.println("Line number " + indexLine + " contains " + record.length + " cells.");
                        for (String cell : record) {
                            System.out.print(cell);
                        }
                        return;
                    }
                    Entry entry = new Entry();
                    entry.setId(null);
                    entry.setDateTime(simpleDateFormat.parse(record[0]));
                    entry.setIp(record[1]);
                    entry.setRequest(record[2]);
                    entry.setStatus(Integer.valueOf(record[3]));
                    entry.setClient(record[4]);
                    entryList.add(entry);
                    indexLine ++;
                }
            }
        } catch (IOException ioException) {
            System.out.println("Unable to read line number: " + indexLine);
            return;
        } catch (NumberFormatException numberFormatException) {
            System.out.println("Unable to read status in line: " + indexLine);
            return;
        } catch (ParseException parseException) {
            System.out.println("Date value is not correct. Error in line: " + indexLine);
            return;
        }

        try {
            DataSource dataSource = DataSourceFactory.getDataSource();
            EntryDao entryDao = new EntryDaoImpl(dataSource);
            entryDao.insertEntries(entryList);
            List<String> blockedIPList = entryDao.getBlockedIPs(startDate, endDate, threshold);
            if (blockedIPList != null && !blockedIPList.isEmpty()) {
                entryDao.insertBlockedIPs(blockedIPList, startDate, endDate, threshold);
                blockedIPList.forEach(System.out::println);
            } else {
                System.out.println("There is no one blocked IP address.");
            }
        } catch (DataAccessException dataAccessException) {
            System.out.println(dataAccessException.getMessage());
        }
    }
}