package io.qameta.allure.tags;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.ConfigurationBuilder;
import io.qameta.allure.DefaultLaunchResults;
import io.qameta.allure.core.Configuration;
import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.LabelName;
import io.qameta.allure.entity.TestResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TagsPluginTest {

    private TagsPlugin tagsPlugin = new TagsPlugin();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private final Configuration configuration = new ConfigurationBuilder().useDefault().build();

    @Test
    public void fileShouldHasAllTags() throws IOException {

        final Path tempPath = folder.newFolder().toPath();
        final TestResult fitstTestResult = new TestResult().setName("fitstTestResult");
        final TestResult secondTestResult = new TestResult().setName("secondTestResult");
        final Set<TestResult> testResults = new HashSet<>(Arrays.asList(fitstTestResult, secondTestResult));
        final LaunchResults launchResults = new DefaultLaunchResults(
                testResults,
                Collections.emptyMap(),
                Collections.emptyMap()
        );
        final List<LaunchResults> launchResultsList = new ArrayList<>();
        launchResultsList.add(launchResults);
        fitstTestResult.addLabel(LabelName.TAG.value(), "tag1");
        fitstTestResult.addLabel(LabelName.TAG.value(), "tag2");
        secondTestResult.addLabel(LabelName.TAG.value(), "tag3");

        tagsPlugin.aggregate(configuration, launchResultsList, tempPath);
        try (InputStream is = Files.newInputStream(tempPath.resolve("tags/tags.json"))) {
            final ObjectMapper mapper = new ObjectMapper();
            assertThat(mapper.readTree(is))
                    .isEqualTo(mapper.readTree("{\"tags\": [\"tag1\",\"tag2\",\"tag3\"]}"));
        }

    }
}
