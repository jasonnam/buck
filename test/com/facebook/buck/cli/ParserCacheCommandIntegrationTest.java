/*
 * Copyright 2017-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.facebook.buck.cli;

import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.facebook.buck.query.QueryException;
import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TemporaryPaths;
import com.facebook.buck.testutil.integration.TestContext;
import com.facebook.buck.testutil.integration.TestDataHelper;
import com.facebook.buck.util.NamedTemporaryFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

public class ParserCacheCommandIntegrationTest {

  @Rule public TemporaryPaths tmp = new TemporaryPaths();

  @Test
  public void testSaveAndLoad() throws IOException, InterruptedException, QueryException {
    ProjectWorkspace workspace =
        TestDataHelper.createProjectWorkspaceForScenario(this, "parser_with_cell", tmp);
    workspace.setUp();

    // Warm the parser cache.
    TestContext context = new TestContext();
    ProjectWorkspace.ProcessResult runBuckResult =
        workspace.runBuckdCommand(context, "query", "deps(//Apps:TestAppsLibrary)");
    runBuckResult.assertSuccess();
    assertThat(
        runBuckResult.getStdout(),
        Matchers.containsString(
            "//Apps:TestAppsLibrary\n"
                + "//Libraries/Dep1:Dep1_1\n"
                + "//Libraries/Dep1:Dep1_2\n"
                + "bar//Dep2:Dep2"));

    // Save the parser cache to a file.
    NamedTemporaryFile tempFile = new NamedTemporaryFile("parser_data", null);
    runBuckResult =
        workspace.runBuckdCommand(context, "parser-cache", "--save", tempFile.get().toString());
    runBuckResult.assertSuccess();

    // Write an empty content to Apps/BUCK.
    Path path = tmp.getRoot().resolve("Apps/BUCK");
    byte[] data = {};
    Files.write(path, data);

    context = new TestContext();
    // Load the parser cache to a new buckd context.
    runBuckResult =
        workspace.runBuckdCommand(context, "parser-cache", "--load", tempFile.get().toString());
    runBuckResult.assertSuccess();

    // Perform the query again. If we didn't load the parser cache, this call would fail because
    // Apps/BUCK is empty.
    runBuckResult = workspace.runBuckdCommand(context, "query", "deps(//Apps:TestAppsLibrary)");
    runBuckResult.assertSuccess();
    assertThat(
        runBuckResult.getStdout(),
        Matchers.containsString(
            "//Apps:TestAppsLibrary\n"
                + "//Libraries/Dep1:Dep1_1\n"
                + "//Libraries/Dep1:Dep1_2\n"
                + "bar//Dep2:Dep2"));
  }
}
