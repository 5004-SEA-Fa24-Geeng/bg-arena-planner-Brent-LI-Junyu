package student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the IPlanner interface.
 * This class is responsible for filtering and sorting board games
 * based on different criteria.
 */
public class Planner implements IPlanner {
    // Original set of games (unmodified)
    private final Set<BoardGame> allGames;

    // Current filtered set of games
    private List<BoardGame> currentFilteredGames;

    // Delimiter for multiple filters
    private static final String FILTER_SEPARATOR = ",";

    /**
     * Constructor for the Planner.
     *
     * @param games The complete set of board games to filter
     */
    public Planner(Set<BoardGame> games) {
        this.allGames = games;
        this.currentFilteredGames = new ArrayList<>(games);
    }

    @Override
    public Stream<BoardGame> filter(String filter) {
        // Default sorting by name in ascending order
        return filter(filter, GameData.NAME, true);
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        // Default to ascending order
        return filter(filter, sortOn, true);
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        // Reset to current filtered games if starting a new filter chain
        if (filter == null || filter.trim().isEmpty()) {
            return sortGames(currentFilteredGames.stream(), sortOn, ascending);
        }

        // Split the filter string by commas
        String[] filterParts = filter.trim().split(FILTER_SEPARATOR);
        Stream<BoardGame> result = currentFilteredGames.stream();

        // Apply each filter part sequentially
        for (String part : filterParts) {
            result = applyFilter(result, part.trim());
        }

        // Update the current filtered games
        currentFilteredGames = result.collect(Collectors.toList());

        // Sort and return the filtered games
        return sortGames(currentFilteredGames.stream(), sortOn, ascending);
    }

    @Override
    public void reset() {
        // Reset to the original set of games
        currentFilteredGames = new ArrayList<>(allGames);
    }

    /**
     * Applies a single filter to the stream of board games.
     *
     * @param games The stream of board games to filter
     * @param filterExpression The filter expression to apply
     * @return A filtered stream of board games
     */
    private Stream<BoardGame> applyFilter(Stream<BoardGame> games, String filterExpression) {
        // Get the operator from the filter expression
        Operations operator = Operations.getOperatorFromStr(filterExpression);
        if (operator == null) {
            return games; // No valid operator found
        }

        // Split the filter expression by the operator
        String[] parts = filterExpression.split(operator.getOperator());
        if (parts.length != 2) {
            return games; // Invalid filter format
        }

        try {
            // Get the column to filter on
            GameData column = GameData.fromString(parts[0].trim());
            String value = parts[1].trim();

            // Apply the filter based on the column type
            return games.filter(game -> matchesFilter(game, column, operator, value));
        } catch (IllegalArgumentException e) {
            // Invalid column name or value
            return games;
        }
    }

    /**
     * Determines if a board game matches a filter condition.
     *
     * @param game The board game to check
     * @param column The column to filter on
     * @param operator The operator to use
     * @param value The value to compare against
     * @return True if the game matches the filter, false otherwise
     */
    private boolean matchesFilter(BoardGame game, GameData column, Operations operator, String value) {
        switch (column) {
            case NAME:
                return matchesStringFilter(game.getName(), operator, value);
            case RATING:
                return matchesNumericFilter(game.getRating(), operator, value);
            case DIFFICULTY:
                return matchesNumericFilter(game.getDifficulty(), operator, value);
            case RANK:
                return matchesNumericFilter(game.getRank(), operator, value);
            case MIN_PLAYERS:
                return matchesNumericFilter(game.getMinPlayers(), operator, value);
            case MAX_PLAYERS:
                return matchesNumericFilter(game.getMaxPlayers(), operator, value);
            case MIN_TIME:
                return matchesNumericFilter(game.getMinPlayTime(), operator, value);
            case MAX_TIME:
                return matchesNumericFilter(game.getMaxPlayTime(), operator, value);
            case YEAR:
                return matchesNumericFilter(game.getYearPublished(), operator, value);
            default:
                return false;
        }
    }

