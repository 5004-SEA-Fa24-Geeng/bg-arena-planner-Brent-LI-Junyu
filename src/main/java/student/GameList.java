package student;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the IGameList interface.
 * Manages a list of games that a user wants to play.
 */
public class GameList implements IGameList {

    private final Set<BoardGame> games;

    /**
     * Constructor for the GameList.
     */
    public GameList() {
        this.games = new HashSet<>();
    }

    @Override
    public List<String> getGameNames() {
        return games.stream()
                .map(BoardGame::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int count() {
        return games.size();
    }

    @Override
    public void saveGame(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            List<String> sortedNames = getGameNames();
            for (String name : sortedNames) {
                writer.println(name);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game list to file: " + filename, e);
        }
    }

    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty input string");
        }

        str = str.trim();

        // Convert stream to list to avoid consumption issues
        List<BoardGame> gamesList = filtered.collect(Collectors.toList());

        // Check if the command is to add all games
        if (str.equalsIgnoreCase(ADD_ALL)) {
            games.addAll(gamesList);
            return;
        }

        // Check if str is a number or range
        if (str.matches("\\d+") || str.matches("\\d+-\\d+")) {
            addGamesByNumbers(str, gamesList);
        } else {
            // Assume str is a game name
            addGameByName(str, gamesList);
        }
    }

    /**
     * Add games to the list by numbers or range.
     *
     * @param str      the string representing a number or range
     * @param gamesList the list of filtered games
     * @throws IllegalArgumentException if the number or range is invalid
     */
    private void addGamesByNumbers(String str, List<BoardGame> gamesList) throws IllegalArgumentException {
        if (str.contains("-")) {
            // Handle range
            String[] parts = str.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid range format: " + str);
            }

            try {
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);

                if (start < 1 || end > gamesList.size() || start > end) {
                    throw new IllegalArgumentException("Invalid range: " + str);
                }

                for (int i = start; i <= end; i++) {
                    games.add(gamesList.get(i - 1)); // -1 because list is 0-indexed
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number in range: " + str);
            }
        } else {
            // Handle single number
            try {
                int index = Integer.parseInt(str);

                if (index < 1 || index > gamesList.size()) {
                    throw new IllegalArgumentException("Invalid game number: " + index);
                }

                games.add(gamesList.get(index - 1)); // -1 because list is 0-indexed
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + str);
            }
        }
    }

    /**
     * Add a game to the list by name.
     *
     * @param name     the name of the game
     * @param gamesList the list of filtered games
     * @throws IllegalArgumentException if the game is not found
     */
    private void addGameByName(String name, List<BoardGame> gamesList) throws IllegalArgumentException {
        List<BoardGame> matchingGames = gamesList.stream()
                .filter(game -> game.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (matchingGames.isEmpty()) {
            throw new IllegalArgumentException("Game not found: " + name);
        }

        // Add all matching games (though there should typically be only one)
        games.addAll(matchingGames);
    }

    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty input string");
        }

        str = str.trim();

        // Check if the command is to remove all games
        if (str.equalsIgnoreCase(ADD_ALL)) {
            clear();
            return;
        }

        // Check if str is a number or range
        if (str.matches("\\d+") || str.matches("\\d+-\\d+")) {
            removeGamesByNumbers(str);
        } else {
            // Assume str is a game name
            removeGameByName(str);
        }
    }

    /**
     * Remove games from the list by numbers or range.
     *
     * @param str the string representing a number or range
     * @throws IllegalArgumentException if the number or range is invalid
     */
    private void removeGamesByNumbers(String str) throws IllegalArgumentException {
        List<BoardGame> gamesList = new ArrayList<>(games);
        // Sort by name for consistent numbering
        gamesList.sort(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER));

        if (str.contains("-")) {
            // Handle range
            String[] parts = str.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid range format: " + str);
            }

            try {
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);

                if (start < 1 || end > gamesList.size() || start > end) {
                    throw new IllegalArgumentException("Invalid range: " + str);
                }

                Set<BoardGame> toRemove = new HashSet<>();
                for (int i = start; i <= end; i++) {
                    toRemove.add(gamesList.get(i - 1)); // -1 because list is 0-indexed
                }
                games.removeAll(toRemove);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number in range: " + str);
            }
        } else {
            // Handle single number
            try {
                int index = Integer.parseInt(str);

                if (index < 1 || index > gamesList.size()) {
                    throw new IllegalArgumentException("Invalid game number: " + index);
                }

                games.remove(gamesList.get(index - 1)); // -1 because list is 0-indexed
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + str);
            }
        }
    }

    /**
     * Remove a game from the list by name.
     *
     * @param name the name of the game
     * @throws IllegalArgumentException if the game is not found
     */
    private void removeGameByName(String name) throws IllegalArgumentException {
        List<BoardGame> matchingGames = games.stream()
                .filter(game -> game.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        if (matchingGames.isEmpty()) {
            throw new IllegalArgumentException("Game not found in list: " + name);
        }

        // Remove all matching games (though there should typically be only one)
        games.removeAll(matchingGames);
    }
}