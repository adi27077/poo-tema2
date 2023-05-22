package com.company;

import com.google.gson.*;

import java.io.*;
import java.util.*;

public class Admin {
    private static final HashMap<String, Word> words = new HashMap<>();

    public static void init() {
        //citire toate fisierele .json din radacina proiectului
        File path = new File(".");
        File[] fileList = path.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if(fileList != null) {
            for (File file : fileList) {
                StringBuffer sb = new StringBuffer();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = sb.toString();

                //parsare string ce contine json si creare obiecte Word si Definition
                JsonArray jsonWords = new JsonParser().parse(jsonString).getAsJsonArray();

                for(int i = 0; i < jsonWords.size(); i++) {
                    JsonObject wordObj = jsonWords.get(i).getAsJsonObject();
                    //setare limba in functie de numele fisierului
                    String language = null;
                    if(file.getName().startsWith("ro"))
                        language = "ro";
                    if(file.getName().startsWith("fr"))
                        language = "fr";

                    //parsare word, word_en si type
                    String word = wordObj.get("word").getAsString();
                    String wordEn = wordObj.get("word_en").getAsString();
                    String type = wordObj.get("type").getAsString();

                    //parsare array singular
                    JsonArray singularArr= wordObj.getAsJsonArray("singular");
                    String[] singular = new String[singularArr.size()];
                    for(int j = 0; j < singularArr.size(); j++) {
                        singular[j] = singularArr.get(j).getAsString();
                    }

                    //parsare array plural
                    JsonArray pluralArr = wordObj.getAsJsonArray("plural");
                    String[] plural = new String[pluralArr.size()];
                    for(int j = 0; j < pluralArr.size(); j++) {
                        plural[j] = pluralArr.get(j).getAsString();
                    }

                    //parsare array definitions
                    JsonArray definitionArr = wordObj.getAsJsonArray("definitions");
                    Definition[] definitions = new Definition[definitionArr.size()];
                    for(int j = 0; j < definitionArr.size(); j++) {
                        JsonObject definitionObj = definitionArr.get(j).getAsJsonObject();
                        //parsare dict, dictType si year
                        String dict = definitionObj.get("dict").getAsString();
                        String dictType = definitionObj.get("dictType").getAsString();
                        int year = definitionObj.get("year").getAsInt();

                        //parsare array text
                        JsonArray textArr = definitionObj.getAsJsonArray("text");
                        String[] text = new String[textArr.size()];
                        for(int k = 0; k < textArr.size(); k++) {
                            text[k] = textArr.get(k).getAsString();
                        }

                        definitions[j] = new Definition(dict, dictType, year, text);
                    }

                    Word w = new Word(word, language, wordEn, type, singular, plural, definitions);
                    words.put(word, w);
                }
            }
        }

    }

    public static boolean addWord(Word word, String language) {
        word.setLanguage(language);
        //verificare daca exista deja cuvantul
        if(words.containsValue(word))
            return false;
        else
            words.put(word.getWord(), word);
        return true;
    }

    public static boolean removeWord(String word, String language) {
        //verificare daca exista cuvantul de eliminat
        if(words.containsKey(word) && language.equals(words.get(word).getLanguage())) {
            words.remove(word);
            return true;
        }
        return false;
    }

    public static boolean addDefinitionForWord(String word, String language, Definition definition) {
        Word w = words.get(word);
        //verificare daca exista cuvant in dictionar
        if(w != null && w.getLanguage().equals(language))
            return w.addDefiniton(definition);
        else
            return false;
    }

    public static boolean removeDefinition(String word, String language, String dictionary) {
        Word w = words.get(word);
        //verificare daca exista cuvant in dictionar
        if(w != null && w.getLanguage().equals(language))
            return w.remDefinition(dictionary);
        else
            return false;
    }

    public static String translateWord(String word, String fromLanguage, String toLanguage) {
        if(words.containsKey(word)) {
            Word w = words.get(word);
            if(!w.getLanguage().equals(fromLanguage))
                return null;
            //daca avem de tradus in engleza, luam pur si simplu cuvantul din campul word_en
            if(toLanguage.equals("en")) {
                return w.getWordEn();
            }

            //daca nu, cauta in dictionar cuvantul in limba de tradus dupa campul word_en
            for(Map.Entry<String, Word> wordEntry : words.entrySet()) {
                Word currentWord = wordEntry.getValue();
                if(currentWord.getWordEn().equals(w.getWordEn()) &&
                        currentWord.getLanguage().equals(toLanguage))
                    return currentWord.getWord();
            }

        }
        return null;
    }

    public static String translateSentence(String sentence, String fromLanguage, String toLanguage) {
        String[] tokens = sentence.split("[., !?-]");
        StringBuffer sb = new StringBuffer();

        //traducem pe rand fiecare cuvant din propozitie
        for(String token : tokens) {
            String word = translateWord(token, fromLanguage, toLanguage);
            if(word == null)
                return null;
            sb.append(word).append(" ");
        }
        return sb.toString();
    }

