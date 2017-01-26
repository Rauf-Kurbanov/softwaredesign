package ru.spbau.mit.repl.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

/**
 * Navigates by users filesystem changing working directory
 * similar to unix cd
 */
public class Cd implements Command {

  public static final String NO_SUCH_FD = "No such file or directory";

  @Override
  public void execute(List<String> arguments, InputStream input, PrintStream output) throws IOException {
    if (arguments.isEmpty()) {
      goHome();
      return;
    }

    String pwd = System.getProperty("user.dir");
    String[] separated = arguments.get(0).split(System.lineSeparator());
    int i;
    for (i = 0; i < separated.length; i++) {
      if (!separated[i].equals(".."))
        break;
      System.setProperty("user.dir", Paths.get(pwd).getParent().toString());
      pwd = System.getProperty("user.dir");
    }

    final StringJoiner sj = new StringJoiner(System.lineSeparator());
    for (int j = i; j < separated.length; j++) {
      sj.add(separated[j]);
    }
    final Path targetPath = Paths.get(pwd, sj.toString());
    if (!Files.exists(targetPath)) {
      output.println(NO_SUCH_FD);
      return;
    }
    System.setProperty("user.dir", targetPath.toString());
  }

  private void goHome() {
    System.setProperty("user.dir", System.getProperty("user.home"));
  }
}
