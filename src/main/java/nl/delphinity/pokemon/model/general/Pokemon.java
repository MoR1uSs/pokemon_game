package nl.delphinity.pokemon.model.general;

import nl.delphinity.pokemon.model.battle.Attack;
import nl.delphinity.pokemon.model.trainer.Trainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Pokemon implements Serializable {

    private final int maxPP;
    private final int currentPP;
    private final Random r = new Random();
    private PokemonData pokedata;
    private Trainer owner;
    private int currentHp;
    private int maxHp;
    private int level;
    private int xpToNext;
    private int currentXp;

    public Pokemon(PokemonData pokedata) {
        this.pokedata = pokedata;
        this.currentHp = pokedata.maxHp;
        this.maxHp = pokedata.maxHp;
        this.currentPP = pokedata.maxPp;
        this.maxPP = pokedata.maxPp;
        this.level = 5;
        this.xpToNext = 8;
        this.currentXp = 0;
    }

    public PokemonData getPokedata() {
        return pokedata;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }
    
    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Trainer getOwner() {
        return owner;
    }

    public void setOwner(Trainer owner) {
        this.owner = owner;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    private int getXpToNext() {
        return xpToNext;
    }

    public List<Attack> getAttacks() {
        List<Attack> attacks = new ArrayList<>();
        attacks.addAll(pokedata.specificAttacks);
        attacks.addAll(pokedata.pokemonType.typeAttacks);
        return attacks;
    }

    //TODO: US-PKM-O-4E
    private void evolve() {
        if(evolveCheck()){
            System.out.println("Evolution incoming!");
            try{
                for(int i = 0; i < 5; i++){
                    String x = "";
                    x += ".";
                    Thread.sleep(1000);
                }
                this.pokedata = pokedata.evolvesIn;
                System.out.println("Your pokemon evolved in " + this.pokedata.name());
            }
            catch (Exception e){
                System.out.println("Something went wrong");
            }
        }
    }

    //TODO: US-PKM-O-4D
    public void gainXp(int amount) {
        if(amount + currentXp >= xpToNext){
            levelUp();
            int remainingXp = (currentXp + amount) - xpToNext;
            gainXp(remainingXp);
        }
        else {
            setCurrentXp(currentXp + amount);
            status();
        }
    }

    //TODO: US-PKM-O-4C
    private void levelUp() {
        Random r = new Random();
        int hpIncrease = r.nextInt(maxHp);

        evolveCheck();
    }

    //TODO: US-PKM-O-4E
    private boolean evolveCheck() {
        if(pokedata.evolvesIn != null && level >= pokedata.evolutionLevel){
            return true;
        }
        return false;
    }

    //TODO: US-PKM-O-4
    public void attack(Pokemon otherPokemon, Attack attack) {
        int power = attack.getPower();

        if(otherPokemon.getCurrentHp() - power < 0){
            otherPokemon.setCurrentHp(0);
        }
        else{
            otherPokemon.setCurrentHp(otherPokemon.getCurrentHp() - power);
        }
    }

    //TODO: US-PKM-O-4B
    public Attack getRandomAttack() {
        int attackSize = getAttacks().size();
        Random r = new Random();

        int randomAttackIndex = r.nextInt(attackSize);
        Attack randomAttack = getAttacks().get(randomAttackIndex);

        return randomAttack;
    }

    public double getHpPercentage() {
        return currentHp / maxHp * 100;
    }

    public void status() {
        System.out.println("----------------");
        System.out.println(this.getPokedata().name());
        System.out.println("LVL " + this.level);
        System.out.println("HP " + this.currentHp + "/" + this.maxHp);
        System.out.println("PP " + this.currentPP + "/" + this.maxPP);
        System.out.println("XP " + this.currentXp + "/" + this.xpToNext);
    }

    //TODO: US-PKM-O-5
    public boolean isKnockout() {
        return currentHp <= 0;
    }
}
