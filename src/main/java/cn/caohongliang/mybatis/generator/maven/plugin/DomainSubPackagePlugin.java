package cn.caohongliang.mybatis.generator.maven.plugin;

import cn.caohongliang.mybatis.generator.maven.util.PluginUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * domain 相关的类分包：key/example/blob
 *
 * @author caohongliang
 */
public class DomainSubPackagePlugin extends PluginAdapter {
    public static String keyPackage = "key";
    public static String examplePackage = "example";
    public static String blobPackage = "blob";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        //该代码表示在生成class的时候，向topLevelClass添加一个@Setter和@Getter注解
        PluginUtils.addLombokAnnotation(topLevelClass);
        FullyQualifiedJavaType type = topLevelClass.getType();
        Class<? extends FullyQualifiedJavaType> typeClass = type.getClass();
        try {
            java.lang.reflect.Method method = typeClass.getDeclaredMethod("simpleParse", String.class);
            method.setAccessible(true);
            String primaryKeyType = type.getPackageName() + wrapper(keyPackage) + type.getShortNameWithoutTypeArguments();
            method.invoke(type, primaryKeyType);
            introspectedTable.setPrimaryKeyType(primaryKeyType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //移动example文件到entity.example中
        FullyQualifiedJavaType type = topLevelClass.getType();
        String exampleType = type.getPackageName() + wrapper(examplePackage) + type.getShortNameWithoutTypeArguments();
        PluginUtils.setMethodValue(type, "simpleParse", exampleType, String.class);
        introspectedTable.setExampleType(exampleType);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //移动 withBLOBs文件到 entity.blob 中
        //该代码表示在生成class的时候，向topLevelClass添加一个@Setter和@Getter注解
        PluginUtils.addLombokAnnotation(topLevelClass);
        FullyQualifiedJavaType type = topLevelClass.getType();
        Class<? extends FullyQualifiedJavaType> typeClass = type.getClass();
        try {
            java.lang.reflect.Method method = typeClass.getDeclaredMethod("simpleParse", String.class);
            method.setAccessible(true);
            String blobKeyType = type.getPackageName() + wrapper(blobPackage) + type.getShortNameWithoutTypeArguments();
            method.invoke(type, blobKeyType);
            topLevelClass.addImportedType(introspectedTable.getBaseRecordType());
            introspectedTable.setRecordWithBLOBsType(blobKeyType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private String wrapper(String str) {
        return "." + str + ".";
    }
}
