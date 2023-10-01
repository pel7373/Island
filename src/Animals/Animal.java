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
        if(!isMovable() || !isAlive() || isProduceOffspring())
            return;

        Island.removeAnimalFromTheCell(this);

        int oldX = getX();
        int oldY = getY();

        tryToMove();
        Island.addAnimalToTheCell(this);

        //Write statistics
        String beginString = "### MOVE!!! " + getName();
        String endString = (isFullySaturated()) ? " and saturated!" : Island.getInfoFromCellWhatCanEat(getX(), getY());
        if(oldX != getX() || oldY != getY())
            Statistics.sendMessage(beginString + " moved from (x:" + oldX + ", y:" + oldY + ") to (x:" + getX() + ", y:" + getY() + ")" + endString);
        else
            Statistics.sendMessage(beginString + " didn't move" + endString);

    }

    public void eat() {
        //try to eat animals
        if(isAlive() && !isFullySaturated() && isThereAnyAnimalMeCanEatInThisCell() ) {
            //try to eat animals from the cell
            Island.getListOfAnimalsInTheCell(x, y).forEach(animalMeTryToEat -> {
                if(!isFullySaturated() && isMeCanEatThisAnimal(animalMeTryToEat)) {
                    putEatenAnimalInMyStomach(animalMeTryToEat);
                    Statistics.sendMessage(String.format("EATING ANIMAL!!!  %s has eaten %s! Food in the stomach: %.2f.", getName(), animalMeTryToEat.getName(), kgFoodInTheStomach));
                    Island.killAnimalAndRemoveFromAliveAndCellLists(x, y, animalMeTryToEat);
                    countOfLiveAnimals--;
                    countOfEatenAnimals++;
                }
            });
        }

        //try to eat plants
        if(isAlive()
                &&!isFullySaturated()
                && isMeAllowedToEatPlants()
                && Island.getQuantityPlantsInTheCell(x, y) > 0
        ) {
            double eatenPlants = putEatenPlantsInMyStomachAndRemoveItFromTheCellAndReturnValueOfEatenPlants();
            Statistics.sendMessage(String.format("### EATING PLANTS!!! %s has eaten %.2f kg plant food! Food in the stomach: %.2f kg.", getName(), eatenPlants, kgFoodInTheStomach));
        }
    }

    public void reproduce() {
        if(!isAlive || isHungry())
            return;

        if(isProduceOffspring()) {
            setProduceOffspring(false);
            return;
        }

        Island.getListOfAnimalsInTheCell(x, y).forEach(animalMeTryToProduceOffspring -> {
            if(isCanProduceOffspringWithThisAnimal(animalMeTryToProduceOffspring)) {
                Statistics.sendMessage("### REPRODUCING!!! " + getName() + " has produced offspring with: " + animalMeTryToProduceOffspring.getName());
                setProduceOffspring(true);
                animalMeTryToProduceOffspring.setProduceOffspring(true);
                Animal animal = Island.createNewAnimalAndPutItToIslandAndAliveAnimalList(getClass(), x, y);
                //set true to newborn animal - that no one in this cycle tries to produce offspring with it
                animal.setProduceOffspring(true);
                countOfBornAnimals++;
                Statistics.sendMessage("### WAS BORN: " + animal);
            }
        });
    }

    public void deathCheck() {
        if(Configuration.kgFoodForSaturationAnimal.get(getClass()) == null) {
            Statistics.sendMessage("!!!!! Error! For " + this.getClass().getSimpleName() + "there's no kgFoodForSaturation parameter!!!");
            return;
        }

        if(Configuration.kgFoodForSaturationAnimal.get(this.getClass()) == 0)
            return;

        if(howManyDaysWasHungry >= Configuration.deathAfterHungryDays) {
            Island.killAnimalAndRemoveFromAliveAndCellLists(x, y, this);
            countOfLiveAnimals--;
            countOfDiedOfStarvationAnimals++;
            return;
        }
        if (isHungry()) {
            howManyDaysWasHungry++;
        }
    }

    public boolean isFullySaturated() {
        return kgFoodForSaturation <= kgFoodInTheStomach;
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
        isMovable = Configuration.maxStepsPerMoveAnimal.get(this.getClass()) != null && Configuration.maxStepsPerMoveAnimal.get(this.getClass()) > 0;
    }

    private void putEatenAnimalInMyStomach(Animal animalTryToEat) {
        kgFoodInTheStomach = Math.min(kgFoodInTheStomach + animalTryToEat.getWeight(), kgFoodForSaturation);
    }

    private void tryToMove(){
        boolean getPermissionToMove = false;
        int tryingToGetPermission = 0;

        while(!getPermissionToMove && (tryingToGetPermission < Configuration.maxTriesToGetPermission)) {
            int supposedX = x;
            int supposedY = y;

            //generate stepForThisMove from 1 to include maxStepsPerMove
            int stepForThisMove = ThreadLocalRandom.current().nextInt(1, maxStepsPerMove + 1);
            //direction: 0 - up, 1 - right, 2 - down, 3 - left
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

    private boolean isThereAnyAnimalMeCanEatInThisCell() {
        return Island.getListOfAnimalsInTheCell(x, y).size() > 1
                && animalsThatMeCanEat != null
                && animalsThatMeCanEat.size() > 0;
    }

    private boolean isMeCanEatThisAnimal(Animal animalMeTryToEat) {
        if(animalsThatMeCanEat.get(animalMeTryToEat.getClass()) != null
                && animalsThatMeCanEat.get(animalMeTryToEat.getClass()) > 0) {
            AtomicInteger chanceToKill = new AtomicInteger();
            chanceToKill.set(ThreadLocalRandom.current().nextInt(0, 100 + 1));
            int probabilityToEat = animalsThatMeCanEat.get(animalMeTryToEat.getClass());
            int chanceToKillInt = chanceToKill.get();
            return chanceToKillInt <= probabilityToEat;
        }
        return false;
    }

    private boolean isMeAllowedToEatPlants() {
        return HerbivorousAnimal.class.isAssignableFrom(this.getClass());
    }

    private double putEatenPlantsInMyStomachAndRemoveItFromTheCellAndReturnValueOfEatenPlants() {
        double howMuchAllowedToEatPlants = Island.howMuchAllowedToEatPlantsInTheCell(x, y, kgFoodForSaturation - kgFoodInTheStomach);
        double eatenPlants = howMuchAllowedToEatPlants;
        if(howMuchAllowedToEatPlants > 0) {
            if (kgFoodInTheStomach + howMuchAllowedToEatPlants > kgFoodForSaturation) {
                eatenPlants = kgFoodForSaturation - kgFoodInTheStomach;
                kgFoodInTheStomach = kgFoodForSaturation;
            } else {
                eatenPlants = howMuchAllowedToEatPlants;
                kgFoodInTheStomach += howMuchAllowedToEatPlants;
            }
            Island.reduceEatenPlantsFromTheCell(x, y, eatenPlants);
        }
        return eatenPlants;
    }

    public void reducingFoodInTheStomachPerCycle() {
        if(!isAlive())
            return;

        double reduceFood = Configuration.foodMultiplierInTheStomachPerCycle * kgFoodForSaturation;
        kgFoodInTheStomach = (kgFoodInTheStomach - reduceFood > 0) ? kgFoodInTheStomach - reduceFood : 0;
    }

    private boolean isCanProduceOffspringWithThisAnimal(Animal animalMeTryToProduceOffspring) {
        return !animalMeTryToProduceOffspring.isHungry()
                && this != animalMeTryToProduceOffspring
                && (getClass() == animalMeTryToProduceOffspring.getClass())
                && !animalMeTryToProduceOffspring.isProduceOffspring()
                && Island.howManyAnimalsOfThisClassInTheCell(x, y, getClass()) + 1 <= maxAmountPerCell;
    }
}