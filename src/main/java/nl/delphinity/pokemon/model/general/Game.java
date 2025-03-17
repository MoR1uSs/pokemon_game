package nl.delphinity.pokemon.model.general;

import nl.delphinity.pokemon.model.area.Area;
import nl.delphinity.pokemon.model.area.Pokecenter;
import nl.delphinity.pokemon.model.battle.Battle;
import nl.delphinity.pokemon.model.item.ItemType;
import nl.delphinity.pokemon.model.trainer.Badge;
import nl.delphinity.pokemon.model.trainer.GymLeader;
import nl.delphinity.pokemon.model.trainer.Trainer;

import javax.xml.crypto.Data;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Stream;

import static nl.delphinity.pokemon.model.general.PokemonData.*;

public class Game {

    public static final ArrayList<Area> areas = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    public static Trainer trainer = null;

    //set up the game in this static block

    static {

        //PEWTER City
        Pokecenter pewterCenter = new Pokecenter("Pewter City's Pokecenter");
        Area pewterCity = new Area("Pewter city", null, true, null, pewterCenter);
        pewterCity.setContainsPokemon(Arrays.asList(
                PokemonType.GRASS,
                PokemonType.FLYING,
                PokemonType.BUG,
                PokemonType.GROUND));

        //VIRIDIAN City
        Pokecenter viridianCenter = new Pokecenter("Viridian City's Pokecenter");
        Area viridianCity = new Area("Viridian city", null, true, pewterCity, viridianCenter);
        viridianCity.setContainsPokemon(Arrays.asList(
                PokemonType.GRASS,
                PokemonType.FLYING,
                PokemonType.BUG,
                PokemonType.GROUND));

        //PALLET Town
        Pokecenter palletCenter = new Pokecenter("Pallet Town's Pokecenter");
        Area palletTown = new Area("Pallet town", null, true, viridianCity, palletCenter);
        palletTown.setContainsPokemon(Arrays.asList(
                PokemonType.GRASS,
                PokemonType.FLYING,
                PokemonType.BUG,
                PokemonType.GROUND));

        areas.add(palletTown);
        areas.add(viridianCity);
        areas.add(pewterCity);

        //SETUP gym leaders
        GymLeader pewterLeader = new GymLeader("Bram", new Badge("Boulder Badge"), pewterCity);
        Pokemon p = new Pokemon(PokemonData.ONIX);
        p.setLevel(5);
        p.setOwner(pewterLeader);
        pewterLeader.setActivePokemon(p);
        pewterLeader.getPokemonCollection().add(p);
        pewterCity.setGymLeader(pewterLeader);
    }

    public static void main(String[] args) {
        Trainer loadedSave = DataSaver.loadGame();

        if(loadedSave != null){
            trainer = (Trainer) loadedSave;
            System.out.println("Welcome back, " + trainer.getName() + "!");
        }
        else{
            System.out.println("Welcome new trainer, what's your name?");
            String name = sc.nextLine();
            trainer = new Trainer(name, areas.get(0));
            System.out.println("Hi, " + trainer.getName());

            Pokemon firstPokemon = chooseFirstPokemon();
            firstPokemon.setOwner(trainer);
            trainer.getPokemonCollection().add(firstPokemon);
            System.out.println("You now have " + trainer.getPokemonCollection().size() + " pokemon in your collection!");
        }

        while(true) {
            showGameOptions();
        }
    }

    private static void showGameOptions() {
        System.out.println("What do you want to do?");
        System.out.println("1 ) Find Pokemon");
        System.out.println("2 ) My Pokemon");
        System.out.println("3 ) Inventory");
        System.out.println("4 ) Badges");
        System.out.println("5 ) Challenge " + trainer.getCurrentArea().getName() + "'s Gym Leader");
        System.out.println("6 ) Travel");
        System.out.println("7 ) Visit Pokecenter");
        System.out.println("8 ) Exit game");
        int action = sc.nextInt();
        switch (action) {
            case 1:
                findAndBattlePokemon();
                break;
            case 2:
                trainer.showPokemonColletion();
                break;
            case 3:
                ItemType item = showInventory();
                if (item != null) {
                    trainer.useItem(item, null);
                }
                break;
            case 4:
                trainer.showBadges();
                break;
            case 5:
                if (trainer.getCurrentArea().getGymLeader() != null) {
                    startGymBattle();
                } else {
                    System.out.println("No Gym Leader in this town!");
                }
                break;
            case 6:
                Area area = showTravel();
                if (area != null) {
                    trainer.travel(area);
                }
                break;
            case 7:
                trainer.visitPokeCenter(trainer.getCurrentArea().getPokecenter());
                break;
            case 8:
                quit();
                break;
            default:
                System.out.println("Sorry, that's not a valid option");
                break;
        }
    }

