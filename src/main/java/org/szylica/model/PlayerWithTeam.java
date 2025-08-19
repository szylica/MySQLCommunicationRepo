package org.szylica.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerWithTeam {
    private Long playerId;
    private String playerName;
    private Integer playerGoals;
    private String teamName;
    private Integer teamPoints;

}

