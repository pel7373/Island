package Island;

import Animals.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Island {
    public static List<Animal> listOfAliveAnimals = new CopyOnWriteArrayList<>();
    protected static Cell[][] islandMap = new Cell[Configuration.maxY][Configuration.maxX];

    public static void main(String[] args) {
        Island island = new Island();
        island.createGame();
        island.runGame();
        Statistics.printStatisticsAtTheEnd();
    }

    public static boolean getPermissionToMove(Animal animal, int supposedX, int supposedY) {
        return islandMap[supposedY][supposedX].howManyAnimalsOfThisClassInTheCell(animal.getClass()) < animal.getMaxAmountPerCell();
    }

    public static Animal createNewAnimalAndPutItToIslandAndAliveAnimalList(Class clazz, int x, int y) {
        Animal animal;
        try {
            animal = (Animal) clazz.getDeclaredConstructor().newInstance();
            animal.setX(x);
            animal.setY(y);
            listOfAliveAnimals.add(animal);
            islandMap[y][x].add(animal);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return animal;
    }

    public static void addAnimalToTheCell(Animal animal) {
        islandMap[animal.getY()][animal.getX()].add(animal);
    }

    public static void removeAnimalFromTheCell(Animal animal) {
        islandMap[animal.getY()][animal.getX()].remove(animal);
    }

    public static String getInfoFromCellWhatCanEat(int x, int y) {
        return String.format(", not saturated and will try to eat. In it cell now is %d other animal(s) and %.2f kg plants.", (islandMap[y][x].getListOfAnimals().size() - 1), islandMap[y][x].getQuantityPlants());
    }

    public static void killAnimalAndRemoveFromAliveAndCellLists(int x, int y, Animal animalToKill) {
        animalToKill.setAlive(false);
        //remove eaten animal from general list of all animals in Island
        listOfAliveAnimals.remove(animalToKill);
        //remove eaten animal from list in the cell
        islandMap[y][x].remove(animalToKill);
    }

    public static void reduceEatenPlantsFromTheCell(int x, int y, double eatenPlants) {
        islandMap[y][x].reduceEatenPlants(eatenPlants);
    }

    public static List<Animal> getListOfAnimalsInTheCell(int x, int y) {
        return islandMap[y][x].getListOfAnimals();
    }

    public static double getQuantityPlantsInTheCell(int x, int y) {
        return islandMap[y][x].getQuantityPlants();
    }

    public static double howMuchAllowedToEatPlantsInTheCell(int x, int y, double wantToEatPlant) {
        return islandMap[y][x].howMuchAllowedToEatPlants(wantToEatPlant);
    }

    public static int howManyAnimalsOfThisClassInTheCell(int x, int y, Class clazz) {
        return islandMap[y][x].howManyAnimalsOfThisClassInTheCell(clazz);
    }

    private void createGame() {
        //initialize islandMap
        for (int i = 0; i < Configuration.maxY; i++) {
            for (int j = 0; j < Configuration.maxX; j++) {
                islandMap[i][j] = new Cell();
            }
        }
        Statistics.printPlantsInAllCells();

        //Create lists of allHerbivorous and Predator Classes from map of pictures for all classes
        List<Class> allHerbivorousClass = new ArrayList<>();
        List<Class> allPredatorClass = new ArrayList<>();
        Configuration.pictureAnimal.forEach((key, value) -> {
            if (Animals.HerbivorousAnimal.class.isAssignableFrom(key))
                allHerbivorousClass.add(key);
            if (Animals.PredatorAnimal.class.isAssignableFrom(key))
                allPredatorClass.add(key);
        });

        //Create random predators in random cells
        Statistics.sendMessage("========= Create predators in random cells: =========");
        createAnimalsAndPutToIslandAndListOfAllAnimals(allPredatorClass, Configuration.predatorsToCreate);

        //Create random herbivorous in random cells
        Statistics.sendMessage("========= Create herbivorous in random cells: =========");
        createAnimalsAndPutToIslandAndListOfAllAnimals(allHerbivorousClass, Configuration.herbivorousToCreate);
    }

    private void runGame() {
        for (int k = 0; k < Configuration.maxLifeCycles; k++) {
            Statistics.sendMessage(System.lineSeparator() + "=========== Life Cycle #" + k + " =============");
            for(Animal animal : listOfAliveAnimals) {
                animal.move();
                animal.eat();
                animal.deathCheck();
                animal.reproduce();
                animal.reducingFoodInTheStomachPerCycle();
                if(animal.isAlive())
                    Statistics.sendMessage(animal.toString());
            }

            if(k < Configuration.maxLifeCycles - 1) {
                growPlantsInEachCellPerCycle();
                Statistics.printPlantsInAllCells();
            }
        }
    }

    private void createAnimalsAndPutToIslandAndListOfAllAnimals(List<Class> animalsClass, int maxAnimals) {
        int animalsCreated = 0;
        while (animalsCreated < maxAnimals) {
            int x = ThreadLocalRandom.current().nextInt(0, Configuration.maxX);
            int y = ThreadLocalRandom.current().nextInt(0, Configuration.maxY);
            int whatAnimalWillBeCreated = ThreadLocalRandom.current().nextInt(0, animalsClass.size());
            Animal animal = createNewAnimalAndPutItToIslandAndAliveAnimalList(animalsClass.get(whatAnimalWillBeCreated), x, y);
            animalsCreated++;
            Statistics.sendMessage("Was created : " + animal);
        }
    }

    private static void growPlantsInEachCellPerCycle() {
        Arrays.stream(islandMap)
                .forEach(arr -> Arrays.stream(arr)
                        .forEach(Cell::multiplyPlantsPerCycle));
    }
}