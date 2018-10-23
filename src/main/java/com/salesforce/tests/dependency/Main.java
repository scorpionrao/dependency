package com.salesforce.tests.dependency;

import java.util.*;

/**
 * The entry point for the Test program
 *
 *
 * https://github.com/nurolahzade/SystemDependencies
 */
public class Main {

    // Classes
    public static class Hierarchy {

        String grandParent = null;
        String grandChild = null;

        public void setGrandParent(String grandParent) {
            this.grandParent = grandParent;
        }

        public void setGrandChild(String grandChild) {
            this.grandChild = grandChild;
        }

    }

    public static class Node {

        public String name;
        public List<Hierarchy> hierarchyStoreList;

        public Node(String name) {
            this.name = name;
            this.hierarchyStoreList = new ArrayList<>();
        }
    }

    // Static members
    public static Map<String, Node> mapForAccess = new HashMap<>();

    public static Map<String, List<Node>> courseGraph = new HashMap<>();

    public static Set<String> installed = new LinkedHashSet<>();


    public static boolean isDependent(String preRequisiteName, String courseName) {
        // no information
        if(preRequisiteName == null
        || courseName == null
        || courseGraph.get(preRequisiteName) == null
        || courseGraph.get(courseName) == null) {
            return false;
        }

        // already in system, nothing to do, checks only ONE level for now
        List<Node> nodeList = courseGraph.get(preRequisiteName);
        for(Node node : nodeList) {
            if(node.name.equals(courseName)) {
                return true;
            }
        }
        // requested dependency is not yet defined
        return false;
    }

    public static void depend(String[] softwareNames) {

        for(int i = softwareNames.length - 1; i >= 0; i--) {

            String softwareName = softwareNames[i];
            if(softwareName == null) {
                throw new NullPointerException();
            }

            Node softwareNode = mapForAccess.get(softwareName);

            if(softwareNode == null) {
                softwareNode = new Node(softwareName);
                mapForAccess.put(softwareName, softwareNode);
                courseGraph.put(softwareName, new ArrayList<>());
            } else {
                softwareNode = mapForAccess.get(softwareName);
            }

            if(i == softwareNames.length - 1) {
                // root node
                boolean isCycleCreator = isDependent(softwareNames[i], softwareNames[i+1]);
                if(isCycleCreator) {
                    System.out.println(softwareNames[i+1] + " depends on " + softwareNames[i] + ", ignoring command");
                    break;
                }

                boolean exist = false;
                for(Hierarchy hierarchy : softwareNode.hierarchyStoreList) {
                    if(hierarchy.grandChild.equals(softwareNames[i-1])) {
                        exist = true;
                    }
                }

                // create {NULL / [i-1]} exist
                if(!exist) {
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.grandParent = null;
                    hierarchy.grandChild = softwareNames[i-1];
                    softwareNode.hierarchyStoreList.add(hierarchy);
                }
                // no check for dependency
                // no updates to courseGraph

            } else if (i < softwareNames.length - 1 && i > 0) {
                // middle element

                boolean isCycleCreator = isDependent(softwareNames[i], softwareNames[i+1]);
                if(isCycleCreator) {
                    System.out.println(softwareNames[i+1] + " depends on " + softwareNames[i] + ", ignoring command");
                    break;
                }
                boolean exist = false;
                for(Hierarchy hierarchy : softwareNode.hierarchyStoreList) {
                    if(hierarchy.grandChild.equals(softwareNames[i-1]) && hierarchy.grandParent.equals(softwareNames[i+1])) {
                        exist = true;
                    }
                }

                // create {[i+1] / NULL} exist
                if(!exist) {
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.grandParent = softwareNames[i+1];
                    hierarchy.grandChild = softwareNames[i-1];
                    softwareNode.hierarchyStoreList.add(hierarchy);
                }
                List<Node> nodeList = courseGraph.get(softwareNames[i+1]);
                nodeList.add(mapForAccess.get(softwareNames[i]));
            } else {
                // leaf (i == 0)
                boolean isDependencyAlreadyDefined = isDependent(softwareNames[i+1], softwareNames[i]);
                if(isDependencyAlreadyDefined) {
                    continue;
                }

                boolean isCycleCreator = isDependent(softwareNames[i], softwareNames[i+1]);
                if(isCycleCreator) {
                    System.out.println(softwareNames[i+1] + " depends on " + softwareNames[i] + ", ignoring command");
                    break;
                }
                boolean exist = false;
                for(Hierarchy hierarchy : softwareNode.hierarchyStoreList) {
                    if(hierarchy.grandParent.equals(softwareNames[i+1])) {
                        exist = true;
                    }
                }

                // create {[i+1] / NULL} exist
                if(!exist) {
                    Hierarchy hierarchy = new Hierarchy();
                    hierarchy.grandParent = softwareNames[i+1];
                    hierarchy.grandChild = null;
                    softwareNode.hierarchyStoreList.add(hierarchy);
                }
                List<Node> nodeList = courseGraph.get(softwareNames[i+1]);
                nodeList.add(mapForAccess.get(softwareNames[i]));
            }

        }
    }

