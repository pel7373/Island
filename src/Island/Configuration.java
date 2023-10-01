package Island;

import Animals.*;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    //Island's settings
    public static int maxX = 7;
    public static int maxY = 5;
    public static int maxLifeCycles = 10;
    public static int predatorsToCreate = 16;
    public static int herbivorousToCreate = 32;

    //plant's settings
    public static double startMaxAmountOfPlantsPerCell = 100;
    public static double maxAmountOfPlantsPerCell = 200;
    public static double plantMultiplierPerCycle = 1.2;

    //animal's settings
    public static Map<Class<? extends Animal>, Map<Class<? extends Animal>, Integer>> animalsThatCanEat = new HashMap<>();
    public static Map<Class<? extends Animal>, String> pictureAnimal = new HashMap<>();
    public static Map<Class<? extends Animal>, Double> weightAnimal = new HashMap<>();
    public static Map<Class<? extends Animal>, Integer> maxAmountPerCellAnimal = new HashMap<>();
    public static Map<Class<? extends Animal>, Integer> maxStepsPerMoveAnimal = new HashMap<>();
    public static Map<Class<? extends Animal>, Double> kgFoodForSaturationAnimal = new HashMap<>();
    public static int deathAfterHungryDays = 2;
    public static int maxTriesToGetPermission = 5;
    public static double foodMultiplierInTheStomachPerCycle = .3;
    public static int maxClassNameLength = 11; //it used for adding spaces to names of classes and names of animals - to align columns in animal's entities when printing

    static {
        createAndSetMapAnimalsThatCanEat();
        createAndSetMapPictureAnimal();
        createAndSetMapWeightAnimal();
        createAndSetMapMaxAmountPerCellAnimal();
        createAndSetMapMaxStepsPerMoveAnimal();
        createAndSetMapKgFoodForSaturationAnimal();
    }

    public static String getPicture(Class clazz) {
        if(clazz != null)
            return pictureAnimal.get(clazz);
        return "";
    }

    public static Double getWeight(Class clazz) {
        if(clazz != null && weightAnimal.get(clazz) != null)
            return weightAnimal.get(clazz);
        return 0.;
    }

    public static Integer getMaxAmountPerCell(Class clazz) {
        if(clazz != null && maxAmountPerCellAnimal.get(clazz) != null)
            return maxAmountPerCellAnimal.get(clazz);
        return 0;
    }

    public static Integer getMaxStepsPerMove(Class clazz) {
        if(clazz != null && maxStepsPerMoveAnimal.get(clazz) != null)
            return maxStepsPerMoveAnimal.get(clazz);
        return 0;
    }

    public static Double getKgFoodForSaturation(Class clazz) {
        if(clazz != null && kgFoodForSaturationAnimal.get(clazz) != null)
            return kgFoodForSaturationAnimal.get(clazz);
        return 0.;
    }

    private static void createAndSetMapPictureAnimal() {
        pictureAnimal.put(Bear.class, "\uD83D\uDC03");
        pictureAnimal.put(Boa.class, "\uD83D\uDC0D");
        pictureAnimal.put(Boar.class, "\uD83D\uDC17");
        pictureAnimal.put(Buffalo.class, "\uD83D\uDC03");
        pictureAnimal.put(Caterpillar.class, "\uD83D\uDC1B");
        pictureAnimal.put(Deer.class, "\uD83E\uDD8C");
        pictureAnimal.put(Duck.class, "\uD83E\uDD86");
        pictureAnimal.put(Eagle.class, "\uD83E\uDD85");
        pictureAnimal.put(Fox.class, "🦊");
        pictureAnimal.put(Goat.class, "\uD83D\uDC10");
        pictureAnimal.put(Horse.class, "🐎");
        pictureAnimal.put(Mouse.class, "\uD83D\uDC01");
        pictureAnimal.put(Rabbit.class, "\uD83D\uDC07");
        pictureAnimal.put(Sheep.class, "\uD83D\uDC11");
        pictureAnimal.put(Wolf.class, "\uD83D\uDC3A");
    }

    private static void createAndSetMapWeightAnimal() {
        weightAnimal.put(Bear.class, 500.0);
        weightAnimal.put(Boa.class, 15.0);
        weightAnimal.put(Boar.class, 400.0);
        weightAnimal.put(Buffalo.class, 700.0);
        weightAnimal.put(Caterpillar.class, 0.01);
        weightAnimal.put(Deer.class, 300.0);
        weightAnimal.put(Duck.class, 1.0);
        weightAnimal.put(Eagle.class, 6.0);
        weightAnimal.put(Fox.class, 8.0);
        weightAnimal.put(Goat.class, 60.0);
        weightAnimal.put(Horse.class, 400.0);
        weightAnimal.put(Mouse.class, 0.05);
        weightAnimal.put(Rabbit.class, 2.0);
        weightAnimal.put(Sheep.class, 70.0);
        weightAnimal.put(Wolf.class, 50.0);
    }

    private static void createAndSetMapMaxAmountPerCellAnimal() {
        maxAmountPerCellAnimal.put(Bear.class, 5);
        maxAmountPerCellAnimal.put(Boa.class, 30);
        maxAmountPerCellAnimal.put(Boar.class, 50);
        maxAmountPerCellAnimal.put(Buffalo.class, 10);
        maxAmountPerCellAnimal.put(Caterpillar.class, 1000);
        maxAmountPerCellAnimal.put(Deer.class, 20);
        maxAmountPerCellAnimal.put(Duck.class, 200);
        maxAmountPerCellAnimal.put(Eagle.class, 20);
        maxAmountPerCellAnimal.put(Fox.class, 30);
        maxAmountPerCellAnimal.put(Goat.class, 140);
        maxAmountPerCellAnimal.put(Horse.class, 20);
        maxAmountPerCellAnimal.put(Mouse.class, 500);
        maxAmountPerCellAnimal.put(Rabbit.class, 150);
        maxAmountPerCellAnimal.put(Sheep.class, 140);
        maxAmountPerCellAnimal.put(Wolf.class, 30);
    }

    private static void createAndSetMapMaxStepsPerMoveAnimal() {
        maxStepsPerMoveAnimal.put(Bear.class, 2);
        maxStepsPerMoveAnimal.put(Boa.class, 1);
        maxStepsPerMoveAnimal.put(Boar.class, 2);
        maxStepsPerMoveAnimal.put(Buffalo.class, 3);
        maxStepsPerMoveAnimal.put(Caterpillar.class, 0);
        maxStepsPerMoveAnimal.put(Deer.class, 4);
        maxStepsPerMoveAnimal.put(Duck.class, 4);
        maxStepsPerMoveAnimal.put(Eagle.class, 3);
        maxStepsPerMoveAnimal.put(Fox.class, 2);
        maxStepsPerMoveAnimal.put(Goat.class, 3);
        maxStepsPerMoveAnimal.put(Horse.class, 4);
        maxStepsPerMoveAnimal.put(Mouse.class, 1);
        maxStepsPerMoveAnimal.put(Rabbit.class, 2);
        maxStepsPerMoveAnimal.put(Sheep.class, 3);
        maxStepsPerMoveAnimal.put(Wolf.class, 3);
    }

    private static void createAndSetMapKgFoodForSaturationAnimal() {
        kgFoodForSaturationAnimal.put(Bear.class, 80.0);
        kgFoodForSaturationAnimal.put(Boa.class, 3.0);
        kgFoodForSaturationAnimal.put(Boar.class, 50.0);
        kgFoodForSaturationAnimal.put(Buffalo.class, 100.);
        kgFoodForSaturationAnimal.put(Caterpillar.class, 0.);
        kgFoodForSaturationAnimal.put(Deer.class, 50.);
        kgFoodForSaturationAnimal.put(Duck.class, 0.15);
        kgFoodForSaturationAnimal.put(Eagle.class, 1.);
        kgFoodForSaturationAnimal.put(Fox.class, 2.);
        kgFoodForSaturationAnimal.put(Goat.class, 10.);
        kgFoodForSaturationAnimal.put(Horse.class, 60.);
        kgFoodForSaturationAnimal.put(Mouse.class, 0.01);
        kgFoodForSaturationAnimal.put(Rabbit.class, 0.45);
        kgFoodForSaturationAnimal.put(Sheep.class, 10.);
        kgFoodForSaturationAnimal.put(Wolf.class, 8.0);
    }

    private static void createAndSetMapAnimalsThatCanEat() {
        Map<Class<? extends Animal>, Integer> bearAnimalsThatCanEat = new HashMap<>();
        bearAnimalsThatCanEat.put(Boa.class, 80);
        bearAnimalsThatCanEat.put(Horse.class, 40);
        bearAnimalsThatCanEat.put(Deer.class, 80);
        bearAnimalsThatCanEat.put(Rabbit.class, 80);
        bearAnimalsThatCanEat.put(Mouse.class, 90);
        bearAnimalsThatCanEat.put(Goat.class, 70);
        bearAnimalsThatCanEat.put(Sheep.class, 70);
        bearAnimalsThatCanEat.put(Boar.class, 50);
        bearAnimalsThatCanEat.put(Buffalo.class, 20);
        bearAnimalsThatCanEat.put(Duck.class, 10);
        animalsThatCanEat.put(Bear.class, bearAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> boaAnimalsThatCanEat = new HashMap<>();
        boaAnimalsThatCanEat.put(Fox.class, 15);
        boaAnimalsThatCanEat.put(Rabbit.class, 20);
        boaAnimalsThatCanEat.put(Mouse.class, 40);
        boaAnimalsThatCanEat.put(Duck.class, 10);
        animalsThatCanEat.put(Boa.class, boaAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> boarAnimalsThatCanEat = new HashMap<>();
        boarAnimalsThatCanEat.put(Mouse.class, 50);
        boarAnimalsThatCanEat.put(Caterpillar.class, 90);
        animalsThatCanEat.put(Boar.class, boarAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> duckAnimalsThatCanEat = new HashMap<>();
        duckAnimalsThatCanEat.put(Caterpillar.class, 90);
        animalsThatCanEat.put(Duck.class, duckAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> eagleAnimalsThatCanEat = new HashMap<>();
        eagleAnimalsThatCanEat.put(Fox.class, 10);
        eagleAnimalsThatCanEat.put(Rabbit.class, 90);
        eagleAnimalsThatCanEat.put(Mouse.class, 90);
        eagleAnimalsThatCanEat.put(Duck.class, 80);
        animalsThatCanEat.put(Eagle.class, eagleAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> foxAnimalsThatCanEat = new HashMap<>();
        foxAnimalsThatCanEat.put(Rabbit.class, 70);
        foxAnimalsThatCanEat.put(Mouse.class, 90);
        foxAnimalsThatCanEat.put(Duck.class, 60);
        foxAnimalsThatCanEat.put(Caterpillar.class, 40);
        animalsThatCanEat.put(Fox.class, foxAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> mouseAnimalsThatCanEat = new HashMap<>();
        mouseAnimalsThatCanEat.put(Caterpillar.class, 90);
        animalsThatCanEat.put(Mouse.class, mouseAnimalsThatCanEat);

        Map<Class<? extends Animal>, Integer> wolfAnimalsThatCanEat = new HashMap<>();
        wolfAnimalsThatCanEat.put(Horse.class, 10);
        wolfAnimalsThatCanEat.put(Deer.class, 15);
        wolfAnimalsThatCanEat.put(Rabbit.class, 60);
        wolfAnimalsThatCanEat.put(Mouse.class, 80);
        wolfAnimalsThatCanEat.put(Goat.class, 60);
        wolfAnimalsThatCanEat.put(Sheep.class, 70);
        wolfAnimalsThatCanEat.put(Boar.class, 15);
        wolfAnimalsThatCanEat.put(Buffalo.class, 10);
        wolfAnimalsThatCanEat.put(Duck.class, 40);
        animalsThatCanEat.put(Wolf.class, wolfAnimalsThatCanEat);
    }
}
