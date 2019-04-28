import cn.caohongliang.mybatis.generator.maven.plugin.MapperPlugin;
import cn.caohongliang.mybatis.generator.maven.util.PluginUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author caohongliang
 */
public class Main {

    public static void main(String[] args) throws Exception {
        boolean overwrite = true;
        List<String> warnings = new ArrayList<>();

        InputStream inputFile = Main.class.getResourceAsStream("generatorConfig.xml");

        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(inputFile);

        //加载默认插件
//        for (Context context : config.getContexts()) {
//            addPlugin(context, EntityPlugin.class);
//            addPlugin(context, MapperPlugin.class);
//        }


        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        PluginUtils.shellCallback = callback;

        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

        myBatisGenerator.generate(null);

        for (String warning : warnings) {
            System.out.println(warning);
        }
    }

    private static void addPlugin(Context context, Class<? extends PluginAdapter> pluginClass) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType(pluginClass.getName());
        context.addPluginConfiguration(pluginConfiguration);
    }

}
