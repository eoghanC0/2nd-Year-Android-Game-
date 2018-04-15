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
import uk.ac.qub.eeecs.game.objects.FootballGameScreen;
import uk.ac.qub.eeecs.game.objects.FootballGame;
import uk.ac.qub.eeecs.game.objects.Card;
import uk.ac.qub.eeecs.game.screens.HelpScreen;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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

        testCard = new Card(500,500,355, mockScreen, "0", 100);
    }

    //////////////////////////////////////////////////////
    //  Constructors
    //////////////////////////////////////////////////////
    @Test
    public void test_Constructor_1_width() {
        assertEquals(225, testCard.getBound().getWidth(), 0);
    }

    @Test
    public void test_Constructor_1_fitness() {
        assertEquals(100,testCard.fitness);
    }

    @Test
    public void test_Constructor_1_playerID() {
        assertEquals("0", testCard.playerID);
    }

    @Test
    public void test_Copy_Constructor_positionX() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.position.x, copyCard.position.x, 0);
    }

    @Test
    public void test_Copy_Constructor_positionY() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.position.y, copyCard.position.y, 0);
    }

    @Test
    public void test_Copy_Constructor_width() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.getBound().getWidth(), copyCard.getBound().getWidth(), 0);
    }

    @Test
    public void test_Copy_Constructor_height() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.getBound().getHeight(), copyCard.getBound().getHeight(), 0);
    }

    @Test
    public void test_Copy_Constructor_fitness() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.fitness, copyCard.fitness, 0);
    }

    @Test
    public void test_Copy_Constructor_playerID() {
        Card copyCard = new Card(testCard);
        assertEquals(testCard.playerID, copyCard.playerID);
    }

    // ///////////////////////////////////////////////////////////
    // Getters
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_getPlayerID() {
        assertEquals(testCard.playerID, testCard.getPlayerID());
    }

    @Test
    public void test_getDisplayName() {
        assertEquals(testCard.displayName, testCard.getDisplayName());
    }

    @Test
    public void test_getFirstName() {
        assertEquals(testCard.firstName, testCard.getFirstName());
    }
    @Test
    public void test_getLastName() {
        assertEquals(testCard.lastName, testCard.getLastName());
    }
    @Test
    public void test_getClub() {
        assertEquals(testCard.club, testCard.getClub());
    }
    @Test
    public void test_getNation() {
        assertEquals(testCard.nation, testCard.getNation());
    }
    @Test
    public void test_getPlayerPosition() {
        assertEquals(testCard.playerPosition, testCard.getPlayerPosition());
    }
    @Test
    public void test_getPace() {
        assertEquals(testCard.pace, testCard.getPace());
    }
    @Test
    public void test_getShooting() {
        assertEquals(testCard.shooting, testCard.getShooting());
    }
    @Test
    public void test_getPassing() {
        assertEquals(testCard.passing, testCard.getPassing());
    }
    @Test
    public void test_getDribbling() {
        assertEquals(testCard.dribbling, testCard.getDribbling());
    }
    @Test
    public void test_getDefending() {
        assertEquals(testCard.defending, testCard.getDefending());
    }
    @Test
    public void test_getHeading() {
        assertEquals(testCard.heading, testCard.getHeading());
    }
    @Test
    public void test_getDiving() {
        assertEquals(testCard.diving, testCard.getDiving());
    }
    @Test
    public void test_getHandling() {
        assertEquals(testCard.handling, testCard.getHandling());
    }
    @Test
    public void test_getKicking() {
        assertEquals(testCard.kicking, testCard.getKicking());
    }
    @Test
    public void test_getReflexes() {
        assertEquals(testCard.reflexes, testCard.getReflexes());
    }
    @Test
    public void test_getSpeed() {
        assertEquals(testCard.speed, testCard.getSpeed());
    }
    @Test
    public void test_getPositioning() {
        assertEquals(testCard.positioning, testCard.getPositioning());
    }
    @Test
    public void test_getRating() {
        assertEquals(testCard.rating, testCard.getRating());
    }
    @Test
    public void test_isRare() {
        assertEquals(testCard.rare, testCard.isRare());
    }
    @Test
    public void test_getFitness() {
        assertEquals(testCard.fitness, testCard.getFitness());
    }

    // ///////////////////////////////////////////////////////////
    // Setters
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_setFitness() {
        testCard.setFitness(80);
        assertEquals(80, testCard.fitness);
    }

    @Test
    public void test_setHeight_halfHeight() {
        testCard.setHeight(710);
        assertEquals(355, testCard.getBound().halfHeight, 0);
    }

    @Test
    public void test_setHeight_halfWidth() {
        testCard.setHeight(710);
        assertEquals(225, testCard.getBound().halfWidth, 0);
    }

    @Test
    public void test_setDraggingEnabled() {
        testCard.draggingEnabled = false;
        testCard.setDraggingEnabled(true);
        assertTrue(testCard.draggingEnabled);
    }

    // ///////////////////////////////////////////////////////////
    // Methods
    // ///////////////////////////////////////////////////////////

    @Test
    public void test_getPlayersArray_notNull() {
        JSONArray testArray;
        try {
            testArray = testCard.getPlayersArray();
            assertNotNull(testArray);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getPlayersArray_checkLength() {
        JSONArray testArray;
        try {
            testArray = testCard.getPlayersArray();
            assertTrue(testArray.length() > 0);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkSize() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(4);
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            assertTrue(relevantPlayerIDs.size() > 0);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkMinRating() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(4);
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assertTrue(testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getInt("rating") >= minRating);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkMaxRating() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(4);
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assertTrue(testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getInt("rating") <= maxRating);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkRare() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(4);
            boolean rare = rand.nextBoolean();
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(rare, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assertEquals(rare, testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getBoolean("rare"));
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_getRelevantPlayerIDs_checkPosition() {
        JSONArray testPlayers;
        try {
            int minRating = rand.nextInt(75);
            int maxRating = rand.nextInt(100 - minRating) + minRating;
            String[] positions = {"Forward", "Midfield", "Defence", "GoalKeeper"};
            int position = rand.nextInt(4);
            testPlayers = testCard.getPlayersArray();
            ArrayList<String> relevantPlayerIDs = testCard.getRelevantPlayerIDs(true, minRating, maxRating, positions[position], testPlayers);
            for (int i = 0; i < relevantPlayerIDs.size(); i++) {
                assertEquals(positions[position], testPlayers.getJSONObject(Integer.parseInt(relevantPlayerIDs.get(i))).getString("position"));
            }
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(player, playersArray.getJSONObject(Integer.parseInt(id)));
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_playerID() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNames(player);
            assertEquals("0", testCard.playerID);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_displayName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNames(player);
            assertEquals("Hazard",testCard.displayName);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_firstName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNames(player);
            assertEquals("Eden", testCard.firstName);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNames_noDisplayName_lastName() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNames(player);
            assertEquals("Hazard", testCard.lastName);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("David Silva", testCard.displayName);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("Jimenez Silva", testCard.lastName);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateClubDetails_club() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateClubDetails(player);
            assertEquals("Chelsea", testCard.club);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateClubDetails_abbrClub() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateClubDetails(player);
            assertEquals("Chelsea", testCard.abbrClub);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateClubDetails_clubBadge() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateClubDetails(player);
            assertNotNull(testCard.clubBadge);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNationDetails_nation() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNationDetails(player);
            assertEquals("Belgium", testCard.nation);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNationDetails_abbrNation() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNationDetails(player);
            assertEquals("Belgium", testCard.abbrNation);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateNationDetails_nationFlag() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateNationDetails(player);
            assertNotNull(testCard.nationFlag);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populatePositionDetails_forward_position() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populatePositionDetails(player);
            assertEquals("Forward", testCard.playerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populatePositionDetails_forward_abbrPosition() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populatePositionDetails(player);
            assertEquals("FWD",testCard.abbrPlayerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("Midfield", testCard.playerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("MID", testCard.abbrPlayerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("Defence", testCard.playerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("DEF", testCard.abbrPlayerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("GoalKeeper", testCard.playerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals("GK", testCard.abbrPlayerPosition);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_pace() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(90, testCard.pace);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_shooting() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(82, testCard.shooting);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_passing() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(84, testCard.passing);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_dribbling() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(92, testCard.dribbling);
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
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(32, testCard.defending);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_heading() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(66, testCard.heading);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateAttributeDetails_rating() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.populateAttributeDetails(player);
            assertEquals(90, testCard.rating);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(83, testCard.diving);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(82, testCard.handling);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(82, testCard.kicking);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(86, testCard.reflexes);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(62, testCard.speed);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertEquals(80, testCard.positioning);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }


    @Test
    public void test_populateHeadshot() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.headshot = null;
            testCard.populateHeadshot(player);
            assertNotNull(testCard.headshot);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

    @Test
    public void test_populateBackground_over75_rare() {
        JSONArray playersArray;
        JSONObject player;
        try {
            playersArray = testCard.getPlayersArray();
            player = testCard.getJSONPlayer("0",playersArray);
            testCard.cardBackground = null;
            testCard.populateBackground(player);
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
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
            assertNotNull(testCard.cardBackground);
        } catch (JSONException e) {
            Log.i("ERROR", "The JSON File could not be read");
            assertTrue(false);
        }
    }

}

