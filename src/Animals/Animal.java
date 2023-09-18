package Animals;

import Island.*;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Animal implements Runnable {
    Map<Class<? extends Animal>, Integer> animalsThatMeCanEat;
    private static int countCreatedAnimals = 0;
    private static int countOfLiveAnimals = 0;
    private static int countOfBornAnimals = 0;
    private static int countOfDiedOfStarvationAnimals = 0;
    private static int countOfEatenAnimals = 0;
    private String picture;
    private String name;
    private String nameToPrint;
    private String nameClassToPrint;
    private double weight;
    private int maxAmountPerCell;
    private double kgFoodForSaturation;
    private double kgFoodInTheStomach;
    private int maxStepsPerMove;
    private int howManyDaysWasHungry = 0;
    private boolean isAlive = true;
    private boolean isMovable = true;
    private boolean isProduceOffspring = false;
    private int x;
    private int y;

    public Animal() {
        countCreatedAnimals++;
        countOfLiveAnimals++;
        createAndSetCommonParameters();
    }

    public void move() {
        if(!isMovable)
            return;

        boolean getPermissionToMove = false;
        int tryingToGetPermission = 0;

        while(!getPermissionToMove && (tryingToGetPermission < Configuration.maxTriesToGetPermission)) {
            int supposedX = x;
            int supposedY = y;

            //generate stepForThisMove from 1 to include maxStepsPerMove
            int stepForThisMove = ThreadLocalRandom.current().nextInt(1, maxStepsPerMove + 1);
            //direction: 0 - up, 1 - rigth, 2 - down, 3 - left
            switch(ThreadLocalRandom.current().nextInt(0, 4)) {
                case 0 -> supposedY = Math.abs(supposedY - stepForThisMove);
                case 1 -> supposedX = (supposedX + stepForThisMove > (Configuration.maxX - 1)) ? Configuration.maxX - 2 : supposedX + stepForThisMove;
                case 2 -> supposedY = (supposedY + stepForThisMove > (Configuration.maxY - 1)) ? Configuration.maxY - 2 : supposedY + stepForThisMove;
                case 3 -> supposedX = Math.abs(supposedX - stepForThisMove);
            }

            if(supposedX != x || supposedY != y) {
                tryingToGetPermission++;
                if(Island.getPermissionToMove(this, supposedX, supposedY)) {
                    getPermissionToMove = true;
                    x = supposedX;
                    y = supposedY;
                }
            }
        }
    }

    public void eat(Cell cell) {
        //try to eat animals
        if(!isFullySaturated() && isThereAnyAnimalMeCanEatInThisCell(cell) ) {
            //try to eat animals from cell
            cell.getListOfAnimals().forEach(animalMeTryToEat -> {
                if(!isFullySaturated() && isMeCanEatThisAnimal(animalMeTryToEat)) {
                    putEatenAnimalInMyStomach(animalMeTryToEat);
                    Statistics.sendMessage(String.format("EATING ANIMAL!!!  %s has eaten %s! Food in the stomach: %.2f.", getName(), animalMeTryToEat.getName(), kgFoodInTheStomach));
                    killAnimalAndRemoveFromAliveAndCellLists(animalMeTryToEat, cell);
                    countOfEatenAnimals++;
                }
            });
        }

        //try to eat plants
        if(!isFullySaturated()
                && isMeAllowedToEatPlants()
                && cell.getQuantityPlants() > 0
        ) {
            double eatenPlants = mePutEatenPlantsInTheStomachAndRemoveItFromTheCellAndReturnValueOfEatenPlants(cell);
            Statistics.sendMessage(String.format("### EATING PLANTS!!! %s has eaten %.2f kg plant food! Food in the stomach: %.2f. Plants left in the cell: %.2f kg.", getName(), eatenPlants, kgFoodInTheStomach, cell.getQuantityPlants()));
        }
    }

    public void reproduce(Cell cell) {
        if(isHungry())
            return;

        cell.getListOfAnimals().forEach(animalMeTryToProduceOffspring -> {
            if(isCanProduceOffspringWithThisAnimal(cell, animalMeTryToProduceOffspring)) {
                Statistics.sendMessage("### REPRODUCING!!! " + getName() + " has produced offspring with: " + animalMeTryToProduceOffspring.getName());
                setProduceOffspring(true);
                animalMeTryToProduceOffspring.setProduceOffspring(true);
                Animal animal = Island.createNewAnimalAndPutItToIslandAndAliveAnimalList(getClass(), getX(), getY());
                //set true to newborn animal - that no one in this cycle tries to produce offspring with it
                animal.setProduceOffspring(true);
                countOfBornAnimals++;
                Statistics.sendMessage("### WAS BORN: " + animal);
            }
        });
    }

    public void deathCheck(Cell cell) {
        if(Configuration.kgFoodForSaturationAnimal.get(getClass()) == null) {
            Statistics.sendMessage("!!!!! Error! For " + this.getClass().getSimpleName() + "there's no kgFoodForSaturation parameter!!!");
            return;
        }

        if(Configuration.kgFoodForSaturationAnimal.get(this.getClass()) == 0)
            return;

        if(howManyDaysWasHungry >= Configuration.deathAfterHungryDays) {
            killAnimalAndRemoveFromAliveAndCellLists(this, cell);
            countOfDiedOfStarvationAnimals++;
            return;
        }
        if (isHungry()) {
            howManyDaysWasHungry++;
        }
    }

    public boolean isFullySaturated() {
        if(kgFoodForSaturation > kgFoodInTheStomach)
            return false;
        return true;
    }

    public boolean isHungry() {
        if(kgFoodInTheStomach > 0)
            return false;
        return true;
    }
    public void setPicture(String picture) {
        this.picture = picture;
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
        nameToPrint = name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setKgFoodForSaturation(double kgFoodForSaturation) {
        this.kgFoodForSaturation = kgFoodForSaturation;
    }

    public double getKgFoodInTheStomach() {
        return kgFoodInTheStomach;
    }

    public void setMaxStepsPerMove(int maxStepsPerMove) {
        this.maxStepsPerMove = maxStepsPerMove;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isProduceOffspring() {
        return isProduceOffspring;
    }

    public void setProduceOffspring(boolean produceOffspring) {
        isProduceOffspring = produceOffspring;
    }

    public boolean isMovable() {
        return isMovable;
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

    public static int getCountOfEatenAnimals() {
        return countOfEatenAnimals;
    }

    public static int getCountOfLiveAnimals() {
        return countOfLiveAnimals;
    }

    public static int getCountOfBornAnimals() {
        return countOfBornAnimals;
    }

    public static int getCountOfDiedOfStarvationAnimals() {
        return countOfDiedOfStarvationAnimals;
    }

    @Override
    public String toString() {
        String type = "unknown";
        if(this instanceof HerbivorousAnimal)
            type = "herbivor";
        if(this instanceof PredatorAnimal)
            type = "predator";

        int countAddSpaces = Configuration.maxClassNameLength - this.getClass().getSimpleName().length();
        if(countAddSpaces < 0)
            countAddSpaces = 0;

        StringBuilder s = new StringBuilder();

        return String.format("Animal{" +
                nameClassToPrint +
                ", name='" + nameToPrint + '\'' +
                ", x=" + " ".repeat((String.valueOf(getX()).length() == 1) ? 1 : 0) + getX() +
                ", y=" + " ".repeat((String.valueOf(getY()).length() == 1) ? 1 : 0) + getY() +
                ",  type=" + type +
                " " + picture +
                ",  foodInTheStomach= %.2f kg,  " +
                "isAlive=" + isAlive +
                ",  isProduceOffspring=" + isProduceOffspring +
                ", weight=" + weight +
                " kg, maxAmountPerCell=" + maxAmountPerCell +
                ", maxStepsPerMove=" + maxStepsPerMove +
                ", foodForSaturation=%.2f" +
                "kg, isMovable=" + isMovable +
                "}",
                kgFoodInTheStomach,
                kgFoodForSaturation);
    }

    private void createAndSetCommonParameters() {
        setName(getClass().getSimpleName().toLowerCase() + "::creature#" + countCreatedAnimals);
        setPicture(Configuration.getPicture(this.getClass()));
        setWeight(Configuration.getWeight(this.getClass()));
        setMaxAmountPerCell(Configuration.getMaxAmountPerCell(this.getClass()));
        setMaxStepsPerMove(Configuration.getMaxStepsPerMove(this.getClass()));
        setKgFoodForSaturation(Configuration.getKgFoodForSaturation(this.getClass()));
        animalsThatMeCanEat = Configuration.animalsThatCanEat.get(this.getClass());
        howManyDaysWasHungry = 0;
        setMovable();

        int addSpaces = Configuration.maxClassNameLength + 13 - name.length();
        nameToPrint = (addSpaces > 0) ? getClass().getSimpleName().toLowerCase() + ":".repeat(addSpaces) + "::creature#" + countCreatedAnimals : name;
        addSpaces = Configuration.maxClassNameLength - getClass().getSimpleName().length();
        nameClassToPrint = (addSpaces > 0) ? getClass().getSimpleName() + ":".repeat(addSpaces): getClass().getSimpleName();
    }

    private void setMovable() {
        if(Configuration.maxStepsPerMoveAnimal.get(this.getClass()) != null && Configuration.maxStepsPerMoveAnimal.get(this.getClass()) > 0)
            isMovable = true;
        else
            isMovable = false;
    }

    private void putEatenAnimalInMyStomach(Animal animalTryToEat) {
        kgFoodInTheStomach = (kgFoodInTheStomach + animalTryToEat.getWeight() > kgFoodForSaturation) ? kgFoodForSaturation : kgFoodInTheStomach + animalTryToEat.getWeight();
    }

    private void killAnimalAndRemoveFromAliveAndCellLists(Animal animalToKill, Cell cell) {
        animalToKill.setAlive(false);
        //remove eaten animal from general list of all animals in Island
        Island.listOfAliveAnimals.remove(animalToKill);
        countOfLiveAnimals--;
        //remove eaten animal from list in the cell
        cell.remove(animalToKill);
    }

    private boolean isThereAnyAnimalMeCanEatInThisCell(Cell cell) {
        if(cell.getListOfAnimals().size() > 1
                && animalsThatMeCanEat != null
                && animalsThatMeCanEat.size() > 0)
            return true;
        return false;
    }

    private boolean isMeCanEatThisAnimal(Animal animalMeTryToEat) {
        if(animalsThatMeCanEat.get(animalMeTryToEat.getClass()) != null
                && animalsThatMeCanEat.get(animalMeTryToEat.getClass()) > 0) {
            AtomicInteger chanceToKill = new AtomicInteger();
            chanceToKill.set(ThreadLocalRandom.current().nextInt(0, 100 + 1));
            int probabilityToEat = animalsThatMeCanEat.get(animalMeTryToEat.getClass());
            int chanceToKillInt = chanceToKill.get();
            if(chanceToKillInt <= probabilityToEat)
                return true;
        }
        return false;
    }

    private boolean isMeAllowedToEatPlants() {
        if(Animals.HerbivorousAnimal.class.isAssignableFrom(this.getClass()))
            return true;
        return false;
    }

    private double mePutEatenPlantsInTheStomachAndRemoveItFromTheCellAndReturnValueOfEatenPlants(Cell cell) {
        double howMuchAllowedToEatPlants = cell.howMuchAllowedToEatPlants(kgFoodForSaturation - kgFoodInTheStomach);
        double eatenPlants = howMuchAllowedToEatPlants;
        if(howMuchAllowedToEatPlants > 0) {
            if (kgFoodInTheStomach + howMuchAllowedToEatPlants > kgFoodForSaturation) {
                eatenPlants = kgFoodForSaturation - kgFoodInTheStomach;
                kgFoodInTheStomach = kgFoodForSaturation;
            } else {
                eatenPlants = howMuchAllowedToEatPlants;
                kgFoodInTheStomach += howMuchAllowedToEatPlants;
            }
            cell.reduceEatenPlants(eatenPlants);
        }
        return eatenPlants;
    }

    public void reducingFoodInTheStomachPerCycle() {
        double reduceFood = Configuration.foodMultiplierInTheStomachPerCycle * kgFoodForSaturation;
        kgFoodInTheStomach = (kgFoodInTheStomach - reduceFood > 0) ? kgFoodInTheStomach - reduceFood : 0;
    }

    private boolean isCanProduceOffspringWithThisAnimal(Cell cell, Animal animalMeTryToProduceOffspring) {
        if(!animalMeTryToProduceOffspring.isHungry()
                && this != animalMeTryToProduceOffspring
                && (getClass() == animalMeTryToProduceOffspring.getClass())
                && !animalMeTryToProduceOffspring.isProduceOffspring()
                && cell.howManyAnimalsOfThisClassInTheCell(getClass()) + 1 <= maxAmountPerCell)
            return true;
        return false;
    }
}