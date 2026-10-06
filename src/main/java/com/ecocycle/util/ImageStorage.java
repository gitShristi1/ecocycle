package com.ecocycle.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Pattern;

/** Saves, finds and deletes uploaded product images. */
public final class ImageStorage {

    // outside the web application: survives redeploys and stays out of Git
    private static final Path DIR =
            Paths.get(System.getProperty("user.home"), "ecocycle-uploads", "products");

    // only names we generated ourselves are ever accepted
    private static final Pattern NAME = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)$");

    private ImageStorage() { }

    /** Looks at the first bytes of the file. Returns "jpg" or "png", or null for anything else. */
    public static String detectType(byte[] data) {
        if (data.length >= 3
                && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (data.length >= 8
                && (data[0] & 0xFF) == 0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G'
                && data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A) {
            return "png";
        }
        return null;
    }

    /** Writes the bytes to a new randomly named file and returns that file name. */
    public static String save(byte[] data, String extension) throws IOException {
        Files.createDirectories(DIR);
        String fileName = UUID.randomUUID() + "." + extension;
        Files.write(DIR.resolve(fileName), data);
        return fileName;
    }

    /** Deletes a stored image. Names that do not match the pattern are ignored. */
    public static void delete(String fileName) {
        Path file = resolve(fileName);
        if (file != null) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                // a leftover file is harmless; nothing else to do
            }
        }
    }

    /** Returns the path of a stored image, or null if the name is not one of ours. */
    public static Path resolve(String fileName) {
        if (fileName == null || !NAME.matcher(fileName).matches()) {
            return null;
        }
        return DIR.resolve(fileName);
    }
}