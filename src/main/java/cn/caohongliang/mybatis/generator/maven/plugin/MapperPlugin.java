package cn.caohongliang.mybatis.generator.maven.plugin;

import cn.caohongliang.mybatis.generator.maven.util.PluginUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.XMLMapperGenerator;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.NullProgressCallback;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static cn.caohongliang.mybatis.generator.maven.util.PluginUtils.shellCallback;

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
    /**
     * 自动生成
     */
    public static String autoGenXmlDir = "autogen";
    public static boolean genCustomFile = true;

    private FullyQualifiedJavaType primaryKeyType;
    private GeneratedXmlFile autoGenXmlFile;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        System.out.println("--------------------" + tableName + "--------------------");
        //支持subPackage
        String mapperPackage = introspectedTable.getMyBatis3XmlMapperPackage();

        String endWithDao = ".dao";
        if (mapperPackage.endsWith(endWithDao)) {
            mapperPackage = mapperPackage.substring(0, mapperPackage.length() - endWithDao.length());
        }
        String endWithMapper = ".mapper";
        if (mapperPackage.endsWith(endWithMapper)) {
            mapperPackage = mapperPackage.substring(0, mapperPackage.length() - endWithMapper.length());
        }

        mapperPackage += "." + autoGenXmlDir;
        introspectedTable.setMyBatis3XmlMapperPackage(mapperPackage);
        String log = String.format("执行 initialized：表 %s 的自动生成xml文件的目录为 %s", tableName, mapperPackage);
        System.out.println(log);
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        GeneratedXmlFile customFile = generateCustomXmlFile(introspectedTable);
        if (customFile != null) {
            return Collections.singletonList(customFile);
        }

        return null;
    }

    private GeneratedXmlFile generateCustomXmlFile(IntrospectedTable introspectedTable) {
        if (!genCustomFile || autoGenXmlFile == null) {
            return null;
        }
        String mapperPackage = autoGenXmlFile.getTargetPackage();
        mapperPackage = mapperPackage.substring(0, mapperPackage.length() - autoGenXmlDir.length() - 1);

        boolean existCustomFile = PluginUtils.existFile(autoGenXmlFile.getTargetProject(), mapperPackage, autoGenXmlFile.getFileName());
        if (existCustomFile) {
            String log = String.format("执行 generateCustomXmlFile：自定义的xml文件 %s 已存在，跳过", mapperPackage);
            System.out.println(log);
            return null;
        }

        //生成自定义xml
        String log = String.format("执行 generateCustomXmlFile：生成自定义的xml文件：%s", mapperPackage);
        System.out.println(log);

        XmlElement answer = new XmlElement("mapper");
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", namespace));
        answer.addElement(new TextElement("<!-- 在这里写自定义的sql，" + autoGenXmlDir + " 目录下的文件不要手动修改 -->"));
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(answer);

        return new GeneratedXmlFile(document, autoGenXmlFile.getFileName(), mapperPackage,
                autoGenXmlFile.getTargetProject(), true, context.getXmlFormatter());
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //已存在的Mapper.java文件不需要覆盖
        JavaClientGeneratorConfiguration configuration = this.getContext().getJavaClientGeneratorConfiguration();
        String targetProject = configuration.getTargetProject();
        String targetPackage = configuration.getTargetPackage();
        String fileName = interfaze.getType().getShortName() + ".java";
        boolean existFile = PluginUtils.existFile(targetProject, targetPackage, fileName);
        if (existFile) {
            String log = String.format("执行 clientGenerated：文件 %s 已存在，跳过", fileName);
            System.out.println(log);
            return false;
        }

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

        String log = String.format("执行 clientGenerated：生成文件 %s", fileName);
        System.out.println(log);
        return true;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        this.autoGenXmlFile = sqlMap;
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
