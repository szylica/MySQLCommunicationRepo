package org.szylica.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.szylica.model.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMapper implements RowMapper<Player> {

    @Override
    public Player map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Player.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .goals(rs.getInt("goals"))
                .teamId(rs.getLong("team_id"))
                .build();
    }
}
