package net.kdt.pojavlaunch.fragments;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.authenticator.impl.CommonLoginUtils;
import net.kdt.pojavlaunch.extra.ExtraConstants;

import java.io.UnsupportedEncodingException;

public class MicrosoftLoginFragment extends OAuthFragment {
    public static final String TAG = "MICROSOFT_LOGIN_FRAGMENT";

    public MicrosoftLoginFragment() {
        super("https://login.live.com/oauth20_desktop.srf", formattedUrl(), ExtraConstants.MICROSOFT_LOGIN_TODO);
    }

    private static String formattedUrl() {
        try {
            return "https://login.live.com/oauth20_authorize.srf?" + CommonLoginUtils.convertToFormData(
                    "client_id", Tools.AZURE_CLIENT_ID,
                    "response_type", "code",
                    "redirect_url", "https://login.live.com/oauth20_desktop.srf",
                    "scope", Tools.LOGIN_SCOPE
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
