package com.skt.crbt.coloring.rank;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class RankQueryService {

    private final SqlSessionFactory sqlSessionFactory;
    private final JdbcTemplate jdbcTemplate;

    public RankQueryService(SqlSessionFactory sqlSessionFactory, JdbcTemplate jdbcTemplate) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<List<Object>> query(String statementId, Map<String, Object> param) {
        MappedStatement statement = sqlSessionFactory.getConfiguration().getMappedStatement(statementId);
        BoundSql boundSql = statement.getBoundSql(param);
        List<Object> args = new ArrayList<Object>();
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            args.add(param.get(mapping.getProperty()));
        }
        return jdbcTemplate.query(boundSql.getSql(), args.toArray(), new RowMapper<List<Object>>() {
            @Override
            public List<Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResultSetMetaData meta = rs.getMetaData();
                List<Object> row = new ArrayList<Object>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                return row;
            }
        });
    }
}