    //TODO: US-PKM-O-6
    private static void findAndBattlePokemon() {
        Pokemon randomPokemon = trainer.findPokemon();
        Battle battle = new Battle(trainer.getActivePokemon(), randomPokemon, trainer);
        battle.start();
    }

    private static Area showTravel() {
        Area travelTo = null;
        int index = 1;
        List<Area> travelToAreas = new ArrayList<>();

        for (Area area : areas) {
            if (!area.equals(trainer.getCurrentArea()) && area.isUnlocked() &&
                    ((area.getNextArea() != null &&
                            area.getNextArea().equals(trainer.getCurrentArea())) ||
                            trainer.getCurrentArea().getNextArea() != null &&
                                    trainer.getCurrentArea().getNextArea().equals(area))) {
                travelToAreas.add(area);
            }
        }
        for (Area a : travelToAreas) {
            System.out.println(index + ") " + a.getName());
            index++;
        }
        System.out.println(index + ") Back");
        int choice = sc.nextInt();
        if (choice != index) {
            travelTo = travelToAreas.get(choice - 1);
        }
        return travelTo;
    }

    private static ItemType showInventory() {
        HashMap<ItemType, Integer> items = trainer.getInventory().getItems();
        Set<Map.Entry<ItemType, Integer>> entries = items.entrySet();
        int index = 1;
        for (Map.Entry<ItemType, Integer> entry : entries) {
            System.out.println(index + ") " + entry.getKey() + " " + entry.getValue());
            index++;
        }
        System.out.println(index + ") Back");
        int choice = sc.nextInt();
        if (choice != index) {
            return ItemType.values()[choice - 1];
        }
        return null;
    }

    //TODO: US-PKM-O-1
    private static Pokemon chooseFirstPokemon() {
        Pokemon chosenPokemon = null;

        System.out.println("Please choose one of these three pokemon");
        System.out.println("1 ) Charmander");
        System.out.println("2 ) Bulbasaur");
        System.out.println("3 ) Squirtle");

        try{
            int choice = sc.nextInt();

            switch (choice){
                case 1: chosenPokemon = new Pokemon(CHARMANDER);
                    chosenPokemon.setLevel(5);
                    trainer.setActivePokemon(chosenPokemon);
                    break;

                case 2: chosenPokemon = new Pokemon(BULBASAUR);
                    chosenPokemon.setLevel(5);
                    trainer.setActivePokemon(chosenPokemon);
                    break;

                case 3: chosenPokemon = new Pokemon(SQUIRTLE);
                    chosenPokemon.setLevel(5);
                    trainer.setActivePokemon(chosenPokemon);
                    break;

                default:System.out.println("Invalid input!");
                        return chooseFirstPokemon();
            }
        }
        catch (Exception ex){
            System.out.println("Invalid input!");
            sc.next();
            return chooseFirstPokemon();
        }

        return chosenPokemon;
    }

    //TODO: US-PKM-O-8
    private static void startGymBattle() {
        Battle trainerBattle = trainer.challengeTrainer(trainer.getCurrentArea().getGymLeader());

        if(trainerBattle != null && trainerBattle.getWinner().getOwner().equals(trainer)){
            if(trainerBattle.getEnemy().getOwner().equals(GymLeader.class)){
                Pokemon enemyPokemon = trainerBattle.getEnemy();
                GymLeader gymLeader = (GymLeader) enemyPokemon.getOwner();

                gymLeader.setDefeated(true);
                awardBadge(gymLeader.getBadge().getName());

                Area gymLeaderArea = gymLeader.getCurrentArea();
                Area nextArea = gymLeaderArea.getNextArea();

                if(nextArea != null){
                    nextArea.setUnlocked(true);
                }
            }
        }
    }

    //TODO: US-PKM-O-9
    public static void awardBadge(String badgeName) {
        Badge newBadge = new Badge(badgeName);
        trainer.addBadge(newBadge);
    }

    public static void gameOver(String message) {
        System.out.println(message);
        System.out.println("Game over");
        quit();
    }

    //TODO: US-PKM-O-2:
    private static void quit() {
        boolean valid = false;
        System.out.println("Would you like to save the game?");
        System.out.println("Yes/No");

        while(!valid){
            String choice = sc.nextLine().trim();
            if(choice.toLowerCase().equals("yes")){
                try{
                    DataSaver.saveGame(trainer);
                    valid = true;
                } catch (Exception e){
                    System.out.println("Error saving game: " + e.getMessage());
                }
            }
            else if(choice.toLowerCase().equals("no")){
                valid = true;
            }
            else {
                System.out.println("Invalid input! Please enter 'Yes' or 'No'.");
            }
        }

        System.out.println("Thanks for playing! See you next time!");
        sc.close();
        System.exit(0);
    }
}
