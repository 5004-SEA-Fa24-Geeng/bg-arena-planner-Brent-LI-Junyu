package student;

import java.util.function.Predicate;

/**
 * Utility class for handling filtering operations on BoardGame objects.
 * Provides methods to create filter predicates based on different criteria.
 */
public final class FilterHandler {

    // Private constructor to prevent instantiation
    private FilterHandler() {
        // Utility class should not be instantiated
    }

    /**
     * Creates a filter predicate for BoardGame objects based on the specified criteria.
     *
     * @param column The game data column to filter on
     * @param operator The operation to apply
     * @param value The value to compare against
     * @return A predicate that can be used to filter BoardGame objects
     */
    public static Predicate<BoardGame> createFilter(GameData column, Operations operator, String value) {
        return switch (column) {
            case NAME -> createStringFilter(game -> game.getName(), operator, value);
            case RATING -> createDoubleFilter(BoardGame::getRating, operator, value);
            case DIFFICULTY -> createDoubleFilter(BoardGame::getDifficulty, operator, value);
            case RANK -> createIntFilter(BoardGame::getRank, operator, value);
            case MIN_PLAYERS -> createIntFilter(BoardGame::getMinPlayers, operator, value);
            case MAX_PLAYERS -> createIntFilter(BoardGame::getMaxPlayers, operator, value);
            case MIN_TIME -> createIntFilter(BoardGame::getMinPlayTime, operator, value);
            case MAX_TIME -> createIntFilter(BoardGame::getMaxPlayTime, operator, value);
            case YEAR -> createIntFilter(BoardGame::getYearPublished, operator, value);
            default -> game -> true; // Default: include all games
        };
    }

    /**
     * Creates a filter predicate for string values.
     *
     * @param extractor Function to extract the string value from a BoardGame
     * @param operator The operation to apply
     * @param value The value to compare against
     * @return A predicate that can be used to filter BoardGame objects
     */
    private static Predicate<BoardGame> createStringFilter(
            java.util.function.Function<BoardGame, String> extractor,
            Operations operator,
            String value) {

        // Convert value to lowercase for case-insensitive comparison
        String lowerValue = value.toLowerCase();

        return switch (operator) {
            case EQUALS -> game -> extractor.apply(game).toLowerCase().equals(lowerValue);
            case NOT_EQUALS -> game -> !extractor.apply(game).toLowerCase().equals(lowerValue);
            case CONTAINS -> game -> extractor.apply(game).toLowerCase().contains(lowerValue);
            case GREATER_THAN -> game -> extractor.apply(game).toLowerCase().compareTo(lowerValue) > 0;
            case GREATER_THAN_EQUALS -> game -> extractor.apply(game).toLowerCase().compareTo(lowerValue) >= 0;
            case LESS_THAN -> game -> extractor.apply(game).toLowerCase().compareTo(lowerValue) < 0;
            case LESS_THAN_EQUALS -> game -> extractor.apply(game).toLowerCase().compareTo(lowerValue) <= 0;
            default -> game -> true; // Default: include all games
        };
    }

    /**
     * Creates a filter predicate for integer values.
     *
     * @param extractor Function to extract the integer value from a BoardGame
     * @param operator The operation to apply
     * @param value The value to compare against
     * @return A predicate that can be used to filter BoardGame objects
     */
    private static Predicate<BoardGame> createIntFilter(
            java.util.function.ToIntFunction<BoardGame> extractor,
            Operations operator,
            String value) {

        try {
            int intValue = Integer.parseInt(value);

            return switch (operator) {
                case EQUALS -> game -> extractor.applyAsInt(game) == intValue;
                case NOT_EQUALS -> game -> extractor.applyAsInt(game) != intValue;
                case GREATER_THAN -> game -> extractor.applyAsInt(game) > intValue;
                case GREATER_THAN_EQUALS -> game -> extractor.applyAsInt(game) >= intValue;
                case LESS_THAN -> game -> extractor.applyAsInt(game) < intValue;
                case LESS_THAN_EQUALS -> game -> extractor.applyAsInt(game) <= intValue;
                default -> game -> true; // Default: include all games
            };
        } catch (NumberFormatException e) {
            // If value is not a valid integer, include no games
            return game -> false;
        }
    }

    /**
     * Creates a filter predicate for double values.
     *
     * @param extractor Function to extract the double value from a BoardGame
     * @param operator The operation to apply
     * @param value The value to compare against
     * @return A predicate that can be used to filter BoardGame objects
     */
    private static Predicate<BoardGame> createDoubleFilter(
            java.util.function.ToDoubleFunction<BoardGame> extractor,
            Operations operator,
            String value) {

        try {
            double doubleValue = Double.parseDouble(value);

            return switch (operator) {
                case EQUALS -> game -> extractor.applyAsDouble(game) == doubleValue;
                case NOT_EQUALS -> game -> extractor.applyAsDouble(game) != doubleValue;
                case GREATER_THAN -> game -> extractor.applyAsDouble(game) > doubleValue;
                case GREATER_THAN_EQUALS -> game -> extractor.applyAsDouble(game) >= doubleValue;
                case LESS_THAN -> game -> extractor.applyAsDouble(game) < doubleValue;
                case LESS_THAN_EQUALS -> game -> extractor.applyAsDouble(game) <= doubleValue;
                default -> game -> true; // Default: include all games
            };
        } catch (NumberFormatException e) {
            // If value is not a valid double, include no games
            return game -> false;
        }
    }

    /**
     * Parses a filter expression into its components.
     *
     * @param expression The filter expression to parse
     * @return An array containing [column, operator, value], or null if parsing fails
     */
    public static FilterComponents parseFilterExpression(String expression) {
        // Get the operator from the expression
        Operations operator = Operations.getOperatorFromStr(expression);
        if (operator == null) {
            return null;
        }

        // Split the expression by the operator
        String[] parts = expression.split(operator.getOperator());
        if (parts.length != 2) {
            return null;
        }

        try {
            // Get the column to filter on
            GameData column = GameData.fromString(parts[0].trim());
            String value = parts[1].trim();

            return new FilterComponents(column, operator, value);
        } catch (IllegalArgumentException e) {
            // Invalid column name
            return null;
        }
    }

    /**
     * Class to hold the components of a filter expression.
     */
    public static class FilterComponents {
        private final GameData column;
        private final Operations operator;
        private final String value;

        /**
         * Constructor for FilterComponents.
         *
         * @param column The game data column
         * @param operator The operation
         * @param value The value to compare against
         */
        public FilterComponents(GameData column, Operations operator, String value) {
            this.column = column;
            this.operator = operator;
            this.value = value;
        }

        /**
         * Gets the game data column.
         *
         * @return The game data column
         */
        public GameData getColumn() {
            return column;
        }

        /**
         * Gets the operation.
         *
         * @return The operation
         */
        public Operations getOperator() {
            return operator;
        }

        /**
         * Gets the value to compare against.
         *
         * @return The value
         */
        public String getValue() {
            return value;
        }
    }
}

