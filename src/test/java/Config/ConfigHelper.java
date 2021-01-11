package Config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigHelper {

    public static ServiceConfig getConfig() {
        return ConfigFactory.newInstance().create(ServiceConfig.class, System.getProperties());
    }
    public static String getSecretPassword() {return getConfig().secretPassword();}
    public static String getSecretLogin() {return getConfig().secretLogin();}
    }



