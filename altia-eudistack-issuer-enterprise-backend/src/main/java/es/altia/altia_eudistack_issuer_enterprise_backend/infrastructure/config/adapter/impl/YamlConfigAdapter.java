package es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.impl;

import es.altia.altia_eudistack_issuer_enterprise_backend.infrastructure.config.adapter.ConfigAdapter;
import org.springframework.stereotype.Component;

@Component
public class YamlConfigAdapter implements ConfigAdapter {

    @Override
    public String getConfiguration(String key){
        return key;
    }

}
