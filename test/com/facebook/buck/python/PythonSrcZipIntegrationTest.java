/*
 * Copyright 2015-present Facebook, Inc.
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

package com.facebook.buck.python;

import static org.junit.Assume.assumeTrue;

import com.facebook.buck.testutil.ProcessResult;
import com.facebook.buck.testutil.TemporaryPaths;
import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TestDataHelper;
import com.facebook.buck.util.environment.Platform;
import java.io.IOException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class PythonSrcZipIntegrationTest {

  @Rule public TemporaryPaths tmp = new TemporaryPaths();
  public ProjectWorkspace workspace;

  @Before
  public void setUp() throws IOException {
    assumeTrue(!Platform.detect().equals(Platform.WINDOWS));

    workspace = TestDataHelper.createProjectWorkspaceForScenario(this, "src_zip", tmp);
    workspace.setUp();
  }

  @Test
  public void testDependingOnSrcZipWorks() throws IOException {
    // This test should pass.
    ProcessResult result1 = workspace.runBuckCommand("test", "//:test");
    result1.assertSuccess();
  }
}
