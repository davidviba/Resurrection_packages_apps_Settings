/*Copyright (C) 2015 The ResurrectionRemix Project
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/
package com.android.settings.rr;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.rr.utils.RRUtils;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class MiscInterfaceSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "MiscInterfaceSettings";
    private static final String RR_OTA = "rr_ota_fab";

    private SwitchPreference mConfig;

    private ListPreference mAnnoyingNotification;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RESURRECTED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rr_interface_other_settings);
        mConfig = (SwitchPreference) findPreference(RR_OTA);
        mConfig.setChecked((Settings.System.getInt(getContentResolver(),
                            Settings.System.RR_OTA_FAB, 1) == 1));
        mConfig.setOnPreferenceChangeListener(this);

        mAnnoyingNotification = (ListPreference) findPreference("less_notification_sounds");
        mAnnoyingNotification.setOnPreferenceChangeListener(this);
        int threshold = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD,
                30000, UserHandle.USER_CURRENT);
        mAnnoyingNotification.setValue(String.valueOf(threshold));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mConfig) {
            boolean newvalue = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RR_OTA_FAB, newvalue ? 1 : 0);
            finish();
            Intent fabIntent = new Intent();
            fabIntent.setClassName("com.android.settings", "com.android.settings.Settings$MainSettingsLayoutActivity");
            startActivity(fabIntent);
            return true;
        } else if (preference.equals(mAnnoyingNotification)) {
            int mode = Integer.parseInt(((String) objValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, mode, UserHandle.USER_CURRENT);
            return true;
        }

        return false;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
        RRUtils.addSearchIndexProvider(R.xml.rr_interface_other_settings);
}
