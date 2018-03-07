package uk.ac.qub.eeecs.game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.screens.LoadGameScreen;
import uk.ac.qub.eeecs.game.cardDemo.screens.MenuScreen;

/**
 * Created by stephenmcveigh on 15/01/2018.
 */

public class FootballGame extends Game {
    private int gameID, wins, losses, draws, xp;
    private ArrayList<Card> club, squad;
    private int pitchBackground, difficulty, gameLength;

    ///////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Create a new demo game
     */
    public FootballGame() {
        super();
        gameID = -1;
        wins = -1;
        losses = -1;
        draws = -1;
        xp = -1;
        difficulty = -1;
        gameLength = -1;
        pitchBackground = -1;
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
        LoadGameScreen stubLoadGameScreen = new LoadGameScreen(this);
        mScreenManager.addScreen(stubLoadGameScreen);

        return view;
    }

    @Override
    public boolean onBackPressed() {

        // If we are already at the menu screen then exit
        if (mScreenManager.getCurrentScreen().getName().equals("StarterPackScreen"))
            return true;

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
    public String getMatchStats(){return String.format("W: %d | D: %d | L: %d", getWins(), getDraws(), getLosses());}
    public int getGameID() {return gameID;}
    public ArrayList<Card> getClub() {return club;}
    public ArrayList<Card> getSquad() {return squad;}
    public int getWins() {return wins;}
    public int getLosses() {return losses;}
    public int getDraws() {return draws;}
    public int getXp() {return xp;}
    public int getDifficulty() {return difficulty;}
    public int getGameLength() {return gameLength;}
    public int getPitchBackground() {return pitchBackground;}

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////
    public void setWins(int numOfWins) {this.wins = numOfWins;}
    public void setLosses(int numOfLosses) {this.losses = numOfLosses;}
    public void setDraws(int numOfDraws) {this.draws = numOfDraws;}
    public void setXp(int xp) {this.xp = xp;}
    public void setDifficulty(int difficulty) {this.difficulty = difficulty;}
    public void setGameLength(int gameLength) {this.gameLength = gameLength;}
    public void setPitchBackground(int pitchBackGround) {this.pitchBackground = pitchBackGround;}

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////
    private JSONArray getCardCollectionAsJson(ArrayList<Card> collection) throws JSONException{
        JSONArray array = new JSONArray();
        for (Card card : collection) {
            array.put(card.getSaveDetails());
        }
        return array;
    }

    private ArrayList<Card> getCardCollectionAsArrayList(JSONArray collection) {
        ArrayList<Card> array = new ArrayList<>();
        try {
            for (int i = 0; i < collection.length(); i++) {
                Card card = new Card(getScreenManager().getCurrentScreen(), collection.getJSONObject(i).getString("playerID"), collection.getJSONObject(i).getInt("fitness"));
                array.add(card);
            }
        } catch (JSONException e) {
            Log.d("JSON", "Load fail : " + e.getMessage());
        }
        return array;
    }

    public void saveGame() {
        try {
            JSONObject gameSavesObj = new JSONObject();
            gameSavesObj.put("gameID", gameID);
            gameSavesObj.put("club", getCardCollectionAsJson(club));
            gameSavesObj.put("squad", getCardCollectionAsJson(squad));
            gameSavesObj.put("wins", wins);
            gameSavesObj.put("losses", losses);
            gameSavesObj.put("draws", draws);
            gameSavesObj.put("xp", xp);
            gameSavesObj.put("difficulty", difficulty);
            gameSavesObj.put("gameLength", gameLength);
            gameSavesObj.put("pitchBackground", pitchBackground);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            mAssetManager.deleteSave(gameID);
            mAssetManager.writeFile("ID_" + gameID + "_" + sdf.format(GregorianCalendar.getInstance().getTime()) + ".json", gameSavesObj.toString());
            Log.i("WRITE", "file written");
        } catch(JSONException e){
            Log.d("JSON", "Save fail : " + e.getMessage());
        }
    }

    public void loadGame(int saveSlot) {
        try {
            JSONObject gameSavesObj = new JSONObject(mAssetManager.readSave(saveSlot));
            gameID = gameSavesObj.getInt("gameID");
            club = getCardCollectionAsArrayList(gameSavesObj.getJSONArray("club"));
            squad = getCardCollectionAsArrayList(gameSavesObj.getJSONArray("squad"));
            wins = gameSavesObj.getInt("wins");
            losses = gameSavesObj.getInt("losses");
            draws = gameSavesObj.getInt("draws");
            xp = gameSavesObj.getInt("xp");
            difficulty = gameSavesObj.getInt("difficulty");
            gameLength = gameSavesObj.getInt("gameLength");
            pitchBackground = gameSavesObj.getInt("pitchBackground");
        } catch (JSONException e) {
            Log.d("JSON", "Load Failed : " + e.getMessage());
        }
    }

    public void initialiseNewGame(int saveSlot) {
        gameID = saveSlot;
        club = new ArrayList<>();
        squad = new ArrayList<>();
        wins = 0;
        losses = 0;
        draws = 0;
        xp = 0;
        difficulty = 1;
        gameLength = 300;
        pitchBackground = 0;
    }
}
