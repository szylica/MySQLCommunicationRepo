package org.szylica.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.szylica.model.Team;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamMapper implements RowMapper<Team> {

    @Override
    public Team map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Team.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .points(rs.getInt("points"))
                .build();
    }
}
