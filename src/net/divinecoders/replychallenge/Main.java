package net.divinecoders.replychallenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static List<Integer> staminaRecoveryTurns = new LinkedList<>();
    public static List<Integer> staminaRecoveryAmount = new LinkedList<>();
    public static List<Demon> demons = new ArrayList<>();

    private static void recover(Pandora player)
    {
        List<Integer> staminaRecoveryTurns = new LinkedList<>();
        List<Integer> staminaRecoveryAmount = new LinkedList<>();

        for(int pos = 0; pos < Main.staminaRecoveryTurns.size(); pos++)
        {
            int time = Main.staminaRecoveryTurns.get(pos);
            int health = Main.staminaRecoveryAmount.get(pos);
            if(time == 0)
            {
                player.addStamina(health);
            } else {
                time--;
                Main.staminaRecoveryTurns.set(pos, time);
                staminaRecoveryTurns.add(Main.staminaRecoveryTurns.get(pos));
                staminaRecoveryAmount.add(Main.staminaRecoveryAmount.get(pos));
            }
        }

        Main.staminaRecoveryTurns = staminaRecoveryTurns;
        Main.staminaRecoveryAmount = staminaRecoveryAmount;
    }

    private static void print(String str)
    {
        System.out.println(str);
    }

    private static int toInt(String str)
    {
        return Integer.parseInt(str);
    }

    private static void fightDemons(Pandora player)
    {
        // copy to avoid concurrent mod. error
        List<Demon> localDemons = new ArrayList<>(demons);
        for(Demon demon : localDemons)
        {
            player.fight(demon);
        }
    }

    public static void main(String[] args)
    {
        // create list of loaded lines
        List<String> fileLines = new ArrayList<>();

        // load input file
        File inputFile = new File("00-example.txt");

        // parse lines from file
        try {
            Scanner fileReader = new Scanner(inputFile);

            while(fileReader.hasNextLine()) {
                String currentLine = fileReader.nextLine();
                fileLines.add(currentLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        print("-- DEBUG --");
        System.out.print(fileLines);
        print("\n-- DEBUG END --");

        // init player data
        List<Integer> firstLineParameters = new ArrayList<>();
        String firstLine = fileLines.get(0);
        String[] firstLineValues = firstLine.split(" ");
        for(String value : firstLineValues)
        {
            int intValue = Integer.parseInt(value);
            firstLineParameters.add(intValue);
        }

        int startingStamina = firstLineParameters.get(0);
        int maxStamina = firstLineParameters.get(1);
        int turns = firstLineParameters.get(2);
        int totalDemons = firstLineParameters.get(3);

        // remove first line because we already parsed it
        fileLines.remove(0);

        // create player
        Pandora player = new Pandora(startingStamina, maxStamina);

        // init demons
        for(String demonLine : fileLines)
        {
            String[] values = demonLine.split(" ");
            Demon demon = new Demon(toInt(values[0]), toInt(values[1]), toInt(values[2]), toInt(values[3]), 0);
            demons.add(demon);
        }

        for(int turn = 0; turn < turns; turn++)
        {
            print("\nTurn " + turn);
            player.nextTurn();
            recover(player);
            print("Player has " + player.getStamina() + " beginning stamina.");

            fightDemons(player);

            print("Player has " + player.getStamina() + " ending stamina.");
        }
    }
}
