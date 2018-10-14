package com.salesforce.tests.dependency;

import java.util.*;

/**
 * The entry point for the Test program
 *
 *
 * https://github.com/nurolahzade/SystemDependencies
 */
public class Main {

    public static class Node {

        String key;
        Node parent;
        Set<Node> children;

        public Node(String key) {
            this.key = key;
            this.parent = null;
            children = new HashSet<>();
        }

        @Override
        public boolean equals(Object other) {
            return this.key.equals((String)other);
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

    }

    static Map<String, Node> componentMap = new HashMap<>();

    static Set<String> installed = new LinkedHashSet<>();



    public static void main(String[] args) {

        //read input from stdin
        Scanner scan = new Scanner(System.in);

        while (true) {
            String line = scan.nextLine();

            //no action for empty input
            if (line == null || line.length() == 0) {
                continue;
            }

            //the END command to stop the program
            if ("END".equals(line)) {
                System.out.println("END");
                break;
            }

            if(line.startsWith("DEPEND")) {
                System.out.println(line);
                String[] components = line.split(" ");

                // update data structure
                for(int i = components.length - 1; i >= 1; i--) {

                    String key = components[i].toUpperCase();
                    Node node = componentMap.get(key);

                    // Adding component for the first time
                    if(node == null) {
                        node = new Node(key);
                        componentMap.put(key, node);
                    }

                    // BFS
                    Queue<Node> queue = new LinkedList<>();
                    queue.add(node);

                    while(!queue.isEmpty()) {
                        Node temp = queue.poll();
                        Set<Node> children = temp.children;
                        for(Node child : children) {
                            if(node.key == child.key) {
                                System.out.println(temp.key + " depends on " + node.key + ", ignoring command");
                                return;
                            }
                            queue.add(child);
                        }
                    }

                    if(i <= components.length - 2) {
                        node.parent = componentMap.get(components[i+1]);
                    }
                }
            }

            if(line.startsWith("INSTALL")) {
                System.out.println(line);
                String[] components = line.split(" ");

                String key = components[1];
                Node node = componentMap.get(key);
                while(node != null) {
                    if(!installed.contains(node.key)) {
                        installed.add(node.key);
                        System.out.println("Installing " + node.key);
                    }
                    node = node.parent;
                }
            }

            if(line.startsWith("REMOVE")) {
                System.out.println(line);
                String[] components = line.split(" ");

                String key = components[1];
                Node node = componentMap.get(key);

                // BFS
                Queue<Node> queue = new LinkedList<>();
                queue.add(node);

                boolean remove = true;
                while(!queue.isEmpty()) {
                    Node temp = queue.poll();

                    Set<Node> children = temp.children;
                    for(Node child : children) {
                        if(installed.contains(child)) {
                            remove = false;
                            break;
                        }
                        queue.add(child);
                    }
                }

                if(remove && installed.contains(node.key)) {
                    installed.remove(node.key);
                    System.out.println("Removing " + node.key);
                } else {
                    System.out.println(node.key + " is still needed");
                }
            }

            if(line.startsWith("LIST")) {
                System.out.println(line);
                for(String str : installed) {
                    System.out.println(str);
                }
            }

        }

    }
}