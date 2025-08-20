package org.szylica.repository.impl;

import org.jdbi.v3.core.Jdbi;
import org.szylica.mapper.TeamMapper;
import org.szylica.model.Team;
import org.szylica.repository.generic.AbstractRepository;
import org.szylica.repository.TeamRepository;

import java.util.List;

public class TeamRepositoryImpl extends AbstractRepository<Team, Long> implements TeamRepository {
    public TeamRepositoryImpl(Jdbi jdbi) {
        super(jdbi);
    }


    @Override
    protected void registerMappers() {
        jdbi.registerRowMapper(Team.class, new TeamMapper());
    }

    @Override
    public List<Team> findAllTeamsWithPointsBetween(int fromPoints, int toPoints) {
        var sql = "select * from teams where points between :pointsFrom and :pointsTo";
        return jdbi.withHandle(handle -> handle
                .createQuery(sql)
                .bind("pointsFrom", fromPoints)
                .bind("pointsTo", toPoints)
                .mapTo(Team.class)
                .list()
        );
    }
}
