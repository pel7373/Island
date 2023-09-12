package Island;

import Animals.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Island {
    public static List<Animal> listOfAliveAnimals = new CopyOnWriteArrayList<>();

    private int maxX = Configuration.maxX;
    private int maxY = Configuration.maxY;
    private int maxPredators = 5;
    private int maxHerbivorous = 10;
    Cell[][] islandMap = new Cell[maxY][maxX];

    public static void main(String[] args) {
        Island island = new Island();
        island.createGame();
        island.runGame();
        System.out.println("Animals were eaten: " + (Animal.getCountCreatedAnimals() - Animal.getCountOfLiveAnimals()));
        System.out.println("Animals were created: " + Animal.getCountCreatedAnimals());
    }

    private void runGame() {
        for (int k = 0; k < 5; k++) {
            System.out.println("=========== Cycle #" + k + " =============");
            listOfAliveAnimals.forEach(animal -> {
                moveAnimalToOtherCell(animal);
                if(!animal.isSaturated())
                    animal.eat(islandMap[animal.getY()][animal.getX()]);
            });
            for (int i = 0; i < maxY; i++) {
                for (int j = 0; j < maxX; j++) {
                    islandMap[i][j].multiplyPlantsPerCycle();
                }
            }
        }
    }

    private void moveAnimalToOtherCell(Animal animal) {
        islandMap[animal.getY()][animal.getX()].remove(animal);
        animal.move();
        islandMap[animal.getY()][animal.getX()].add(animal);
        System.out.println(animal);
    }

    private void createGame() {
        //initialize islandMap
        for (int i = 0; i < maxY; i++) {
            for (int j = 0; j < maxX; j++) {
                islandMap[i][j] = new Cell();
            }
        }

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
        System.out.println("========= Create predators in random cells: =========");
        createAnimalsAndPutToIslandMapAndListOfAllAnimals(allPredatorClass, maxPredators);

        //Create random herbovorous in random cells
        System.out.println("========= Create herbivorous in random cells: =========");
        createAnimalsAndPutToIslandMapAndListOfAllAnimals(allHerbivorousClass, maxHerbivorous);
    }

    private void createAnimalsAndPutToIslandMapAndListOfAllAnimals(List<Class> animalsClass, int maxAnimals) {
        int animalsCreated = 0;
        while (animalsCreated < maxAnimals) {
            int x = ThreadLocalRandom.current().nextInt(0, maxX);
            int y = ThreadLocalRandom.current().nextInt(0, maxY);
            int whatAnimalWillBeCreated = ThreadLocalRandom.current().nextInt(0, animalsClass.size());
            try {
                Animal animal = (Animal) animalsClass.get(whatAnimalWillBeCreated).getDeclaredConstructor().newInstance();
                animal.setX(x);
                animal.setY(y);
                animalsCreated++;
                System.out.println(animal);
                System.out.println("X: " + x + "; Y: " + y);
                islandMap[y][x].add(animal);
                listOfAliveAnimals.add(animal);
            } catch (InstantiationException | IllegalAccessException e) {
                //throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
