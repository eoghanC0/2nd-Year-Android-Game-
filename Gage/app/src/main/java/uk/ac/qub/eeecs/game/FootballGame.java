package uk.ac.qub.eeecs.game;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
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

    private final int SAVE_SLOT_MAX = 3;


    //AssetStore assetManager = getAssetManager();

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

      //  assetManager = getAssetManager();
  //      saveGame(1);
//        Log.d("JSON", assetManager.readFile("test5.txt"));
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
    public void saveGame(int saveSlot, Context context) {
        JSONObject gameSavesObj = new JSONObject();
        String filePath = context.getFilesDir().getPath().toString() + "/save" + saveSlot + ".json";
        File f = new File(filePath);
        try {
            gameSavesObj.put("gameID", gameID);
            JSONArray clubArray = new JSONArray();
            clubArray.put(club);
            gameSavesObj.put("wins", wins);
            gameSavesObj.put("losses", losses);
            gameSavesObj.put("draws", draws);
            gameSavesObj.put("xp", xp);
            gameSavesObj.put("difficulty", difficulty);
            gameSavesObj.put("gameLength", gameLength);
            gameSavesObj.put("pitchBackGround", pitchBackGround);
            //Log.d("JSON", gameSavesObj.toString());
            //Log.d("JSON", mAssetManager.readFile("saves.json"));
        }
        catch(JSONException e){
            Log.d("JSON", "Save fail : " + e.getMessage());
        }

        try {
            Log.d("JSON", filePath);
            FileWriter fileWriter = new FileWriter(f);
            fileWriter.write(gameSavesObj.toString());
            fileWriter.flush();
            Log.d("JSON", "Save success");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("JSON", "Save fail " + e.getMessage());
        }
//        System.out.println(gameSavesObj);
    }

    public void loadGame(int saveSlot, Context context) {
        JSONObject gameSavesObj = new JSONObject();
        //TODO Limit to 3 save slots
        String filePath = context.getFilesDir().getPath().toString() + "/save" + saveSlot + ".json";
        File f = new File(filePath);
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            gameSavesObj = new JSONObject(in.readLine());
            Log.d("JSON", "Load Success: ");
        } catch (Exception e){
            e.printStackTrace();
            Log.d("JSON", "Load fail " + e.getMessage());
        }
        try {
//            //JSON examples below are just for testing
//            JSONObject selectedSave;
//            //TODO: Obtain JSONs from storage
//            JSONObject save0 = new JSONObject("{\"gameID\":0,\"wins\":0,\"losses\":0,\"draws\":0,\"xp\":0,\"difficulty\":0,\"gameLength\":0,\"pitchBackGround\":\"\"}");
//            JSONObject save1 = new JSONObject("{\"gameID\":1,\"wins\":1,\"losses\":1,\"draws\":1,\"xp\":1,\"difficulty\":1,\"gameLength\":1,\"pitchBackGround\":\"camp nou\"}");
//            JSONObject save2 = new JSONObject("{\"gameID\":2,\"wins\":2,\"losses\":2,\"draws\":2,\"xp\":2,\"difficulty\":2,\"gameLength\":2,\"pitchBackGround\":\"old trafford\"}");

            wins = (int) gameSavesObj.get("wins");
            losses = (int) gameSavesObj.get("losses");
            draws = (int) gameSavesObj.get("draws");
            xp = (int) gameSavesObj.get("xp");
            difficulty = (int) gameSavesObj.get("difficulty");
            gameLength = (int) gameSavesObj.get("gameLength");
            pitchBackGround = gameSavesObj.get("pitchBackGround").toString();

            //for testing TODO: remove logs
            Log.d("JSON", String.valueOf(wins));
            Log.d("JSON", String.valueOf(losses));
            Log.d("JSON", String.valueOf(draws));
            Log.d("JSON", String.valueOf(xp));
            Log.d("JSON", String.valueOf(difficulty));
            Log.d("JSON", String.valueOf(gameLength));
            Log.d("JSON", pitchBackGround);

        } catch (JSONException e) {
            Log.d("JSON", "Load Failed : " + e.getMessage());
        }
    }
}
