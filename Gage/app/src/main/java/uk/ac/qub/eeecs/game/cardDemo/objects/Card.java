package uk.ac.qub.eeecs.game.cardDemo.objects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import uk.ac.qub.eeecs.gage.world.Sprite;

import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.DOUBLE_TAP;
import static uk.ac.qub.eeecs.gage.engine.input.TouchEvent.TOUCH_DRAGGED;


public class Card extends Sprite {
    // ////////////////////////////////////////////////
    // Constants
    // /////////////////////////////////////////////////

    //Size Ratios
    private final float HEADSHOT_TO_CARD_RATIO = 21f/50f;

    private final float BADGE_TO_CARD_RATIO = 7f/50f;

    private final float FLAG_TO_CARD_HEIGHT_RATIO = 19f/250f;
    private final float FLAG_TO_CARD_WIDTH_RATIO = 3f/25f;

    private final float RATING_SIZE_TO_CARD_HEIGHT_RATIO = 1f/10f;
    private final float ATTRIBUTE_SIZE_TO_CARD_HEIGHT_RATIO = 1f/20f;
    private final float POSITION_SIZE_TO_CARD_HEIGHT_RATIO = 1f/20f;
    private final float NAME_SIZE_TO_CARD_HEIGHT_RATIO = 1f/20f;
    private final float FITNESS_SIZE_TO_CARD_HEIGHT_RATIO = 1f/20f;
    private final float FITNESS_ARC_DIAMETER_TO_CARD_HEIGHT_RATIO = 3f/25f;

    //Position Ratios
    private final float HEADSHOT_RELATIVE_POSITION_LEFT_RATIO = 4f/25f;
    private final float HEADSHOT_RELATIVE_POSITION_TOP_RARE_RATIO = 28f/500f;
    private final float HEADSHOT_RELATIVE_POSITION_TOP_COMMON_RATIO = 24f/500f;

    private final float BADGE_RELATIVE_POSITION_TOP_RATIO = 17f/50f;

    private final float FLAG_RELATIVE_POSITION_LEFT_RATIO = 1f/100f;
    private final float FLAG_RELATIVE_POSITION_TOP_RATIO = 1f/2f;

    private final float RATING_RELATIVE_POSITION_LEFT_RATIO = 7f/100f;
    private final float RATING_RELATIVE_POSITION_TOP_RATIO = 6f/25f;

    private final float ATTRIBUTES_RELATIVE_POSITION_LEFT_RATIO = 1f/5f;
    private final float ATTRIBUTES_RELATIVE_POSITION_TOP_RATIO = 16f/25f;
    private final float ATTRIBUTE_HORIZONTAL_SPACING_RATIO = 6f/25f;
    private final float ATTRIBUTE_VERTICAL_SPACING_RATIO = 1f/10f;
    private final float ATTRIBUTE_STAT_NAME_SPACING_RATIO = 1f/10f;

    private final float POSITION_RELATIVE_POSITION_LEFT_RATIO = 17f/250f;
    private final float POSITION_RELATIVE_POSITION_TOP_RATIO = 3f/10f;

    private final float NAME_RELATIVE_POSITION_LEFT_RATIO = 9f/25f;
    private final float NAME_RELATIVE_POSITION_TOP_RATIO = 27f/50f;

    private final float FITNESS_ARC_RELATIVE_POSITION_LEFT_RATIO = 1f/100f;
    private final float FITNESS_ARC_RELATIVE_POSITION_TOP_RATIO = 16f/25f;

    private final float FITNESS_RELATIVE_POSITION_LEFT_RATIO = 17f/250f;
    private final float FITNESS_RELATIVE_POSITION_TOP_RATIO = 21f/25f;

    private final float DETAILS_RELATIVE_POSITION_LEFT_RATIO = 9f/25f;
    private final float DETAILS_RELATIVE_POSITION_TOP_RATIO = 31f/50f;
    private final float DETAILS_SPACING_RATIO = 1f/10f;

    // ////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////
    private String playerID;
    private Bitmap cardBackground;
    private String displayName, firstName, lastName;
    private String club, nation;
    private String abbrClub, abbrNation;
    private String playerPosition;
    private String abbrPlayerPosition;
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

    private boolean showingStats = true;

    private boolean draggingEnabled = false;

