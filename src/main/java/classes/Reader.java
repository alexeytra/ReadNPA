package classes;

import org.openxmlformats.schemas.drawingml.x2006.main.STAdjCoordinate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
    private String Path;
    private static final String CHAPTER = "Глава";
    private static final String APPLICATION = "Приложение";
    private static final String ARTICLE = "Статья";
    private static final String INDENT = "Абзац";
    private static final String ITEM = "Пункт";
    private static final String PART = "Часть";
    private static final String SUBITEM = "Подпункт";
    private static final String SECTION = "Раздел";
    private static final String REALESE = "Выпуск";
    private static final String PARAGRAPH = "Параграф";
    private static final String SUBSECTION = "Подраздел";

    private List<Element> listElements;
    private List<NPA> listNpa;

    private boolean withNameSection = false;




    public Reader(String path){
        this.Path = path;
    }

    public void ReadFile() throws IOException {
        listElements = new ArrayList<Element>();
        String[] str = new String[4];
        Arrays.fill(str, "");
        int count = 0;
        boolean isArticle;
        boolean isSection;
        boolean isPar;
        boolean isItem;
        boolean isSubitem;
        boolean isUpIndent = false; // Используем когда абзац -> пункты

        try{
            FileInputStream fstream = new FileInputStream(Path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream,  "Cp1251"));
            String strLine;
            while ((strLine = br.readLine()) != null){
                if (strLine.equals("") || strLine.charAt(0) == '(') continue;
                int i = 0;

                isSection = false;
                isItem = false;
                isSubitem = false;

                for (String retval : strLine.split("\\s", 2)){
                    if (testRoman(retval.replaceAll("\\.", "")) || isSection){
                        str[i] = retval;
                        isSection = true;
                        i++;
                        str[3] = SECTION;
                    }else if (retval.equals("Раздел")){
                        withNameSection = true;
                        str[2] = strLine;
                        str[3] = SECTION;
                        break;
                    } else if (retval.contains("Глава")) {
                        str[2] = strLine;
                        str[3] = CHAPTER;
                        break;
                    } else if(retval.contains("Статья")){
                        str[2] = strLine;
                        str[3] = ARTICLE;
                        isUpIndent = false;
                        break;
                    //}else if (testLetterItem(retval) || isItem || testNumeral(retval) /*|| isUpIndent*/) {
                    }else if ( isItem || testNumeral(retval) /*|| isUpIndent*/) {
                        str[i] = retval;
                        isItem = true;
                        i++;
                        str[3] = ITEM;
                    //}else if (testNumItem(retval) || isSubitem ){
                    }else if (testNumItem(retval) || isSubitem || testLetterItem(retval)){
                        str[i] = retval;
                        isSubitem = true;
                        i++;
                        str[3] = SUBITEM;
                    } else {
                        isUpIndent = false;
                        if(testLetter(String.valueOf(retval.charAt(0)))) {
                            str[2] = strLine;
                            str[3] = INDENT;
                            break;
                        }else if (strLine.charAt(strLine.length() - 1) == ';'){
                            str[2] = strLine;
                            str[3] = INDENT;
                            break;
                        }else {
                            //if (testColon(strLine)) isUpIndent = true;
                            str[2] = strLine;
                            str[3] = INDENT;
                            break;
                        }

                    }

                    if(isSection && i == 2) {
                        br.mark(999);
                        String s = br.readLine();
                        if(!s.equals("Статья")) str[1] += " " + s; else br.reset();
                    }

                }

                //System.out.println(str[0] + " " + str[1] + " " + str[2] + " " + str[3]);
                listElements.add(new Element(str[0], str[1], str[2], str[3]));
                Arrays.fill(str, "");

            }
        }catch (IOException e){
            System.out.println("Ошибка");
        }

        createListNPA();
        WriteExcel.WriteToExcel(listNpa);

    }

    private void createListNPA(){
        listNpa = new ArrayList<NPA>();

        boolean existSection = false;
        boolean existArticle = false;
        boolean existPart = false;
        boolean existIndent = false;
        boolean existItem = false;
        boolean existSubitem = false;
        boolean existChapter = false;
        boolean existUpIndent = false;

        boolean endItem = false;
        boolean continueItem = false;

        Float sequenceSection = 0f;
        Float sequenceArticle = 0f;
        Float sequencePart = 0f;
        Float sequenceIndent = 0f;
        Float sequenceItem = 0f;
        Float sequenceSubitem = 0f;
        Float sequenceChapter = 0f;
        Float sequenceSubIntent = 0f;

        int upLevel = 0;

        RomanNumeral r = new RomanNumeral();
        Float sequence = 1.0f;

        for (Element el: listElements) {
            if (el.getElementType().equals(SECTION)){ // Раздел
                existSection = true;
                upLevel = 0;


                sequenceSection++;
                sequenceArticle = 0f;
                sequencePart = 0f;
                sequenceIndent = 0f;
                sequenceItem = 0f;
                sequenceSubitem = 0f;
                sequenceSubIntent = 0f;

                existArticle = false;
                existPart = false;
                existItem = false;

                if (withNameSection){
                    String[] s = getRomanNum(el.getContent());
                    listNpa.add(new NPA(upLevel, SECTION, s[0].replaceAll("\\.$", ""), s[1], el.getName(), sequenceSection));
                }else {
                    String num = String.valueOf(r.convertRomanToInt(el.getNum().replaceAll("\\.", "")));
                    listNpa.add(new NPA(upLevel, SECTION, num, el.getName(), el.getContent(), sequenceSection));
                }

            }else if (el.getElementType().equals(ARTICLE)) { // Статья
                sequenceItem = 0f;
                sequenceIndent = 0f;
                sequencePart = 0f;
                sequenceSubIntent = 0f;
                existArticle = true;
                existItem = false;
                existUpIndent = false;

                endItem = false;

                if (existChapter || existSection) {
                    upLevel = 1;
                }else{
                    sequencePart = 0f;
                    sequenceIndent = 0f;
                    sequenceItem = 0f;
                    sequenceSubitem = 0f;
                    sequenceSubIntent = 0f;


                    existPart = false;
                    existItem = false;
                    existIndent = false;
                    existUpIndent = false;
                    upLevel = 0;
                }

                sequenceArticle++;
                String[] s = getNumArticle(el.getContent());
                listNpa.add(new NPA(upLevel, ARTICLE, s[0].replaceAll("\\.$", ""), s[1], el.getName(), sequenceArticle));

            } else if (el.getElementType().equals(CHAPTER)) { // Глава
                sequenceSection++;
                sequenceArticle = 0f;
                sequencePart = 0f;
                sequenceIndent = 0f;
                sequenceItem = 0f;
                sequenceSubitem = 0f;
                sequenceChapter++;
                existChapter = true;
                upLevel = 0;
                existArticle = false;
                existPart = false;
                existItem = false;
                String[] s = getNumChapter(el.getContent());
                String num = "";
                if (testRoman(s[0])) num = String.valueOf(r.convertRomanToInt(s[0].replaceAll("\\.$", "")));
                else num = s[0].replaceAll("\\.$", "");

                listNpa.add(new NPA(upLevel, CHAPTER, num, s[1], el.getName(), sequenceChapter));

            } else if (el.getElementType().equals(PART)){ //Часть
                sequenceItem = 0f;
                sequenceIndent = 0f;
                sequencePart++;
                if (existChapter || existSection) {
                    upLevel = 2;
                }else{
                    upLevel = 1;
                }
                existPart = true;
                listNpa.add(new NPA(upLevel, PART, el.getNum(), el.getName(), el.getContent(), sequencePart));

            }else if (el.getElementType().equals(INDENT)){//Абзац
                if (existUpIndent) {
                    sequenceSubIntent++;
                    if (endItem) upLevel = 2; else upLevel = 3;
                    if (continueItem) upLevel = 4;
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceSubIntent));
                    continue;
                }
                existIndent = true;

                if (existPart){
                    sequenceIndent++;
                    if (existChapter || existSection) {
                        upLevel = 3;
                    }else{
                        upLevel = 2;
                    }
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceIndent));
                } else if (existSubitem){
                    sequenceIndent++;
                    if (existChapter || existSection) {
                        upLevel = 4;
                    }else{
                        upLevel = 3;
                    }
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceIndent));
                } else if (existItem) {
                    sequenceIndent++;
                    if (existChapter || existSection) {
                        upLevel = 3;
                    }else{
                        upLevel = 2;
                    }
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceIndent));
                }else if(existArticle) {
                    sequenceIndent++;
                    if (existChapter || existSection) {
                        upLevel = 2;
                    }else{
                        upLevel = 1;
                    }
                    if (testColon(el.getContent())) existUpIndent = true;
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceIndent));
                }
                else {
                    sequenceIndent++;
                    listNpa.add(new NPA(upLevel, INDENT, el.getNum(), el.getName(), el.getContent(), sequenceIndent));
                }

            }else if (el.getElementType().equals(ITEM)){// Пункт
                sequenceSubitem = 0f;
                sequenceIndent = 0f;

                existItem = true;

                continueItem = false;

                //Если после пунктов нет абзацев
                if (testEndItem(el.getName())) endItem = true;
                if (testContinue(el.getName())) continueItem = true;

                existSubitem = false;
                existIndent = false;
                if (existSection) {
                    sequenceItem++;
                    if (existChapter || existSection) {
                        upLevel = 2;
                    }else{
                        upLevel = 2;
                    }
                    //listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("\\)", ""), el.getName(), el.getContent(), sequenceItem));

                    listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                    //listNpa.add(new NPA(upLevel, PART, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                } else if (existIndent) {
                    sequenceItem++;
                    if (existChapter || existSection) {
                        upLevel = 3;
                    }else{
                        upLevel = 2;
                    }
                    listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                    //listNpa.add(new NPA(upLevel, PART, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                }else if (existUpIndent) {
                    sequenceItem++;
                    if (existChapter || existSection) {
                        upLevel = 3;
                    }else{
                        upLevel = 2;
                    }
                    listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                    //listNpa.add(new NPA(upLevel, PART, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                } else if (existArticle) {
                    sequenceItem++;
                    if (existChapter || existSection) {
                        upLevel = 2;
                    }else{
                        upLevel = 1;
                    }
                    listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                    //listNpa.add(new NPA(upLevel, PART, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                }
                else {
                    sequenceItem++;
                    //listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("\\)", ""), el.getName(), el.getContent(), sequenceItem));

                    listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                    //listNpa.add(new NPA(upLevel, PART, el.getNum().replaceAll("[\\.\\)]$", ""), "", el.getName(), sequenceItem));
                }

            }else if (el.getElementType().equals(SUBITEM)){
                sequenceIndent = 0f;
                existSubitem = true;
                if (existItem){
                    sequenceSubitem++;
                    if (existChapter || existSection) {
                        upLevel = 3;
                    }else{
                        upLevel = 2;
                    }
                    listNpa.add(new NPA(upLevel, SUBITEM, el.getNum().replaceAll("\\)", ""), "", el.getName(), sequenceSubitem));
                    //listNpa.add(new NPA(upLevel, ITEM, el.getNum().replaceAll("\\)", ""), "", el.getName(), sequenceSubitem));
                }
            }
        }
    }

    private static boolean testRoman(String str) {
        Pattern p = Pattern.compile("^[IVXLCDM]+");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testLetter(String str) {
        Pattern p = Pattern.compile("^[а-я]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testNumeral(String str){
        Pattern p = Pattern.compile("^[0-9.]+\\.$");
        //Pattern p = Pattern.compile("^[0-9.]+[\\.\\)]$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testNumItem(String str){
        Pattern p = Pattern.compile("^[0-9.]+\\)");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testLetterItem(String str){
        Pattern p = Pattern.compile("^[а-я]+\\)$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testEndItem(String str){
        Pattern p = Pattern.compile("\\.$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static boolean testContinue(String str) {
        Pattern p = Pattern.compile("\\:$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static String[] getNumArticle(String str) {
        String[] s = new String[2];
        int i = 0;

        for (String retval: str.split("\\s", 3)){
            if (retval.contains("Статья")) continue;
            s[i] = retval;
            i++;
        }
        return s;
    }

    private static String[] getNumChapter(String str) {
        String[] s = new String[2];
        int i = 0;

        for (String retval: str.split("\\s", 3)){
            if (retval.contains("Глава")) continue;
            s[i] = retval;
            i++;
        }
        return s;
    }

    private static boolean testColon(String str) {
        Pattern p = Pattern.compile("[а-я]+\\:$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static String[] getRomanNum(String str) {
        String[] s = new String[2];
        int i = 0;

        for (String retval: str.split("\\s", 3)){
            if (retval.contains("Раздел")) continue;
            s[i] = retval;
            i++;
        }
        return s;
    }




}
