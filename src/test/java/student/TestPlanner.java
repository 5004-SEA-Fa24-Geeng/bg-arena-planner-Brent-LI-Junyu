package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.BoardGame;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import student.Planner;
import student.IPlanner;
import student.GameData;

/**
 * JUnit test for the Planner class.
 *
 * Tests various filtering and sorting functionalities.
 */
public class TestPlanner {
    Set<BoardGame> games;

    @BeforeEach
    public void setup() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));
    }

    @Test
    public void testFilterName() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name == Go").toList();
        assertEquals(1, filtered.size());
        assertEquals("Go", filtered.get(0).getName());
    }

    @Test
    public void testEmptyFilter() {
        // Test that empty filter returns all games
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("").toList();
        assertEquals(8, filtered.size());
    }

    @Test
    public void testFilterByMinPlayers() {
        // Test filtering by minimum players
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("minPlayers >= 6").toList();
        assertEquals(3, filtered.size());

        // Verify results contain expected games
        boolean foundMonopoly = false;
        boolean foundGorami = false;
        boolean foundTucano = false;

        for (BoardGame game : filtered) {
            if (game.getName().equals("Monopoly")) {
                foundMonopoly = true;
            } else if (game.getName().equals("GoRami")) {
                foundGorami = true;
            } else if (game.getName().equals("Tucano")) {
                foundTucano = true;
            }
        }

        assertTrue(foundMonopoly && foundGorami && foundTucano,
                "Results should include Monopoly, GoRami, and Tucano");
    }

    @Test
    public void testFilterByRating() {
        // Test filtering by rating
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("rating >= 9.0").toList();
        assertEquals(3, filtered.size());

        // Check first result with default name sorting
        assertEquals("17 days", filtered.get(0).getName());
    }

    @Test
    public void testFilterWithCustomSorting() {
        // Test filtering with custom sorting
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("minPlayers <= 2", GameData.YEAR, false).toList();

        // Verify descending sorting by year
        assertEquals(5, filtered.size());
        assertTrue(filtered.get(0).getYearPublished() >= filtered.get(1).getYearPublished(),
                "Games should be sorted in descending order by year");
    }

    @Test
    public void testMultipleFilters() {
        // Test applying multiple filters
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name ~= Go, maxPlayers <= 7").toList();
        assertEquals(3, filtered.size());
    }

    @Test
    public void testFilterReset() {
        // Test resetting filters
        IPlanner planner = new Planner(games);
        planner.filter("minPlayers >= 6");

        // Apply a filter and verify
        List<BoardGame> filtered1 = planner.filter("").toList();
        assertEquals(3, filtered1.size());

        // Reset filters and verify
        planner.reset();
        List<BoardGame> filtered2 = planner.filter("").toList();
        assertEquals(8, filtered2.size());
    }

    @Test
    public void testFilterWithInvalidCondition() {
        // Test filtering with invalid condition
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("invalidColumn > 5").toList();
        assertEquals(8, filtered.size(), "Invalid filter should return all games");
    }

    @Test
    public void testFilterByPlayTime() {
        // Test filtering by play time
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("maxPlayTime >= 100").toList();
        assertEquals(2, filtered.size());

        // Verify Go Fish and Monopoly are in the results
        boolean foundGoFish = false;
        boolean foundMonopoly = false;

        for (BoardGame game : filtered) {
            if (game.getName().equals("Go Fish")) {
                foundGoFish = true;
            } else if (game.getName().equals("Monopoly")) {
                foundMonopoly = true;
            }
        }

        assertTrue(foundGoFish && foundMonopoly,
                "Results should include Go Fish and Monopoly");
    }
}

