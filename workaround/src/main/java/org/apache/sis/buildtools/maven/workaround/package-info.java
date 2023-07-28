/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Generates {@code META-INF/services} files with the content of {@code module-info.class} files.
 * This is used for compatibility when a modularized dependency is declared on the class-path
 * rather than the module-path. This need occurs when dependencies are automatically dispatched
 * between {@code --class-path} and {@code --module-path} options by a tool on which we have no
 * control, such as Maven 3.8.6 or Gradle 8.2.1.
 *
 * <h2>Arguments</h2>
 * The arguments given to the main method shall contain {@code --target=dir} where {@code dir}
 * is the target directory where to write generated files. Other arguments that are not options
 * are JAR files.
 *
 * @author  Martin Desruisseaux (Geomatys)
 */
package org.apache.sis.buildtools.maven.workaround;
