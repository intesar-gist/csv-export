import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Main {
    private static final Map<String, String> departments = new HashMap<>();
    static {
        departments.put("GF", "GRPM_HS Fulda_Pflege und Gesundheit_Gesundheitsförderung");
        departments.put("GM", "GRPM_HS Fulda_Pflege und Gesundheit_Gesundheitsmanagement");
        departments.put("GMB", "GRPM_HS Fulda_Pflege und Gesundheit_Gesundheitsmanagement");
        departments.put("GOP", "GRPM_HS Fulda_Pflege und Gesundheit_Gesundheitsökonomie und -politik");
        departments.put("GÖP", "GRPM_HS Fulda_Pflege und Gesundheit_Gesundheitsökonomie und -politik");
        departments.put("HEK", "GRPM_HS Fulda_Pflege und Gesundheit_Hebammenkunde");
        departments.put("PG", "GRPM_HS Fulda_Pflege und Gesundheit_Pflege");
        departments.put("PH", "GRPM_HS Fulda_Pflege und Gesundheit_Public Health");
        departments.put("PHB", "GRPM_HS Fulda_Pflege und Gesundheit_Public Health");
        departments.put("PM", "GRPM_HS Fulda_Pflege und Gesundheit_Pflegemanagement");
        departments.put("PMB", "GRPM_HS Fulda_Pflege und Gesundheit_Pflegemanagement");
        departments.put("PPG", "GRPM_HS Fulda_Pflege und Gesundheit_Pädagogik f. Pflege- u. Gesundheitsberufe");
        departments.put("PT", "GRPM_HS Fulda_Pflege und Gesundheit_Physiotherapie");
        departments.put("PHN", "GRPM_HS Fulda_Pflege und Gesundheit_Public Health Nutrition");
        departments.put("IHS", "GRPM_HS Fulda_Pflege und Gesundheit_International Health Sciences");
        departments.put("BBG", "GRPM_HS Fulda_Pflege und Gesundheit_Berufspädagogik Fach Gesundheit");
    }

    private static final char DEFAULT_SEPARATOR = ';';
    private static final char DEFAULT_QUOTE = '"';

    public static void main(String[] args) throws Exception {


        if(args == null || args.length != 3) {
            System.out.println("Param1 (Old-list), Param2 (new-list) and Param3 (final-list) name required");
            return;
        }

        String oldCsv = args[0]; //"C:\\Users\\Venturedive\\Desktop\\s2t\\src\\old_users.csv";
        String newCsv = args[1]; //"C:\\Users\\Venturedive\\Desktop\\s2t\\src\\new_users.csv";
        String finalCsv = args[2]; //"C:\\Users\\Venturedive\\Desktop\\s2t\\src\\final_users.csv";

        FileWriter writer = new FileWriter(finalCsv);

        //reading old list of users
        Map<String, List<String>> oldUsers = readOldUsers(oldCsv);
        List<List<String>> newUsers = filterUsers(oldUsers, newCsv);

        writeLine(writer, newUsers);
        writer.flush();
        writer.close();

    }

    public static void writeLine(Writer w, List<List<String>> newUsers) throws IOException {
        boolean firstLine = true;
        StringBuilder sb = new StringBuilder();
        for(List<String> line : newUsers) {
            setLine(sb, line, firstLine);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
            firstLine = false;
        }
        w.append(sb.toString());

        System.out.println("Total users exported: " + (newUsers.size()>1 ? newUsers.size() : 0));
    }

    public static void setLine(StringBuilder sb, List<String> line, boolean firstLine) {
        boolean firstElem = true;
        //creating a line
        for(String data : line) {
            sb.append((firstElem && !firstLine) ? departments.get(data) : data).append(DEFAULT_SEPARATOR);
            firstElem = false;
        }
    }

    public static String getKey(List<String> line) {

        String key = "";

        try {
            if(line!=null && line.size()>0){
                String department = line.get(0);
                String email = line.get(line.size()-1);

                key = department+";"+email;
            }
        } catch (Exception ex) {
            System.out.println(line.toString());
            System.out.println(ex);
        }

        return key;
    }

    public static  Map<String, List<String>> readOldUsers(String oldCsv) throws Exception {
        Map<String, List<String>> oldUsers = new HashMap<>();

        Scanner scanner = new Scanner(new File(oldCsv));
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            String key = getKey(line);
            oldUsers.put(key, line);
        }
        System.out.println("Total old users: " + oldUsers.size());
        scanner.close();

        return oldUsers;
    }

    public static List<List<String>> filterUsers(Map<String, List<String>> oldUsers, String newCsv) throws Exception {

        List<List<String>> newUsers = new ArrayList<>();

        boolean firstLine = true;
        //reading new list of users
        Scanner scanner = new Scanner(new File(newCsv));
        while (scanner.hasNext()) {
            List<String> line = parseLine(scanner.nextLine());
            String key = getKey(line);

            //if same user does not exist in old list
            if(firstLine) {
                newUsers.add(line);
            } else if(!firstLine && oldUsers.get(key) == null) {
                newUsers.add(line);
            }

            firstLine = false;
        }
        scanner.close();
        System.out.println("Total new users: " + newUsers.size());

        return newUsers;
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
}
