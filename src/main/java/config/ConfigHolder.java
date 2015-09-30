/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import com.compomics.pride_asa_pipeline.util.ResourceUtils;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

public class ConfigHolder extends PropertiesConfiguration {

    private static final Logger LOGGER = Logger.getLogger(ConfigHolder.class);
    private static ConfigHolder ourInstance;

    static {
        try {
            Resource propertiesResource = ResourceUtils.getResourceByRelativePath("resources/BookChapter.properties");
            ourInstance = new ConfigHolder(propertiesResource);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gets the PropertiesConfiguration instance
     *
     * @return the PropertiesConfigurationHolder instance
     */
    public static ConfigHolder getInstance() {
        return ourInstance;
    }

    public static ConfigHolder getInstanceProteinDiversity() throws ConfigurationException, IOException {
        Resource propertiesResource = ResourceUtils.getResourceByRelativePath("resources/ProteinDiversity.properties");
        ourInstance = new ConfigHolder(propertiesResource);
        return ourInstance;
    }

    private ConfigHolder(Resource propertiesResource) throws ConfigurationException, IOException {
        super(propertiesResource.getURL());
    }
}
