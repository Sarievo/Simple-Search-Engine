package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

interface QueryMethod {

    Set<Integer> fetch(HashMap<String, HashSet<Integer>> hashMap, String[] entries);
}

class QueryAll implements QueryMethod {

    @Override
    public Set<Integer> fetch(HashMap<String, HashSet<Integer>> hashMap, String[] entries) {
        Set<Integer> index = new LinkedHashSet<>(Set.of());

        for (String entry : entries) {
            if (hashMap.containsKey(entry)) {
                if (index.isEmpty()) {
                    index.addAll(hashMap.get(entry));
                } else {
                    index.retainAll(hashMap.get(entry));
                }
            }
        }
        return index;
    }
}

class QueryAny implements QueryMethod {

    @Override
    public Set<Integer> fetch(HashMap<String, HashSet<Integer>> hashMap, String[] entries) {
        Set<Integer> index = new LinkedHashSet<>();

        for (String entry : entries) {
            if (hashMap.containsKey(entry)) {
                index.addAll(hashMap.get(entry));
            }
        }
        return index;
    }
}

class QueryNone implements QueryMethod {

    @Override
    public Set<Integer> fetch(HashMap<String, HashSet<Integer>> hashMap, String[] entries) {
        Set<Integer> index = new LinkedHashSet<>();
        Set<Integer> temp = new LinkedHashSet<>();

        for (String entry : entries) {
            if (hashMap.containsKey(entry)) {
                temp.addAll(hashMap.get(entry));
            }
        }

        for (HashSet<Integer> set : hashMap.values()) {
            index.addAll(set);
        }
        index.removeAll(temp);
        return index;
    }
}

class Context {

    private QueryMethod queryMethod;

    public void setQueryMethod(QueryMethod queryMethod) {
        this.queryMethod = queryMethod;
    }

    public Set<Integer> fetch(HashMap<String, HashSet<Integer>> hashMap, String[] entries) {
        return this.queryMethod.fetch(hashMap, entries);
    }
}

public class Main {
    static Scanner sc = new Scanner(System.in);
    static HashMap<String, HashSet<Integer>> hashMap = new HashMap<>();
    static List<String> entryList = new ArrayList<>();

    public static void printMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }

    public static boolean querySelection(Context context) {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String choice = sc.nextLine().trim().toUpperCase();
        System.out.println();

        switch (choice) {
            case "ALL":
                context.setQueryMethod(new QueryAll());
                return true;
            case "ANY":
                context.setQueryMethod(new QueryAny());
                return true;
            case "NONE":
                context.setQueryMethod(new QueryNone());
                return true;
            default:
                System.out.println("Incorrect option! Try again.\n");
                return false;
        }
    }

    public static void search(Context context) {
        System.out.println("Enter a name or email to search all suitable people:");
        String[] entries = sc.nextLine().trim().toLowerCase().split("\\s");
        Set<Integer> resultIndex = context.fetch(hashMap, entries);

        if (!resultIndex.isEmpty()) {
            System.out.println("\nFound people:");
            for (Integer i : resultIndex) {
                System.out.println(entryList.get(i));
            }
            System.out.println();
        } else {
            System.out.println("No matching people found.\n");
        }
    }

    public static void printAll() {
        System.out.println("=== List of people ===");
        for (String str : entryList) {
            System.out.println(str);
        }
        System.out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        if ("--data".equals(args[0])) {
            File file = new File(args[1]);
            Scanner reader = new Scanner(file);

            int counter = 0;
            while (reader.hasNext()) {
                String line = reader.nextLine().trim();
                entryList.add(line);

                String[] parts = line.toLowerCase().split("\\s");
                for (String part : parts) {
                    HashSet<Integer> index = new HashSet<>();
                    if (hashMap.containsKey(part)) {
                        index = hashMap.get(part);
                    }
                    index.add(counter);
                    hashMap.put(part, index);
                }
                counter++;
            }
            reader.close();

            boolean flag = true;
            while (flag) {
                printMenu();
                String choice = sc.nextLine().trim();
                System.out.println();

                switch (choice) {
                    case "0":
                        flag = false;
                        break;
                    case "1":
                        Context context = new Context();
                        if (!querySelection(context)) {
                            continue;
                        } else {
                            search(context);
                            break;
                        }
                    case "2":
                        printAll();
                        break;
                    default:
                        System.out.println("Incorrect option! Try again.\n");
                }
            }
            System.out.println("Bye!");
        }
    }
}