    /**
     * Checks if a string value matches a filter condition.
     *
     * @param actual The actual string value
     * @param operator The operator to use
     * @param expected The expected string value
     * @return True if the actual value matches the condition, false otherwise
     */
    private boolean matchesStringFilter(String actual, Operations operator, String expected) {
        // Case-insensitive comparison
        String actualLower = actual.toLowerCase();
        String expectedLower = expected.toLowerCase();

        return switch (operator) {
            case EQUALS -> actualLower.equals(expectedLower);
            case NOT_EQUALS -> !actualLower.equals(expectedLower);
            case CONTAINS -> actualLower.contains(expectedLower);
            case GREATER_THAN -> actualLower.compareTo(expectedLower) > 0;
            case LESS_THAN -> actualLower.compareTo(expectedLower) < 0;
            case GREATER_THAN_EQUALS -> actualLower.compareTo(expectedLower) >= 0;
            case LESS_THAN_EQUALS -> actualLower.compareTo(expectedLower) <= 0;
        };
    }

    /**
     * Checks if a numeric value matches a filter condition.
     *
     * @param actual The actual numeric value
     * @param operator The operator to use
     * @param expected The expected numeric value as a string
     * @return True if the actual value matches the condition, false otherwise
     */
    private boolean matchesNumericFilter(double actual, Operations operator, String expected) {
        try {
            double expectedValue = Double.parseDouble(expected);

            return switch (operator) {
                case EQUALS -> actual == expectedValue;
                case NOT_EQUALS -> actual != expectedValue;
                case GREATER_THAN -> actual > expectedValue;
                case LESS_THAN -> actual < expectedValue;
                case GREATER_THAN_EQUALS -> actual >= expectedValue;
                case LESS_THAN_EQUALS -> actual <= expectedValue;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false; // Invalid numeric value
        }
    }

    /**
     * Checks if an integer value matches a filter condition.
     *
     * @param actual The actual integer value
     * @param operator The operator to use
     * @param expected The expected integer value as a string
     * @return True if the actual value matches the condition, false otherwise
     */
    private boolean matchesNumericFilter(int actual, Operations operator, String expected) {
        try {
            int expectedValue = Integer.parseInt(expected);

            return switch (operator) {
                case EQUALS -> actual == expectedValue;
                case NOT_EQUALS -> actual != expectedValue;
                case GREATER_THAN -> actual > expectedValue;
                case LESS_THAN -> actual < expectedValue;
                case GREATER_THAN_EQUALS -> actual >= expectedValue;
                case LESS_THAN_EQUALS -> actual <= expectedValue;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return false; // Invalid integer value
        }
    }

    /**
     * Sorts a stream of board games based on a column and direction.
     *
     * @param games The stream of board games to sort
     * @param sortOn The column to sort on
     * @param ascending Whether to sort in ascending order
     * @return A sorted stream of board games
     */
    private Stream<BoardGame> sortGames(Stream<BoardGame> games, GameData sortOn, boolean ascending) {
        Comparator<BoardGame> comparator = createComparator(sortOn, ascending);
        return games.sorted(comparator);
    }

    /**
     * Creates a comparator for sorting board games.
     *
     * @param sortOn The column to sort on
     * @param ascending Whether to sort in ascending order
     * @return A comparator for board games
     */
    private Comparator<BoardGame> createComparator(GameData sortOn, boolean ascending) {
        Comparator<BoardGame> comparator = switch (sortOn) {
            case NAME -> Comparator.comparing(game -> game.getName().toLowerCase());
            case RATING -> Comparator.comparing(BoardGame::getRating);
            case DIFFICULTY -> Comparator.comparing(BoardGame::getDifficulty);
            case RANK -> Comparator.comparing(BoardGame::getRank);
            case MIN_PLAYERS -> Comparator.comparing(BoardGame::getMinPlayers);
            case MAX_PLAYERS -> Comparator.comparing(BoardGame::getMaxPlayers);
            case MIN_TIME -> Comparator.comparing(BoardGame::getMinPlayTime);
            case MAX_TIME -> Comparator.comparing(BoardGame::getMaxPlayTime);
            case YEAR -> Comparator.comparing(BoardGame::getYearPublished);
            default -> Comparator.comparing(game -> game.getName().toLowerCase());
        };

        return ascending ? comparator : comparator.reversed();
    }
}

