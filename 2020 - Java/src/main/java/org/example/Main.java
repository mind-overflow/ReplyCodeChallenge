package org.example;

import java.io.File;
import java.util.*;

public class Main {

    // todo togliere già usati
    public static HashMap<Point, Job> availablePoints = new LinkedHashMap<>();
    public static List<String> companies = new ArrayList<>();

    public static List<Person> developers = new ArrayList<>();
    public static List<Person> projectManagers = new ArrayList<>();

    public static int width, height;
    public static int totalDevelopers, totalManagers;

    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            println("Please specify which file to parse!");
            return;
        }

        final String fileName = args[0];
        List<String> fileLines = new ArrayList<>();

        try {
            File inputFile = new File(fileName);
            Scanner fileScanner = new Scanner(inputFile);

            while (fileScanner.hasNext()) {
                fileLines.add(fileScanner.nextLine());
            }
        } catch (Exception e) {
            println("File not found!");
            return;
        }

        String firstLine = fileLines.get(0);
        String[] firstLineSplit = firstLine.split(" ");
        width = Integer.parseInt(firstLineSplit[0]);
        height = Integer.parseInt(firstLineSplit[1]);

        /*
        # = unavailable place
        _ = possible developer place
        M = possible manager place
         */

        for(int x = 0; x < height; x++)
        {
            String currentLine = fileLines.get(x+1);

            for(int y = 0; y < width; y++)
            {
                char currentCharacter = currentLine.charAt(y);
                Point position = new Point(y, x); // è giusto scambiare x e y

                if(currentCharacter == '_') {

                    availablePoints.put(position, Job.DEVELOPER);
                } else if(currentCharacter == 'M') {
                    availablePoints.put(position, Job.MANAGER);
                }
            }
        }

        totalDevelopers = Integer.parseInt(fileLines.get(height + 1));

        int offset = height + 2;

        for(int currentDev = 0; currentDev < totalDevelopers; currentDev++)
        {
            // nome ditta | bonus | quante skill | nome skill
            String[] currentDevLine = fileLines.get(offset + currentDev).split(" ");

            String company = currentDevLine[0];
            if(!companies.contains(company)) companies.add(company);

            int bonus = Integer.parseInt(currentDevLine[1]);
            int skillAmount = Integer.parseInt(currentDevLine[2]);

            List<String> skills = new ArrayList<>(Arrays.asList(currentDevLine).subList(3, skillAmount + 3));

            Person developer = new Person(Job.DEVELOPER, company, bonus, skills);
            developers.add(developer);
        }

        offset += totalDevelopers;

        totalManagers = Integer.parseInt(fileLines.get(offset));

        offset++;
        for(int currentMan = 0; currentMan < totalManagers; currentMan++) {
            // ditta | bonus

            String[] currentLine = fileLines.get(offset + currentMan).split(" ");

            String company = currentLine[0];
            if(!companies.contains(company)) companies.add(company);

            int bonus = Integer.parseInt(currentLine[1]);

            Person manager = new Person(Job.MANAGER, company, bonus);
            projectManagers.add(manager);
        }

    }

    public static void println(Object input) { System.out.println(input); }
    public static void print(Object input) { System.out.print(input); }

    public static List<Person> getHighestBonusAvailableManagers() {

        List<Person> sorted = new ArrayList<>();

        for(Person p : projectManagers) {
            if(p.getPosition() != null) continue;

            sorted.add(p);
        }

        class PersonScoreComparator implements Comparator<Person> {
             @Override
            public int compare(Person p1, Person p2) {
                 return Integer.compare(p1.getBonus(), p2.getBonus());
             }
        }

        sorted.sort(new PersonScoreComparator());
        return sorted;

    }


    public static Person getBestAvailableManager(Point center)
    {
        List<Person> highestBonusManagers = getHighestBonusAvailableManagers();

        Person lowestDeltaPerson = null;
        int lowestDelta = 1000;

        for(Person currHighestBonusManager : highestBonusManagers) {
            String company = currHighestBonusManager.getCompany();

            HashMap<Job, Integer> employeesAmount = getAvailableEmployees(company);

            HashMap<Job, List<Point>> adjacentPoints = getAdjacentPoints(center);

            // numero posti developer adiacenti - numero developer disponibili per compagnia
            int developersDelta = adjacentPoints.get(Job.DEVELOPER).size() - employeesAmount.get(Job.DEVELOPER);
            if(developersDelta < 0) developersDelta = 0;

            int managersDelta = adjacentPoints.get(Job.MANAGER).size() - (employeesAmount.get(Job.MANAGER) - 1);
            if(managersDelta < 0) managersDelta = 0;

            int totalDelta = managersDelta + developersDelta;

            if(totalDelta == 0)
            {
                return currHighestBonusManager;
            } else {
                if(totalDelta < lowestDelta) {
                    lowestDelta = totalDelta;
                    lowestDeltaPerson = currHighestBonusManager;
                }
            }
        }

        return lowestDeltaPerson;
    }

    public static Point getBestManagerSpot(Point center) {

        Point bestPoint = null;
        int bestAvailableSpots = 0;

        for(Point p : availablePoints.keySet()) {
            if(availablePoints.get(p) != Job.MANAGER) continue;

            HashMap<Job, List<Point>> nearbyPoints = getAdjacentPoints(p);

            int tot = 0;

            for(List<Point> thisJobPoints : nearbyPoints.values()) {
                tot += thisJobPoints.size();
            }

            if(tot < bestAvailableSpots)
            {
                bestPoint = p;
                bestAvailableSpots = tot;
            }

        }

        return bestPoint;
    }

    public static  HashMap<Job, Integer> getAvailableEmployees(String company)
    {
        HashMap<Job, Integer> total = new HashMap<>();

        int devs = 0, mans = 0;

        if(!companies.contains(company)) return null;

        for(Person p : developers) {
            if(p.getPosition() != null) continue;

            if(p.getCompany().equals(company)) {
                devs++;
            }
        }

        total.put(Job.DEVELOPER, devs);


        for(Person p : projectManagers) {
            if(p.getPosition() != null) continue;

            if(p.getCompany().equals(company)) {
                mans++;
            }
        }
        total.put(Job.MANAGER, mans);

        return total;
    }

    public static HashMap<Job, Integer> getTotalCompanyEmployees(String company) {
        HashMap<Job, Integer> total = new HashMap<>();

        int devs = 0, managers = 0;

        for(Person p : developers) {
            if(p.getCompany().equals(company)) devs++;
        }

        total.put(Job.DEVELOPER, devs);


        for(Person p : projectManagers) {
            if(p.getCompany().equals(company)) managers++;
        }
        total.put(Job.MANAGER, managers);

        return total;
    }

    public static HashMap<Job, List<Point>> getAdjacentPoints(Point center) {
        HashMap<Job, List<Point>> selectedPoints = new HashMap<>();

        List<Point> managers = new ArrayList<>();
        List<Point> devs = new ArrayList<>();

        for(Point p : availablePoints.keySet()) {

            if(p.getY() == center.getY() && p.getX() == center.getX()) continue;

            int diffX = Math.abs(p.getX() - center.getX());
            int diffY = Math.abs(p.getY() - center.getY());

            if(diffY < 2 && diffX < 2) {
                if(availablePoints.get(p) == Job.MANAGER) {
                    managers.add(p);
                } else {
                    devs.add(p);
                }
            }
        }

        selectedPoints.put(Job.MANAGER, managers);
        selectedPoints.put(Job.DEVELOPER, devs);

        return selectedPoints;
    }
}