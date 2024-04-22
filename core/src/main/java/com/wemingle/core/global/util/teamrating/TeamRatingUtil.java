package com.wemingle.core.global.util.teamrating;

public class TeamRatingUtil {
    public double adjustTeamRating(double teamRating){
        return Math.floor(teamRating * 2) / 2;
    }
}
