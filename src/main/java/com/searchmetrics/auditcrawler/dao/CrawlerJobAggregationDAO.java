package com.searchmetrics.auditcrawler.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arobinson on 3/23/17.
 */
public class CrawlerJobAggregationDAO {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(CrawlerJobAggregationDAO.class);
    private static final String JOBS_BY_DATE =
            "SELECT DATE(createDate) as date, COUNT(*) as count, type, max_pages, prio " +
                    "FROM jobs " +
                    "WHERE status='proc' " +
                    "GROUP BY date, type, max_pages, prio ASC " +
                    "ORDER BY date, type, max_pages, prio ASC";

    private final Connection connection;
    private Map<String, PreparedStatement> statementMap = new HashMap<>();

    public CrawlerJobAggregationDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ByDateJobRecord> getProcessingJobsByDate() throws SQLException {
        List<ByDateJobRecord> byDateJobRecordList = null;
        PreparedStatement statement = null;
        try {
            statement = statementMap.get("jobs_by_date");
            if (null == statement || statement.isClosed()) {
                statement = connection.prepareStatement(JOBS_BY_DATE);
                statementMap.put("jobs_by_date", statement);
            }

            statement.execute();
            ResultSet resultSet = statement.getResultSet();

            if (resultSet != null)
                byDateJobRecordList = ByDateJobRecord.getListFromResult( resultSet );
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error calling getProcessingJobsByDate(): {}", e.getMessage());
            throw new WebApplicationException(e);
        }
        finally {
            if (statement != null)
                statement.close();
        }

        return byDateJobRecordList;
    }

    public static class ByDateJobRecord {
        private final String date;
        private final Integer count;
        private final String type;
        private final Integer maxPages;
        private final String prio;

        @JsonCreator
        public ByDateJobRecord(
                @JsonProperty final String date,
                @JsonProperty final Integer count,
                @JsonProperty final String type,
                @JsonProperty("max_pages") final Integer maxPages,
                @JsonProperty final String prio) {

            this.date = date;
            this.count = count;
            this.type = type;
            this.maxPages = maxPages;
            this.prio = prio;
        }

        private static List<ByDateJobRecord> getListFromResult(ResultSet resultSet) throws SQLException {
            List<ByDateJobRecord> records = new ArrayList<>();

            while (resultSet.next()) {
                records.add(
                        new ByDateJobRecord(
                                resultSet.getString("date"),
                                resultSet.getInt("count"),
                                resultSet.getString("type"),
                                resultSet.getInt("max_pages"),
                                resultSet.getString("prio")
                        )
                );
            }

            return records;
        }


        public String getDate() {
            return date;
        }

        public Integer getCount() {
            return count;
        }

        public String getType() {
            return type;
        }

        @JsonProperty("max_pages")
        public Integer getMaxPages() {
            return maxPages;
        }

        public String getPrio() {
            return prio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ByDateJobRecord that = (ByDateJobRecord) o;

            if (!date.equals(that.date)) return false;
            if (!count.equals(that.count)) return false;
            if (!type.equals(that.type)) return false;
            if (!maxPages.equals(that.maxPages)) return false;
            return prio.equals(that.prio);
        }

        @Override
        public int hashCode() {
            int result = date.hashCode();
            result = 31 * result + count.hashCode();
            result = 31 * result + type.hashCode();
            result = 31 * result + maxPages.hashCode();
            result = 31 * result + prio.hashCode();
            return result;
        }
    }
}
