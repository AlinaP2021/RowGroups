import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class RowGroups {

    public static void main(String[] args) throws IOException {
        long time0 = System.currentTimeMillis();
        Path path = Paths.get("C:\\Users\\alina\\Downloads\\lng-big\\lng-big.csv");
        List<String> linesList = new ArrayList<>(Files.lines(path).collect(Collectors.toSet()));
        Map<String, Integer> partGroupNumbers = new HashMap<>();
        List<Set<String>> groups = new ArrayList<>();
        Map<Integer, Integer> mergedGroups = new HashMap<>();
        int index = 0;

        linesList.removeIf(str -> !str.matches("(\"[\\d.]*\"|);(\"[\\d.]*\"|);(\"[\\d.]*\"|)"));

        int size = linesList.size();
        for (int i = 0; i < size; i++) {
            String line = linesList.get(i);
            String[] parts = line.split(";");
            int groupNumber = -1;
            Set<String> newParts = new HashSet<>();

            for (int j = 0; j < parts.length; j++) {
                String part = parts[j];
                if (!part.matches("\"\"|")) {
                    Integer partGroupNumber = partGroupNumbers.get(part);
                    if (partGroupNumber != null) {
                        while (mergedGroups.containsKey(partGroupNumber)) {
                            partGroupNumber = mergedGroups.get(partGroupNumber);
                        }
                        if (groupNumber == -1) {
                            groupNumber = partGroupNumber;
                        } else if (groupNumber != partGroupNumber) {
                            mergedGroups.put(partGroupNumber, groupNumber);
                            groups.get(groupNumber).addAll(groups.get(partGroupNumber));
                            groups.set(partGroupNumber, null);
                        }
                    } else {
                        newParts.add(part);
                    }
                }
            }

            if (groupNumber == -1) {
                groupNumber = index++;
                groups.add(new HashSet<>());
            }
            for (String newPart : newParts) {
                partGroupNumbers.put(newPart, groupNumber);
            }
            groups.get(groupNumber).add(line);
        }

        groups.removeAll(Collections.singleton(null));

        FileWriter writer = new FileWriter("C:\\Users\\alina\\Downloads\\lng-big\\lng-big1.csv");
        int count = (int) groups.stream()
                .filter(group -> group.size() > 1).count();
        writer.write("Number of groups whose size > 1: " + count + "\n");

        groups.sort((o1, o2) -> o2.size() - o1.size());

        size = groups.size();
        for (int i = 0; i < size; i++) {
            Set<String> group = groups.get(i);
            writer.write("Group " + (i + 1) + "\n");
            for (String str : group) {
                writer.write(str + "\n");
            }
        }
        writer.close();

        long time1 = System.currentTimeMillis();
        System.out.println("Number of groups whose size > 1: " + count);
        System.out.println("Number of groups: " + groups.size());
        System.out.println("Group1 size: " + groups.get(0).size());
        System.out.println("Group2 size: " + groups.get(1).size());
        System.out.println("Time: " + (time1 - time0) / 1000);
    }

}