    public static void install(String softwareName) {
        if(softwareName == null) {
            return;
        }

        Node node = mapForAccess.get(softwareName);

        if(node == null) {
            node = new Node(softwareName);
            mapForAccess.put(softwareName, node);
            courseGraph.put(softwareName, new ArrayList<>());
        }

        for(Hierarchy hierarchy : node.hierarchyStoreList) {
            String grandParent = hierarchy.grandParent;
            if(grandParent != null) {
                // keep going up
                install(grandParent, softwareName);
            }
        }

        if(!installed.contains(softwareName)) {
            installed.add(softwareName);
            System.out.println("Installing " + softwareName);
        } else {
            System.out.println(softwareName + " is already installed");
        }
    }

    public static void install(String softwareName, String requestingChild) {
        if(softwareName == null) {
            return;
        }

        Node node = mapForAccess.get(softwareName);

        for(Hierarchy hierarchy : node.hierarchyStoreList) {
            if(hierarchy.grandChild.equals(requestingChild)) {
                String grandParent = hierarchy.grandParent;
                if(grandParent != null) {
                    // keep going up
                    install(grandParent, softwareName);
                }
            }
        }
        if(!installed.contains(softwareName)) {
            installed.add(softwareName);
            System.out.println("Installing " + softwareName);
        }
    }

    public static void remove(String softwareName, boolean isHardRemove) {
        if(softwareName == null) {
            return;
        }

        if(!installed.contains(softwareName)) {
            System.out.println(softwareName + " is not installed");
        }

        Node node = mapForAccess.get(softwareName);

        boolean isRemovable = true;

        for(Hierarchy hierarchy : node.hierarchyStoreList) {
            // root prerequisite
            String grandChild = hierarchy.grandChild;
            if(grandChild != null && installed.contains(grandChild)) {
                isRemovable = false;
                break;
            }
        }

        if(isRemovable) {
            installed.remove(softwareName);
            System.out.println("Removing " + softwareName);

            for(Hierarchy hierarchy : node.hierarchyStoreList) {
                String grandParent = hierarchy.grandParent;
                if(grandParent != null && installed.contains(grandParent)) {
                    remove(grandParent, false);
                }
            }
        } else if (isHardRemove) {
            System.out.println(softwareName + " is still needed");
        }
    }

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
                String[] inputArray = line.split(" ");
                String[] softwareNames = Arrays.copyOfRange(inputArray, 1, inputArray.length);
                depend(softwareNames);
            }

            if(line.startsWith("INSTALL")) {
                System.out.println(line);
                String[] inputArray = line.split(" ");
                install(inputArray[1]);
            }

            if(line.startsWith("REMOVE")) {
                System.out.println(line);
                String[] inputArray = line.split(" ");
                remove(inputArray[1], true);
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