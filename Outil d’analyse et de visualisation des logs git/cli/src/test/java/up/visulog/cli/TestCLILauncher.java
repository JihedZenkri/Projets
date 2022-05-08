package up.visulog.cli;

import org.junit.Assert;
import org.junit.Test;

public class TestCLILauncher {
    /*
     * TODO: one can also add integration tests here: - run the whole program with
     * some valid options and look whether the output has a valid format - run the
     * whole program with bad command and see whether something that looks like help
     * is printed
     */
    @Test
    public void testArgumentParser() {
	final var config1 = CLILauncher.makeConfigFromCommandLineArgs(new String[] { ".", "--addPlugin=countCommits" });
	Assert.assertTrue(config1.isPresent());
	final var config2 = CLILauncher.makeConfigFromCommandLineArgs(new String[] { "--nonExistingOption" });
	Assert.assertFalse(config2.isPresent());
    }
}
