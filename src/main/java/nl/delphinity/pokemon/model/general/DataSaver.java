package nl.delphinity.pokemon.model.general;
import java.io.*;

import nl.delphinity.pokemon.model.trainer.Trainer;

public class DataSaver implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class GameState implements Serializable{
        private Trainer trainer;

        GameState(Trainer trainer){
            this.trainer = trainer;
        }
    }

    public static boolean saveGame(Trainer trainer){
        try(FileOutputStream fileOut = new FileOutputStream("person.ser")){
            GameState gameState = new GameState(trainer);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(gameState);

            System.out.println("Game was successfully saved!");
            out.close();
            fileOut.close();
            return true;
        }
        catch (Exception e){
            System.out.println("Game saving exception: " + e.getMessage());
            return false;
        }
    }

    public static Trainer loadGame(){
        try(FileInputStream fileIn = new FileInputStream("person.ser")){
            ObjectInputStream in = new ObjectInputStream(fileIn);

            GameState gameState = (GameState) in.readObject();
            System.out.println("Game was loaded successfully!");

            fileIn.close();
            in.close();
            return gameState.trainer;
        }
        catch (Exception e){
            System.out.println("Game loading exception: " + e.getMessage());
            return null;
        }
    }
}
