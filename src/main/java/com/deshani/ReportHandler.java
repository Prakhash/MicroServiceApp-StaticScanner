package com.deshani;


import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.*;

/**
 * Created by deshani on 8/4/17.
 */
class ReportHandler {

    static void findFilesAndMoveToFolder(String sourcePath, String destinationPath, String fileName) throws IOException {
        File dir = new File(destinationPath);
        dir.mkdir();

        Files.find(Paths.get(sourcePath),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> filePath.getFileName().toString().equals(fileName)).forEach((f) -> {
            try {
                File file = f.toFile();

                String newFileName = file.getAbsolutePath().replace(sourcePath, Constant.NULL_STRING).replace(File.separator, Constant.UNDERSCORE);
                File newFile = new File(destinationPath + File.separator + newFileName);

                file.renameTo(newFile);
                FileUtils.copyFileToDirectory(newFile, dir);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + File.separator + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();

    }

    static void unzip(String inputZip, String destinationDirectory) throws IOException {

        int BUFFER = 2048;
        List zipFiles = new ArrayList();
        File sourceZipFile = new File(inputZip);
        File unzipDestinationDirectory = new File(destinationDirectory);
        unzipDestinationDirectory.mkdir();

        ZipFile zipFile;
        // Open Zip file for reading
        zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);

        // Create an enumeration of the entries in the zip file
        Enumeration zipFileEntries = zipFile.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile = new File(unzipDestinationDirectory, currentEntry);
            destFile = new File(unzipDestinationDirectory, destFile.getName());

            if (currentEntry.endsWith(Constant.ZIP_FILE_EXTENSION)) {
                zipFiles.add(destFile.getAbsolutePath());
            }

            // grab file's parent directory structure
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            try {
                // extract file if not a directory
                if (!entry.isDirectory()) {
                    BufferedInputStream is =
                            new BufferedInputStream(zipFile.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest =
                            new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        zipFile.close();

        for (Iterator iterator = zipFiles.iterator(); iterator.hasNext(); ) {
            String zipName = (String) iterator.next();
            unzip(
                    zipName,
                    destinationDirectory +
                            File.separatorChar +
                            zipName.substring(0, zipName.lastIndexOf(Constant.ZIP_FILE_EXTENSION))
            );
        }
        FileUtils.deleteDirectory(new File(inputZip));

    }


}
