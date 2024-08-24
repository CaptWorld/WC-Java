package org.capt.world;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestWC {

    record TestCase(String[] args, Set<CommandLineOption> expectedOptions, List<WCOutput> expectedWCOutputs) {
    }

    static List<TestCase> TEST_CASES;

    @BeforeAll
    static void init() {
        System.out.println("Init method");
        String artOfWarPath = "src/test/resources/ArtOfWar.txt";
        String romeoAndJulietPath = "src/test/resources/RomeoAndJuliet.txt";
        TEST_CASES = List.of(
                new TestCase(
                        new String[]{"-c", artOfWarPath},
                        Set.of(CommandLineOption.BYTES),
                        List.of(new WCOutput(artOfWarPath, new long[]{342190}))
                ),
                new TestCase(
                        new String[]{"--lines", romeoAndJulietPath},
                        Set.of(CommandLineOption.LINES),
                        List.of(new WCOutput(romeoAndJulietPath, new long[]{5647}))
                ),
                new TestCase(
                        new String[]{"-mw", artOfWarPath},
                        Set.of(CommandLineOption.CHARACTERS, CommandLineOption.WORDS),
                        List.of(new WCOutput(artOfWarPath, new long[]{58164, 339292}))
                ),
                new TestCase(
                        new String[]{"--bytes", "--chars", romeoAndJulietPath, artOfWarPath},
                        Set.of(CommandLineOption.BYTES, CommandLineOption.CHARACTERS),
                        List.of(
                                new WCOutput(romeoAndJulietPath, new long[]{167424, 169541}),
                                new WCOutput(artOfWarPath, new long[]{339292, 342190}),
                                new WCOutput("total", new long[]{506716, 511731})
                        )
                ),
                new TestCase(
                        new String[]{"--chars", "-cl", "--words", artOfWarPath, romeoAndJulietPath, artOfWarPath},
                        Set.of(
                                CommandLineOption.CHARACTERS,
                                CommandLineOption.BYTES,
                                CommandLineOption.LINES,
                                CommandLineOption.WORDS
                        ),
                        List.of(
                                new WCOutput(artOfWarPath, new long[]{7145, 58164, 339292, 342190}),
                                new WCOutput(romeoAndJulietPath, new long[]{5647, 29000, 167424, 169541}),
                                new WCOutput(artOfWarPath, new long[]{7145, 58164, 339292, 342190}),
                                new WCOutput("total", new long[]{19937, 145328, 846008, 853921})
                        )
                ),
                new TestCase(
                        new String[]{romeoAndJulietPath},
                        Set.of(
                                CommandLineOption.LINES,
                                CommandLineOption.WORDS,
                                CommandLineOption.BYTES
                        ),
                        List.of(new WCOutput(romeoAndJulietPath, new long[]{5647, 29000, 169541}))
                )
        );
    }

    @Test
    void testParsedArgs() {
        TEST_CASES.forEach(testCase -> {
            ParsedArgs parsedArgs = ParsedArgs.parse(testCase.args);

            Assertions.assertEquals(testCase.expectedOptions(), parsedArgs.options(),
                    "Expected options: " + testCase.expectedOptions() + " But got: " +
                            parsedArgs.options() +
                            " for input: " + Arrays.toString(testCase.args()));
        });
    }

    @Test
    void testWC() {
        TEST_CASES.forEach(testCase -> {
            ParsedArgs parsedArgs = ParsedArgs.parse(testCase.args);
            List<WCOutput> actualOutputs = Utils.countMultipleInput(parsedArgs.options(), Utils.getInputStreams(parsedArgs.files()));
            Assertions.assertIterableEquals(testCase.expectedWCOutputs(), actualOutputs);
        });
    }
}