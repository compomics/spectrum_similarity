/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protein_spectrum_diversity;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class finds individual mgfs inside of child folders at the rootPath
 * 
 * @author Davy
 */
public class KennethFileWalker {

    static Path rootPath = null;
    static KennethFileWalkerImpl walker = new KennethFileWalkerImpl();
    static File outputFolder;

    public static void main(String[] args) {
        if (args.length == 0 || Objects.equals(args[0], "--help") || Objects.equals(args[0], "-h")) {
            System.out.println("first argument : root folder to walk\n second argument: list of comma separated peptides to look for");
        } else {
            rootPath = new File(args[0]).toPath();
            if (!rootPath.toFile().exists()) {
                System.err.println("root path does not exist");
            }
            for (String peptide : Arrays.asList(args[1].split(","))) {
                try {
                    walkFiles(peptide);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    static public void walkFiles(String peptide) throws IOException {
        if (rootPath == null) {
            throw new IOException("rootpath not set");
        }
        List<String> filePath = new ArrayList<String>();
        int counter = 0;
        StringBuilder builder = new StringBuilder("");

        char[] pepChars = peptide.toCharArray();

        for (Character pepChar : pepChars) {

            if (counter == 3) {
                filePath.add(builder.toString());
                builder = new StringBuilder();
                counter = 0;
            }
            builder.append(pepChar);
            counter++;
        }
        if (builder.length() != 0) {
            filePath.add(builder.toString());
        }
        walker.setPath(peptide, filePath);
        Files.walkFileTree(rootPath, walker);
    }

    static public class KennethFileWalkerImpl extends SimpleFileVisitor<Path> {

        Iterator<String> localPath;
        String localPeptide = "";
        String fullPeptide = "";

        public void setPath(String peptide, List<String> path) {
            fullPeptide = peptide;
            System.out.println(fullPeptide);

            localPath = path.iterator();
            System.out.println(localPath);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            localPeptide = localPath.next();
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.getFileName().toString().equals(localPeptide)) {
                return FileVisitResult.SKIP_SIBLINGS;
            } else if (attrs.isRegularFile() && file.getFileName().toString().equals(fullPeptide)) {
                System.out.println(file.toAbsolutePath().toString());
                return FileVisitResult.TERMINATE;
            } else {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }
    }
}
