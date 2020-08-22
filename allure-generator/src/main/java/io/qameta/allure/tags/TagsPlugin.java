/*
 *  Copyright 2019 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.tags;

import io.qameta.allure.Aggregator;
import io.qameta.allure.Constants;
import io.qameta.allure.context.JacksonContext;
import io.qameta.allure.core.Configuration;
import io.qameta.allure.core.LaunchResults;
import io.qameta.allure.entity.LabelName;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author charlie (Dmitry Baev).
 */
public class TagsPlugin implements Aggregator {

    public static final String TAGS_BLOCK_NAME = "tags";
    public static final String TAGS_FILE_NAME = "tags.json";
    private final Set<String> allTags = new HashSet<>();

    @Override
    public void aggregate(final Configuration configuration,
                          final List<LaunchResults> launchesResults,
                          final Path outputDirectory) throws IOException {
        launchesResults.stream()
                .map(LaunchResults::getAllResults)
                .flatMap(Collection::stream)
                .forEach(result -> {
                    final Set<String> tags = new HashSet<>(result.findAllLabels(LabelName.TAG));
                    allTags.addAll(tags);
                    result.addExtraBlock(TAGS_BLOCK_NAME, tags);
                });

        final JacksonContext context = configuration.requireContext(JacksonContext.class);
        final Path dataFolder = Files.createDirectories(outputDirectory.resolve(Constants.DATA_DIR));
        final Path tagsFile = dataFolder.resolve(TAGS_FILE_NAME);
        final Map<String, Set> tags = new HashMap<>();
        tags.put(TAGS_BLOCK_NAME, allTags);
        try (OutputStream os = Files.newOutputStream(tagsFile)) {
            context.getValue().writeValue(os, tags);
        }
    }
}
