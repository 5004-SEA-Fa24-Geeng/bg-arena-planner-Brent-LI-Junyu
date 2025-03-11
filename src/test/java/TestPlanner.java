import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.BoardGame;
import student.GameData;
import student.IPlanner;
import student.Planner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test for the Planner class.
 * Most tests use 7 games as per your setup.
 */
public class TestPlanner {

    private IPlanner planner;
    private Set<BoardGame> games;

    @BeforeEach
    public void setup() {
        games = new HashSet<>();
        // Add various games for testing - exact same games as in your original test
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));

        planner = new Planner(games);
    }

    @Test
    public void testFilterName() {
        List<BoardGame> filtered = planner.filter("name == Go").collect(Collectors.toList());
        assertEquals(1, filtered.size());
        assertEquals("Go", filtered.get(0).getName());
    }

    @Test
    public void testFilterByN() {
        // Filter games with "an" in the name
        List<BoardGame> result = planner.filter("name~=go").collect(Collectors.toList());

        // Check size and contents
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("Go")));
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("golang")));
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("GoRami")));
    }

    @Test
    public void testFilterByPl() {
        // Filter games with min players > 5
        List<BoardGame> result = planner.filter("minPlayers>5").collect(Collectors.toList());

        // Check size and contents
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("GoRami")));
        assertTrue(result.stream().anyMatch(g -> g.getName().equals("Monopoly")));
    }

    @Test
    public void testFilterWithSorting() {
        // Filter by name containing "go" and sort by year descending
        List<BoardGame> result = planner.filter("name~=go", GameData.YEAR, false).collect(Collectors.toList());

        // Should be 3 results in descending year order
        assertEquals(3, result.size());
        assertEquals("golang", result.get(0).getName()); // 2003
        assertEquals("GoRami", result.get(1).getName()); // 2002
        assertEquals("Go", result.get(2).getName()); // 2000
    }

    @Test
    public void testReset() {
        // Apply a filter
        planner.filter("maxPlayers<3");

        // Should be only one game
        assertEquals(1, planner.filter("").count());

        // Reset
        planner.reset();

        // Should have all games again
        assertEquals(8, planner.filter("").count());
    }

    @Test
    public void testProgressiveFiltering() {
        // First filter: games with "go" in name
        planner.filter("name~=go");

        // Should have 3 games
        assertEquals(3, planner.filter("").count());

        // Add second filter: min players >= 3
        planner.filter("minPlayers>=3");

        // Should now have just 1 game (GoRami)
        assertEquals(1, planner.filter("").count());

        List<BoardGame> result = planner.filter("").collect(Collectors.toList());
        assertEquals("GoRami", result.get(0).getName());
    }
}