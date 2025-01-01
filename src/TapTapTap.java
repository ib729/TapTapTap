import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;
import java.util.*;

public class TapTapTap {
    private static final String NOTES_FOLDER = ".taptaptap";
    private static final String EXPORTS_FOLDER = "TapTapTap_Exports";

    public static void main(String[] args) {
        if (args.length == 0) {
            showHelp();
            return;
        }

        String input = String.join(" ", args);

        switch (input) {
            case "-l":
                listNotes();
                break;
            case "-ea":
                exportAllNotes();
                break;
            case "-s":
                showNoteStats();
                break;
            case "-da":
                deleteAllNotes();
                break;
            default:
                if (input.startsWith("-v ")) {
                    viewNote(input.substring(3).trim());
                }
                else if (input.startsWith("-d ")) {
                    deleteNote(input.substring(3).trim());
                }
                else if (input.startsWith("-e ")) {
                    exportNote(input.substring(3).trim());
                }
                else if (input.startsWith("-f ")) {
                    findInNotes(input.substring(3).trim());
                }
                else {
                    String[] parts = input.split("::");
                    if (parts.length == 2) {
                        saveNote(parts[0].trim(), parts[1].trim());
                    } else {
                        saveNote(generateNoteName(), input.trim());
                    }
                }
                break;
        }
    }

    private static void saveNote(String name, String content) {
        File notesDir = getNotesDirectory();
        if (notesDir == null) {
            System.out.println("Error: Could not create notes directory");
            return;
        }

        try {
            File noteFile = new File(notesDir, name + ".txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(noteFile))) {
                writer.println("Note: " + name);
                writer.println("Content: " + content);
            }

            System.out.println("Saved note [" + name + "] with content: " + content);
        } catch (IOException e) {
            System.out.println("Error saving note: " + e.getMessage());
        }
    }

    private static void viewNote(String name) {
        File noteFile = getNoteFile(name);
        if (!noteFile.exists()) {
            System.out.println("Note [" + name + "] not found!");
            return;
        }

        try {
            displayNoteContent(noteFile);
        } catch (IOException e) {
            System.out.println("Error reading note: " + e.getMessage());
        }
    }

