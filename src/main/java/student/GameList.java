package student;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the IGameList interface.
 * This class manages a list of games that a user wants to play.
 */
public class GameList implements IGameList {
    /** Set to store game names (no duplicates). */
    private final Set<String> gameNames;

    /** Range separator for adding/removing games by range. */
    private static final String RANGE_SEPARATOR = "-";

    /**
     * Constructor for GameList.
     * Initializes an empty game list.
     */
    public GameList() {
        this.gameNames = new HashSet<>();
    }

    @Override
    public List<String> getGameNames() {
        // Return a sorted list of game names (case insensitive)
        return gameNames.stream()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        // Remove all games from the list
        gameNames.clear();
    }

    @Override
    public int count() {
        // Return the number of games in the list
        return gameNames.size();
    }

    @Override
    public void saveGame(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write each game name on a new line
            List<String> sortedNames = getGameNames();
            for (String name : sortedNames) {
                writer.write(name);
                writer.newLine();
            }
            System.out.println("Game list saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game list: " + e.getMessage());
        }
    }

    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        // Validate input parameters
        if (str == null || filtered == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        // Trim input string
        String input = str.trim();

        // Convert filtered stream to list for multiple passes
        List<BoardGame> gamesList = filtered.collect(Collectors.toList());

        // Check if filtered list is empty
        if (gamesList.isEmpty()) {
            throw new IllegalArgumentException("No games available to add");
        }

        // Handle "all" option
        if (input.equalsIgnoreCase(ADD_ALL)) {
            for (BoardGame game : gamesList) {
                gameNames.add(game.getName());
            }
            return;
        }

        // Try to add by exact name match
        boolean nameFound = false;
        for (BoardGame game : gamesList) {
            if (game.getName().equalsIgnoreCase(input)) {
                gameNames.add(game.getName());
                nameFound = true;
                break;
            }
        }

        if (nameFound) {
            return;
        }

        // Try to add by range (e.g. "1-5")
        if (input.contains(RANGE_SEPARATOR)) {
            addByRange(input, gamesList);
            return;
        }

        // Try to add by index
        try {
            int index = Integer.parseInt(input);
            addByIndex(index, gamesList);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: " + input);
        }
    }

    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        // Trim input string
        String input = str.trim();

        // Handle "all" option (clear the list)
        if (input.equalsIgnoreCase(ADD_ALL)) {
            clear();
            return;
        }

        // Check if list is empty
        if (gameNames.isEmpty()) {
            throw new IllegalArgumentException("The game list is empty");
        }

        // Get current sorted list of games
        List<String> sortedNames = getGameNames();

        // Try to remove by exact name match
        boolean nameFound = false;
        for (String name : sortedNames) {
            if (name.equalsIgnoreCase(input)) {
                gameNames.remove(name);
                nameFound = true;
                break;
            }
        }

        if (nameFound) {
            return;
        }

        // Try to remove by range (e.g. "1-5")
        if (input.contains(RANGE_SEPARATOR)) {
            removeByRange(input, sortedNames);
            return;
        }

        // Try to remove by index
        try {
            int index = Integer.parseInt(input);
            removeByIndex(index, sortedNames);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: " + input);
        }
    }

    /**
     * Helper method to add games by range.
     *
     * @param range The range string (e.g. "1-5")
     * @param games The list of games to add from
     * @throws IllegalArgumentException If the range is invalid
     */
    private void addByRange(String range, List<BoardGame> games) throws IllegalArgumentException {
        String[] parts = range.split(RANGE_SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        try {
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            // Validate range
            if (start <= 0 || start > end) {
                throw new IllegalArgumentException("Invalid range values: " + range);
            }

            if (start > games.size()) {
                throw new IllegalArgumentException("Start index out of bounds: " + start);
            }

            // Adjust end if it exceeds list size
            if (end > games.size()) {
                end = games.size();
            }

            // Add games in range
            for (int i = start - 1; i < end; i++) {
                gameNames.add(games.get(i).getName());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }
    }

    /**
     * Helper method to add a game by index.
     *
     * @param index The index of the game to add (1-based)
     * @param games The list of games to add from
     * @throws IllegalArgumentException If the index is invalid
     */
    private void addByIndex(int index, List<BoardGame> games) throws IllegalArgumentException {
        if (index <= 0 || index > games.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }

        gameNames.add(games.get(index - 1).getName());
    }

    /**
     * Helper method to remove games by range.
     *
     * @param range The range string (e.g. "1-5")
     * @param names The sorted list of game names
     * @throws IllegalArgumentException If the range is invalid
     */
    private void removeByRange(String range, List<String> names) throws IllegalArgumentException {
        String[] parts = range.split(RANGE_SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        try {
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            // Validate range
            if (start <= 0 || start > end) {
                throw new IllegalArgumentException("Invalid range values: " + range);
            }

            if (start > names.size()) {
                throw new IllegalArgumentException("Start index out of bounds: " + start);
            }

            // Adjust end if it exceeds list size
            if (end > names.size()) {
                end = names.size();
            }

            // Remove games in range (from a copy to avoid concurrent modification)
            List<String> namesToRemove = new ArrayList<>();
            for (int i = start - 1; i < end; i++) {
                namesToRemove.add(names.get(i));
            }

            for (String name : namesToRemove) {
                gameNames.remove(name);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }
    }

    /**
     * Helper method to remove a game by index.
     *
     * @param index The index of the game to remove (1-based)
     * @param names The sorted list of game names
     * @throws IllegalArgumentException If the index is invalid
     */
    private void removeByIndex(int index, List<String> names) throws IllegalArgumentException {
        if (index <= 0 || index > names.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }

        String nameToRemove = names.get(index - 1);
        gameNames.remove(nameToRemove);
    }
}

