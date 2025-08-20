package org.szylica.repository;

import org.szylica.model.Team;
import org.szylica.repository.generic.GenericRepository;

import java.util.List;

public interface TeamRepository extends GenericRepository<Team, Long> {

    List<Team> findAllTeamsWithPointsBetween(int fromPoints, int toPoints);

}
