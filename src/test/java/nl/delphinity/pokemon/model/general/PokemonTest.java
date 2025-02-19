package nl.delphinity.pokemon.model.general;

import nl.delphinity.pokemon.model.battle.Attack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nl.delphinity.pokemon.model.general.PokemonData.BULBASAUR;
import static nl.delphinity.pokemon.model.general.PokemonData.CHARMANDER;
import static org.junit.jupiter.api.Assertions.*;
class PokemonTest {
    private Pokemon attacker;
    private Pokemon defender;
    private Attack attack;

    @BeforeEach
    void setUp() {
        attacker = new Pokemon(CHARMANDER);
        defender = new Pokemon(BULBASAUR);

        attack = new Attack("Tackle", 10, 10);
    }

    @Test
    void testAttack() {
        int initialHp = defender.getCurrentHp();

        attacker.attack(defender, attack);

        assertEquals(initialHp - attack.getPower(), defender.getCurrentHp(), "The defender's HP should decrease by the attack's power.");
    }
}