    // /////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////
    public Card(float startX, float startY, float height, GameScreen gameScreen, String playerID, int fitness) {
        //The aspect ratio of the card is 225/355
        super(startX, startY, height * 225/355, height, null, gameScreen);
        this.fitness = fitness;
        this.playerID = playerID;
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
            ArrayList<String> relevantPlayerIDs = getRelevantPlayerIDs(rare, minRating, maxRating,null, playersArray);
            Random rnd = new Random();
            String randPlayerID = relevantPlayerIDs.get(rnd.nextInt(relevantPlayerIDs.size()));
            populateCardProperties(randPlayerID, playersArray);
            this.playerID = playerID;
        } catch (JSONException e) {
            Log.e("Error", "The JSON file could not be read", e);
        }
    }

    public Card(Card c) {
        super(c.position.x, c.position.y, c.mBound.getWidth(), c.mBound.getHeight(), null, c.mGameScreen);
        this.fitness = c.fitness;
        this.playerID = c.playerID;
        try {
            populateCardProperties(playerID, getPlayersArray());
        } catch (JSONException e) {
            Log.e("Error", "The JSON file could not be read", e);
        }
    }

    // ///////////////////////////////////////////////////////////
    // Getters
    // ///////////////////////////////////////////////////////////
    public String getPlayerID() {
        return playerID;
    }

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

    public void setHeight(int height) {
        mBound.halfHeight = height / 2f;
        mBound.halfWidth = height * 225/355/2f;
    }

    public void setDraggingEnabled(boolean value) {
        this.draggingEnabled = value;
    }


    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    private ArrayList<String> getRelevantPlayerIDs(boolean rare, int minRating, int maxRating, String playerPosition, JSONArray playersArray) throws JSONException{
        //Get an array of playerIDs where the player is rare/non-rare (depending on the parameter)
        ArrayList<String> playerIDs = new ArrayList<>();
        for (int i = 0; i < playersArray.length(); i++) {
            if ((boolean) playersArray.getJSONObject(i).get("rare") == rare &&
                    (int) playersArray.getJSONObject(i).get("rating") >= minRating &&
                    (int) playersArray.getJSONObject(i).get("rating") <= maxRating) {
                if (playerPosition == null || playerPosition.equals("")) {
                    playerIDs.add((String) playersArray.getJSONObject(i).get("id"));
                } else if (playersArray.getJSONObject(i).get("rating").equals(playerPosition)) {
                    playerIDs.add((String) playersArray.getJSONObject(i).get("id"));
                }
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
        if (displayName.equals(""))
            displayName = lastName;
        JSONObject clubDetails = (JSONObject) thisPlayerJSON.get("club");
        club = (String) clubDetails.get("name");
        abbrClub = (String) clubDetails.get("abbrName");
        JSONObject nationDetails = (JSONObject) thisPlayerJSON.get("nation");
        nation = (String) nationDetails.get("name");
        abbrNation = (String) nationDetails.get("abbrName");
        playerPosition = (String) thisPlayerJSON.get("position");
        switch (playerPosition) {
            case "Forward":
                abbrPlayerPosition = "FWD";
                break;
            case "Midfield":
                abbrPlayerPosition = "MID";
                break;
            case "Defence":
                abbrPlayerPosition = "DEF";
                break;
            case "GoalKeeper":
                abbrPlayerPosition = "GK";
                break;
        }
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
            assetManager.loadAndAddBitmap("player_" + playerID, "img/playerBitmaps/" + (String) thisPlayerJSON.get("headshotBitmap"));
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

    private boolean useSimulatedTouchEvents = false;
    private List<TouchEvent> simulatedTouchEvents = new ArrayList<TouchEvent>();

    public void setSimulatedTouchEvents(List<TouchEvent> simulatedTouchEvents) {
        this.simulatedTouchEvents = simulatedTouchEvents;
    }

    public void setUseSimulatedTouchEvents(boolean useSimulatedTouchEvents) {
        this.useSimulatedTouchEvents = useSimulatedTouchEvents;
    }

    @Override
    public void update(ElapsedTime elapsedTime) {
        // Consider any touch events occurring in this update
        List<TouchEvent> touchEvents;
        if(useSimulatedTouchEvents) touchEvents = simulatedTouchEvents;
        else touchEvents = mGameScreen.getGame().getInput().getTouchEvents();

        // Check for a touch event on this listBox
        for (TouchEvent touchEvent : touchEvents) {
            if (getBound().contains(touchEvent.x, touchEvent.y)) {
                if (touchEvent.type == DOUBLE_TAP)
                    showingStats = !showingStats;
                if (draggingEnabled && (touchEvent.type == TOUCH_DRAGGED)) {
                    position.x = touchEvent.x;
                    position.y = touchEvent.y;
                }
            }
        }
    }

    @Override
    public void draw(ElapsedTime elapsedTime,IGraphics2D graphics2D) {
        //Get the game's paint object which will be used for drawing
        Paint paint = mGameScreen.getGame().getPaint();
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        //Draw the card background
        drawScreenRect.set((int) (position.x - mBound.halfWidth),
                (int) (position.y - mBound.halfHeight),
                (int) (position.x + mBound.halfWidth),
                (int) (position.y + mBound.halfHeight));
        graphics2D.drawBitmap(cardBackground, null, drawScreenRect, null);

        //Overlay the Player Rating
        paint.setTextSize((int) (RATING_SIZE_TO_CARD_HEIGHT_RATIO * mBound.getHeight()));
        paint.setFakeBoldText(true);
        graphics2D.drawText(String.valueOf(rating), position.x - mBound.halfWidth + RATING_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + RATING_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), paint);

        //Overlay the player's position
        paint.setTextSize((int) (POSITION_SIZE_TO_CARD_HEIGHT_RATIO * mBound.getHeight()));
        graphics2D.drawText(abbrPlayerPosition, position.x - mBound.halfWidth + POSITION_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + POSITION_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), paint);

        //Overlay the player's name
        paint.setTextSize((int) (NAME_SIZE_TO_CARD_HEIGHT_RATIO * mBound.getHeight()));
        graphics2D.drawText(displayName, position.x - mBound.halfWidth + NAME_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + NAME_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), paint);

        paint.setTextSize((int) (ATTRIBUTE_SIZE_TO_CARD_HEIGHT_RATIO * mBound.getHeight()));
        if (showingStats) {
            //Overlay the Attributes
            float attributeLeftX = position.x - mBound.halfWidth + ATTRIBUTES_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight();
            float attributeRightX = attributeLeftX + ATTRIBUTE_HORIZONTAL_SPACING_RATIO * mBound.getHeight();
            float attributeTopY = position.y - mBound.halfHeight + ATTRIBUTES_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight();
            int attributeSpacingY = (int) (ATTRIBUTE_VERTICAL_SPACING_RATIO * mBound.getHeight());

            if (playerPosition.equals("GoalKeeper")) {
                graphics2D.drawText(String.valueOf(diving), attributeLeftX, attributeTopY, paint);
                graphics2D.drawText(String.valueOf(reflexes), attributeRightX, attributeTopY, paint);
                graphics2D.drawText(String.valueOf(handling), attributeLeftX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText(String.valueOf(speed), attributeRightX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText(String.valueOf(kicking), attributeLeftX, attributeTopY + attributeSpacingY * 2, paint);
                graphics2D.drawText(String.valueOf(positioning), attributeRightX, attributeTopY + attributeSpacingY * 2, paint);
                paint.setFakeBoldText(false);
                attributeLeftX += ATTRIBUTE_STAT_NAME_SPACING_RATIO * mBound.getHeight();
                attributeRightX += ATTRIBUTE_STAT_NAME_SPACING_RATIO * mBound.getHeight();
                graphics2D.drawText("DIV", attributeLeftX, attributeTopY, paint);
                graphics2D.drawText("REF", attributeRightX, attributeTopY, paint);
                graphics2D.drawText("HAN", attributeLeftX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText("SPD", attributeRightX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText("KIC", attributeLeftX, attributeTopY + attributeSpacingY * 2, paint);
                graphics2D.drawText("POS", attributeRightX, attributeTopY + attributeSpacingY * 2, paint);
            } else {
                graphics2D.drawText(String.valueOf(pace), attributeLeftX, attributeTopY, paint);
                graphics2D.drawText(String.valueOf(dribbling), attributeRightX, attributeTopY, paint);
                graphics2D.drawText(String.valueOf(shooting), attributeLeftX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText(String.valueOf(defending), attributeRightX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText(String.valueOf(passing), attributeLeftX, attributeTopY + attributeSpacingY * 2, paint);
                graphics2D.drawText(String.valueOf(heading), attributeRightX, attributeTopY + attributeSpacingY * 2, paint);
                paint.setFakeBoldText(false);
                attributeLeftX += ATTRIBUTE_STAT_NAME_SPACING_RATIO * mBound.getHeight();
                attributeRightX += ATTRIBUTE_STAT_NAME_SPACING_RATIO * mBound.getHeight();
                graphics2D.drawText("PAC", attributeLeftX, attributeTopY, paint);
                graphics2D.drawText("DRI", attributeRightX, attributeTopY, paint);
                graphics2D.drawText("SHO", attributeLeftX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText("DEF", attributeRightX, attributeTopY + attributeSpacingY, paint);
                graphics2D.drawText("PAS", attributeLeftX, attributeTopY + attributeSpacingY * 2, paint);
                graphics2D.drawText("HEA", attributeRightX, attributeTopY + attributeSpacingY * 2, paint);
            }
        } else {
            //Overlay the player's details
            paint.setFakeBoldText(false);
            graphics2D.drawText(firstName, position.x - mBound.halfWidth  + DETAILS_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + DETAILS_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), paint);
            graphics2D.drawText(lastName, position.x - mBound.halfWidth  + DETAILS_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + DETAILS_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight() + DETAILS_SPACING_RATIO * mBound.getHeight(), paint);
            graphics2D.drawText(abbrClub, position.x - mBound.halfWidth  + DETAILS_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + DETAILS_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight() + DETAILS_SPACING_RATIO * mBound.getHeight() * 2, paint);
            graphics2D.drawText(abbrNation, position.x - mBound.halfWidth  + DETAILS_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + DETAILS_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight() + DETAILS_SPACING_RATIO * mBound.getHeight() * 3, paint);
        }

        //Overlay the player headshot
        int rectTop;
        int rectLeft = (int) (position.x - mBound.halfWidth  + HEADSHOT_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight());
        if (rare) {
            rectTop = (int) (position.y - mBound.halfHeight + HEADSHOT_RELATIVE_POSITION_TOP_RARE_RATIO * mBound.getHeight());
        } else {
            rectTop = (int) (position.y - mBound.halfHeight + HEADSHOT_RELATIVE_POSITION_TOP_COMMON_RATIO * mBound.getHeight());
        }
        int rectWidth = (int) (HEADSHOT_TO_CARD_RATIO * mBound.getHeight());
        int rectHeight = (int) (HEADSHOT_TO_CARD_RATIO * mBound.getHeight());
        drawScreenRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
        graphics2D.drawBitmap(headshot, null, drawScreenRect, null);

        //Overlay the club badge
        rectLeft = (int) (position.x - mBound.halfWidth );
        rectTop = (int) (position.y - mBound.halfHeight + BADGE_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight());
        rectWidth = (int) (BADGE_TO_CARD_RATIO * mBound.getHeight());
        rectHeight = (int) (BADGE_TO_CARD_RATIO * mBound.getHeight());
        drawScreenRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
        graphics2D.drawBitmap(clubBadge, null, drawScreenRect, null);

        //Overlay the nation flag
        rectLeft = (int) (position.x - mBound.halfWidth + FLAG_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight());
        rectTop = (int) (position.y - mBound.halfHeight + FLAG_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight());
        rectWidth = (int) (FLAG_TO_CARD_WIDTH_RATIO * mBound.getHeight());
        rectHeight = (int) (FLAG_TO_CARD_HEIGHT_RATIO * mBound.getHeight());
        drawScreenRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
        graphics2D.drawBitmap(nationFlag, null, drawScreenRect, null);

        //Overlay the fitness stat as an arc
        RectF ovel = new RectF(position.x - mBound.halfWidth  + FITNESS_ARC_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + FITNESS_ARC_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), position.x - mBound.halfWidth  + FITNESS_ARC_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight() + FITNESS_ARC_DIAMETER_TO_CARD_HEIGHT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + FITNESS_ARC_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight() + FITNESS_ARC_DIAMETER_TO_CARD_HEIGHT_RATIO * mBound.getHeight());
        if (fitness >= 80) {
            paint.setColor(Color.GREEN);
        } else if (fitness >= 60) {
            paint.setColor(Color.rgb(255,140,0));
        } else {
            paint.setColor(Color.RED);
        }
        graphics2D.drawArc(ovel, -90, fitness / 100f * 360f,  true, paint);

        //Draw the fitness label
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize((int) (FITNESS_SIZE_TO_CARD_HEIGHT_RATIO * mBound.getHeight()));
        graphics2D.drawText("FIT", position.x - mBound.halfWidth + FITNESS_RELATIVE_POSITION_LEFT_RATIO * mBound.getHeight(), position.y - mBound.halfHeight + FITNESS_RELATIVE_POSITION_TOP_RATIO * mBound.getHeight(), paint);
    }
}
