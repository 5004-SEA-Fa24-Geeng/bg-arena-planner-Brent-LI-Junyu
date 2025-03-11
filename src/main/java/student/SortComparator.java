package student;

import java.util.Comparator;

/**
 * Utility class that provides comparators for sorting BoardGame objects.
 * This class offers static methods to create different comparators
 * based on game attributes and sorting directions.
 */
public final class SortComparator {

    // Private constructor to prevent instantiation
    private SortComparator() {
        // Utility class should not be instantiated
    }

    /**
     * Creates a comparator for BoardGame objects based on the specified column and sort direction.
     *
     * @param column The game data column to sort by
     * @param ascending True for ascending order, false for descending
     * @return A comparator that can be used to sort BoardGame objects
     */
    public static Comparator<BoardGame> createComparator(GameData column, boolean ascending) {
        // Create base comparator
        Comparator<BoardGame> baseComparator = getColumnComparator(column);

        // Apply sort direction
        return ascending ? baseComparator : baseComparator.reversed();
    }

    /**
     * Gets a comparator for a specific column of BoardGame data.
     *
     * @param column The game data column to sort by
     * @return A comparator for the specified column
     */
    private static Comparator<BoardGame> getColumnComparator(GameData column) {
        return switch (column) {
            case NAME -> Comparator.comparing(game -> game.getName().toLowerCase());
            case RATING -> Comparator.comparing(BoardGame::getRating);
            case DIFFICULTY -> Comparator.comparing(BoardGame::getDifficulty);
            case RANK -> Comparator.comparing(BoardGame::getRank);
            case MIN_PLAYERS -> Comparator.comparing(BoardGame::getMinPlayers);
            case MAX_PLAYERS -> Comparator.comparing(BoardGame::getMaxPlayers);
            case MIN_TIME -> Comparator.comparing(BoardGame::getMinPlayTime);
            case MAX_TIME -> Comparator.comparing(BoardGame::getMaxPlayTime);
            case YEAR -> Comparator.comparing(BoardGame::getYearPublished);
            // For any other column, default to sorting by name
            default -> Comparator.comparing(game -> game.getName().toLowerCase());
        };
    }

    /**
     * Creates a name-based comparator that ignores case.
     *
     * @return A case-insensitive comparator for game names
     */
    public static Comparator<String> nameComparator() {
        return String.CASE_INSENSITIVE_ORDER;
    }
}

