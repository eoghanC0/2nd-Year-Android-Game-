package uk.ac.qub.eeecs.game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.screens.LoadGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;

/**
 * Created by stephenmcveigh on 15/01/2018.
 */

public class FootballGame extends Game {
    private int gameID, wins, losses, draws, xp, difficulty, gameLength;
    private ArrayList<Card> club;
    private String pitchBackGround, playerName;

    private final int SAVE_SLOT_MAX = 3;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Create a new demo game
     */
    public FootballGame() {
        super();
        gameID = 0;
        playerName = "Name";
        club = new ArrayList<>();
        wins = 0;
        losses = 0;
        draws = 0;
        xp = 0;
        difficulty = 0;
        gameLength = 0;
        pitchBackGround = "";
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.ac.qub.eeecs.gage.Game#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Go with a default 30 UPS/FPS
        setTargetFramesPerSecond(30);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Call the Game's onCreateView to get the view to be returned.
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Create and add a stub game screen to the screen manager. We don't
        // want to do this within the onCreate method as the menu screen
        // will layout the buttons based on the size of the view.
        //TODO: Chnage to loadscreen
        LoadGameScreen stubLoadGameScreen = new LoadGameScreen(this);
        mScreenManager.addScreen(stubLoadGameScreen);

        return view;
    }

    @Override
    public boolean onBackPressed() {
        // If we are already at the menu screen then exit
        if (mScreenManager.getCurrentScreen().getName().equals("MenuScreen"))
            return false;

        // Go back to the menu screen
        getScreenManager().removeScreen(mScreenManager.getCurrentScreen().getName());
        MenuScreen menuScreen = new MenuScreen(this);
        getScreenManager().addScreen(menuScreen);
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////
    public String getMatchStats(){return String.format("%d | %d | %d", getWins(), getDraws(), getLosses());}
    public int getGameID() {return gameID;}
    public String getPlayerName() {return playerName;}
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
    public void setPlayerName(String name){this.playerName = name;}
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
    public void saveGame(int saveSlot) {
        try {
            JSONObject gameSavesObj = new JSONObject();
            gameSavesObj.put("gameID", gameID);
            gameSavesObj.put("playerName", playerName);
            JSONArray clubArray = new JSONArray();
            clubArray.put(club);
            gameSavesObj.put("wins", wins);
            gameSavesObj.put("losses", losses);
            gameSavesObj.put("draws", draws);
            gameSavesObj.put("xp", xp);
            gameSavesObj.put("difficulty", difficulty);
            gameSavesObj.put("gameLength", gameLength);
            gameSavesObj.put("pitchBackGround", pitchBackGround);
            mAssetManager.writeFile("save_" + saveSlot + ".json", gameSavesObj.toString());
        }
        catch(JSONException e){
            Log.d("JSON", "Save fail : " + e.getMessage());
        }
    }

    public void loadGame(int saveSlot) {
        try {
            JSONObject gameSavesObj = new JSONObject(mAssetManager.readFile("save_" + saveSlot + ".json"));
            gameID = (int) gameSavesObj.get("gameID");
            playerName = gameSavesObj.get("playerName").toString();
            wins = (int) gameSavesObj.get("wins");
            losses = (int) gameSavesObj.get("losses");
            draws = (int) gameSavesObj.get("draws");
            xp = (int) gameSavesObj.get("xp");
            difficulty = (int) gameSavesObj.get("difficulty");
            gameLength = (int) gameSavesObj.get("gameLength");
            pitchBackGround = gameSavesObj.get("pitchBackGround").toString();
        } catch (JSONException e) {
            Log.d("JSON", "Load Failed : " + e.getMessage());
        }
    }
}
