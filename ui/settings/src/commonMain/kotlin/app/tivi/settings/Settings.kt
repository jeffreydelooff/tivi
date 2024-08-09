// Copyright 2020, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package app.tivi.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.tivi.common.compose.HazeScaffold
import app.tivi.common.compose.itemSpacer
import app.tivi.common.compose.ui.ArrowBackForPlatform
import app.tivi.common.compose.ui.CheckboxPreference
import app.tivi.common.compose.ui.Preference
import app.tivi.common.compose.ui.PreferenceDivider
import app.tivi.common.compose.ui.PreferenceHeader
import app.tivi.common.ui.resources.strings.Res
import app.tivi.common.ui.resources.strings.cdNavigateUp
import app.tivi.common.ui.resources.strings.developerSettingsTitle
import app.tivi.common.ui.resources.strings.settingsAboutCategoryTitle
import app.tivi.common.ui.resources.strings.settingsAnalyticsDataCollectionSummary
import app.tivi.common.ui.resources.strings.settingsAnalyticsDataCollectionTitle
import app.tivi.common.ui.resources.strings.settingsAppVersion
import app.tivi.common.ui.resources.strings.settingsAppVersionSummary
import app.tivi.common.ui.resources.strings.settingsCrashDataCollectionSummary
import app.tivi.common.ui.resources.strings.settingsCrashDataCollectionTitle
import app.tivi.common.ui.resources.strings.settingsDataSaverSummaryOff
import app.tivi.common.ui.resources.strings.settingsDataSaverSummaryOn
import app.tivi.common.ui.resources.strings.settingsDataSaverTitle
import app.tivi.common.ui.resources.strings.settingsDynamicColorSummary
import app.tivi.common.ui.resources.strings.settingsDynamicColorTitle
import app.tivi.common.ui.resources.strings.settingsIgnoreSpecialsSummary
import app.tivi.common.ui.resources.strings.settingsIgnoreSpecialsTitle
import app.tivi.common.ui.resources.strings.settingsNotificationsAiringEpisodesSummary
import app.tivi.common.ui.resources.strings.settingsNotificationsAiringEpisodesTitle
import app.tivi.common.ui.resources.strings.settingsNotificationsCategoryTitle
import app.tivi.common.ui.resources.strings.settingsOpenSource
import app.tivi.common.ui.resources.strings.settingsOpenSourceSummary
import app.tivi.common.ui.resources.strings.settingsPrivacyCategoryTitle
import app.tivi.common.ui.resources.strings.settingsThemeTitle
import app.tivi.common.ui.resources.strings.settingsTitle
import app.tivi.common.ui.resources.strings.settingsUiCategoryTitle
import app.tivi.common.ui.resources.strings.viewPrivacyPolicy
import app.tivi.entitlements.ui.Paywall
import app.tivi.screens.SettingsScreen
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
class SettingsUiFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? = when (screen) {
    is SettingsScreen -> {
      ui<SettingsUiState> { state, modifier ->
        Settings(state, modifier)
      }
    }

