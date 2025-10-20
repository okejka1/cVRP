package io;
import model.Instance;
import model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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




}
