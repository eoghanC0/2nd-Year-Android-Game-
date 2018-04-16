package uk.ac.qub.eeecs.game.objects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.game.screens.LoadGameScreen;
import uk.ac.qub.eeecs.game.screens.MenuScreen;

/**
 * Created by stephenmcveigh on 15/01/2018.
 */

public class FootballGame extends Game {
    public final int MAX_SAVE_SLOTS = 3;
    private int gameID, wins, losses, draws, xp;
    private ArrayList<Card> club, squad;
    private String formation;
    private int difficulty, gameLength;

    private Music bgMusic;

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
        formation = "";
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event Handlers
    ///////////////////////////////////////////////////////////////////////////

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

        playBackgroundMusic();
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
    public ArrayList<Card> getClub() {return club;}
    public ArrayList<Card> getSquad() {return squad;}
    public int getWins() {return wins;}
    public int getLosses() {return losses;}
    public int getDraws() {return draws;}
    public int getXp() {return xp;}
    public int getDifficulty() {return difficulty;}
    public int getGameLength() {return gameLength;}
    public String getFormation() {return formation;}
    public Music getBGMusic() {return bgMusic;}

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////
    public void setWins(int numOfWins) {this.wins = numOfWins;}
    public void setLosses(int numOfLosses) {this.losses = numOfLosses;}
    public void setDraws(int numOfDraws) {this.draws = numOfDraws;}
    public void setXp(int xp) {this.xp = xp;}
    public void setDifficulty(int difficulty) {this.difficulty = difficulty;}
    public void setGameLength(int gameLength) {this.gameLength = gameLength;}
    public void setFormation(String formation) {this.formation = formation;}

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Adds XP to the current value
     *
     * @param xp
     */
    public void addXP(int xp) {
        this.xp += xp;
    }

    /**
     * Gets an array of card objects as an array of "JSONified" card objects
     *
     * @param collection
     * @return
     * @throws JSONException
     *
     * Author - Stephen McVeigh
     */
    private JSONArray getCardCollectionAsJson(ArrayList<Card> collection) throws JSONException{
        JSONArray array = new JSONArray();
        for (Card card : collection) {
            array.put(card.getSaveDetails());
        }
        return array;
    }

    /**
     * Gets a an array of "JSONified card objects as an array of Card objects
     *
     * @param collection
     * @return
     * @throws JSONException
     *
     * Author - Stephen McVeigh
     */
    private ArrayList<Card> getCardCollectionAsArrayList(JSONArray collection) throws JSONException{
        ArrayList<Card> array = new ArrayList<>();
        for (int i = 0; i < collection.length(); i++) {
            Card card = new Card(getScreenManager().getCurrentScreen(), collection.getJSONObject(i).getString("playerID"), collection.getJSONObject(i).getInt("fitness"));
            array.add(card);
        }
        return array;
    }

    /**
     * Saves the current state of the game to a JSON file with the current time stamp in the GameIDth save slot
     * Author - Inaki McKearney
     */
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            mAssetManager.deleteSave(gameID);
            mAssetManager.writeFile("ID_" + gameID + "_" + sdf.format(GregorianCalendar.getInstance().getTime()) + ".json", gameSavesObj.toString());
        } catch(JSONException e){
            Log.d("JSON", "Save fail : " + e.getMessage());
        }
    }

    /**
     * Loads the "JSONified" game properties from the given save slot
     *
     * @param saveSlot
     *
     * Author - Stephen McVeigh and Inaki McKearney
     */
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
        } catch (JSONException e) {
            Log.d("JSON", "Load Failed : " + e.getMessage());
        }
    }

    /**
     * Sets the initial properties of a new game
     *
     * @param saveSlot
     */
    public void initialiseNewGame(int saveSlot) {
        gameID = saveSlot;
        club = new ArrayList<>();
        squad = new ArrayList<>();
        wins = 0;
        losses = 0;
        draws = 0;
        xp = 500;
        difficulty = 1;
        gameLength = 60;
        saveGame();
    }

    /**
     * Gets a random music file and plays it
     * Author - Inaki McKearney
     */
    private void playBackgroundMusic(){
        Random rand = new Random(System.currentTimeMillis());
        switch (rand.nextInt(3)){
            case 0:
                bgMusic = new Music(getResources().openRawResourceFd(R.raw.allstar));
                break;
            case 1:
                bgMusic = new Music(getResources().openRawResourceFd(R.raw.usesomebody));
                break;
            case 2:
                bgMusic = new Music(getResources().openRawResourceFd(R.raw.clubfoot));
            break;
        }
        bgMusic.setLooping(true);
        bgMusic.play();
    }

    public void refillClubFitness() {
        for (Card card : club) {
            card.setFitness(100);
        }
    }

    /**
     * Adds a card to the game's squad property
     *
     * @param card
     *
     * Author - Eimhin Laverty
     */
    public void addToSquad(Card card) {
        if(squad.size() <= 11) {
            squad.add(card);
        }
    }

    /**
     * Adds a card to the game's squad property
     *
     * @param card
     *
     * Author - Eimhin Laverty
     */
    public void addToClub(Card card) {
        club.add(card);
    }

    /**
     * Clears the squad array
     *
     * Author - Eimhin Laverty
     */
    public void clearSquad() {
        squad.clear();
    }

    /**
     * Clears the club array
     *
     * Author - Eimhin Laverty
     */
    public void clearClub() {
        club.clear();
    }
}
