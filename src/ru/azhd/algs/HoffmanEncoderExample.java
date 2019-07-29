package ru.azhd.algs;

import java.util.*;
import java.util.stream.Collectors;

public class HoffmanEncoderExample {

    public static void main(String... args) {
        String text = null;
        try {
            text = "Hello World!";//args[0];
            HoffmanEncoderExample hoffmanEncoderExample = new HoffmanEncoderExample();
            hoffmanEncoderExample.start(text);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Enter thr path of file");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String txt) {
        Map<Character, Integer> worldCountMap = new HashMap<>();
        for (char ch : txt.toCharArray())
            worldCountMap.put(ch, Optional.ofNullable(worldCountMap.get(ch)).map(Math::incrementExact).orElse(1));
        Set<HoffmanEntry> hoffmanEntries = worldCountMap.entrySet().stream()
                .map(es -> new HoffmanEntry(new HashSet<>(Arrays.asList(es.getKey())), es.getValue()))
                .collect(Collectors.toSet());
        HoffmanEncoder handler = new HoffmanEncoder(hoffmanEntries);
        Collection<HoffmanEntry> hoffmanCodeTable = handler.start();
        hoffmanCodeTable.forEach(he -> System.out.println(he.getCharactersAsString() + " -> " + he.getFreq() + " -> " + he.getCode()));
        Map<Character, String> charCodeBindings = new HashMap<>();
        for (HoffmanEntry hoffmanEntry : hoffmanCodeTable) {
            charCodeBindings.put(hoffmanEntry.getCharactersAsString().charAt(0), hoffmanEntry.getCode());
        }
        StringBuilder resultBuilder = new StringBuilder();
        for (char ch : txt.toCharArray()) {
            String code = charCodeBindings.get(ch);
            resultBuilder.append(code);
        }

        System.out.println("------------------------------------------\n");
        System.out.println(resultBuilder);

    }

    private class HoffmanEncoder {

        private Set<HoffmanEntry> entries;

        HoffmanEncoder(Set<HoffmanEntry> entries) {
            this.entries = entries;
        }

        Collection<HoffmanEntry> start() {
            List<HoffmanNode> nodes = entries.stream().map(HoffmanNode::new).collect(Collectors.toList());
            handlroots(nodes);
            return nodes.stream()
                    .filter(n -> n.getRightChild() == null && n.getLeftChild() == null)
                    .map(hoffmanNode -> hoffmanNode.getValue()).collect(Collectors.toList());
        }

        private void handlroots(List<HoffmanNode> roots) {
            List<HoffmanNode> lrNodes = roots.stream()
                    .filter(hn -> hn.getRoot() == null)
                    .sorted(Comparator.comparingInt(hn -> hn.getValue().getFreq()))
                    .limit(2)
                    .collect(Collectors.toList());
            if (lrNodes.size() == 1) {
                HoffmanNode root = lrNodes.get(0);
                computeCodes(root);
                return;
            }
            HoffmanNode rightNode = lrNodes.get(0);
            HoffmanNode leftNode = lrNodes.get(1);
            Set<Character> values = new HashSet<>(leftNode.getValue().getCharacters());
            values.addAll(rightNode.getValue().getCharacters());
            Integer freq = leftNode.getValue().getFreq() + rightNode.getValue().getFreq();
            HoffmanEntry rootEntry = new HoffmanEntry(values, freq);
            HoffmanNode root = new HoffmanNode(rootEntry);
            root.addLeftLeaf(leftNode);
            root.addRightLeaf(rightNode);
            roots.add(root);

            handlroots(roots);
        }

        void computeCodes(HoffmanNode root) {
            String selfCode = Optional.ofNullable(root.getValue().getCode()).orElse("");
            HoffmanNode right = root.getRightChild();
            if (right == null)
                return;
            right.getValue().setCode(selfCode, "1");
            computeCodes(right);
            HoffmanNode left = root.getLeftChild();
            left.getValue().setCode(selfCode, "0");
            computeCodes(left);
        }

    }

    private class HoffmanEntry {

        private Set<Character> characters;
        private Integer freq;
        private String code;

        HoffmanEntry(Set<Character> characters, Integer freq) {
            this.characters = characters;
            this.freq = freq;
        }

        Set<Character> getCharacters() {
            return characters;
        }

        String getCharactersAsString() {
            StringBuilder stringBuilder = new StringBuilder();
            characters.forEach(stringBuilder::append);
            return stringBuilder.toString();
        }

        Integer getFreq() {
            return freq;
        }

        String getCode() {
            return code;
        }

        void setCode(String rootCode, String leafCode) {
            this.code = rootCode + leafCode;
        }

    }

    class HoffmanNode {

        HoffmanNode root;
        HoffmanNode leftChild;
        HoffmanNode rightChild;
        HoffmanEntry value;

        HoffmanNode(HoffmanEntry value) {
            this.value = value;
        }

        void addRoot(HoffmanNode node) {
            this.root = node;
        }

        void addRightLeaf(HoffmanNode node) {
            node.addRoot(this);
            this.rightChild = node;
        }

        void addLeftLeaf(HoffmanNode node) {
            node.addRoot(this);
            this.leftChild = node;
        }

        HoffmanNode getRoot() {
            return root;
        }

        HoffmanNode getLeftChild() {
            return leftChild;
        }

        HoffmanNode getRightChild() {
            return rightChild;
        }

        HoffmanEntry getValue() {
            return value;
        }
    }

}
