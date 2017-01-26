package ru.spbau.mit.repl.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.mit.repl.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class CdTest {

  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();

  private final Shell shell = new Shell();

  private final String oldPwd = System.getProperty("user.dir");
  private final String oldHome = System.getProperty("user.home");

  File rootFolder;
  File leftFolder;

  @Before
  public void before() throws IOException {
    rootFolder = tmpFolder.newFolder("root");
    leftFolder = new File(rootFolder, "left");
    while (!leftFolder.mkdir()) {
    }

    System.setProperty("user.home", tmpFolder.getRoot().getAbsolutePath());
    System.setProperty("user.dir", rootFolder.getAbsolutePath());
  }

  @After
  public void after() throws IOException {
    System.setProperty("user.dir", oldPwd);
    System.setProperty("user.home", oldHome);
  }

  @Test
  public void testNoArgs() throws IOException {
    shell.execute("cd", null);
    assertEquals(tmpFolder.getRoot().getAbsolutePath(), System.getProperty("user.dir"));
  }

  @Test
  public void casualTest() throws IOException {
    shell.execute("cd left", null);
    assertEquals(leftFolder.getAbsolutePath(), System.getProperty("user.dir"));
  }

  @Test
  public void goUp() throws IOException {
    shell.execute("cd ..", null);
    assertEquals(tmpFolder.getRoot().getAbsolutePath(), System.getProperty("user.dir"));
  }

  @Test
  public void goInUpAndDown() throws IOException {
    shell.execute("cd left", null);
    shell.execute("cd .." + System.lineSeparator() + "left", null);
    assertEquals(leftFolder.getAbsolutePath(), System.getProperty("user.dir"));
  }

  @Test
  public void noSuchFile() throws IOException {
    assertEquals(Cd.NO_SUCH_FD, executeAndGet("cd dontexist").trim());
  }

  private String executeAndGet(String cmd) throws IOException {
    final File resultFile = File.createTempFile("result", null);
    shell.execute(cmd, new PrintStream(resultFile));

    byte[] encoded = Files.readAllBytes(resultFile.toPath());
    return new String(encoded, "UTF-8");
  }
}