    private static void listNotes() {
        File notesDir = getNotesDirectory();
        if (notesDir == null || !notesDir.exists() || !notesDir.isDirectory()) {
            System.out.println("No notes found!");
            return;
        }

        File[] noteFiles = notesDir.listFiles(f -> f.getName().endsWith(".txt"));

        if (noteFiles == null || noteFiles.length == 0) {
            System.out.println("No notes found!");
            return;
        }

        Arrays.sort(noteFiles, Comparator.comparingLong(File::lastModified).reversed());

        for (File file : noteFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String noteLine = reader.readLine();
                String contentLine = reader.readLine();
                System.out.println(noteLine);
                System.out.println(contentLine);
                System.out.println("-----------");
            } catch (IOException e) {
                System.out.println("Error reading note: " + file.getName());
            }
        }
    }

    private static void findInNotes(String searchText) {
        File notesDir = getNotesDirectory();
        if (notesDir == null || !notesDir.exists()) {
            System.out.println("No notes found!");
            return;
        }

        File[] noteFiles = notesDir.listFiles(f -> f.getName().endsWith(".txt"));
        if (noteFiles == null || noteFiles.length == 0) {
            System.out.println("No notes found!");
            return;
        }

        System.out.println("\nSearch results for: " + searchText);
        System.out.println("-----------");
        boolean found = false;

        for (File file : noteFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String noteLine = reader.readLine();
                String contentLine = reader.readLine();

                if (noteLine != null && contentLine != null) {
                    if (noteLine.toLowerCase().contains(searchText.toLowerCase()) ||
                            contentLine.toLowerCase().contains(searchText.toLowerCase())) {
                        System.out.println(noteLine);
                        System.out.println(contentLine);
                        System.out.println("-----------");
                        found = true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading note: " + file.getName());
            }
        }

        if (!found) {
            System.out.println("No matches found.");
        }
    }

    private static void deleteAllNotes() {
        File notesDir = getNotesDirectory();
        if (notesDir == null || !notesDir.exists() || !notesDir.isDirectory()) {
            System.out.println("No notes found to delete!");
            return;
        }

        File[] noteFiles = notesDir.listFiles(f -> f.getName().endsWith(".txt"));
        if (noteFiles == null || noteFiles.length == 0) {
            System.out.println("No notes found to delete!");
            return;
        }

        System.out.println("Are you sure you want to delete all notes? This cannot be undone! (y/n)");
        try (Scanner scanner = new Scanner(System.in)) {
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y")) {
                int deleted = 0;
                for (File file : noteFiles) {
                    if (file.delete()) {
                        deleted++;
                    }
                }
                System.out.println("Deleted " + deleted + " notes.");
            } else {
                System.out.println("Operation cancelled.");
            }
        }
    }

    private static void showNoteStats() {
        File notesDir = getNotesDirectory();
        if (notesDir == null || !notesDir.exists()) {
            System.out.println("No notes found!");
            return;
        }

        File[] noteFiles = notesDir.listFiles(f -> f.getName().endsWith(".txt"));
        if (noteFiles == null) {
            System.out.println("No notes found!");
            return;
        }

        System.out.println("\nNote Statistics:");
        System.out.println("-----------");
        System.out.println("Total notes: " + noteFiles.length);
        if (noteFiles.length > 0) {
            long lastModified = Arrays.stream(noteFiles)
                    .max(Comparator.comparingLong(File::lastModified))
                    .get().lastModified();
            System.out.println("Last modified: " + new Date(lastModified));
        }
    }

    private static void exportNote(String name) {
        File noteFile = getNoteFile(name);
        if (!noteFile.exists()) {
            System.out.println("Note [" + name + "] not found!");
            return;
        }

        try {
            String homeDir = System.getProperty("user.home");
            String exportPath = homeDir + "/Downloads/" + EXPORTS_FOLDER;
            Files.createDirectories(Paths.get(exportPath));

            Path source = noteFile.toPath();
            Path destination = Paths.get(exportPath, name + ".txt");
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Exported note [" + name + "] to: " + destination);
        } catch (IOException e) {
            System.out.println("Error exporting note: " + e.getMessage());
        }
    }

    private static void exportAllNotes() {
        File notesDir = getNotesDirectory();
        if (notesDir == null || !notesDir.exists() || !notesDir.isDirectory()) {
            System.out.println("No notes found to export!");
            return;
        }

        File[] noteFiles = notesDir.listFiles(f -> f.getName().endsWith(".txt"));

        if (noteFiles == null || noteFiles.length == 0) {
            System.out.println("No notes found to export!");
            return;
        }

        try {
            String homeDir = System.getProperty("user.home");
            String exportPath = homeDir + "/Downloads/" + EXPORTS_FOLDER;
            Files.createDirectories(Paths.get(exportPath));

            for (File file : noteFiles) {
                Path source = file.toPath();
                Path destination = Paths.get(exportPath, file.getName());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("All notes exported to: " + exportPath);
        } catch (IOException e) {
            System.out.println("Error exporting notes: " + e.getMessage());
        }
    }

    private static void deleteNote(String name) {
        File noteFile = getNoteFile(name);
        if (!noteFile.exists()) {
            System.out.println("Note [" + name + "] not found!");
            return;
        }

        if (noteFile.delete()) {
            System.out.println("Deleted note: [" + name + "]");
        } else {
            System.out.println("Error deleting note");
        }
    }

    private static String generateNoteName() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private static File getNotesDirectory() {
        File notesDir = new File(System.getProperty("user.home"), NOTES_FOLDER);
        if (!notesDir.exists() && !notesDir.mkdir()) {
            return null;
        }
        return notesDir;
    }

    private static File getNoteFile(String name) {
        return new File(System.getProperty("user.home"), NOTES_FOLDER + "/" + name + ".txt");
    }

    private static void displayNoteContent(File noteFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(noteFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    private static void showHelp() {
        System.out.println("\nTapTapTap (ttt) - A simple note-taking app\n");
        System.out.println("Usage:");
        System.out.println("  ttt content                      -> saves a note with auto-generated name");
        System.out.println("  ttt name::content                -> saves a note with the given name");
        System.out.println("  ttt -v name                      -> view a specific note");
        System.out.println("  ttt -l                           -> list all notes");
        System.out.println("  ttt -f searchtext                -> search notes for text");
        System.out.println("  ttt -s                           -> show note statistics");
        System.out.println("  ttt -d name                      -> delete a note");
        System.out.println("  ttt -da                          -> delete all notes");
        System.out.println("  ttt -e name                      -> export note to Downloads");
        System.out.println("  ttt -ea                          -> export all notes to Downloads");
    }
}