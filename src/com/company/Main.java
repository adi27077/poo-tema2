package com.company;

import java.util.ArrayList;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        Admin.init();
        Admin.printDictionary();

        testAddWord();
        testRemoveWord();
        testAddDefinition();
        testRemoveDefinition();
        testTranslateWord();
        testTranslateSentence();
        testTranslateSentences();
        testGetDefinitions();

        Admin.exportDictionary("fr");

    }

    static void testAddWord() {
        String word = "joc";
        String wordEn = "game";
        String type = "noun";
        String[] singular = {"joc"};
        String[] plural = {"jocuri"};
        String dict = "Dicționar de sinonime";
        String dictType = "synonyms";
        int year = 2002;
        String[] text = {"joacă","zbenguială","dispută"};
        Definition def1 = new Definition(dict, dictType, year, text);
        dict = "Dicționarul explicativ al limbii române, ediția a II-a";
        dictType = "definitions";
        year = 2009;
        text = new String[]{"Acțiunea de a se juca (1) și rezultatul ei"};
        Definition def2 = new Definition(dict, dictType, year, text);
        Definition[] defs = {def1, def2};
        Word w = new Word(word, null, wordEn, type, singular, plural, defs);

        if(Admin.addWord(w, "ro"))
            System.out.println("Cuvant adaugat");
        else
            System.out.println("Cuvantul exista deja");

        if(Admin.addWord(w, "ro"))
            System.out.println("Cuvant adaugat");
        else
            System.out.println("Cuvantul exista deja");
        Admin.printDictionary();
    }

    static void testRemoveWord() {
        if(Admin.removeWord("manger","fr"))
            System.out.println("Cuvant eliminat");
        else
            System.out.println("Cuvantul nu exista");

        if(Admin.removeWord("soare","ro"))
            System.out.println("Cuvant eliminat");
        else
            System.out.println("Cuvantul nu exista");
        Admin.printDictionary();
    }

    static void testAddDefinition() {
        String dict = "Dictionnaire oui oui";
        String dictType = "definitions";
        int year = 1969;
        String[] text = {"Terme d'injure"};
        Definition def = new Definition(dict, dictType, year, text);

        if(Admin.addDefinitionForWord("chien", "fr", def))
            System.out.println("Definitie adaugata");
        else
            System.out.println("Definitia exista deja sau cuvantul nu exista");

        if(Admin.addDefinitionForWord("chien", "fr", def))
            System.out.println("Definitie adaugata");
        else
            System.out.println("Definitia exista deja sau cuvantul nu exista");
        Admin.printDictionary();
    }

     static void testRemoveDefinition() {
        if(Admin.removeDefinition("chien", "fr", "Dictionnaire oui oui"))
            System.out.println("Definitie eliminata");
        else
            System.out.println("Definitia sau cuvantul nu exista");

        if(Admin.removeDefinition("chien", "fr", "Dictionnaire oui oui"))
            System.out.println("Definitie eliminata");
        else
            System.out.println("Definitia sau cuvantul nu exista");
        Admin.printDictionary();
    }

    static void testTranslateWord() {
        String test = Admin.translateWord("chien", "fr", "ro");
        System.out.println(Objects.requireNonNullElse(test, "Nu exista traducere"));
        test = Admin.translateWord("merge", "ro", "en");
        System.out.println(Objects.requireNonNullElse(test, "Nu exista traducere"));
        test = Admin.translateWord("merge", "ro", "fr");
        System.out.println(Objects.requireNonNullElse(test, "Nu exista traducere"));
    }

    static void testTranslateSentence() {
        String test = Admin.translateSentence("pisică joc câine", "ro", "fr");
        System.out.println(Objects.requireNonNullElse(test, "Nu exista traducere"));
        test = Admin.translateSentence("Ana are mere", "ro", "fr");
        System.out.println(Objects.requireNonNullElse(test, "Nu exista traducere"));
    }

    static void testTranslateSentences() {
        ArrayList<String> test = Admin.translateSentences("pisică joc câine", "ro", "fr");
        if(test != null) {
            for(String sentence : test) {
                System.out.println(sentence);
            }
        } else {
            System.out.println("Nu exista traduceri");
        }
        test = Admin.translateSentences("chien chat jeu", "fr", "ro");
        if(test != null) {
            for(String sentence : test) {
                System.out.println(sentence);
            }
        } else {
            System.out.println("Nu exista traduceri");
        }
        test = Admin.translateSentences("chien manger chat", "fr", "ro");
        if(test != null) {
            for(String sentence : test) {
                System.out.println(sentence);
            }
        } else {
            System.out.println("Nu exista traduceri");
        }
    }

    static void testGetDefinitions() {
        ArrayList<Definition> test = Admin.getDefinitionsForWord("câine", "ro");
        if(test != null) {
            for(Definition def : test) {
                System.out.println(def.toString());
            }
        } else {
            System.out.println("Nu exista definitii sau nu exista cuvantul");
        }

        test = Admin.getDefinitionsForWord("urs", "ro");
        if(test != null) {
            for(Definition def : test) {
                System.out.println(def.toString());
            }
        } else {
            System.out.println("Nu exista definitii sau nu exista cuvantul");
        }

    }
}
