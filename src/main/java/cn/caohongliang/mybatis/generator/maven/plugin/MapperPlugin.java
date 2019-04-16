package cn.caohongliang.mybatis.generator.maven.plugin;

import cn.caohongliang.mybatis.generator.maven.util.PluginUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;

import java.util.List;

/**
 * entity 插件
 *
 * @author caohongliang
 */
public class MapperPlugin extends PluginAdapter {
    /**
     * mapper接口的父类（含主键）
     */
    public static String rootInterface;
    /**
     * mapper接口的父类（不含主键）
     */
    public static String rootInterfaceNotPrimaryKey;
    private FullyQualifiedJavaType primaryKeyType;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        System.out.println("执行 clientGenerated");
        boolean hasPrimaryKey = !introspectedTable.getPrimaryKeyColumns().isEmpty();
        if (!hasPrimaryKey && PluginUtils.isEmpty(rootInterfaceNotPrimaryKey)) {
            String tableName = introspectedTable.getTableConfiguration().getTableName();
            String message = "表 " + tableName + " 没有主键且没有配置 daoRootInterfaceNotPrimaryKey";
            throw new RuntimeException(message);
        }
        List<String> javaDocLines = interfaze.getJavaDocLines();
        PluginUtils.classComment(javaDocLines, introspectedTable, interfaze.getType().getShortName(), " Mapper");

        FullyQualifiedJavaType baseInterface = hasPrimaryKey ?
                new FullyQualifiedJavaType(rootInterface) : new FullyQualifiedJavaType(rootInterfaceNotPrimaryKey);
        //设置泛型
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        FullyQualifiedJavaType exampleType = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        baseInterface.addTypeArgument(entityType);
        baseInterface.addTypeArgument(exampleType);
        if (hasPrimaryKey) {
            baseInterface.addTypeArgument(primaryKeyType);
        }

        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addSuperInterface(baseInterface);
        interfaze.addImportedType(entityType);
        interfaze.addImportedType(exampleType);
        if (hasPrimaryKey) {
            interfaze.addImportedType(primaryKeyType);
            interfaze.addImportedType(new FullyQualifiedJavaType(rootInterface));
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType(rootInterfaceNotPrimaryKey));
        }

        //已存在的Mapper.java文件不需要覆盖
        JavaClientGeneratorConfiguration configuration = this.getContext().getJavaClientGeneratorConfiguration();
        String targetProject = configuration.getTargetProject();
        String targetPackage = configuration.getTargetPackage();
        String fileName = interfaze.getType().getShortName() + ".java";
        boolean existFile = PluginUtils.existFile(targetProject, targetPackage, fileName);
        return !existFile;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        System.out.println("执行 sqlMapGenerated");
        return true;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // insert
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // insertSelective
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // selectByPrimaryKey
        primaryKeyType = method.getParameters().get(0).getType();
        System.out.println("执行 selectByPrimaryKey: " + primaryKeyType);
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // updateByPrimaryKeySelective
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // updateByPrimaryKey
        return false;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }
}
