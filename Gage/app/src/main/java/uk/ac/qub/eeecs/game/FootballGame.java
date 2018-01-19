package uk.ac.qub.eeecs.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;

/**
 * Created by stephenmcveigh on 15/01/2018.
 */

public class FootballGame extends DemoGame {
    private int gameID;
    private ArrayList<Card> club;
    private int wins, losses, draws;
    private int xp;
    private int difficulty;
    private int gameLength;
    private String pitchBackGround;
    ///////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Create a new demo game
     */
    public FootballGame() {
        super();
        gameID = 0;
        club = new ArrayList<>();
        wins = 0;
        losses = 0;
        draws = 0;
        xp = 0;
        difficulty = 0;
        gameLength = 0;
        pitchBackGround = "";
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////
    public int getGameID() {return gameID;}
    public ArrayList<Card> getClub() {return club;}
    public int getWins() {return wins;}
    public int getLosses() {return losses;}
    public int getDraws() {return draws;}
    public int getXp() {return xp;}
    public int getDifficulty() {return difficulty;}
    public int getGameLength() {return gameLength;}
    public String getPitchBackGround() {return pitchBackGround;}

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////
    public void setClub(ArrayList<Card> club) {this.club = club;}
    public void setWins(int numOfWins) {this.wins = numOfWins;}
    public void setLosses(int numOfLosses) {this.losses = numOfLosses;}
    public void setDraws(int numOfDraws) {this.draws = numOfDraws;}
    public void setXp(int xp) {this.xp = xp;}
    public void setDifficulty(int difficulty) {this.difficulty = difficulty;}
    public void setGameLength(int gameLength) {this.gameLength = gameLength;}
    public void setPitchBackGround(String pitchBackGround) {this.pitchBackGround = pitchBackGround;}

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////
    public void saveGame() throws JSONException {
        JSONObject gameSavesObj = new JSONObject();
        gameSavesObj.put("gameID", gameID);
        JSONArray clubArray = new JSONArray();
        clubArray.put(club);
        gameSavesObj.put("club", clubArray);


    }

    public void loadGame(int gameID) {
        this.gameID = gameID;
    }
}
