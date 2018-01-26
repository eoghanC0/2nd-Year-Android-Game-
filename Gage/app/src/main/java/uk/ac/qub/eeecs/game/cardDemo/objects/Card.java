package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import uk.ac.qub.eeecs.gage.world.Sprite;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DOWN;


public class Card extends Sprite {
    // ////////////////////////////////////////////////
    // Constants
    // /////////////////////////////////////////////////
    private final Float FLAG_TO_CARD_WIDTH_RATIO = 80f/495f;
    private final Float FLAG_TO_CARD_HEIGHT_RATIO = 50f/789f;
    private final Float HEADSHOT_TO_CARD_WIDTH_RATIO = 134f/495f * 2.5f;
    private final Float HEADSHOT_TO_CARD_HEIGHT_RATIO = 134f/789f * 2.5f;
    private final Float BADGE_TO_CARD_WIDTH_RATIO = 68f/495f;
    private final Float BADGE_TO_CARD_HEIGHT_RATIO = 68f/789f;

    private final Float HEADSHOT_RELATIVE_POSITION_LEFT = 160 * HEADSHOT_TO_CARD_WIDTH_RATIO;
    private final Float HEADSHOT_RELATIVE_POSITION_TOP_RARE = 32 * HEADSHOT_TO_CARD_HEIGHT_RATIO;
    private final Float HEADSHOT_RELATIVE_POSITION_TOP_COMMON = 25 * HEADSHOT_TO_CARD_HEIGHT_RATIO;
    // ////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////
    private Bitmap cardBackground;
    private String displayName, firstName, lastName;
    private String club, nation;
    private String playerPosition;
    private int pace = 0;
    private int shooting = 0;
    private int passing = 0;
    private int dribbling = 0;
    private int defending = 0;
    private int heading = 0;
    private int diving = 0;
    private int handling = 0;
    private int kicking = 0;
    private int reflexes = 0;
    private int speed = 0;
    private int positioning = 0;
    private int rating = 75;
    private boolean rare = false;
    private int fitness = 100;
    private Bitmap headshot, nationFlag, clubBadge;

    private AssetStore assetManager = mGameScreen.getGame().getAssetManager();

    private int lastGamePlayNumber = -1;

    private boolean showingFitness = false;

    // /////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////
    public Card(float startX, float startY, float height, GameScreen gameScreen, String playerID, int fitness) {
        //The aspect ratio of the card is 225/355
        super(startX, startY, height * 225/355, height, null, gameScreen);
        this.fitness = fitness;
        try {
            populateCardProperties(playerID, getPlayersArray());
        } catch (JSONException e) {
            Log.e("Error", "The JSON file could not be read", e);
        }

    }

