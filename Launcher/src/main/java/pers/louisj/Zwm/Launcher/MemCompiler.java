/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pers.louisj.Zwm.Launcher;

import static java.util.Collections.singleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

public class MemCompiler {
    private static final URI EMPTY_URI;

    static {
        try {
            // Needed to keep SimpleFileObject constructor happy.
            EMPTY_URI = new URI("");
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
    }

    private final Map<String, ByteArrayOutputStream> byteCodeForClasses = new HashMap<>();

    public Map<String, ByteArrayOutputStream> GetClasses() {
        return byteCodeForClasses;
    }

    // @Override
    // public Class<?> findClass(String name) throws ClassNotFoundException {
    // ByteArrayOutputStream byteCode = byteCodeForClasses.get(name);
    // if (byteCode == null) {
    // throw new ClassNotFoundException(name);
    // }
    // return defineClass(name, byteCode.toByteArray(), 0, byteCode.size());
    // }

    public boolean Compile(String className, String sourceCode, List<String> options, Writer writer) {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        // Set up the in-memory filesystem.
        InMemoryFileManager fileManager = new InMemoryFileManager(
                javaCompiler.getStandardFileManager(null, null, null));
        JavaFileObject javaFile = new InMemoryJavaFile(className, sourceCode);

        return javaCompiler.getTask(writer, fileManager, null, options, null, singleton(javaFile)).call();
    }

    private static class InMemoryJavaFile extends SimpleJavaFileObject {
        private final String sourceCode;

        InMemoryJavaFile(String className, String sourceCode) {
            super(makeUri(className), Kind.SOURCE);
            this.sourceCode = sourceCode;
        }

        private static URI makeUri(String className) {
            try {
                return new URI(className.replaceAll("\\.", "/") + Kind.SOURCE.extension);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e); // Not sure what could cause this.
            }
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return sourceCode;
        }
    }

    private class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        InMemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, final String className, JavaFileObject.Kind kind,
                FileObject sibling) throws IOException {
            return new SimpleJavaFileObject(EMPTY_URI, kind) {
                @Override
                public OutputStream openOutputStream() throws IOException {
                    ByteArrayOutputStream outputStream = byteCodeForClasses.get(className);
                    if (outputStream != null) {
                        throw new IllegalStateException("Cannot write more than once");
                    }
                    // Reasonable size for a simple .class.
                    outputStream = new ByteArrayOutputStream(256);
                    byteCodeForClasses.put(className, outputStream);
                    return outputStream;
                }
            };
        }
    }
}