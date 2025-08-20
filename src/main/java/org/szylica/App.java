package org.szylica;

import org.jdbi.v3.core.Jdbi;
import org.szylica.model.Player;
import org.szylica.model.Team;
import org.szylica.repository.PlayerRepository;
import org.szylica.repository.TeamRepository;
import org.szylica.repository.impl.PlayerRepositoryImpl;
import org.szylica.repository.impl.TeamRepositoryImpl;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        var URL = "jdbc:mysql://localhost:3307/db_1?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        var USERNAME = "user";
        var PASSWORD = "user1234";

        var jdbi = Jdbi.create(URL, USERNAME, PASSWORD);

//        var createPeopleTableSql = """
//                    create table if not exists people (
//                        id integer primary key auto_increment,
//                        name varchar(50) not null,
//                        age integer default 18
//                    );
//                """;
//
//        var createTeamsTableSql = """
//                    create table if not exists teams (
//                        id integer primary key auto_increment,
//                        name varchar(50) not null,
//                        points integer default 18
//                    );
//                """;
//
//        var createPlayersTableSql = """
//                    create table if not exists players (
//                        id integer primary key auto_increment,
//                        name varchar(50) not null,
//                        goals integer default 0,
//                        team_id integer,
//                        foreign key (team_id) references teams(id) on delete cascade on update cascade
//                    );
//                """;
//
//        jdbi.useHandle(handle -> {
//            handle.execute(createPeopleTableSql);
//            handle.execute(createTeamsTableSql);
//            handle.execute(createPlayersTableSql);
//        });

        TeamRepository teamRepository = new TeamRepositoryImpl(jdbi);
        PlayerRepository playerRepository = new PlayerRepositoryImpl(jdbi);

        var team = Team.builder()
                .name("Team 1")
                .points(10)
                .build();

        teamRepository.insert(team);

        var player1 = Player.builder()
                .name("Player 1")
                .goals(4)
                .teamId(8L)
                .build();
        var player2 = Player.builder()
                .name("Player 2")
                .goals(3)
                .build();
        playerRepository.insert(player1);

        playerRepository.update(5L, player2);

    }
}
