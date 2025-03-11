package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GameList implementation.
 * Tests functionality for managing lists of games.
 */
class GameListTest {
    private Set<BoardGame> games;
    private IGameList gameList;

    @BeforeEach
    void setup() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));

        gameList = new GameList();
    }

    @Test
    void testInitialCountIsZero() {
        assertEquals(0, gameList.count());
    }

    @Test
    void testAddSingleGameByName() {
        gameList.addToList("Chess", games.stream());
        assertEquals(1, gameList.count());
        assertEquals("Chess", gameList.getGameNames().get(0));
    }

    @Test
    void testAddGameByIndexOneBasedIndexing() {
        IPlanner planner = new Planner(games);
        List<BoardGame> sorted = planner.filter("", GameData.NAME, true).toList();

        // Adding game at index 3 (which is the third game in sorted list)
        gameList.addToList("3", sorted.stream());
        assertEquals(1, gameList.count());
        assertEquals(sorted.get(2).getName(), gameList.getGameNames().get(0));
    }

    @Test
    void testAddAllGames() {
        gameList.addToList("all", games.stream());
        assertEquals(8, gameList.count());
    }

    @Test
    void testRemoveGame() {
        // Add all games first
        gameList.addToList("all", games.stream());

        // Remove Chess
        gameList.removeFromList("Chess");
        assertEquals(7, gameList.count());

        // Verify Chess is no longer in the list
        List<String> names = gameList.getGameNames();
        for (String name : names) {
            assertNotEquals("Chess", name);
        }
    }

    @Test
    void testRemoveByIndex() {
        // Add all games first
        gameList.addToList("all", games.stream());

        // Get the first game name
        String firstName = gameList.getGameNames().get(0);

        // Remove the first game
        gameList.removeFromList("1");
        assertEquals(7, gameList.count());

        // Verify the first game is no longer in the list
        List<String> namesAfterRemoval = gameList.getGameNames();
        for (String name : namesAfterRemoval) {
            assertNotEquals(firstName, name);
        }
    }

    @Test
    void testRemoveByRange() {
        // Add all games first
        gameList.addToList("all", games.stream());

        // Remove games 2-4
        gameList.removeFromList("2-4");
        assertEquals(5, gameList.count());
    }

    @Test
    void testRemoveAll() {
        // Add all games first
        gameList.addToList("all", games.stream());

        // Remove all games
        gameList.removeFromList("all");
        assertEquals(0, gameList.count());
    }

    @Test
    void testAddByRange() {
        // Add games 1-3
        IPlanner planner = new Planner(games);
        List<BoardGame> sorted = planner.filter("", GameData.NAME, true).toList();

        gameList.addToList("1-3", sorted.stream());
        assertEquals(3, gameList.count());
    }

    @Test
    void testClear() {
        // Add some games
        gameList.addToList("all", games.stream());

        // Clear the list
        gameList.clear();
        assertEquals(0, gameList.count());
        assertTrue(gameList.getGameNames().isEmpty());
    }

    @Test
    void testAddInvalidGameName() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("Non-existent Game", games.stream());
        });
    }

    @Test
    void testAddInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("10", games.stream());
        });
    }

    @Test
    void testRemoveFromEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.removeFromList("Go");
        });
    }

    @Test
    void testCaseInsensitiveAdd() {
        gameList.addToList("go", games.stream());
        assertEquals(1, gameList.count());

        // The original case should be preserved
        assertEquals("Go", gameList.getGameNames().get(0));
    }

    @Test
    void testGetGameNamesAlphabeticalOrder() {
        // Add games in non-alphabetical order
        gameList.addToList("Tucano", games.stream());
        gameList.addToList("Chess", games.stream());
        gameList.addToList("Go", games.stream());

        // Verify games are returned in alphabetical order
        List<String> names = gameList.getGameNames();
        assertEquals(3, names.size());
        assertEquals("Chess", names.get(0));
        assertEquals("Go", names.get(1));
        assertEquals("Tucano", names.get(2));
    }

    @Test
    void testAddDuplicateGame() {
        // Add a game
        gameList.addToList("Go", games.stream());
        assertEquals(1, gameList.count());

        // Try to add the same game again
        gameList.addToList("Go", games.stream());
        assertEquals(1, gameList.count(), "Game should not be added twice");
    }
}

