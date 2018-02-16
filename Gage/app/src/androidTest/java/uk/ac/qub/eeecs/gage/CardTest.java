package uk.ac.qub.eeecs.gage;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.AssetStore;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;
import uk.ac.qub.eeecs.gage.world.FootballGameScreen;
import uk.ac.qub.eeecs.game.FootballGame;
import uk.ac.qub.eeecs.game.cardDemo.objects.Card;
import uk.ac.qub.eeecs.game.cardDemo.screens.HelpScreen;

/**
 * Created by stephenmcveigh on 15/02/2018.
 */
@RunWith(AndroidJUnit4.class)
public class CardTest {
    Context appContext;
    FootballGameScreen mockScreen;
    Card testCard;
    FootballGame game;
    Random rand = new Random();

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getTargetContext();

        game = new FootballGame();

        FileIO fileIO = new FileIO(appContext);
        game.mFileIO = fileIO;
        game.mAssetManager = new AssetStore(fileIO);

        mockScreen = new HelpScreen(game);

        testCard = new Card(500,500,355, mockScreen, "1", 100);
    }

    //////////////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////////////
    @Test
    public void test_Constructor_1_width() {
        assert(testCard.getBound().getWidth() == 225);
    }

    @Test
    public void test_Constructor_1_fitness() {
        assert(testCard.fitness == 100);
    }

    @Test
    public void test_Constructor_1_playerID() {
        assert(testCard.playerID.equals("1"));
    }

    @Test
    public void test_Copy_Constructor_positionX() {
        Card copyCard = new Card(testCard);
        assert(testCard.position.x == copyCard.position.x);
    }

    @Test
    public void test_Copy_Constructor_positionY() {
        Card copyCard = new Card(testCard);
        assert(testCard.position.y == copyCard.position.y);
    }

    @Test
    public void test_Copy_Constructor_width() {
        Card copyCard = new Card(testCard);
        assert(testCard.getBound().getWidth() == copyCard.getBound().getWidth());
    }

    @Test
    public void test_Copy_Constructor_height() {
        Card copyCard = new Card(testCard);
        assert(testCard.getBound().getHeight() == copyCard.getBound().getHeight());
    }

    @Test
    public void test_Copy_Constructor_fitness() {
        Card copyCard = new Card(testCard);
        assert(testCard.fitness == copyCard.fitness);
    }

    @Test
    public void test_Copy_Constructor_playerID() {
        Card copyCard = new Card(testCard);
        assert(testCard.playerID.equals(copyCard.playerID));
    }

    // ///////////////////////////////////////////////////////////
    // Getters
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_getPlayerID() {
        assert(testCard.playerID == testCard.getPlayerID());
    }

    @Test
    public void test_getDisplayName() {
        assert(testCard.displayName == testCard.getDisplayName());
    }

    @Test
    public void test_getFirstName() {
        assert(testCard.firstName == testCard.getFirstName());
    }
    @Test
    public void test_getLastName() {
        assert(testCard.lastName == testCard.getLastName());
    }
    @Test
    public void test_getClub() {
        assert(testCard.club == testCard.getClub());
    }
    @Test
    public void test_getNation() {
        assert(testCard.nation == testCard.getNation());
    }
    @Test
    public void test_getPlayerPosition() {
        assert(testCard.playerPosition == testCard.getPlayerPosition());
    }
    @Test
    public void test_getPace() {
        assert(testCard.pace == testCard.getPace());
    }
    @Test
    public void test_getShooting() {
        assert(testCard.shooting == testCard.getShooting());
    }
    @Test
    public void test_getPassing() {
        assert(testCard.passing == testCard.getPassing());
    }
    @Test
    public void test_getDribbling() {
        assert(testCard.dribbling == testCard.getDribbling());
    }
    @Test
    public void test_getDefending() {
        assert(testCard.defending == testCard.getDefending());
    }
    @Test
    public void test_getHeading() {
        assert(testCard.heading == testCard.getHeading());
    }
    @Test
    public void test_getDiving() {
        assert(testCard.diving == testCard.getDiving());
    }
    @Test
    public void test_getHandling() {
        assert(testCard.handling == testCard.getHandling());
    }
    @Test
    public void test_getKicking() {
        assert(testCard.kicking == testCard.getKicking());
    }
    @Test
    public void test_getReflexes() {
        assert(testCard.reflexes == testCard.getReflexes());
    }
    @Test
    public void test_getSpeed() {
        assert(testCard.speed == testCard.getSpeed());
    }
    @Test
    public void test_getPositioning() {
        assert(testCard.positioning == testCard.getPositioning());
    }
    @Test
    public void test_getRating() {
        assert(testCard.rating == testCard.getRating());
    }
    @Test
    public void test_isRare() {
        assert(testCard.rare == testCard.isRare());
    }
    @Test
    public void test_getFitness() {
        assert(testCard.fitness == testCard.getFitness());
    }

    // ///////////////////////////////////////////////////////////
    // Setters
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_setFitness() {
        testCard.setFitness(80);
        assert(testCard.fitness == 80);
    }

    @Test
    public void test_setHeight_halfHeight() {
        testCard.setHeight(710);
        assert(testCard.getBound().halfHeight == 355);
    }

    @Test
    public void test_setHeight_halfWidth() {
        testCard.setHeight(710);
        assert(testCard.getBound().halfWidth == 112.5);
    }

    @Test
    public void test_setDraggingEnabled() {
        testCard.draggingEnabled = false;
        testCard.setDraggingEnabled(true);
        assert(testCard.draggingEnabled);
    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_getPlayersArray_notNull() {
        JSONArray testArray;
        try {
            testArray = testCard.getPlayersArray();
            assert (testArray != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getPlayersArray_checkLength() {
        JSONArray testArray;
        try {
            testArray = testCard.getPlayersArray();
            assert (testArray.length() > 0);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkSize() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"", "Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(5);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            assert (relevantPlayerIDs.size() > 0);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkMinRating() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"", "Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(5);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assert (testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getInt("rating") >= minRating);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkMaxRating() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"", "Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(5);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assert (testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getInt("rating") <= maxRating);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkRare() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"", "Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(5);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assert (testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getBoolean("rare") == rare);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkPosition() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"", "Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(5);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assert (testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getString("position").equals(positions[position]));
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_getJSONPlayer() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            String id = String.valueOf(rand.nextInt(629));
            player = testCard.getJSONPlayer(id,playersArray);
            assert (player == playersArray.getJSONObject(Integer.parseInt(id)));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_playerID() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNames(player);
            assert (testCard.playerID.equals("1"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_displayName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNames(player);
            assert (testCard.displayName.equals("Hazard"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_firstName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNames(player);
            assert (testCard.firstName.equals("Eden"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_lastName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNames(player);
            assert (testCard.lastName.equals("Hazard"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_hasDisplayName_displayName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("9",playersArray);
            testCard.populateNames(player);
            assert (testCard.displayName.equals("David Silva"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNames_hasDisplayName_lastName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("9",playersArray);
            testCard.populateNames(player);
            assert (testCard.lastName.equals("Jimenez Silva"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateClubDetails_club() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateClubDetails(player);
            assert (testCard.club.equals("Chelsea"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateClubDetails_abbrClub() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateClubDetails(player);
            assert (testCard.abbrClub.equals("Chelsea"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateClubDetails_clubBadge() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateClubDetails(player);
            assert (testCard.clubBadge != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNationDetails_nation() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNationDetails(player);
            assert (testCard.nation.equals("Belgium"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNationDetails_abbrNation() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNationDetails(player);
            assert (testCard.abbrNation.equals("Belgium"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateNationDetails_nationFlag() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateNationDetails(player);
            assert (testCard.nationFlag != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_forward_position() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.playerPosition.equals("Forward"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_forward_abbrPosition() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.abbrPlayerPosition.equals("FWD"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_midfield_position() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("3",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.playerPosition.equals("Midfield"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_midfield_abbrPosition() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("3",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.abbrPlayerPosition.equals("MID"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_defence_position() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("16",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.playerPosition.equals("Defence"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_defence_abbrPosition() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("16",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.abbrPlayerPosition.equals("DEF"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_goalKeeper_position() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.playerPosition.equals("GoalKeeper"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populatePositionDetails_goalKeeper_abbrPosition() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populatePositionDetails(player);
            assert (testCard.abbrPlayerPosition.equals("GK"));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_pace() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.pace == 90);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_shooting() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.shooting == 82);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_passing() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.passing == 84);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_dribbling() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.dribbling == 92);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_defending() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.defending == 32);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_heading() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.heading == 66);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_rating() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.rating == 90);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_diving() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.diving == 83);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_handling() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.handling == 82);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_kicking() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.kicking == 82);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_reflexes() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.reflexes == 86);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_speed() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.speed == 62);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateAttributeDetails_positioning() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("41",playersArray);
            testCard.populateAttributeDetails(player);
            assert (testCard.positioning == 80);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }


    @Test
    public void test_populateHeadshot() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.headshot = null;
            testCard.populateHeadshot(player);
            assert (testCard.headshot != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_over75_rare() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("1",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_over75_common() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("59",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_over65_rare() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("61",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_over65_common() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("397",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_under65_rare() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("615",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

    @Test
    public void test_populateBackground_under65_common() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("628",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assert (testCard.cardBackground != null);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assert (false);
        }
    }

}

