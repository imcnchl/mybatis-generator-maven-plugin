package cn.caohongliang.mybatis.generator.maven.util;

import org.apache.maven.plugin.AbstractMojo;
import org.mybatis.generator.api.IntrospectedTable;

import java.io.File;
import java.util.*;

/**
 * @author caohongliang
 */
public class PluginUtils {
    private static final Map<Class<? extends AbstractMojo>, AbstractMojo> mojoCache = new HashMap<>();



    /**
     * 类注释
     *
     * @param javaDocLines
     * @param introspectedTable
     */
    public static void classComment(List<String> javaDocLines, IntrospectedTable introspectedTable, String defaultRemarks, String suffix) {
        javaDocLines.clear();
        //设置类注释
        String remarks = isEmpty(introspectedTable.getRemarks()) ? defaultRemarks : introspectedTable.getRemarks();
        remarks += " " + suffix;
        javaDocLines.add("/**");
        javaDocLines.add(" * " + remarks);
        javaDocLines.add(" * ");
        javaDocLines.add(" * @author @mbg.generated");
        javaDocLines.add(" */");
    }

    public static boolean existFile(String targetProject, String targetPackage, String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }
        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return false;
            }
        }

        File file = new File(directory, fileName);
        return file.exists();
    }

    public static String getString(Properties properties, String key, String defaultValue) {
        String property = properties.getProperty(key);
        return property == null || "".equals(property) ? defaultValue : property;
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static void setMethodValue(Object instance, String methodName, Object value, Class... parameterTypes) {
        try {
            //设置key类到entity/key中
            Class<?> type = instance.getClass();
            java.lang.reflect.Method method = type.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            method.invoke(instance, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
