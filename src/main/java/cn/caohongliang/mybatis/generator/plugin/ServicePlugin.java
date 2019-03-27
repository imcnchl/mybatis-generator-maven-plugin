package cn.caohongliang.mybatis.generator.plugin;

import cn.caohongliang.mybatis.generator.util.PluginUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author caohongliang
 * @date 2018/11/16
 */
public class ServicePlugin extends PluginAdapter {
    /**
     * service接口的包
     */
    private String basePackage;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        basePackage = PluginUtils.getString(properties, "basePackage", null);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        if (PluginUtils.isEmpty(basePackage)) {
            throw new NullPointerException("basePackage can't be null");
        }

        String entityName = introspectedTable.getBaseRecordType();
        entityName = entityName.substring(entityName.lastIndexOf(".") + 1);

        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
        //接口
        Interface serviceInterface = new Interface(basePackage + "." + entityName + "Service");
        //类注释
        List<String> javaDocLines = serviceInterface.getJavaDocLines();
        PluginUtils.classComment(javaDocLines, introspectedTable, entityName, " Service");
        //service接口文件
        String targetProject = context.getJavaClientGeneratorConfiguration().getTargetProject();

        String fileName = serviceInterface.getType().getShortName() + ".java";
        boolean existFile = PluginUtils.existFile(targetProject, basePackage, fileName);
        if (!existFile) {
            GeneratedJavaFile serviceInterfaceFile = new GeneratedJavaFile(serviceInterface,
                    targetProject,
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());
            serviceInterface.setVisibility(JavaVisibility.PUBLIC);
            generatedJavaFiles.add(serviceInterfaceFile);
        }
        //实现类
        TopLevelClass serviceImpl = new TopLevelClass(basePackage + ".impl." + entityName + "ServiceImpl");
        serviceImpl.setVisibility(JavaVisibility.PUBLIC);
        serviceImpl.addAnnotation("@Service");
        //继承BaseServiceImpl和实现service
        serviceImpl.addSuperInterface(serviceInterface.getType());
        //导包
        serviceImpl.addImportedType(serviceInterface.getType());
        serviceImpl.addImportedType("org.springframework.stereotype.Service");
        javaDocLines = serviceImpl.getJavaDocLines();
        PluginUtils.classComment(javaDocLines, introspectedTable, entityName, " ServiceImpl");

        fileName = serviceImpl.getType().getShortName() + ".java";
        existFile = PluginUtils.existFile(targetProject, serviceImpl.getType().getPackageName(), fileName);
        if (!existFile) {
            GeneratedJavaFile serviceImplFile = new GeneratedJavaFile(serviceImpl,
                    targetProject,
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());
            generatedJavaFiles.add(serviceImplFile);
        }

        return generatedJavaFiles;
    }
}
