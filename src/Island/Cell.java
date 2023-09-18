package Island;

import Animals.Animal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cell {
    private List<Animal> listOfAnimals = new CopyOnWriteArrayList<>();
    private double quantityPlants = ThreadLocalRandom.current().nextDouble(1,  Configuration.startMaxAmountOfPlantsPerCell + 1);

    public void add(Animal animal) {
        listOfAnimals.add(animal);
    }

    public void remove(Animal animal) {
        listOfAnimals.remove(animal);
    }

    public List<Animal> getListOfAnimals() {
        return listOfAnimals;
    }

    public double howMuchAllowedToEatPlants(double wantToEatPlant) {
        if(quantityPlants >= wantToEatPlant) {
            return wantToEatPlant;
        } else
            return quantityPlants;
    }

    public int howManyAnimalsOfThisClassInTheCell(Class clazz) {
        int result = 0;
        for(Animal animal : listOfAnimals) {
            if (animal.getClass() == clazz)
                result++;
        }
        return result;
    }

    public void reduceEatenPlants(double howMuchPlantsEatenByAnimal) {
        quantityPlants -= howMuchPlantsEatenByAnimal;
    }

    public void multiplyPlantsPerCycle() {
        quantityPlants *= Configuration.plantMultiplierPerCycle;
        quantityPlants = (quantityPlants > Configuration.maxAmountOfPlantsPerCell) ?
                Configuration.maxAmountOfPlantsPerCell : quantityPlants;
    }

    public double getQuantityPlants() {
        return quantityPlants;
    }

}
