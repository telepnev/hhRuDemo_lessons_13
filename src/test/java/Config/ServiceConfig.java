package Config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "file:~/project.properties"
})

public interface ServiceConfig extends Config {

    @Key("secret.password.hhDemo")
    String secretPassword();

    @Key("secret.login.hhDemo")
    String secretLogin();

}
