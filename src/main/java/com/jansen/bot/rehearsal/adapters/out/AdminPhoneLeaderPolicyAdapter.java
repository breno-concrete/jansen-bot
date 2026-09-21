package com.jansen.bot.rehearsal.adapters.out;

import com.jansen.bot.config.AppProperties;
import com.jansen.bot.rehearsal.ports.LeaderPolicyPort;
import com.jansen.bot.util.PhoneUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Adapter de {@link LeaderPolicyPort}: líder é quem está em {@code AppProperties.getAdminPhones()}
 * (research.md D3, mesma lógica de telefones de {@code ActionDispatcher.isAdmin()}).
 */
@Component
public class AdminPhoneLeaderPolicyAdapter implements LeaderPolicyPort {

    private final AppProperties properties;

    public AdminPhoneLeaderPolicyAdapter(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isLider(String telefone) {
        String normalizado = PhoneUtils.normalize(telefone);
        return Arrays.stream(properties.getAdminPhones().split(","))
                .map(String::trim)
                .map(PhoneUtils::normalize)
                .anyMatch(normalizado::equals);
    }
}