    public Card(float startX, float startY, float height, GameScreen gameScreen, boolean rare, int minRating, int maxRating) {
        //The aspect ratio of the card is 225/355
        super(startX, startY, height * 225/355, height, null, gameScreen);
        try {
            JSONArray playersArray = getPlayersArray();
            ArrayList<String> releventPlayerIDs = getReleventPlayerIDs(rare, minRating, maxRating, playersArray);
            Random rnd = new Random();
            String randPlayerID = releventPlayerIDs.get(rnd.nextInt(releventPlayerIDs.size()));
            populateCardProperties(randPlayerID, playersArray);
        } catch (JSONException e) {
            Log.e("Error", "The JSON file could not be read", e);
        }
    }
    // ///////////////////////////////////////////////////////////
    // Getters
    // ///////////////////////////////////////////////////////////
    public String getDisplayName() {
        return displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getClub() {
        return club;
    }

    public String getNation() {
        return nation;
    }

    public String getPlayerPosition() {
        return playerPosition;
    }

    public int getPace() {
        return pace;
    }

    public int getShooting() {
        return shooting;
    }

    public int getPassing() {
        return passing;
    }

    public int getDribbling() {
        return dribbling;
    }

    public int getDefending() {
        return defending;
    }

    public int getHeading() {
        return heading;
    }

    public int getDiving() {
        return diving;
    }

    public int getHandling() {
        return handling;
    }

    public int getKicking() {
        return kicking;
    }

    public int getReflexes() {
        return reflexes;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPositioning() {
        return positioning;
    }

    public int getRating() {
        return rating;
    }

    public boolean isRare() {
        return rare;
    }

    public int getFitness() {
        return fitness;
    }

    public int getLastGamePlayNumber() {
        return lastGamePlayNumber;
    }

    // ///////////////////////////////////////////////////////////
    // Setters
    // ///////////////////////////////////////////////////////////
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void setLastGamePlayNumber(int lastGamePlayNumber) {
        this.lastGamePlayNumber = lastGamePlayNumber;
    }


    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    private ArrayList<String> getReleventPlayerIDs(boolean rare, int minRating, int maxRating, JSONArray playersArray) throws JSONException{
        //Get an array of playerIDs where the player is rare/non-rare (depending on the parameter)
        ArrayList<String> playerIDs = new ArrayList<>();
        for (int i = 0; i < playersArray.length(); i++) {
            if ((boolean) playersArray.getJSONObject(i).get("rare") == rare &&
                    (int) playersArray.getJSONObject(i).get("rating") >= minRating &&
                    (int) playersArray.getJSONObject(i).get("rating") <= maxRating) {
                playerIDs.add((String) playersArray.getJSONObject(i).get("id"));
            }
        }
        return playerIDs;
    }

    private JSONArray getPlayersArray() throws JSONException {
        JSONObject playerJson = new JSONObject(assetManager.readAsset("player_json/all_cards.json"));
        return (JSONArray) playerJson.get("players");
    }

    private void populateCardProperties(String playerID, JSONArray playersArray) throws JSONException {
        JSONObject thisPlayerJSON = playersArray.getJSONObject(Integer.parseInt(playerID));
        displayName = (String) thisPlayerJSON.get("displayName");
        firstName = (String) thisPlayerJSON.get("firstName");
        lastName = (String) thisPlayerJSON.get("lastName");
        JSONObject clubDetails = (JSONObject) thisPlayerJSON.get("club");
        club = (String) clubDetails.get("name");
        JSONObject nationDetails = (JSONObject) thisPlayerJSON.get("nation");
        nation = (String) nationDetails.get("name");
        playerPosition = (String) thisPlayerJSON.get("position");
        JSONArray attributes = (JSONArray) thisPlayerJSON.get("attributes");
        for (int i = 0; i < attributes.length(); i++) {
            JSONObject attribute = (JSONObject) attributes.get(i);
            switch ((String) attribute.get("name")) {
                case "PAC":
                    pace = (int) attribute.get("value");
                    break;
                case "SHO":
                    shooting = (int) attribute.get("value");
                    break;
                case "PAS":
                    passing = (int) attribute.get("value");
                    break;
                case "DRI":
                    dribbling = (int) attribute.get("value");
                    break;
                case "DEF":
                    defending = (int) attribute.get("value");
                    break;
                case "HEA":
                    heading = (int) attribute.get("value");
                    break;
                case "DIV":
                    diving = (int) attribute.get("value");
                    break;
                case "HAN":
                    handling = (int) attribute.get("value");
                    break;
                case "KIC":
                    kicking = (int) attribute.get("value");
                    break;
                case "REF":
                    reflexes = (int) attribute.get("value");
                    break;
                case "SPD":
                    speed = (int) attribute.get("value");
                    break;
                case "POS":
                    positioning = (int) attribute.get("value");
                    break;
            }
        }
        rating = (int) thisPlayerJSON.get("rating");
        rare = (boolean) thisPlayerJSON.get("rare");
        if (assetManager.getBitmap("player_" + playerID) == null)
            assetManager.loadAndAddBitmap("player_" + playerID,"img/playerBitmaps/" + (String) thisPlayerJSON.get("headshotBitmap"));
        if (assetManager.getBitmap("club_" + (String) clubDetails.get("name")) == null)
            assetManager.loadAndAddBitmap("club_" + (String) clubDetails.get("name"), "img/clubBadgeBitmaps/" + (String) clubDetails.get("logo"));
        if (assetManager.getBitmap("nation_" + (String) nationDetails.get("name")) == null)
            assetManager.loadAndAddBitmap("nation_" + (String) nationDetails.get("name"), "img/nationBitmaps/" + (String) nationDetails.get("logo"));
        headshot = assetManager.getBitmap("player_" + playerID);
        nationFlag = assetManager.getBitmap("nation_" + (String) nationDetails.get("name"));
        clubBadge = assetManager.getBitmap("club_" + (String) clubDetails.get("name"));
        if (rating >= 75) {
            if (rare) {
                if (assetManager.getBitmap("gold_rare_card") == null)
                    assetManager.loadAndAddBitmap("gold_rare_card", "img/RareGoldCard.png");
                cardBackground = assetManager.getBitmap("gold_rare_card");
            } else {
                if (assetManager.getBitmap("gold_common_card") == null)
                    assetManager.loadAndAddBitmap("gold_common_card", "img/CommonGoldCard.png");
                cardBackground = assetManager.getBitmap("gold_common_card");
            }
        } else if (rating >= 65) {
            if (rare) {
                if (assetManager.getBitmap("silver_rare_card") == null)
                    assetManager.loadAndAddBitmap("silver_rare_card", "img/RareSilverCard.png");
                cardBackground = assetManager.getBitmap("silver_rare_card");
            } else {
                if (assetManager.getBitmap("silver_common_card") == null)
                    assetManager.loadAndAddBitmap("silver_common_card", "img/CommonSilverCard.png");
                cardBackground = assetManager.getBitmap("silver_common_card");
            }
        } else {
            if (rare) {
                if (assetManager.getBitmap("bronze_rare_card") == null)
                    assetManager.loadAndAddBitmap("bronze_rare_card", "img/RareBronzeCard.png");
                cardBackground = assetManager.getBitmap("bronze_rare_card");
            } else {
                if (assetManager.getBitmap("bronze_common_card") == null)
                    assetManager.loadAndAddBitmap("bronze_common_card", "img/CommonBronzeCard.png");
                cardBackground = assetManager.getBitmap("bronze_common_card");
            }
        }

    }

    @Override
    public void update(ElapsedTime elapsedTime) {

    }

    @Override
    public void draw(ElapsedTime elapsedTime,IGraphics2D graphics2D) {
        //Draw the card background
        drawScreenRect.set((int) mBound.getLeft(), (int) mBound.getBottom(), (int) mBound.getRight(), (int) mBound.getTop());
        graphics2D.drawBitmap(cardBackground, null, drawScreenRect, null);

        //Overlay the player headshot
        int rectTop;
        int rectLeft = (int) (mBound.getLeft() + HEADSHOT_RELATIVE_POSITION_LEFT);
        if (rare) {
            rectTop = (int) (mBound.getBottom() + HEADSHOT_RELATIVE_POSITION_TOP_RARE);
        } else {
            rectTop = (int) (mBound.getBottom() + HEADSHOT_RELATIVE_POSITION_TOP_COMMON);
        }
        int rectWidth = (int) (mBound.getWidth() * HEADSHOT_TO_CARD_WIDTH_RATIO);
        int rectHeight = (int) (mBound.getTop() * HEADSHOT_TO_CARD_HEIGHT_RATIO);
        drawScreenRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
        graphics2D.drawBitmap(headshot, null, drawScreenRect, null);

        //Overlay the club badge
        rectLeft = (int) (mBound.getLeft() + HEADSHOT_RELATIVE_POSITION_LEFT);
        if (rare) {
            rectTop = (int) (mBound.getBottom() + HEADSHOT_RELATIVE_POSITION_TOP_RARE);
        } else {
            rectTop = (int) (mBound.getBottom() + HEADSHOT_RELATIVE_POSITION_TOP_COMMON);
        }
        rectWidth = (int) (mBound.getWidth() * HEADSHOT_TO_CARD_WIDTH_RATIO);
        rectHeight = (int) (mBound.getTop() * HEADSHOT_TO_CARD_HEIGHT_RATIO);
        drawScreenRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
        graphics2D.drawBitmap(clubBadge, null, drawScreenRect, null);


    }
}
