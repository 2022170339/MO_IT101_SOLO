package com.mycompany.motorph.helpers;

import java.util.ArrayList;

public class TextFileParser {
    public static ArrayList<String> ParseRow(String line) {
        ArrayList<String> items = new ArrayList<String>();
        int startPosition = 0;
        boolean isInQuotes = false;
        for (int currentPosition = 0; currentPosition < line.length(); currentPosition++) {
            if (line.charAt(currentPosition) == '\"') {
                isInQuotes = !isInQuotes;
            } else if (line.charAt(currentPosition) == ',' && !isInQuotes) {
                items.add(line.substring(startPosition, currentPosition).replaceAll(",", "").replaceAll("\"", "")
                        .strip());
                startPosition = currentPosition + 1;
            }
        }

        String lastItem = line.substring(startPosition);
        if (lastItem.equals(",")) {
            items.add("");
        } else {
            items.add(lastItem);
        }
        return items;
    }
}
