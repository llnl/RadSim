package gov.llnl.rtk.mcnp;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MCNP_Job {

    private String name;
    private MCNP_Deck deck;
    private Path outputDir;
    private Path mcnpPath;

    public MCNP_Job(String name, MCNP_Deck deck, Path outputDir, Path mcnpPath) throws Exception {
        if (name.contains(" ")) {
            throw new Exception("Job name cannot contain spaces");
        }
        this.name = name;
        this.deck = deck;
        new File(outputDir.toUri()).mkdir();
        this.outputDir = outputDir;
        this.mcnpPath = mcnpPath;
    }

    public MCNP_Job(String name, MCNP_Deck deck, Path outputDir, String mcnpFilename) throws Exception {
        this(name, deck, outputDir, getMcnpPath(mcnpFilename));
    }

    public static MCNP_Job MCNP6_Job(String name, MCNP_Deck deck, Path outputDir) throws Exception{
        return new MCNP_Job(name, deck, outputDir, "mcnp6");
    }

    public File run() throws Exception {
        return run(1);
    }

    public File run(int tasks) throws Exception {

        // Create the various files
        Path path = Paths.get(outputDir.toString(), name + "_" + System.currentTimeMillis());
        File inputFile = new File(path +  ".input");
        File outputFile = new File(path +  ".output");
        File runFile = new File(path +  ".run");

        // Write the input file
        inputFile.createNewFile();
        FileWriter writer = new FileWriter(inputFile);
        writer.write(deck.toString());
        writer.close();

        // Create the mcnp command
        String command = this.mcnpPath.toFile().getAbsolutePath();
        command += " i=" + inputFile.getAbsolutePath();
        command += " o=" + outputFile.getAbsolutePath();
        command += " run=" + runFile.getAbsolutePath();
        if (tasks > 1) command += " tasks " + tasks;
        Process p = Runtime.getRuntime().exec(command);

        // todo: verbose option
        // Execute the command
        System.out.println(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = stdInput.readLine();
        while (line != null) {
            System.out.println(line);
            line = stdInput.readLine();
        }

        return outputFile;
    }



    private static Path getMcnpPath(String query){
        ArrayList<Path> candidatePaths = new ArrayList<>();
        for (String pathEntry : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(pathEntry);
            // If it's a directory, we'll dig one layer in to see if MCNP is there
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    if (!subFile.isDirectory() && subFile.getName().contains(query)) {
                        candidatePaths.add(subFile.toPath());
                    }
                }
            } else {
                if (file.getName().contains(query)) {
                    candidatePaths.add(file.toPath());
                }
            }
        }

        if (candidatePaths.isEmpty()) return null;
        if (candidatePaths.size() > 1) System.err.println("Multiple path candidates were identified for MCNP");
        return candidatePaths.get(0);
    }

    public static void main(String ... args) throws Exception {
    }
}
