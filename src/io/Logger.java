package io;
import algorithm.AlgorithmType;
import algorithm.BaseAlgorithm;
import model.Instance;
import model.Node;
import model.ResultSummary;
import model.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logger {

    public static Instance loadFromFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        String name = "";
        int capacity = 0;
        List<Node> cities = new ArrayList<>();
        Map<Integer, Integer> demandMap = new HashMap<>();
        int depotId = -1;

        enum Section {NONE, NODE_COORD, DEMAND, DEPOT}
        Section current = Section.NONE;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("NAME")) {
                name = line.split(":")[1].trim();
            } else if (line.startsWith("CAPACITY")) {
                capacity = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.startsWith("NODE_COORD_SECTION")) {
                current = Section.NODE_COORD;
            } else if (line.startsWith("DEMAND_SECTION")) {
                current = Section.DEMAND;
            } else if (line.startsWith("DEPOT_SECTION")) {
                current = Section.DEPOT;
            } else if (line.startsWith("EOF")) {
                break;
            } else {
                switch (current) {
                    case NODE_COORD -> {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            int id = Integer.parseInt(parts[0]);
                            int x = Integer.parseInt(parts[1]);
                            int y = Integer.parseInt(parts[2]);
                            cities.add(new Node(id, x, y, 0));
                        }
                    }
                    case DEMAND -> {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 2) {
                            int id = Integer.parseInt(parts[0]);
                            int demand = Integer.parseInt(parts[1]);
                            demandMap.put(id, demand);
                        }
                    }
                    case DEPOT -> {
                        int id = Integer.parseInt(line);
                        if (id == -1) current = Section.NONE;
                        else depotId = id;
                    }
                }
            }
        }
        reader.close();

        // Assign demands
        for (int i = 0; i < cities.size(); i++) {
            Node city = cities.get(i);
            Integer demand = demandMap.get(city.getId());
            if (demand != null) {
                cities.set(i, new Node(city.getId(), city.getX(), city.getY(), demand));
            }
        }

        return new Instance(name, capacity, cities, depotId);
    }


    public static List<Instance> loadAllFromDirectory(String dirPath) throws IOException {
        List<Instance> instances = new ArrayList<>();

        File folder = new File(dirPath);
        File[] files = folder.listFiles(( file) -> file.getName().toLowerCase().endsWith(".vrp"));

        if (files == null || files.length == 0) {
            System.err.println("No .vrp files found in: " + dirPath);
            return instances;
        }

        for (File file : files) {
            Instance instance = loadFromFile(file.getPath());
            instances.add(instance);
        }

        return instances;
    }

    public static void saveInstanceResultsToCSV(String instanceName, List<ResultSummary> summaries) throws IOException {
        String dir = "src/io/output/";
        File folder = new File(dir);

        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = dir + instanceName + ".csv";
        FileWriter writer = new FileWriter(fileName);
        writer.write(instanceName);
        writer.append("\nAlgorithm,Best,Worst,Average,Std\n");
        for (ResultSummary summary : summaries) {
            writer.append(String.format("%s,%.2f,%.2f,%.2f,%.2f\n",
                    summary.getAlgorithmName(),
                    summary.getBest(),
                    summary.getWorst(),
                    summary.getAvg(),
                    summary.getStd()));
        }

        writer.flush();
        writer.close();
        System.out.println("Saved results to " + fileName);
    }


    public static FileWriter createEvaluationFile(String instanceName, AlgorithmType algorithmType) {
        String dir = "src/io/output/" + algorithmType.name() + "/";
        File folder = new File(dir);
        FileWriter writer;
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = dir + instanceName + "_" + algorithmType.name() + "_" + timestamp + ".csv";
        try {
            writer = new FileWriter(fileName, false);
            writer.write("generation;best;average;worst\n");
            writer.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
        return writer;

    }

    public static void logGeneration(FileWriter writer, int generation, double best, double avg, double worst) throws IOException {
        writer.write(String.format("%d;%.2f;%.2f;%.2f%n", generation, best, avg, worst));
        writer.flush();
    }




}