    private static ArrayList<String> translateWords(String word, String fromLanguage, String toLanguage) {
        if(words.containsKey(word)) {
            Word w = words.get(word);
            if(!w.getLanguage().equals(fromLanguage))
                return null;
            //daca avem de tradus in engleza, luam pur si simplu cuvantul din campul word_en
            if(toLanguage.equals("en")) {
                Definition[] defs = w.getDefinitions();
                int synonymsObtained = 0;
                ArrayList<String> result = new ArrayList<>();
                //pentru rezultatul traducerii vom avea cuvantul in sine si inca 2 sinonime daca exista
                result.add(w.getWordEn());
                return getSynonyms(defs, synonymsObtained, result);
            }

            //daca nu, cauta in dictionar cuvantul in limba de tradus dupa campul word_en
            for(Map.Entry<String, Word> wordEntry : words.entrySet()) {
                Word currentWord = wordEntry.getValue();
                if(currentWord.getWordEn().equals(w.getWordEn()) &&
                        currentWord.getLanguage().equals(toLanguage)) {
                    Definition[] defs = currentWord.getDefinitions();
                    int synonymsObtained = 0;
                    ArrayList<String> result = new ArrayList<>();
                    //pentru rezultatul traducerii vom avea cuvantul in sine si inca 2 sinonime daca exista
                    result.add(currentWord.getWord());
                    return getSynonyms(defs, synonymsObtained, result);
                }
            }
        }
        return null;
    }

    private static ArrayList<String> getSynonyms(Definition[] defs,
                                                 int synonymsObtained, ArrayList<String> result) {
        //cautare sinonime in definitiile cuvantului
        for(Definition def : defs) {
            //avem nevoie de 2 sinonime maxim
            if (synonymsObtained == 2)
                break;
            if (def.getDictType().equals("synonyms")) {
                String[] synonyms = def.getText();
                for (String syn : synonyms) {
                    if (synonymsObtained == 2)
                        break;
                    result.add(syn);
                    synonymsObtained++;
                }
            }
        }

        return result;
    }

    public static ArrayList<String> translateSentences(String sentence,
                                                       String fromLanguage, String toLanguage) {
        String[] tokens = sentence.split("[., !?-]");
        ArrayList<StringBuffer> resultSb = new ArrayList<>();
        for(String token : tokens) {
            //obtinem traducerea si sinonimele fiecarui cuvant
            ArrayList<String> translations = translateWords(token, fromLanguage, toLanguage);
            if(translations == null)
                return null;
            if(resultSb.isEmpty()) {
                //adaugam prima varianta de traducere a cuvantului la prima varianta a propozitiei
                resultSb.add(new StringBuffer(translations.get(0)));
                //daca avem cel putin inca un sinonim, il adaugam la a doua varianta a propozitiei
                if(translations.size() > 1)
                    resultSb.add(new StringBuffer(translations.get(1)));
                //daca nu adaugam acelasi cuvant la a doua varianta
                else
                    resultSb.add(new StringBuffer(translations.get(0)));
                //daca avem si al treilea sinonim il adaugam la a treia varianta a propozitiei
                if(translations.size() > 2)
                    resultSb.add(new StringBuffer(translations.get(2)));
                //daca nu adaugam cuvantul initial la a treia varianta
                else
                    resultSb.add(new StringBuffer(translations.get(0)));
                continue;
            }
            //concatenare restul cuvintelor la cele 3 variante de propozitii, similar cu mai sus
            resultSb.get(0).append(" ").append(translations.get(0));
            if(translations.size() > 1)
                resultSb.get(1).append(" ").append(translations.get(1));
            else
                resultSb.get(1).append(" ").append(translations.get(0));
            if(translations.size() > 2)
                resultSb.get(2).append(" ").append(translations.get(2));
            else
                resultSb.get(2).append(" ").append(translations.get(0));

        }

        ArrayList<String> result = new ArrayList<>();
        for(StringBuffer sb : resultSb)
            result.add(sb.toString());

        /*
        verificare daca avem variante identice de traducere printre cele 3 rezultate
        daca da le eliminam
        */
        if(result.get(2).equals(result.get(1)) || result.get(2).equals(result.get(0)))
            result.remove(2);
        if(result.get(1).equals(result.get(0)))
            result.remove(1);

        return result;
    }

    public static ArrayList<Definition> getDefinitionsForWord(String word, String language) {
        Word w = words.get(word);
        ArrayList<Definition> definitions;
        if(w != null && w.getLanguage().equals(language)) {
            //obtinere si sortate definitii in functie de anul dictionarului
            definitions = new ArrayList<>(List.of(w.getDefinitions()));
            definitions.sort(Comparator.comparingInt(Definition::getYear));
            return definitions;
        } else
            return null;
    }

    public static void printDictionary() {
        //afisarea intregului dictionar
        for(Map.Entry<String, Word> entry : words.entrySet()) {
            System.out.println(entry.getValue().toString());
        }
    }

    public static void exportDictionary(String language) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(words.values().toArray());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(language + "_outDict.out"));
            bw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
