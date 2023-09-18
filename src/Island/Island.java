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
    }

    public static boolean getPermissionToMove(Animal animal, int supposedX, int supposedY) {
        if(islandMap[supposedY][supposedX].howManyAnimalsOfThisClassInTheCell(animal.getClass()) >= animal.getMaxAmountPerCell())
            return false;
        return true;
    }

    private void runGame() {
        for (int k = 0; k < Configuration.maxLifeCycles; k++) {
            Statistics.sendMessage(System.lineSeparator() + "=========== Life Cycle #" + k + " =============");
            for(Animal animal : listOfAliveAnimals) {
                if(animal.isAlive()) {
                    if(!animal.isProduceOffspring())
                        moveAnimalToOtherCell(animal);
                    else
                        animal.setProduceOffspring(false);
                    animal.eat(islandMap[animal.getY()][animal.getX()]);
                    animal.deathCheck(islandMap[animal.getY()][animal.getX()]);
                    if(!animal.isProduceOffspring())
                        animal.reproduce(islandMap[animal.getY()][animal.getX()]);
                    animal.reducingFoodInTheStomachPerCycle();
                    Statistics.sendMessage(animal.toString());
                }
            };

            if(k < Configuration.maxLifeCycles - 1) {
                growPlantsInEachCellPerCycle();
                Statistics.printPlantsInAllCells();
            }
        }
        Statistics.printStatisticsAtTheEnd();
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

        //Create random herbovorous in random cells
        Statistics.sendMessage("========= Create herbivorous in random cells: =========");
        createAnimalsAndPutToIslandAndListOfAllAnimals(allHerbivorousClass, Configuration.herbivorousToCreate);
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

    public static Animal createNewAnimalAndPutItToIslandAndAliveAnimalList(Class clazz, int x, int y) {
        Animal animal = null;
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
    private void moveAnimalToOtherCell(Animal animal) {
        if(!animal.isMovable())
            return;

        islandMap[animal.getY()][animal.getX()].remove(animal);
        int oldX = animal.getX();
        int oldY = animal.getY();
        animal.move();
        islandMap[animal.getY()][animal.getX()].add(animal);
        String beginString = "### MOVE!!! " + animal.getName();
        String endString = (animal.isFullySaturated()) ? " and saturated!" : String.format(", not saturated and will try to eat. In it cell now is %d other animal(s) and %.2f kg plants.", (islandMap[animal.getY()][animal.getX()].getListOfAnimals().size() - 1), islandMap[animal.getY()][animal.getX()].getQuantityPlants());
        if(oldX != animal.getX() || oldY != animal.getY())
            Statistics.sendMessage(beginString + " moved from (x:" + oldX + ", y:" + oldY + ") to (x:" + animal.getX() + ", y:" + animal.getY() + ")" + endString);
        else
            Statistics.sendMessage(beginString + " didn't move" + endString);
    }

    private static void growPlantsInEachCellPerCycle() {
        Arrays.stream(islandMap)
                .forEach(arr -> Arrays.stream(arr)
                        .forEach(Cell::multiplyPlantsPerCycle));
    }

}
