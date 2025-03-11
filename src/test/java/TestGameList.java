import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import student.BoardGame;
import student.GameList;
import student.IGameList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for the GameList class.
 */
public class TestGameList {

    private IGameList gameList;
    private BoardGame game1, game2, game3;

    @BeforeEach
    public void setup() {
        gameList = new GameList();

        // Create some test games
        game1 = new BoardGame("Catan", 1, 3, 4, 60, 120, 2.5, 1, 4.5, 1995);
        game2 = new BoardGame("Azul", 2, 2, 4, 30, 45, 1.8, 2, 4.8, 2017);
        game3 = new BoardGame("Pandemic", 3, 2, 4, 45, 60, 2.3, 3, 4.6, 2008);
    }

    @Test
    public void testEmptyList() {
        // A new list should be empty
        assertEquals(0, gameList.count());
        assertTrue(gameList.getGameNames().isEmpty());
    }

    @Test
    public void testAddByName() {
        // Add a game by name - Create a new stream each time we need one
        gameList.addToList("Catan", Arrays.asList(game1, game2, game3).stream());

        // Check that the game was added
        assertEquals(1, gameList.count());
        assertEquals("Catan", gameList.getGameNames().get(0));
    }

    @Test
    public void testAddByIndex() {
        // Add a game by index - Create a new stream each time we need one
        gameList.addToList("2", Arrays.asList(game1, game2, game3).stream());

        // Check that the game was added (Azul is the 2nd game, index 1)
        assertEquals(1, gameList.count());
        assertEquals("Azul", gameList.getGameNames().get(0));
    }

    @Test
    public void testAddByRange() {
        // Add games by range - Create a new stream each time we need one
        gameList.addToList("1-2", Arrays.asList(game1, game2, game3).stream());

        // Check that the games were added
        assertEquals(2, gameList.count());

        // Verify the games are in the correct order (alphabetical)
        List<String> gameNames = gameList.getGameNames();
        assertEquals("Azul", gameNames.get(0));
        assertEquals("Catan", gameNames.get(1));
    }

    @Test
    public void testAddAll() {
        // Add all games - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Check that all games were added
        assertEquals(3, gameList.count());

        // Verify the games are in the correct order (alphabetical)
        List<String> gameNames = gameList.getGameNames();
        assertEquals("Azul", gameNames.get(0));
        assertEquals("Catan", gameNames.get(1));
        assertEquals("Pandemic", gameNames.get(2));
    }

    @Test
    public void testRemoveByName() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Remove a game by name
        gameList.removeFromList("Catan");

        // Check that the game was removed
        assertEquals(2, gameList.count());
        List<String> gameNames = gameList.getGameNames();
        assertFalse(gameNames.contains("Catan"));
        assertTrue(gameNames.contains("Azul"));
        assertTrue(gameNames.contains("Pandemic"));
    }

    @Test
    public void testRemoveByIndex() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Remove a game by index (Azul is at index 0, but in position 1)
        gameList.removeFromList("1");

        // Check that the game was removed
        assertEquals(2, gameList.count());
        List<String> gameNames = gameList.getGameNames();
        assertFalse(gameNames.contains("Azul"));
        assertTrue(gameNames.contains("Catan"));
        assertTrue(gameNames.contains("Pandemic"));
    }

    @Test
    public void testRemoveByRange() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Remove games by range (positions 1-2 which are Azul and Catan)
        gameList.removeFromList("1-2");

        // Check that the games were removed
        assertEquals(1, gameList.count());
        List<String> gameNames = gameList.getGameNames();
        assertFalse(gameNames.contains("Azul"));
        assertFalse(gameNames.contains("Catan"));
        assertTrue(gameNames.contains("Pandemic"));
    }

    @Test
    public void testRemoveAll() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Remove all games
        gameList.removeFromList("all");

        // Check that all games were removed
        assertEquals(0, gameList.count());
        assertTrue(gameList.getGameNames().isEmpty());
    }

    @Test
    public void testClear() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Clear the list
        gameList.clear();

        // Check that the list is empty
        assertEquals(0, gameList.count());
        assertTrue(gameList.getGameNames().isEmpty());
    }

    @Test
    public void testInvalidAddOutOfRange() {
        // Try to add a game with an index out of range - Create a new stream each time we need one
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("4", Arrays.asList(game1, game2, game3).stream());
        });
    }

    @Test
    public void testInvalidRemoveOutOfRange() {
        // Add some games first - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Try to remove a game with an index out of range
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.removeFromList("4");
        });
    }

    @Test
    public void testAddDuplicateGame() {
        // Add a game - Create a new stream each time we need one
        gameList.addToList("Catan", Arrays.asList(game1, game2, game3).stream());

        // Try to add the same game again - Create a new stream each time we need one
        gameList.addToList("Catan", Arrays.asList(game1, game2, game3).stream());

        // Check that the game was only added once (because of the Set implementation)
        assertEquals(1, gameList.count());
    }

    @Test
    public void testSaveGame(@TempDir Path tempDir) throws IOException {
        // Add some games - Create a new stream each time we need one
        gameList.addToList("all", Arrays.asList(game1, game2, game3).stream());

        // Save the games to a file
        String filename = tempDir.resolve("test_games.txt").toString();
        gameList.saveGame(filename);

        // Read the file and check the contents
        List<String> lines = Files.readAllLines(new File(filename).toPath());
        assertEquals(3, lines.size());
        assertEquals("Azul", lines.get(0));
        assertEquals("Catan", lines.get(1));
        assertEquals("Pandemic", lines.get(2));
    }
}