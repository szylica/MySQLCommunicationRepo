package org.szylica.repository.impl;

import org.jdbi.v3.core.Jdbi;
import org.szylica.mapper.PlayerMapper;
import org.szylica.model.Player;
import org.szylica.repository.PlayerRepository;
import org.szylica.repository.generic.AbstractRepository;

import java.util.Map;

public class PlayerRepositoryImpl extends AbstractRepository<Player, Long> implements PlayerRepository {
    public PlayerRepositoryImpl(Jdbi jdbi) {
        super(jdbi);
    }

    @Override
    protected void registerMappers() {
        jdbi.registerRowMapper(Player.class, new PlayerMapper());
    }

    @Override
    protected Map<String, String> getFieldColumnMap(){
        return Map.of("teamId", "team_id");
    }
}
