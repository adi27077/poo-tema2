package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Word {
    private String word;
    private String language;
    private String wordEn;
    private String type;
    private String[] singular;
    private String[] plural;
    private Definition[] definitions;

    public Word(String word, String language, String wordEn, String type,
                String[] singular, String[] plural, Definition[] definitions) {
        this.word = word;
        this.language = language;
        this.wordEn = wordEn;
        this.type = type;
        this.singular = singular;
        this.plural = plural;
        this.definitions = definitions;
    }

    public String getWord() {
        return word;
    }

    public String getLanguage() {
        return language;
    }

    public String getWordEn() {
        return wordEn;
    }

    public Definition[] getDefinitions() {
        return definitions;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean addDefiniton(Definition definition) {
        for(Definition def : definitions) {
            if(def.getDict().equals(definition.getDict()))
                return false;
        }

        Definition[] definitionsNew = Arrays.copyOf(definitions, definitions.length + 1);
        definitionsNew[definitionsNew.length - 1] = definition;
        definitions = definitionsNew;
        return true;
    }

    public boolean remDefinition(String dict) {
        int indexToRemove = -1;
        for(int i = 0; i < definitions.length; i++) {
            if(definitions[i].getDict().equals(dict)) {
                indexToRemove = i;
                break;
            }
        }

        if(indexToRemove == -1)
            return false;

        ArrayList<Definition> definitionsList = new ArrayList<>(Arrays.stream(definitions).toList());
        definitionsList.remove(indexToRemove);
        Definition[] definitionsNew = new Definition[definitions.length - 1];
        definitionsList.toArray(definitionsNew);
        definitions = definitionsNew;
        return true;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Word{");
        sb.append("word='").append(word).append('\'');
        sb.append(",\nlanguage='").append(language).append('\'');
        sb.append(",\nwordEn='").append(wordEn).append('\'');
        sb.append(",\ntype='").append(type).append('\'');
        sb.append(",\nsingular=").append(singular == null ? "null" : Arrays.asList(singular).toString());
        sb.append(",\nplural=").append(plural == null ? "null" : Arrays.asList(plural).toString());
        sb.append(",\ndefinitions=").append(definitions == null ? "null" : Arrays.asList(definitions).toString());
        sb.append("}\n");
        return sb.toString();
    }
}
