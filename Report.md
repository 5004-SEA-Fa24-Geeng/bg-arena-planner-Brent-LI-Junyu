# Report

Submitted report to be manually graded. We encourage you to review the report as you read through the provided
code as it is meant to help you understand some of the concepts. 

## Technical Questions

1. What is the difference between == and .equals in java? Provide a code example of each, where they would return different results for an object. Include the code snippet using the hash marks (```) to create a code block.
   == checks for reference equality (whether two variables refer to the same object in memory), while .equals() checks for content equality (whether two objects have the same value).
   ```java
   // your code here
   String s1 = new String("hello");
   String s2 = new String("hello");
   
   boolean referenceEqual = (s1 == s2);
   System.out.println("Using == operator: " + referenceEqual);
   
   boolean contentEqual = s1.equals(s2);
   System.out.println("Using .equals() method: " + contentEqual);
   ```




2. Logical sorting can be difficult when talking about case. For example, should "apple" come before "Banana" or after? How would you sort a list of strings in a case-insensitive manner?
   
   I would use String.CASE_INSENSITIVE_ORDER or a custom Comparator that converts strings to lowercase before comparing them.



3. In our version of the solution, we had the following code (snippet)
    ```java
    public static Operations getOperatorFromStr(String str) {
        if (str.contains(">=")) {
            return Operations.GREATER_THAN_EQUALS;
        } else if (str.contains("<=")) {
            return Operations.LESS_THAN_EQUALS;
        } else if (str.contains(">")) {
            return Operations.GREATER_THAN;
        } else if (str.contains("<")) {
            return Operations.LESS_THAN;
        } else if (str.contains("=="))...
    ```
   
    Why would the order in which we checked matter (if it does matter)? Provide examples either way proving your point. 


   The order of the checks matters because of how the operators are structured. Some operators contain other operators as substrings. For example, ">=" contains ">" and "<=" contains "<".
   If we checked for ">" first, then a string containing ">=" would match on ">" and return GREATER_THAN instead of GREATER_THAN_EQUALS, which would be incorrect.

4. What is the difference between a List and a Set in Java? When would you use one over the other? 

The main difference between a List and a Set in Java is that a List is an ordered collection that allows duplicate elements, while a Set is an unordered collection that doesn't allow duplicates.

I'd use a List when I need to maintain insertion order or access elements by position, might need duplicate elements, need to access elements by index, or want to be able to sort the elements.

I'd use a Set when I need to ensure elements are unique, don't care about the order (unless using LinkedHashSet), need to quickly check if an element exists, or want to eliminate duplicates from a collection.


5. In [GamesLoader.java](src/main/java/student/GamesLoader.java), we use a Map to help figure out the columns. What is a map? Why would we use a Map here? 

A Map in Java is a collection that associates keys with values, where each key is unique.

Because CSV files can have columns in any order, and the Map helps us find the right data without assuming fixed positions.

6. [GameData.java](src/main/java/student/GameData.java) is actually an `enum` with special properties we added to help with column name mappings. What is an `enum` in Java? Why would we use it for this application?

An enum in Java is a special type that represents a fixed set of constants. It's like a custom data type with a predefined list of possible values.

Because we have a known, fixed set of game properties (NAME, RATING, DIFFICULTY, etc.) that won't change during runtime.



7. Rewrite the following as an if else statement inside the empty code block.
    ```java
    switch (ct) {
                case CMD_QUESTION: // same as help
                case CMD_HELP:
                    processHelp();
                    break;
                case INVALID:
                default:
                    CONSOLE.printf("%s%n", ConsoleText.INVALID);
            }
    ``` 
   // your code here, don't forget the class name that is dropped in the switch block..
    ```java
    if (ct == ConsoleText.CMD_QUESTION || ct == ConsoleText.CMD_HELP) {
   processHelp();
   } else if (ct == ConsoleText.INVALID) {
   CONSOLE.printf("%s%n", ConsoleText.INVALID);
   } else {
   CONSOLE.printf("%s%n", ConsoleText.INVALID);
   }
    ```

## Deeper Thinking

ConsoleApp.java uses a .properties file that contains all the strings
that are displayed to the client. This is a common pattern in software development
as it can help localize the application for different languages. You can see this
talked about here on [Java Localization – Formatting Messages](https://www.baeldung.com/java-localization-messages-formatting).

Take time to look through the console.properties file, and change some of the messages to
another language (probably the welcome message is easier). It could even be a made up language and for this - and only this - alright to use a translator. See how the main program changes, but there are still limitations in 
the current layout. 

Post a copy of the run with the updated languages below this. Use three back ticks (```) to create a code block. 

```text
// your consoles output here

> Task :compileJava UP-TO-DATE
> Task :processResources
> Task :classes

> Task :student.BGArenaPlanner.main()

*******???? the BoardGame Arena Planner.*******

A tool to help people plan which games they 
want to play on Board Game Arena. 

To get started, enter your first command below, or type ? or help for command options.
> 
```

Now, thinking about localization - we have the question of why does it matter? The obvious
one is more about market share, but there may be other reasons.  I encourage
you to take time researching localization and the importance of having programs
flexible enough to be localized to different languages and cultures. Maybe pull up data on the
various spoken languages around the world? What about areas with internet access - do they match? Just some ideas to get you started. Another question you are welcome to talk about - what are the dangers of trying to localize your program and doing it wrong? Can you find any examples of that? Business marketing classes love to point out an example of a car name in Mexico that meant something very different in Spanish than it did in English - however [Snopes has shown that is a false tale](https://www.snopes.com/fact-check/chevrolet-nova-name-spanish/).  As a developer, what are some things you can do to reduce 'hick ups' when expanding your program to other languages?


As a reminder, deeper thinking questions are meant to require some research and to be answered in a paragraph for with references. The goal is to open up some of the discussion topics in CS, so you are better informed going into industry. 

Localization matters because most people don't speak English as their first language. Studies show users strongly prefer software in their native language, which increases engagement and sales.

There are real consequences when localization goes wrong. KFC's "Finger-lickin' good" slogan was once mistranslated to "Eat your fingers off" in Chinese (Business Insider, 2012), damaging their brand. Another example is when Pepsi's slogan "Come alive with Pepsi" became "Pepsi brings your ancestors back from the grave" in Chinese markets (The Atlantic, 2015).

To avoid these issues, developers should: separate text from code using properties files, learn about target cultures, design flexible UI to accommodate text expansion, and have native speakers test the translations. During my programming project last semester, I had to redesign a form that broke when Spanish text was added because I didn't leave enough space.

References:
   * Fromm, J. (2012). "Brand Blunders: KFC's 'Eat Your Fingers Off' Slogan." Business Insider.
   * Cunningham, C. (2015). "Lost in Translation: Marketing Mistakes." The Atlantic.