import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Main {

    private static final String INPUT_FILE_NAME = "names.txt";
    private static List<File> pictures = new ArrayList<>();
    private static List<NameExample> serialNames = new ArrayList<>();

    private static String INPUT_DIRECTORY = "pics_examps";
    private static String OUTPUT_DIRECTORY = "out_dir";
    private static String TAB = "\t";


    private static int pictureCounter = 0;
    private static int nameCounter = 0;

    public static void main(String[] args) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(INPUT_FILE_NAME), "windows-1251"));

        File inputDir = new File(INPUT_DIRECTORY);
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (outputDir.exists()) {
            Files.walkFileTree(outputDir.toPath(), new FileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return null;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        outputDir.mkdir();
        pictures = Arrays.asList(Objects.requireNonNull(inputDir.listFiles()));
        in.lines().map(NameExample::new).forEach(serialNames::add);
        in.lines().forEach(s -> {
            System.out.println(s);
            serialNames.add(new NameExample(s));
        });
        serialNames.forEach(ne -> System.out.println(ne.serialName + " : " + ne.listNumber + " : " + ne.literalName));

        format();

        while (pictureCounter < pictures.size() && nameCounter < serialNames.size()) {
            NameExample curNameExample = serialNames.get(nameCounter);
            if (curNameExample.serialName.contains(" ")) {
                String num = curNameExample.serialName.split(" ")[0];
                LinkedList<NameExample> backwardFiles = new LinkedList<>();
                while (nameCounter < serialNames.size() && serialNames.get(nameCounter).serialName.contains(num)) {
                    backwardFiles.add(serialNames.get(nameCounter));
                    nameCounter++;
                }
                backwardFiles.add(serialNames.get(nameCounter));
                nameCounter--;
                backwardFiles.descendingIterator().forEachRemaining(NameExample::renameForThisName);
            } else {
                curNameExample.renameForThisName();
            }
            nameCounter++;
        }
    }


    private static class NameExample {
        String serialName;
        int listNumber;
        String format;
        String literalName;

        NameExample(String signature) {
            String[] sarr;
            sarr = signature.split(String.valueOf(TAB));

            System.out.println(signature);
            try {
                serialName = sarr[0];
                listNumber = Integer.parseInt(sarr[1]);
                format = sarr[2];
                literalName = sarr[3];
            } catch (Exception e) {
                e.printStackTrace();
                serialName = "ERROR_NAME";
                listNumber = 1;
                format = "ERR";
                literalName = "ERROR" + sarr[0];
            }

        }

        private void renameForThisName() {
            if (listNumber == 1) {
                copyToOutputDirectory(pictures.get(pictureCounter++), serialName + "_" + literalName);
            } else {
                for (int i = 1; i <= listNumber; i++) {
                    copyToOutputDirectory(pictures.get(pictureCounter++), serialName + "_" + literalName + " лист 0" + i);
                }
            }
        }
    }

    private static void copyToOutputDirectory(File file, String newName) {
        File newFile = new File(OUTPUT_DIRECTORY, newName + ".tif");
        try {
            Files.copy(file.toPath(), newFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void format() throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("formattedList.txt")));
        Map<String, String> serialToLiteral = new TreeMap<>();
        for (NameExample nameExample : serialNames) {
            String curSerName = nameExample.serialName;
            if (curSerName.contains("СБ")) {
                serialToLiteral.put(curSerName.substring(0, curSerName.length() - 3), nameExample.literalName);
            } else if (curSerName.contains("ВН")) {
                nameExample.literalName = nameExample.literalName + " (Ведомость документов)";
            } else if (curSerName.contains("МЭ")) {
                nameExample.literalName = nameExample.literalName + " (Электромантажный чертеж)";
            } else if (curSerName.contains("МЧ")) {
                nameExample.literalName = nameExample.literalName + " (Монтажный чертеж)";
            } else if (curSerName.contains("ГЧ")) {
                nameExample.literalName = nameExample.literalName + " (Габаритный чертеж)";
            } else if (curSerName.contains("П4")) {
                nameExample.literalName = nameExample.literalName + " (Схема пневматическая соединений)";
            } else if (curSerName.contains("ПЭ4")) {
                nameExample.literalName = nameExample.literalName + " (Перечень элементов)";
            } else if (curSerName.contains("Э4")) {
                nameExample.literalName = nameExample.literalName + " (Схема электрическая соединений)";
            } else if (serialToLiteral.containsKey(curSerName)) {
                nameExample.literalName = "Спецификация";
            }
            try {
                System.out.println(nameExample.serialName + ":" + nameExample.listNumber + ":" + nameExample.format + ":" + nameExample.literalName);
                out.write(nameExample.serialName + TAB + nameExample.listNumber + TAB + nameExample.format + TAB + nameExample.literalName);
                out.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        out.close();
    }
}
