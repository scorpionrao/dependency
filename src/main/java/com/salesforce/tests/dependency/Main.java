package com.salesforce.tests.dependency;

import java.util.*;

/**
 * The entry point for the Test program
 *
 *
 * https://github.com/nurolahzade/SystemDependencies
 */
public class Main {

    public static class HierarchyStore {

        String grandParent = null;
        String grandChild = null;

        public HierarchyStore() {}

        public void setGrandParent(String grandParent) {
            this.grandParent = grandParent;
        }

        public void setGrandChild(String grandChild) {
            this.grandChild = grandChild;
        }

    }

    public static class Component {

        public String name;
        public List<HierarchyStore> hierarchyStoreList;

        Component(String name) {
            this.name = name;
            this.hierarchyStoreList = new ArrayList<>();
        }
    }

    public static Map<String, List<Component>> graph = new HashMap<>();

    public static Set<String> installed = new LinkedHashSet<>();

    public static boolean isDependent(String preRequisite, String course) {
        // pre-requisite=NETCARD, course=TCPIP
        List<Component> components = graph.get(preRequisite);
        if(components == null) {
            return false;
        }
        for(Component component : components) {
            if(component.name.equals(course)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addDependency(String preRequisite, String course) {

        boolean willThisCreateCycle = isDependent(course, preRequisite);

        if(willThisCreateCycle) {
            // ignore scenario
            return false;
        }

        // Definitely prerequisite will be created at the end of this method
        List<Component> components = graph.get(preRequisite);
        if(components == null) {
            components = new ArrayList<>();
            graph.put(preRequisite, components);
        }

        for (Component component : components) {
            if (component.name.equals(course)) {
                // dependency already exists, nothing to do
                return true;
            }
        }
        // simple add
        components.add(new Component(course));
        return true;
    }
/*
    public static void install(String component) {

        for(String preRequisite : component.preRequisites()) {
            if(!installed.contains(preRequisite)) {
                install(preRequisite);
            }
        }
        installed.add(component);
        System.out.println(component);
    }

    public static void install(String component) {

        for(String preRequisite : component.preRequisites()) {
            if(!installed.contains(preRequisite)) {
                install(preRequisite);
            }
        }
        installed.add(component);
        System.out.println(component);
    }

    public static void remove(String component) {


        boolean isMyDependentsInstalled = false;

        for(String dependent : component.dependents()) {
            if(installed.contains(dependent) {
                return false;
            }
        }

        if(isMyImmediateDependentInvolved) {
            System.out.println(component + " is still needed");
        } else {
            System.out.println("Removing " + component);
            remove(component.immediateDependent());
        }
    }
    */

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
                for(int i = components.length - 1; i >= 2; i--) {
                    boolean check = addDependency(components[i], components[i - 1]);
                    if(!check) {
                        System.out.println(components[i] + " depends on " +
                                           components[i - 1] + ", ignoring command");
                        break;
                    }
                }
            }
/*
            if(line.startsWith("INSTALL")) {
                System.out.println(line);
                String[] components = line.split(" ");

                String key = components[1];
                Node node = componentMap.get(key);

                Stack<Node> stack = new Stack<>();
                stack.add(node);

                while(!stack.isEmpty()) {
                    Node temp = stack.peek();
                    if(temp.parent != null) {
                        stack.push(temp.parent);
                    } else {
                        Node pop = stack.pop();
                        if(!installed.contains(pop.key)) {
                            installed.add(pop.key);
                            System.out.println("Installing " + pop.key);
                        }
                    }
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
            */

        }

    }
}