package Animals;

import Island.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Animal implements Runnable {
    private static int countCreatedAnimals = 0;
    private static int countOfLiveAnimals = 0;
    private String picture;
    private String name;
    private double weight;
    private int maxAmountPerCell;
    private double kgFoodForSaturation;
    private double kgFoodInTheStomach;
    private int maxStepsPerMove;
    private boolean isAlive = true;
    private boolean isPregnant = false;
    private int x;
    private int y;


    public Animal() {
        countCreatedAnimals++;
        countOfLiveAnimals++;
        createAndSetCommonParameters();
    }

    public Animal(String picture, String name, double weight, double kgFoodForSaturation, double kgFoodInTheStomach, int maxStepsPerMove, int maxAmountPerCell) {
        countCreatedAnimals++;
        countOfLiveAnimals++;
        this.picture = picture;
        this.name = name;
        this.weight = weight;
        this.kgFoodForSaturation = kgFoodForSaturation;
        this.kgFoodInTheStomach = kgFoodInTheStomach;
        this.maxStepsPerMove = maxStepsPerMove;
        this.maxAmountPerCell = maxAmountPerCell;
    }

    public void eat(Cell cell) {
        if(cell.getListOfAnimals().size() < 2)
            return;
        AtomicInteger chanceToKill = new AtomicInteger();
        if(!isSaturated()) {
            var animalsThatMeCanEat = Configuration.animalsThatCanEat.get(this.getClass());
            if(animalsThatMeCanEat != null && animalsThatMeCanEat.size() > 0) {
                cell.getListOfAnimals().forEach(animalTryToEat -> {
                    int probabilityToEat = 0;
                    if(!this.equals(animalTryToEat)
                            && !isSaturated()
                            && animalsThatMeCanEat.get(animalTryToEat.getClass()) != null
                            && animalsThatMeCanEat.get(animalTryToEat.getClass()) > 0) {
                        System.out.println("####" + this.getName() + " #### try to eat: " + animalTryToEat.getName());
                        probabilityToEat = animalsThatMeCanEat.get(animalTryToEat.getClass());
                        chanceToKill.set(ThreadLocalRandom.current().nextInt(0, 100 + 1));
                        System.out.println("probabilityToEat: " + probabilityToEat + "; chanceToKill: " + chanceToKill.get());
                        int chanceToKillInt = chanceToKill.get();
                        if(chanceToKillInt <= probabilityToEat) {
                            System.out.println("####  Wow! " + this.getName() + " #### ate: " + animalTryToEat.getName());
                            this.kgFoodInTheStomach += animalTryToEat.getWeight();
                            animalTryToEat.setAlive(false);
                            cell.remove(animalTryToEat);
                            Island.listOfAliveAnimals.remove(animalTryToEat);
                            countOfLiveAnimals--;
                        }
                    }
                });
            }
        }
    }

    public void move() {
        if(maxStepsPerMove == 0)
            return;
        //generate stepForThisMove from 1 to include maxStepsPerMove
        int stepForThisMove = ThreadLocalRandom.current().nextInt(1, maxStepsPerMove + 1);
        //0 - up, 1 - rigth, 2 - down, 3 - left
        int direction = ThreadLocalRandom.current().nextInt(0, 4);
        switch(direction) {
            case 0 -> {
                y -= stepForThisMove;
                y = (y < 0) ? -y : y;
            }
            case 1 -> {
                x += stepForThisMove;
                x = (x > (Configuration.maxX - 1)) ? Configuration.maxX - 1 : x;
            }
            case 2 -> {
                y += stepForThisMove;
                y = (y > (Configuration.maxY - 1)) ? Configuration.maxY - 1 : y;
            }
            case 3 -> {
                x -= stepForThisMove;
                x = (x < 0) ? -x : x;
            }
        }
    }

    public boolean isSaturated() {
        if(kgFoodForSaturation > kgFoodInTheStomach)
            return false;
        return true;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public int getMaxAmountPerCell() {
        return maxAmountPerCell;
    }

    public void setMaxAmountPerCell(int maxAmountPerCell) {
        this.maxAmountPerCell = maxAmountPerCell;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public double getKgFoodForSaturation() {
        return kgFoodForSaturation;
    }

    public void setKgFoodForSaturation(double kgFoodForSaturation) {
        this.kgFoodForSaturation = kgFoodForSaturation;
    }

    public double getKgFoodInTheStomach() {
        return kgFoodInTheStomach;
    }

    public void setKgFoodInTheStomach(double kgFoodInTheStomach) {
        this.kgFoodInTheStomach = kgFoodInTheStomach;
    }

    public void setMaxStepsPerMove(int maxStepsPerMove) {
        this.maxStepsPerMove = maxStepsPerMove;
    }

    public int getMaxStepsPerMove() {
        return maxStepsPerMove;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isPregnant() {
        return isPregnant;
    }

    public void setPregnant(boolean pregnant) {
        isPregnant = pregnant;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static int getCountCreatedAnimals() {
        return countCreatedAnimals;
    }

    public static int getCountOfLiveAnimals() {
        return countOfLiveAnimals;
    }

    @Override
    public String toString() {
        String type = "unknown";
        if(this instanceof HerbivorousAnimal)
            type = "herbivorous";
        if(this instanceof PredatorAnimal)
            type = "predator";

        return "Animal{" +
                "class: '" + this.getClass().getSimpleName() + '\'' +
                ", " + picture +
                ", name='" + name + '\'' +
                ", x = " + getX() +
                ", y = " + getY() +
                ", weight=" + weight +
                " kg, type: " + type +
                ", maxAmountPerCell=" + maxAmountPerCell +
                ", maxStepsPerMove=" + maxStepsPerMove +
                ", kgFoodForSaturation=" + kgFoodForSaturation +
                " kg, kgFoodInTheStomach=" + kgFoodInTheStomach +
                " kg, isAlive = " + isAlive +
                ", isPregnant = " + isPregnant +
                "}";
    }

    private void createAndSetCommonParameters() {
        setName(getClass().getSimpleName().toLowerCase() + "::creature#" + countCreatedAnimals);
        setPicture(Configuration.getPicture(this.getClass()));
        setWeight(Configuration.getWeight(this.getClass()));
        setMaxAmountPerCell(Configuration.getMaxAmountPerCell(this.getClass()));
        setMaxStepsPerMove(Configuration.getMaxStepsPerMove(this.getClass()));
        setKgFoodForSaturation(Configuration.getKgFoodForSaturation(this.getClass()));
    }

    private static <K, V> void printMap(Map<K, V> map) {
        map.forEach((key, value) -> {
            System.out.println(key + " ::: " + value);
        });
    }
}