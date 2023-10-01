package Island;

import Animals.*;

public class Statistics {

    public static void printPlantsInAllCells() {
        sendMessage("============= Kg plant in cells: =============");
        String numberString;
        int lengthMaxY = String.valueOf(Configuration.maxY).length();
        int countUnderline = 0;
        sendMessageWithoutLineSeparator(" ".repeat(lengthMaxY + 1) + "|| ");
        for (int j = 0; j < Configuration.maxX; j++) {
            numberString = " ".repeat(4 - String.valueOf(j).length()) + j + "   ";
            if(j < Configuration.maxX - 1)
                numberString += "| ";
            countUnderline += numberString.length();
            sendMessageWithoutLineSeparator(numberString);
        }
        sendMessage("");
        sendMessage("=".repeat(countUnderline + 4));

        for (int i = 0; i < Configuration.maxY; i++) {
            numberString = " ".repeat(lengthMaxY - String.valueOf(i).length()) + i;
            sendMessageWithoutLineSeparator(numberString + " || ");
            for (int j = 0; j < Configuration.maxX; j++) {
                numberString = String.format("%.2f", Island.islandMap[i][j].getQuantityPlants());
                numberString = " ".repeat(6 - numberString.length()) + numberString;
                sendMessageWithoutLineSeparator(numberString);
                if(j < Configuration.maxX - 1)
                    sendMessageWithoutLineSeparator(" | ");
            }
            sendMessage("");
        }
        sendMessage("");
    }

    public static void printStatisticsAtTheEnd() {
        sendMessage("-------------------------------------------");
        sendMessage("-------------------------------------------");
        sendMessage("-------------------------------------------");
        sendMessage("!!!!!!!!!!!!!!!!!!!!!! Statistics at the very end: !!!!!!!!!!!!!!!!!!!!!!");
        printPlantsInAllCells();
        sendMessage("============Alive animals: =================");
        Island.listOfAliveAnimals.stream()
                .forEach(animal -> sendMessage(animal.toString()));
        sendMessage("-------------------------------------------");
        sendMessage("Animals were created at the very beginning: " + (Configuration.predatorsToCreate + Configuration.herbivorousToCreate));
        sendMessage("Animals were created and born: " + Animal.getCountCreatedAnimals());
        sendMessage("Animals that were born: " + Animal.getCountOfBornAnimals());
        sendMessage("Animals were eaten: " + Animal.getCountOfEatenAnimals());
        sendMessage("Animals that died of starvation: " + Animal.getCountOfDiedOfStarvationAnimals());
        sendMessage("Alive animals at the end: " + Animal.getCountOfLiveAnimals());
    }

    public static void sendMessage(String message) {
        sendMessageWithoutLineSeparator(message);
        sendMessageWithoutLineSeparator(System.lineSeparator());
    }

    public static void sendMessageWithoutLineSeparator(String message) {
        System.out.print(message);
    }

}
