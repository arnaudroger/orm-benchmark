package org.sfm.beans;

import java.io.FileWriter;
import java.io.IOException;

public class GenerateBeans {

    public static final String[] types = new String[] { "String", "int", "long"};


    public static void main(String[] args) throws IOException {
//        for(int i = 2; i < 16384; i *=2) {
//            generateGS(i);
//        }
//        for(int i = 2; i < 256; i *=2) {
//            generateC(i);
//        }
//
//        for(int i = 2; i < 16384; i *=2) {
//            generateCGS(i);
//        }
//        for(int i = 2; i < 16384; i *=2) {
//            generateGSC(i);
//        }
        //generateC(191);
            generateGS(776);
           generateGS(784);
            generateGS(792);
    }

    private static void generateCGS(int i) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/org/sfm/beans/CGS"+ i + ".java");
        try {
            writer.append("package org.sfm.beans;\n\n");
            writer.append("public class CGS").append(Integer.toString(i)).append(" {\n");


            int startGS = Math.min(i / 2, 128);

            for(int j = 0; j < startGS; j++) {
                appendFinalField(writer, j);
            }

            for(int j = startGS; j < i; j++) {
                appendField(writer, j);
            }

            appendConstructor(i, writer, "CGS", 0, startGS);

            for(int j = 0; j < startGS; j++) {
                appendGetter(writer, j);
            }

            for(int j = startGS; j < i; j++) {
                appendGetterAndSetter(writer, j);
            }

            writer.append("}\n");
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private static void generateGSC(int i) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/org/sfm/beans/GSC"+ i + ".java");
        try {
            writer.append("package org.sfm.beans;\n\n");
            writer.append("public class GSC").append(Integer.toString(i)).append(" {\n");


            int startGS = Math.min(i / 2, 128);

            for(int j = 0; j < i - startGS; j++) {
                appendField(writer, j);
            }

            for(int j = i - startGS; j < i; j++) {
                appendFinalField(writer, j);
            }

            appendConstructor(i, writer, "GSC", i - startGS, i);

            for(int j = 0; j < i - startGS; j++) {
                appendGetterAndSetter(writer, j);
            }

            for(int j = i - startGS; j < i; j++) {
                appendGetter(writer, j);
            }

            writer.append("}\n");
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private static void generateGS(int i) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/org/sfm/beans/GS"+ i + ".java");
        try {
            writer.append("package org.sfm.beans;\n\n");
            writer.append("public class GS").append(Integer.toString(i)).append(" {\n");

            for(int j = 0; j < i; j++) {
                appendField(writer, j);
            }

            for(int j = 0; j < i; j++) {
                appendGetterAndSetter(writer, j);
            }

            writer.append("}\n");
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private static void appendGetterAndSetter(FileWriter writer, int j) throws IOException {
        appendGetter(writer, j);
        appendSetter(writer, j);
    }

    private static void appendSetter(FileWriter writer, int j) throws IOException {
        String type2 = types[j % types.length];
        writer.append("\tpublic void setVal").append(Integer.toString(j)).append("(").append(type2).append(" v) {\n")
                .append("\t\tthis.val").append(Integer.toString(j)).append(" = v;\n\t}\n");
    }

    private static void appendGetter(FileWriter writer, int j) throws IOException {
        String type = types[j % types.length];
        writer.append("\tpublic ").append(type).append(" getVal").append(Integer.toString(j)).append("() {\n")
                .append("\t\treturn val").append(Integer.toString(j)).append(";\n\t}\n");
    }

    private static void appendField(FileWriter writer, int j) throws IOException {
        String type = types[j % types.length];
        writer.append("\tprivate ").append(type).append(" val").append(Integer.toString(j)).append(";\n");
    }


    private static void generateC(int i) throws IOException {
        FileWriter writer = new FileWriter("src/main/java/org/sfm/beans/C"+ i + ".java");
        try {
            writer.append("package org.sfm.beans;\n\n");
            writer.append("public class C").append(Integer.toString(i)).append(" {\n");

            for(int j = 0; j < i; j++) {
                appendFinalField(writer, j);
            }

            String name = "C";
            int start = 0;
            int end = i;

            appendConstructor(i, writer, name, start, end);

            for(int j = 0; j < i; j++) {
                appendGetter(writer, j);
            }

            writer.append("}\n");
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private static void appendFinalField(FileWriter writer, int j) throws IOException {
        String type = types[j % types.length];
        writer.append("\tprivate final ").append(type).append(" val").append(Integer.toString(j)).append(";\n");
    }

    private static void appendConstructor(int i, FileWriter writer, String name, int start, int end) throws IOException {
        writer.append("\tpublic ").append(name).append(Integer.toString(i)).append("(");
        for(int j = start; j < end; j++) {
            String type = types[j % types.length];
            if (j > start) {
                writer.append(", ");
            }
            writer.append(type).append(" val").append(Integer.toString(j));
        }
        writer.append(") {\n");

        for(int j = start; j < end; j++) {
            writer.append("\tthis.val").append(Integer.toString(j)).append(" = val").append(Integer.toString(j)).append(";\n");
        }
        writer.append("\t}\n");
    }
}
