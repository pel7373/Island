package Island;

import Animals.Animal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cell {
    private List<Animal> listOfAnimals = new CopyOnWriteArrayList<>();
    private int quantityPlants = ThreadLocalRandom.current().nextInt(1,  Configuration.maxStartAmountOfPlantsPerCell + 1);

    public void add(Animal animal) {
        listOfAnimals.add(animal);
    }

    public void remove(Animal animal) {
        listOfAnimals.remove(animal);
    }

    public Animal get(int i) {
        return listOfAnimals.get(i);
    }

    public List<Animal> getListOfAnimals() {
        return listOfAnimals;
    }

    public void multiplyPlantsPerCycle() {
        quantityPlants *= Configuration.plantMultiplierPerCycle;
    }
}