    else -> null
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun Settings(
  state: SettingsUiState,
  modifier: Modifier = Modifier,
) {
  // Need to extract the eventSink out to a local val, so that the Compose Compiler
  // treats it as stable. See: https://issuetracker.google.com/issues/256100927
  val eventSink = state.eventSink

  if (state.proUpsellVisible) {
    Paywall(
      onDismissRequest = { eventSink(SettingsUiEvent.DismissProUpsell) },
    )
  }

  HazeScaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(Res.string.settingsTitle)) },
        navigationIcon = {
          IconButton(onClick = { eventSink(SettingsUiEvent.NavigateUp) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBackForPlatform,
              contentDescription = stringResource(Res.string.cdNavigateUp),
            )
          }
        },
      )
    },
    modifier = modifier,
  ) { contentPadding ->
    LazyColumn(
      contentPadding = contentPadding,
      modifier = Modifier.fillMaxWidth(),
    ) {
      stickyHeader {
        PreferenceHeader(stringResource(Res.string.settingsUiCategoryTitle))
      }

      item {
        ThemePreference(
          title = stringResource(Res.string.settingsThemeTitle),
          selected = state.theme,
          onThemeSelected = { eventSink(SettingsUiEvent.SetTheme(it)) },
        )
      }

      item { PreferenceDivider() }

      if (state.dynamicColorsAvailable) {
        item {
          CheckboxPreference(
            title = stringResource(Res.string.settingsDynamicColorTitle),
            summaryOff = stringResource(Res.string.settingsDynamicColorSummary),
            onCheckClicked = { eventSink(SettingsUiEvent.ToggleUseDynamicColors) },
            checked = state.useDynamicColors,
          )
        }

        item { PreferenceDivider() }
      }

      item {
        CheckboxPreference(
          title = stringResource(Res.string.settingsDataSaverTitle),
          summaryOff = stringResource(Res.string.settingsDataSaverSummaryOff),
          summaryOn = stringResource(Res.string.settingsDataSaverSummaryOn),
          onCheckClicked = { eventSink(SettingsUiEvent.ToggleUseLessData) },
          checked = state.useLessData,
        )
      }

      item { PreferenceDivider() }

      item {
        CheckboxPreference(
          title = stringResource(Res.string.settingsIgnoreSpecialsTitle),
          summaryOff = stringResource(Res.string.settingsIgnoreSpecialsSummary),
          onCheckClicked = { eventSink(SettingsUiEvent.ToggleIgnoreSpecials) },
          checked = state.ignoreSpecials,
        )
      }

      itemSpacer(24.dp)

      stickyHeader {
        PreferenceHeader(stringResource(Res.string.settingsNotificationsCategoryTitle))
      }

      item {
        CheckboxPreference(
          title = stringResource(Res.string.settingsNotificationsAiringEpisodesTitle),
          summaryOff = stringResource(Res.string.settingsNotificationsAiringEpisodesSummary),
          onCheckClicked = { eventSink(SettingsUiEvent.ToggleAiringEpisodeNotificationsEnabled) },
          checked = state.airingEpisodeNotificationsEnabled,
          beforeControl = {
            if (!state.isPro) {
              Icon(
                imageVector = Icons.Default.Loyalty,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
              )
            }
          },
        )
      }

      itemSpacer(24.dp)

      stickyHeader {
        PreferenceHeader(stringResource(Res.string.settingsPrivacyCategoryTitle))
      }

      item {
        Preference(
          title = stringResource(Res.string.viewPrivacyPolicy),
          onClick = { eventSink(SettingsUiEvent.NavigatePrivacyPolicy) },
        )
      }

      item { PreferenceDivider() }

      item {
        CheckboxPreference(
          title = stringResource(Res.string.settingsCrashDataCollectionTitle),
          summaryOff = stringResource(Res.string.settingsCrashDataCollectionSummary),
          onCheckClicked = { eventSink(SettingsUiEvent.ToggleCrashDataReporting) },
          checked = state.crashDataReportingEnabled,
        )
      }

      item { PreferenceDivider() }

      item {
        CheckboxPreference(
          title = stringResource(Res.string.settingsAnalyticsDataCollectionTitle),
          summaryOff = stringResource(Res.string.settingsAnalyticsDataCollectionSummary),
          onCheckClicked = { eventSink(SettingsUiEvent.ToggleAnalyticsDataReporting) },
          checked = state.analyticsDataReportingEnabled,
        )
      }

      itemSpacer(24.dp)

      stickyHeader {
        PreferenceHeader(stringResource(Res.string.settingsAboutCategoryTitle))
      }

      item {
        Preference(
          title = stringResource(Res.string.settingsAppVersion),
          summary = {
            Text(
              text = stringResource(
                Res.string.settingsAppVersionSummary,
                state.applicationInfo.versionName,
                state.applicationInfo.versionCode,
              ),
            )
          },
        )
      }

      if (state.openSourceLicenseAvailable) {
        item { PreferenceDivider() }

        item {
          Preference(
            title = stringResource(Res.string.settingsOpenSource),
            summary = {
              Text(stringResource(Res.string.settingsOpenSourceSummary))
            },
            onClick = { eventSink(SettingsUiEvent.NavigateOpenSource) },
          )
        }
      }

      if (state.showDeveloperSettings) {
        item { PreferenceDivider() }

        item {
          Preference(
            title = stringResource(Res.string.developerSettingsTitle),
            onClick = { eventSink(SettingsUiEvent.NavigateDeveloperSettings) },
          )
        }
      }

      itemSpacer(16.dp)
    }
  }
}

@Composable
private fun ThemePreference(
  selected: TiviPreferences.Theme,
  onThemeSelected: (TiviPreferences.Theme) -> Unit,
  title: String,
  modifier: Modifier = Modifier,
) {
  Preference(
    title = title,
    control = {
      Row(Modifier.selectableGroup()) {
        ThemeButton(
          icon = Icons.Default.AutoMode,
          onClick = { onThemeSelected(TiviPreferences.Theme.SYSTEM) },
          isSelected = selected == TiviPreferences.Theme.SYSTEM,
        )

        ThemeButton(
          icon = Icons.Default.LightMode,
          onClick = { onThemeSelected(TiviPreferences.Theme.LIGHT) },
          isSelected = selected == TiviPreferences.Theme.LIGHT,
        )

        ThemeButton(
          icon = Icons.Default.DarkMode,
          onClick = { onThemeSelected(TiviPreferences.Theme.DARK) },
          isSelected = selected == TiviPreferences.Theme.DARK,
        )
      }
    },
    modifier = modifier,
  )
}

@Composable
private fun ThemeButton(
  isSelected: Boolean,
  icon: ImageVector,
  onClick: () -> Unit,
) {
  FilledIconToggleButton(
    checked = isSelected,
    onCheckedChange = { onClick() },
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
    )
  }
}
