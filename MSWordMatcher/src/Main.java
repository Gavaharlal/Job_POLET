import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static String INPUT_FILE_NAME = "C:\\Users\\Denis\\IdeaProjects\\Job_POLET\\MSWordMatcher\\real.txt";
    private static String OUTPUT_FILE_NAME = "out.txt";
    private static char TAB = '\t';

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT_FILE_NAME), "windows-1251"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE_NAME), StandardCharsets.UTF_8));

        in.lines().forEach(l -> {
            List<String> comps = Arrays.stream(l.split(String.valueOf(TAB))).filter(s -> !s.isEmpty()).collect(Collectors.toList());
            String serialNumber = comps.get(0);
            String name;
            if (comps.size() < 4) {
                name = "";
            } else {
                name = comps.get(3);
            }
            System.out.println(serialNumber + "_" + name);
            String serialNum = null;
            String paperName = null;
            try {
                String[] pair = name.split("_");
                serialNum = pair[0];
                paperName = pair[1];
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    out.write("_" + TAB + "_" + TAB + "_" + TAB + "_");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println(name);
            }
            switch (comps.size()) {
                case 1:
                    try {
                        out.write(serialNum + TAB + "1" + TAB + "A4" + TAB + paperName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    if (comps.get(1).charAt(0) == 'A') {
                        try {
                            out.write(serialNum + TAB + "1" + TAB + comps.get(1) + TAB + paperName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            out.write(serialNum + TAB + comps.get(1) + TAB + "A4" + TAB + paperName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    try {
                        out.write(serialNum + TAB + comps.get(1) + TAB + comps.get(2) + TAB + paperName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            try {
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        List<Pair> pairs = in.lines().map(s -> s.split("_"))
                .map(a -> new Pair(a[0], a[1]))
                .sorted(Comparator.comparing(pair -> pair.key))
                .collect(Collectors.toList());
        pairs.forEach(s -> {
            try {
                out.write(s.key + TAB + '_' + TAB + '_' + TAB + s.value);
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        out.close();
    }

    private static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
